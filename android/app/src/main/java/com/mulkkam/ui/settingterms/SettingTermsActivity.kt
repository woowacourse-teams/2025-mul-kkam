package com.mulkkam.ui.settingterms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.health.connect.client.HealthConnectClient
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingTermsBinding
import com.mulkkam.ui.settingterms.adapter.TermsAdapter
import com.mulkkam.ui.settingterms.adapter.TermsViewHolder.TermsHandler
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.isHealthConnectAvailable
import com.mulkkam.ui.util.extensions.navigateToHealthConnectStore
import com.mulkkam.ui.util.extensions.openTermsLink

class SettingTermsActivity : BindingActivity<ActivitySettingTermsBinding>(ActivitySettingTermsBinding::inflate) {
    private val viewModel: SettingTermsViewModel by viewModels()

    private val termsAdapter: TermsAdapter by lazy {
        TermsAdapter(termsHandler)
    }

    private val healthConnectIntent: Intent by lazy {
        Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS)
    }

    private val termsHandler =
        object : TermsHandler {
            override fun loadToTermsPage(termsAgreement: TermsUiModel) {
                when (termsAgreement.labelId) {
                    R.string.setting_terms_agree_health_connect -> {
                        if (isHealthConnectAvailable()) {
                            startActivity(healthConnectIntent)
                        } else {
                            navigateToHealthConnectStore()
                        }
                    }

                    else -> openTermsLink(termsAgreement.uri)
                }
            }
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
