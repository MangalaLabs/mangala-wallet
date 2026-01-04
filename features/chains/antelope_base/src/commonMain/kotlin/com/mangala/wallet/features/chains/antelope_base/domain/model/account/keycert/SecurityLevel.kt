package com.mangala.wallet.features.chains.antelope_base.domain.model.account.keycert

enum class SecurityLevel(val value: Int, val N: Int, val r: Int, val p: Int) {
    DEFAULT(0b001_001_00, 32768, 16, 1),
    HIGH(0b010_001_00, 65536, 16, 1),
    PARANOID(0b011_010_00, 131072, 32, 1);

    fun fromLevelValue(value: Int) {
        when (value) {
            0b001_001_00 -> DEFAULT
            0b010_001_00 -> HIGH
            0b011_010_00 -> PARANOID
        }
    }
}