package com.mangala.wallet.twofactorauth.data.model

/**
 * Represents backup data structure
 */
data class BackupData(
    val version: Int,
    val totpSecret: ByteArray,
    val backupCodes: ByteArray?,
    val timestamp: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is BackupData) return false

        return version == other.version &&
                timestamp == other.timestamp &&
                totpSecret.contentEquals(other.totpSecret) &&
                (backupCodes == null && other.backupCodes == null ||
                        backupCodes != null && other.backupCodes != null &&
                        backupCodes.contentEquals(other.backupCodes))
    }

    override fun hashCode(): Int {
        var result = version
        result = 31 * result + totpSecret.contentHashCode()
        result = 31 * result + (backupCodes?.contentHashCode() ?: 0)
        result = 31 * result + timestamp.hashCode()
        return result
    }
}