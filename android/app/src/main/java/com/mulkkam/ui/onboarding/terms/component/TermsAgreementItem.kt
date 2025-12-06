package com.mulkkam.ui.onboarding.terms.component

import android.R.attr.onClick
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun TermsAgreementItem(
    termsLabel: String,
    isChecked: Boolean,
    onClickCheck: () -> Unit,
    onClickNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .noRippleClickable(onClick = onClickCheck),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier =
                    Modifier
                        .size(40.dp)
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                painter = painterResource(R.drawable.ic_terms_agreement_check),
                contentDescription = null,
                tint = getCheckIconTint(isChecked),
            )

            Text(
                text = termsLabel,
                style = MulKkamTheme.typography.body4,
                color = Gray400,
            )
        }

        Icon(
            modifier =
                Modifier
                    .size(40.dp)
                    .noRippleClickable(onClick = onClickNext)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            painter = painterResource(R.drawable.ic_terms_agreement_next),
            contentDescription = null,
            tint = Gray100,
        )
    }
}

private fun getCheckIconTint(isChecked: Boolean): Color =
    if (isChecked) {
        Primary200
    } else {
        Gray100
    }

@Preview(showBackground = true, name = "체크된 상태일 때")
@Composable
private fun TermsAgreementItemPreview_Checked() {
    MulkkamTheme {
        TermsAgreementItem(
            termsLabel = "서비스 이용 약관 동의 (필수)",
            isChecked = true,
            onClickCheck = {},
            onClickNext = {},
        )
    }
}

@Preview(showBackground = true, name = "체크되지 않은 상태일 때")
@Composable
private fun TermsAgreementItemPreview_Unchecked() {
    MulkkamTheme {
        TermsAgreementItem(
            termsLabel = "서비스 이용 약관 동의 (필수)",
            isChecked = false,
            onClickCheck = {},
            onClickNext = {},
        )
    }
}
