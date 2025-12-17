package com.mulkkam.ui.settingbioinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.health.connect.client.HealthConnectClient
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.util.extensions.isHealthConnectAvailable
import com.mulkkam.ui.util.extensions.navigateToHealthConnectStore
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingBioInfoActivity : ComponentActivity() {
    private val viewModel: SettingBioInfoViewModel by viewModel()

    private val healthConnectIntent: Intent by lazy {
        Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                SettingBioInfoScreen(
                    navigateToBack = ::finish,
                    navigateToHealthConnect = {
                        if (isHealthConnectAvailable()) {
                            startActivity(healthConnectIntent)
                        } else {
                            navigateToHealthConnectStore()
                        }
                    },
                    viewModel = viewModel,
                )
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingBioInfoActivity::class.java)
    }
}
