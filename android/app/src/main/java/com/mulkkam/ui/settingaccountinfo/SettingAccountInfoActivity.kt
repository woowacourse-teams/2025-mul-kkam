package com.mulkkam.ui.settingaccountinfo

import android.os.Bundle
import androidx.activity.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingAccountInfoBinding
import com.mulkkam.ui.settingaccountinfo.adapter.AccountInfoAdapter
import com.mulkkam.ui.settingaccountinfo.adapter.AccountInfoViewHolder
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.setSingleClickListener

class SettingAccountInfoActivity : BindingActivity<ActivitySettingAccountInfoBinding>(ActivitySettingAccountInfoBinding::inflate) {
    private val viewModel: SettingAccountInfoViewModel by viewModels()

    private val adapter: AccountInfoAdapter by lazy {
        AccountInfoAdapter(
            object : AccountInfoViewHolder.Handler {
                override fun onSettingNormalClick(item: SettingAccountUiModel) {
                    when (item.title) {
                        R.string.setting_account_info_logout -> {
                            // TODO: 로그아웃
                        }

                        R.string.setting_account_info_delete_account -> {
                            viewModel.deleteAccount()
                        }
                    }
                }
            },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initAdapter()
        initObservers()
        initClickListeners()
    }

    private fun initAdapter() {
        binding.rvUserInfo.adapter = adapter
    }

    private fun initObservers() {
        viewModel.userInfo.observe(this) {
            (binding.rvUserInfo.adapter as? AccountInfoAdapter)?.submitList(it)
        }
    }

    private fun initClickListeners() {
        binding.ivBack.setSingleClickListener {
            finish()
        }
    }
}
