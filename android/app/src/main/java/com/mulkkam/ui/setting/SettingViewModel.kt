package com.mulkkam.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel
    @Inject
    constructor(
        private val cupsRepository: CupsRepository,
        private val logger: Logger,
    ) : ViewModel() {
        fun saveCupOrder(cups: List<CupUiModel>) {
            val reordered = Cups(cups.map { it.toDomain() }).reorderRanks()
            viewModelScope.launch {
                runCatching {
                    logger.info(LogEvent.USER_ACTION, "Saving cup reorder from settings")
                    cupsRepository.putCupsRank(reordered).getOrError()
                }
            }
        }
    }
