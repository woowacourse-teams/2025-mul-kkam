package com.mulkkam.ui.settingaccountinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingAccountInfoBinding
import com.mulkkam.ui.login.LoginActivity
import com.mulkkam.ui.settingaccountinfo.adapter.AccountInfoAdapter
import com.mulkkam.ui.settingaccountinfo.adapter.AccountInfoViewHolder
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.setSingleClickListener

class SettingAccountInfoActivity : BindingActivity<ActivitySettingAccountInfoBinding>(ActivitySettingAccountInfoBinding::inflate) {
    private val viewModel: SettingAccountInfoViewModel by viewModels()

    private val adapter: AccountInfoAdapter by lazy {
        AccountInfoAdapter(accountHandler)
    }

    private val accountHandler =
        object : AccountInfoViewHolder.Handler {
            override fun onSettingNormalClick(item: SettingAccountUiModel) {
                when (item.title) {
                    R.string.setting_account_info_logout -> {
                        // TODO: 로그아웃
                    }

                    R.string.setting_account_info_delete_account -> {
                        val asdf = AccountDeleteDialogFragment()
                        asdf.show(supportFragmentManager, null)
                    }
                }
            }
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
            adapter.submitList(it)
        }

        viewModel.onDeleteAccount.observe(this) {
            moveToLogin()
        }
    }

    private fun initClickListeners() {
        binding.ivBack.setSingleClickListener {
            finish()
        }
    }

    private fun moveToLogin() {
        val intent =
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        startActivity(intent)
        finish()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingAccountInfoActivity::class.java)
    }
}
