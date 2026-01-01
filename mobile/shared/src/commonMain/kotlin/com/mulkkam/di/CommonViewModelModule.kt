package com.mulkkam.di

import com.mulkkam.ui.auth.splash.SplashViewModel
import com.mulkkam.ui.friends.friends.FriendsViewModel
import com.mulkkam.ui.history.HistoryViewModel
import com.mulkkam.ui.home.home.HomeViewModel
import com.mulkkam.ui.home.home.ManualDrinkViewModel
import com.mulkkam.ui.login.LoginViewModel
import com.mulkkam.ui.main.MainViewModel
import com.mulkkam.ui.notification.NotificationViewModel
import com.mulkkam.ui.onboarding.bioinfo.BioInfoViewModel
import com.mulkkam.ui.onboarding.cups.CupViewModel
import com.mulkkam.ui.onboarding.cups.CupsViewModel
import com.mulkkam.ui.onboarding.nickname.NicknameViewModel
import com.mulkkam.ui.onboarding.targetamount.TargetAmountViewModel
import com.mulkkam.ui.pendingfriends.PendingFriendsViewModel
import com.mulkkam.ui.searchmembers.SearchMembersViewModel
import com.mulkkam.ui.setting.setting.SettingViewModel
import com.mulkkam.ui.settingaccountinfo.SettingAccountInfoViewModel
import com.mulkkam.ui.settingbioinfo.SettingBioInfoViewModel
import com.mulkkam.ui.settingcups.SettingCupViewModel
import com.mulkkam.ui.settingcups.SettingCupsViewModel
import com.mulkkam.ui.settingnickname.SettingNicknameViewModel
import com.mulkkam.ui.settingnotification.SettingNotificationViewModel
import com.mulkkam.ui.settingreminder.SettingReminderViewModel
import com.mulkkam.ui.settingtargetamount.SettingTargetAmountViewModel
import com.mulkkam.ui.settingterms.SettingTermsViewModel
import com.mulkkam.ui.terms.TermsAgreementViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val commonViewModelModule: Module =
    module {
        viewModel { LoginViewModel(get(), get(), get(), get()) }
        viewModel { NotificationViewModel(get(), get()) }
        viewModel { SettingNicknameViewModel(get(), get(), get()) }
        viewModel { SearchMembersViewModel(get(), get()) }
        viewModel { SettingViewModel(get(), get()) }
        viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
        viewModel { ManualDrinkViewModel() }
        viewModel { SplashViewModel(get(), get(), get()) }
        viewModel { MainViewModel(get(), get(), get(), get(), get(), get(), get()) }
        viewModel { NicknameViewModel(get()) }
        viewModel { BioInfoViewModel() }
        viewModel { PendingFriendsViewModel(get()) }
        viewModel { SettingReminderViewModel(get(), get(), get()) }
        viewModel { HistoryViewModel(get(), get()) }
        viewModel { CupsViewModel(get(), get(), get()) }
        viewModel { SettingBioInfoViewModel(get(), get()) }
        viewModel { TargetAmountViewModel(get()) }
        viewModel { FriendsViewModel(get()) }
        viewModel { SettingAccountInfoViewModel(get(), get(), get(), get()) }
        viewModel { SettingTargetAmountViewModel(get(), get(), get()) }
        viewModel { SettingCupsViewModel(get(), get()) }
        viewModel { SettingNotificationViewModel(get(), get()) }
        viewModel { SettingCupViewModel(get(), get()) }
        viewModel { CupViewModel(get()) }
        viewModel { TermsAgreementViewModel() }
        viewModel { SettingTermsViewModel() }
    }
