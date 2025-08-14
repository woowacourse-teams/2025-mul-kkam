package com.mulkkam.ui.settingterms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mulkkam.databinding.ActivitySettingNicknameBinding.inflate
import com.mulkkam.databinding.ActivitySettingTermsBinding
import com.mulkkam.ui.settingterms.adapter.TermsAdapter
import com.mulkkam.ui.settingterms.adapter.TermsViewHolder.TermsHandler
import com.mulkkam.ui.util.binding.BindingActivity

class SettingTermsActivity : BindingActivity<ActivitySettingTermsBinding>(ActivitySettingTermsBinding::inflate) {
    private val viewModel: SettingTermsViewModel by viewModels()

    private val termsAdapter: TermsAdapter by lazy {
        TermsAdapter(
            object : TermsHandler {
                override fun loadToTermsPage(termsAgreement: TermsUiModel) {
                }
            },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initAdapter()
        initObservers()
    }

    private fun initAdapter() {
        binding.rvTerms.adapter = termsAdapter
    }

    private fun initObservers() {
        viewModel.terms.observe(this) {
            termsAdapter.submitList(it)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingTermsActivity::class.java)
    }
}
