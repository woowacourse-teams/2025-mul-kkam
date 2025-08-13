package com.mulkkam.ui.settingnickname

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingNicknameBinding
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.applyImeMargin
import com.mulkkam.ui.util.extensions.setSingleClickListener

class SettingNicknameActivity : BindingActivity<ActivitySettingNicknameBinding>(ActivitySettingNicknameBinding::inflate) {
    private val viewModel: SettingNicknameViewModel by viewModels()

    private val debounceHandler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initClickListeners()
        initObservers()
        initNicknameInputWatcher()
        initDoneListener()
        binding.tvSaveNickname.applyImeMargin()
    }

    private fun initClickListeners() {
        with(binding) {
            btnCheckDuplicate.setSingleClickListener {
                viewModel.checkNicknameDuplicate(getTrimmedNickname())
            }

            ivBack.setSingleClickListener {
                finish()
            }

            tvSaveNickname.setSingleClickListener {
                viewModel.saveNickname(getTrimmedNickname())
            }
        }
    }

    private fun getTrimmedNickname(): String =
        binding.etInputNickname.text
            .toString()
            .trim()

    private fun initObservers() {
        viewModel.currentNickname.observe(this) {
            binding.etInputNickname.setText(it)
        }

        viewModel.isValidNickname.observe(this) { isValid ->
            if (isValid == null) {
                clearNicknameValidationUI()
                return@observe
            }
            updateNicknameValidationUI(isValid)
            updateSaveButtonEnabled(isValid)
        }

        viewModel.onNicknameChanged.observe(this) {
            Toast
                .makeText(this, R.string.setting_nickname_change_complete, Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }

    private fun clearNicknameValidationUI() {
        with(binding) {
            etInputNickname.backgroundTintList =
                ColorStateList.valueOf(
                    getColor(R.color.gray_400),
                )
            tvNicknameValidationMessage.text = ""
            tvSaveNickname.isEnabled = false
            tvSaveNickname.backgroundTintList =
                ColorStateList.valueOf(
                    getColor(R.color.gray_200),
                )
        }
    }

    private fun updateNicknameValidationUI(isValid: Boolean) {
        val color =
            getColor(
                if (isValid) R.color.primary_200 else R.color.secondary_200,
            )
        val messageResId =
            if (isValid) {
                R.string.setting_nickname_valid
            } else {
                R.string.setting_nickname_warning_duplicated_nickname
            }

        with(binding) {
            tvNicknameValidationMessage.text = getString(messageResId)
            tvNicknameValidationMessage.setTextColor(color)
            etInputNickname.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    private fun updateSaveButtonEnabled(enabled: Boolean) {
        binding.tvSaveNickname.isEnabled = enabled
        if (enabled) {
            binding.tvSaveNickname.backgroundTintList =
                ColorStateList.valueOf(getColor(R.color.primary_200))
        } else {
            binding.tvSaveNickname.backgroundTintList =
                ColorStateList.valueOf(getColor(R.color.gray_200))
        }
    }

    private fun initNicknameInputWatcher() {
        binding.etInputNickname.doAfterTextChanged {
            debounceRunnable?.let { debounceHandler.removeCallbacks(it) }

            debounceRunnable =
                Runnable {
                    val isValid = getTrimmedNickname().isNotEmpty()
                    val colorResId = if (isValid) R.color.primary_200 else R.color.gray_200
                    val color = getColor(colorResId)

                    with(binding.btnCheckDuplicate) {
                        isEnabled = isValid
                        backgroundTintList = ColorStateList.valueOf(color)
                    }
                    viewModel.clearNicknameValidationState()
                }.apply { debounceHandler.postDelayed(this, 100L) }
        }
    }

    private fun initDoneListener() {
        binding.etInputNickname.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(view)
                binding.etInputNickname.clearFocus()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingNicknameActivity::class.java)
    }
}
