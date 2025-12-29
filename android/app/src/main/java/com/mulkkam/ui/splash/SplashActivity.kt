package com.mulkkam.ui.splash

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.mulkkam.databinding.ActivitySplashBinding
import com.mulkkam.ui.login.LoginActivity
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.UserAuthState
import com.mulkkam.ui.model.UserAuthState.ACTIVE_USER
import com.mulkkam.ui.model.UserAuthState.UNONBOARDED
import com.mulkkam.ui.onboarding.OnboardingActivity
import com.mulkkam.ui.util.binding.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BindingActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    private val viewModel: SplashViewModel by viewModels()
    private var isSplashFinished: Boolean = false

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
                    isSplashFinished = true
                    navigateToNextScreen(viewModel.authUiState.value ?: return)
                }

                override fun onAnimationRepeat(p0: Animator) = Unit

                override fun onAnimationStart(p0: Animator) {
                    isSplashFinished = false
                }
            },
        )
    }

    private fun initObservers() {
        viewModel.authUiState.observe(this) { authUiState ->
            navigateToNextScreen(authUiState)
        }
    }

    private fun navigateToNextScreen(authUiState: MulKkamUiState<UserAuthState>) {
        if (isSplashFinished.not()) return

        val intent =
            when (authUiState) {
                is MulKkamUiState.Success<UserAuthState> -> {
                    when (authUiState.data) {
                        UNONBOARDED -> OnboardingActivity.newIntent(this)
                        ACTIVE_USER -> MainActivity.newIntent(this)
                    }
                }

                is MulKkamUiState.Loading -> return
                is MulKkamUiState.Idle -> return
                is MulKkamUiState.Failure -> LoginActivity.newIntent(this)
            }
        startActivity(intent)
        finish()
    }

    companion object {
        private const val BLINK_PROGRESS: Float = 0.84f
    }
}
