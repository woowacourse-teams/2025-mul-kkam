package com.mulkkam.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.cupsRepository
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.toDomain
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {
    fun saveCupOrder(cups: List<CupUiModel>) {
        val reordered = Cups(cups.map { it.toDomain() }).reorderRanks()
        viewModelScope.launch {
            runCatching {
                cupsRepository.putCupsRank(reordered).getOrError()
            }
        }
    }
}
