package com.mulkkam.ui.onboarding

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import com.mulkkam.R
import com.mulkkam.databinding.ActivityOnboardingBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.onboarding.dialog.CompleteDialogFragment
import com.mulkkam.ui.onboarding.terms.TermsFragment

class OnboardingActivity : BindingActivity<ActivityOnboardingBinding>(ActivityOnboardingBinding::inflate) {
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(null)
            add(R.id.fcv_onboarding, TermsFragment::class.java, null, OnboardingStep.TERMS.name)
            viewModel.updateOnboardingState(OnboardingStep.TERMS)
        }

        initProgressBarView()
        initClickListeners()
        initObservers()
        initBackPressHandler()
    }

    private fun initProgressBarView() {
        binding.viewOnboardingProgress.apply {
            setSegmentCount(OnboardingStep.entries.size)
            setActiveColor(R.color.gray_400)
            setInactiveColor(R.color.gray_200)
            setProgress(PROGRESS_DEFAULT)
        }
    }

    private fun initClickListeners() {
        binding.tvSkip.setOnClickListener {
            viewModel.moveToNextStep()
        }
    }

    private fun initObservers() {
        viewModel.onboardingState.observe(this) { step ->
            navigateToStep(step)
        }

        viewModel.canSkip.observe(this) { canSkip ->
            binding.tvSkip.isVisible = canSkip
        }

        viewModel.onCompleteOnboarding.observe(this) {
            showCompleteDialogFragment()
        }
    }

    private fun navigateToStep(step: OnboardingStep) {
        binding.viewOnboardingProgress.setProgress(step.ordinal + OFFSET_STEP_ORDINAL)
        if (supportFragmentManager.findFragmentByTag(step.name) == null) {
            supportFragmentManager.commit {
                addToBackStack(null)
                setReorderingAllowed(true)
                add(R.id.fcv_onboarding, step.fragment, null, step.name)
            }
        }
    }

    private fun showCompleteDialogFragment() {
        val completeDialogFragment = CompleteDialogFragment()
        completeDialogFragment.isCancelable = false
        completeDialogFragment.show(supportFragmentManager, null)
    }

    private fun initBackPressHandler() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    supportFragmentManager.popBackStack()
                    viewModel.moveToPreviousStep()
                }
            },
        )
    }

    companion object {
        private const val PROGRESS_DEFAULT: Int = 0
        private const val OFFSET_STEP_ORDINAL: Int = 1
    }
}
