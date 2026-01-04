package com.mangala.wallet.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.mangala.wallet.common.mokoresources.Dimensions


@Composable
@OptIn(ExperimentalMaterialApi::class)
fun PullRefreshState(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    refreshThreshold: Dp = Dimensions.refreshThresholdDefault,
    refreshingOffset : Dp = Dimensions.refreshingOffsetDefault
): PullRefreshState {
    return rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh,
        refreshThreshold = refreshThreshold,
        refreshingOffset = refreshingOffset
    )
}
