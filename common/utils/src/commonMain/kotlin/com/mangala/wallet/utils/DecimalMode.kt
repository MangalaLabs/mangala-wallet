package com.mangala.wallet.utils

import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode

val ramDecimalMode = DecimalMode(
    scale = 5L,
    roundingMode = RoundingMode.ROUND_HALF_TO_EVEN,
    decimalPrecision = 20
)

// Need to specify scale for non-terminating division
val calculatingDecimalMode = DecimalMode(
    roundingMode = RoundingMode.ROUND_HALF_TO_EVEN,
    decimalPrecision = 20
)

val displayRoundingMode = RoundingMode.ROUND_HALF_CEILING