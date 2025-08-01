package com.mulkkam.ui.splash

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Bundle
import com.mulkkam.databinding.ActivitySplashBinding
import com.mulkkam.ui.binding.BindingActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BindingActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lottieSplash.addAnimatorListener(
            object : Animator.AnimatorListener {
                override fun onAnimationCancel(p0: Animator) = Unit

                override fun onAnimationEnd(p0: Animator) {
                    finish()
                }

                override fun onAnimationRepeat(p0: Animator) = Unit

                override fun onAnimationStart(p0: Animator) = Unit
            },
        )
        binding.lottieSplash.resumeAnimation()
    }
}
