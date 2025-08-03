package com.mulkkam.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.commit
import com.mulkkam.R
import com.mulkkam.databinding.ActivityOnboardingBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.onboarding.terms.TermsFragment

class OnboardingActivity : BindingActivity<ActivityOnboardingBinding>(ActivityOnboardingBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fcv_onboarding, TermsFragment())
        }
    }
}
