package com.dalvik.dimadenyslessons.review

import android.app.Activity
import android.content.Context
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import me.ads.akads.util.InterstitialHolder

class Review {
    sealed class Result<out T> {
        object Initial : Result<Nothing>()
        object Loading : Result<Nothing>()
        data class Success<out R>(val data: R) : Result<R>()
        data class Error(val error: Throwable) : Result<Nothing>() {
            fun getMessage(): String = error.message ?: error.toString()
        }

        override fun toString(): String {
            return when (this) {
                Initial -> "Initial"
                is Success<*> -> "Success[data=$data]"
                is Error -> "Error[exception=${getMessage()}]"
                Loading -> "Loading"
            }
        }

        val value: T?
            get() = if (this is Success) data else null

        val initial: Boolean
            get() = this is Initial

        val succeeded: Boolean
            get() = this is Success

        val failed: Boolean
            get() = this is Error

        val loading: Boolean
            get() = this is Loading
    }

    interface InterstitialHolder {
        fun showInterstitial(activity: Activity): Boolean
    }

    class MaxInterstitialHolder(
        private val context: Context,
        private val loadTimeout: Long,
        adUnits: List<String>
    ) : InterstitialHolder {

        data class MaxInterstitialAdWrapper(
            val adUnitId: String,
            val maxAd: MaxInterstitialAd,
            var state: Result<MaxAd> = Result.Initial
        )

        private val maxInterstitialAdListener = object : MaxAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                updateAdState(ad.adUnitId, Result.Success(ad))
            }

            override fun onAdDisplayed(p0: MaxAd) {

            }

            override fun onAdHidden(ad: MaxAd) {
                // ad dismissed
                updateAdState(ad.adUnitId, Result.Initial)
                startWaterfallLoading(loadTimeout)
            }

            override fun onAdClicked(p0: MaxAd) {

            }

            override fun onAdLoadFailed(adUnitId: String, p1: MaxError) {
                updateAdState(adUnitId, Result.Error(Throwable("Failed to load")))
            }

            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {

            }
        }

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        private val mutex = Mutex()

        private val interstitials = adUnits.map {
            MaxInterstitialAdWrapper(
                adUnitId = it,
                maxAd = MaxInterstitialAd(it, context).apply {
                    setListener(maxInterstitialAdListener)
                }
            )
        }

        init {
            startWaterfallLoading(loadTimeout)
        }

        private fun startWaterfallLoading(timeout: Long){
            scope.launch {
                val states = interstitials.map { it.state }
                val allAdsLoading = states.all { it is Result.Loading }

                if (!allAdsLoading){
                    for (interstitial in interstitials){
                        when (interstitial.state){
                            is Result.Initial, is Result.Error -> {
                                mutex.withLock {
                                    interstitial.state = Result.Loading
                                }

                                withTimeoutOrNull(timeout){
                                    loadAd(interstitial)
                                }

                                if (interstitial.state is Result.Success){
                                    break
                                }
                            }

                            is Result.Loading -> {
                                delay(timeout)

                                if (interstitial.state is Result.Success){
                                    break
                                }
                            }

                            is Result.Success -> {

                            }
                        }
                    }
                }
            }
        }

        private suspend fun loadAd(maxInterstitialAdWrapper: MaxInterstitialAdWrapper): Unit = suspendCancellableCoroutine { continuation ->
            maxInterstitialAdWrapper.maxAd.loadAd()
        }

        private fun updateAdState(adUnitId: String, state: Result<MaxAd>) {
            scope.launch {
                mutex.withLock {
                    interstitials.firstOrNull{ it.adUnitId == adUnitId}?.let { it.state = state }
                }
            }
        }

        override fun showInterstitial(activity: Activity): Boolean {
            val loadedAd = interstitials.map { it.maxAd }.firstOrNull { maxAd -> maxAd.isReady }

            return loadedAd?.let {
                it.showAd(activity)
                true
            } ?: run {
                startWaterfallLoading(loadTimeout)
                false
            }
        }
    }
}