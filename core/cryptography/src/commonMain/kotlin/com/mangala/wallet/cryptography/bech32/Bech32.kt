package com.mangala.wallet.cryptography.bech32

const val CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l"

private fun bech32Polymod(values: IntArray): Int {
    val GENERATORS = intArrayOf(0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3)
    var chk = 1
    for (v in values) {
        val b = chk shr 25
        chk = (chk and 0x1ffffff shl 5) xor v
        for (i in 0..4) {
            if (((b shr i) and 1) != 0) {
                chk = chk xor GENERATORS[i]
            }
        }
    }
    return chk
}

private fun bech32HrpExpand(hrp: String): IntArray {
    val ret = IntArray(hrp.length * 2 + 1)
    for (i in hrp.indices) {
        val c = hrp[i].code
        ret[i] = c shr 5
        ret[i + hrp.length + 1] = c and 31
    }
    return ret
}

fun bech32Encode(hrp: String, data: IntArray): String {
    val combined = bech32HrpExpand(hrp) + data + IntArray(6) { 0 }
    val polymod = bech32Polymod(combined) xor 1
    val checksum = IntArray(6) { (polymod shr 5 * (5 - it)) and 31 }
    val chars = data + checksum
    val encodedData = chars.map { CHARSET[it] }.joinToString("")
    return hrp + '1' + encodedData
}