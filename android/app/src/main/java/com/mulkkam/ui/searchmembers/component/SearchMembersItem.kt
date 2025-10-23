package com.mulkkam.ui.searchmembers.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.friends.FriendRequestStatus
import com.mulkkam.domain.model.members.MemberSearchInfo
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.members.RequestDirection
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun SearchMembersItem(
    memberSearchInfo: MemberSearchInfo,
    onClick: () -> Unit,
) {
    Column {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Gray100,
        )

        Row(
            modifier =
                Modifier
                    .height(height = 72.dp)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = memberSearchInfo.nickname.name,
                color = Black,
                style = MulKkamTheme.typography.title1,
            )

            when {
                memberSearchInfo.isFriends() -> {
                    Text(
                        text = stringResource(R.string.search_friends_already_friend),
                        color = Gray200,
                        style = MulKkamTheme.typography.label1,
                    )
                }

                memberSearchInfo.isRequestedByMe() -> {
                    Text(
                        text = stringResource(R.string.search_friends_requested),
                        color = Primary100,
                        style = MulKkamTheme.typography.label1,
                    )
                }

                else -> {
                    Box(
                        modifier =
                            Modifier
                                .size(48.dp)
                                .noRippleClickable(onClick = onClick)
                                .padding(6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Primary100),
                    ) {
                        Icon(
                            modifier =
                                Modifier
                                    .align(Alignment.Center)
                                    .fillMaxSize(),
                            painter = painterResource(R.drawable.ic_setting_add),
                            contentDescription = null,
                            tint = White,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "아무 사이도 아닌 멤버 결과")
@Composable
private fun SearchMembersItemPreview_None() {
    MulkkamTheme {
        SearchMembersItem(
            memberSearchInfo =
                MemberSearchInfo(
                    id = 1L,
                    nickname = Nickname("돈가스먹는환노"),
                    status = FriendRequestStatus.NONE,
                    direction = RequestDirection.NONE,
                ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, name = "이미 친구인 멤버 결과")
@Composable
private fun SearchMembersItemPreview_Friend() {
    MulkkamTheme {
        SearchMembersItem(
            memberSearchInfo =
                MemberSearchInfo(
                    id = 1L,
                    nickname = Nickname("돈가스싫은이든"),
                    status = FriendRequestStatus.ACCEPTED,
                    direction = RequestDirection.NONE,
                ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, name = "친구 신청 보낸 멤버 결과")
@Composable
private fun SearchMembersItemPreview_Requested() {
    MulkkamTheme {
        SearchMembersItem(
            memberSearchInfo =
                MemberSearchInfo(
                    id = 1L,
                    nickname = Nickname("돈가스좋은공백"),
                    status = FriendRequestStatus.REQUESTED,
                    direction = RequestDirection.REQUESTED_BY_ME,
                ),
            onClick = {},
        )
    }
}
