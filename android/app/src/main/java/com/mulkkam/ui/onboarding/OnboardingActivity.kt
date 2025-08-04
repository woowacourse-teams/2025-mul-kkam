package com.mulkkam.ui.onboarding

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import com.mulkkam.R
import com.mulkkam.databinding.ActivityOnboardingBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.onboarding.terms.TermsFragment

class OnboardingActivity : BindingActivity<ActivityOnboardingBinding>(ActivityOnboardingBinding::inflate) {
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fcv_onboarding, TermsFragment())
            viewModel.updateOnboardingState(OnboardingStep.TERMS)
        }

        initProgressBarView()
        initObservers()
    }

    private fun initProgressBarView() {
        binding.viewOnboardingProgress.apply {
            setSegmentCount(OnboardingStep.entries.size)
            setActiveColor(R.color.gray_400)
            setInactiveColor(R.color.gray_200)
            setProgress(PROGRESS_DEFAULT)
        }
    }

    private fun initObservers() {
        viewModel.onboardingState.observe(this) { step ->
            navigateToStep(step)
        }

        viewModel.canSkip.observe(this) { canSkip ->
            binding.tvSkip.isVisible = canSkip
        }
    }

    private fun navigateToStep(step: OnboardingStep) {
        binding.viewOnboardingProgress.setProgress(step.ordinal + OFFSET_STEP_ORDINAL)
        step.create().also { fragment ->
            supportFragmentManager.commit {
                addToBackStack(null)
                setReorderingAllowed(true)
                replace(R.id.fcv_onboarding, fragment)
            }
        }
    }

    companion object {
        private const val PROGRESS_DEFAULT: Int = 0
        private const val OFFSET_STEP_ORDINAL: Int = 1
    }
}
