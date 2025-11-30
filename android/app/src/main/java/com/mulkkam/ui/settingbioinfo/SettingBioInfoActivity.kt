package com.mulkkam.ui.settingbioinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.settingbioinfo.dialog.SettingWeightFragment
import com.mulkkam.ui.util.extensions.isHealthConnectAvailable
import com.mulkkam.ui.util.extensions.navigateToHealthConnectStore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingBioInfoActivity : AppCompatActivity() {
    private val viewModel: SettingBioInfoViewModel by viewModels()

    private val healthConnectIntent: Intent by lazy {
        Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS)
    }

    private val weightFragment: SettingWeightFragment by lazy {
        SettingWeightFragment()
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
                    onClickWeightSection = {
                        weightFragment.show(supportFragmentManager, null)
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
