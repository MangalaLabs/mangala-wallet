/*
 * Copyright 2022 Himanshu Singh
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file has been modified from the original Charty library.
 */
package com.mangala.wallet.chart.bar.model

data class StackedBarData(
    val xValue: Any,
    val yValue: List<Float>,
)

internal fun List<StackedBarData>.isValid(count: Int) = totalItems() == count

private fun List<StackedBarData>.totalItems(): Int = this.maxOf {
    it.yValue.count()
}

internal fun List<StackedBarData>.maxYValue() = maxOf {
    it.yValue.sum()
}
