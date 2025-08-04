package com.mulkkam.ui.splash

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.mulkkam.databinding.ActivitySplashBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BindingActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lottieSplashCircle.resumeAnimation()
        initAnimatorUpdateListener()
        initAnimatorListener()
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
                    // TODO: 추후 자동 로그인 기능 등이 추가되면 로티가 끝남과 로그인 여부를 동시에 검증하고 이동
                    val intent = MainActivity.newIntent(this@SplashActivity)
                    startActivity(intent)
                }

                override fun onAnimationRepeat(p0: Animator) = Unit

                override fun onAnimationStart(p0: Animator) = Unit
            },
        )
    }

    companion object {
        private const val BLINK_PROGRESS: Float = 0.84f
    }
}
