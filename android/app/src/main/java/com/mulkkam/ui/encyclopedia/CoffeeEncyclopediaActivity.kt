package com.mulkkam.ui.encyclopedia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.ui.designsystem.MulkkamTheme

class CoffeeEncyclopediaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                CoffeeEncyclopediaScreen(
                    navigateToBack = ::finish,
                    navigateToInformationSource = { uri ->
                        val intent =
                            Intent(
                                Intent.ACTION_VIEW,
                                uri,
                            )
                        startActivity(intent)
                    },
                )
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CoffeeEncyclopediaActivity::class.java)
    }
}
