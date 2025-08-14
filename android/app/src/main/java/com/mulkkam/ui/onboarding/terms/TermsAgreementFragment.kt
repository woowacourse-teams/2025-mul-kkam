package com.mulkkam.ui.onboarding.terms

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import com.mulkkam.R
import com.mulkkam.databinding.FragmentTermsBinding
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.onboarding.terms.adapter.TermsAgreementAdapter
import com.mulkkam.ui.onboarding.terms.adapter.TermsAgreementViewHolder.TermsAgreementHandler
import com.mulkkam.ui.util.binding.BindingFragment
import com.mulkkam.ui.util.extensions.applyImeMargin
import com.mulkkam.ui.util.extensions.getAppearanceSpannable
import com.mulkkam.ui.util.extensions.navigateToHealthConnectStore
import com.mulkkam.ui.util.extensions.setSingleClickListener

class TermsAgreementFragment :
    BindingFragment<FragmentTermsBinding>(
        FragmentTermsBinding::inflate,
    ) {
    private val parentViewModel: OnboardingViewModel by activityViewModels()
    private val viewModel: TermsAgreementViewModel by viewModels()

    private val termsAdapter: TermsAgreementAdapter by lazy {
        TermsAgreementAdapter(termsHandler)
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(PermissionController.createRequestPermissionResultContract()) { results ->
            viewModel.updateHealthPermissionStatus(
                results.containsAll(HEALTH_CONNECT_PERMISSIONS),
            )
        }

    private val termsHandler =
        object : TermsAgreementHandler {
            override fun checkAgreement(termsAgreement: TermsAgreementUiModel) {
                when (termsAgreement.labelId) {
                    R.string.terms_agree_health_connect -> viewModel.requestHealthPermission()
                    else -> viewModel.toggleCheckState(termsAgreement)
                }
            }

            override fun loadToTermsPage(termsAgreement: TermsAgreementUiModel) {
                when (termsAgreement.labelId) {
                    R.string.terms_agree_health_connect -> requireContext().navigateToHealthConnectStore()
                    else -> openTermsLink(termsAgreement.uri)
                }
            }
        }

    private fun openTermsLink(
        @StringRes uri: Int,
    ) {
        val intent =
            Intent(
                Intent.ACTION_VIEW,
                requireContext()
                    .getString(uri)
                    .toUri(),
            )
        requireContext().startActivity(intent)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initTextAppearance()
        initTermsAdapter()
        initClickListeners()
        initObservers()
        binding.tvNext.applyImeMargin()
    }

    private fun initTextAppearance() {
        binding.tvTermsLabel.text =
            getString(R.string.terms_agree_hint).getAppearanceSpannable(
                requireContext(),
                R.style.title1,
                getString(R.string.terms_agree_hint_highlight),
            )
    }

    private fun initTermsAdapter() {
        binding.rvList.adapter = termsAdapter
    }

    private fun initClickListeners() {
        with(binding) {
            tvNext.setSingleClickListener {
                parentViewModel.moveToNextStep()
            }

            llAllCheck.setOnClickListener {
                viewModel.checkAllAgreement()
            }
        }
    }

    private fun initObservers() {
        with(viewModel) {
            termsAgreements.observe(viewLifecycleOwner) {
                termsAdapter.submitList(it)

                parentViewModel.updateTermsAgreementState(
                    it.find { it.labelId == R.string.terms_agree_marketing }?.isChecked == true,
                    it.find { it.labelId == R.string.terms_agree_night_notification }?.isChecked == true,
                )
            }

            isAllChecked.observe(viewLifecycleOwner) {
                binding.cbAllCheck.isChecked = it
            }

            canNext.observe(viewLifecycleOwner) {
                binding.tvNext.isEnabled = it
            }

            tryHealthPermission.observe(viewLifecycleOwner) {
                requestPermissionsLauncher.launch(HEALTH_CONNECT_PERMISSIONS)
            }
        }
    }

    companion object {
        private val HEALTH_CONNECT_PERMISSIONS =
            setOf(
                HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
                HealthPermission.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND,
            )
    }
}
