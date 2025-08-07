package com.mulkkam.ui.splash

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.mulkkam.databinding.ActivitySplashBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.login.LoginActivity
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.model.AppEntryState.ACTIVE_USER
import com.mulkkam.ui.model.AppEntryState.UNAUTHENTICATED
import com.mulkkam.ui.model.AppEntryState.UNONBOARDED
import com.mulkkam.ui.onboarding.OnboardingActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BindingActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lottieSplashCircle.resumeAnimation()
        initAnimatorUpdateListener()
        initAnimatorListener()
        initObservers()
    }

    private fun initAnimatorUpdateListener() {
        binding.lottieSplashCircle.addAnimatorUpdateListener { animation ->
            val progress = animation.animatedFraction
            if (progress >= BLINK_PROGRESS && binding.ivSplashBlink.visibility != View.VISIBLE) {
                binding.ivSplashBlink.visibility = View.VISIBLE
            }
        }
    }

    private fun initAnimatorListener() {
        binding.lottieSplashCircle.addAnimatorListener(
            object : Animator.AnimatorListener {
                override fun onAnimationCancel(p0: Animator) = Unit

                override fun onAnimationEnd(p0: Animator) {
                    viewModel.updateEntryState()
                }

                override fun onAnimationRepeat(p0: Animator) = Unit

                override fun onAnimationStart(p0: Animator) = Unit
            },
        )
    }

    private fun initObservers() {
        viewModel.entryState.observe(this@SplashActivity) { entryState ->
            val intent =
                when (entryState) {
                    UNAUTHENTICATED -> LoginActivity.newIntent(this@SplashActivity)
                    UNONBOARDED -> OnboardingActivity.newIntent(this@SplashActivity)
                    ACTIVE_USER -> MainActivity.newIntent(this@SplashActivity)
                }

            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val BLINK_PROGRESS: Float = 0.84f
    }
}
