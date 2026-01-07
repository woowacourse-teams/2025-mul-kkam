package com.mulkkam.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.OnboardingRepository
import com.mulkkam.domain.repository.TokenRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.UserAuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val tokenRepository: TokenRepository,
        private val onboardingRepository: OnboardingRepository,
        private val logger: Logger,
    ) : ViewModel() {
        private val _authUiState: MutableLiveData<MulKkamUiState<UserAuthState>> =
            MutableLiveData<MulKkamUiState<UserAuthState>>(MulKkamUiState.Idle)
        val authUiState: LiveData<MulKkamUiState<UserAuthState>> get() = _authUiState

        init {
            updateAuthState()
        }

        private fun updateAuthState() {
            if (_authUiState.value is MulKkamUiState.Loading) return
            viewModelScope.launch {
                runCatching {
                    _authUiState.value = MulKkamUiState.Loading
                    tokenRepository.getAccessToken().getOrError()
                }.onSuccess { accessToken ->
                    logger.info(LogEvent.SPLASH, "Access token retrieved: ${accessToken != null}")
                    when (accessToken.isNullOrBlank().not()) {
                        true -> updateAuthStateWithOnboarding()
                        false -> _authUiState.value = MulKkamUiState.Failure(MulKkamError.AccountError.InvalidToken)
                    }
                }.onFailure {
                    logger.error(LogEvent.SPLASH, "Failed to retrieve access token ${it.message}")
                    _authUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
                }
            }
        }

        private fun updateAuthStateWithOnboarding() {
            viewModelScope.launch {
                runCatching {
                    onboardingRepository.getOnboardingCheck().getOrError()
                }.onSuccess { userAuthState ->
                    _authUiState.value = MulKkamUiState.Success<UserAuthState>(userAuthState)
                }.onFailure {
                    _authUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
                }
            }
        }
    }
