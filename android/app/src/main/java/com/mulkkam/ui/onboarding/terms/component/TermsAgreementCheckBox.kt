package com.mulkkam.ui.onboarding.terms.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun TermsAgreementCheckBox(
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
) {
    var checkBoxState by remember { mutableStateOf(checked) }

    Box(
        modifier =
            modifier
                .size(40.dp)
                .noRippleClickable(onClick = {
                    checkBoxState = !checkBoxState
                    onCheckedChange(checkBoxState)
                })
                .padding(8.dp),
    ) {
        if (checkBoxState) {
            CheckBoxSelected(modifier = Modifier.fillMaxSize())
        } else {
            CheckBoxUnSelected(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun CheckBoxSelected(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .clip(CircleShape)
                .background(Primary200),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_terms_agreement_check),
            contentDescription = null,
            tint = White,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(4.dp),
        )
    }
}

@Composable
private fun CheckBoxUnSelected(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(White)
                .border(
                    width = 1.dp,
                    color = Gray200,
                    shape = CircleShape,
                ),
    )
}

@Preview(showBackground = true)
@Composable
private fun CheckBoxSelectedPreview() {
    MulkkamTheme {
        CheckBoxSelected()
    }
}

@Preview(showBackground = true)
@Composable
private fun CheckBoxUnSelectedPreview() {
    MulkkamTheme {
        CheckBoxUnSelected()
    }
}
