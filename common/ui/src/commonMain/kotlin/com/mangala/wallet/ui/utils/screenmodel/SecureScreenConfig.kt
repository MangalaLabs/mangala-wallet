package com.mangala.wallet.ui.utils.screenmodel

/**
 * Centralized registry of screen class names that require screenshot and screen-recording
 * protection via [SecureScreen].
 *
 * ## How to protect a screen
 *
 * ### Option A — Registry (recommended for bulk / configurable control)
 * Add the screen's `simpleName` to [secureScreenClassNames]:
 * ```kotlin
 * object SecureScreenConfig {
 *     val secureScreenClassNames: Set<String> = setOf(
 *         "ShowRecoveryPhraseScreen",
 *         "MyNewSensitiveScreen",   // ← add here
 *     )
 * }
 * ```
 * No changes to the screen file are needed.
 *
 * ### Option B — Per-screen flag (for one-off overrides)
 * Override [BaseScreen.isSecure] directly in the screen class:
 * ```kotlin
 * class MyScreen : BaseScreen<MyScreenModel>() {
 *     override val isSecure = true
 * }
 * ```
 *
 * Both options are checked by [BaseScreen.Content]; either one being `true` enables protection.
 *
 * ## Turning off protection for a screen
 * - Registry: remove the class name from [secureScreenClassNames].
 * - Flag: set `override val isSecure = false` (or remove the override).
 */
object SecureScreenConfig {

    /**
     * Set of screen [simpleName]s that must have screenshot / screen-recording protection.
     * Checked at composition time by [BaseScreen].
     */
    val secureScreenClassNames: Set<String> = setOf(
        // ── Wallet backup flow ──────────────────────────────────────────────
        "ShowRecoveryPhraseScreen",
        "VerifyRecoveryPhraseScreen",

        // ── Antelope private-key backup ─────────────────────────────────────
        "BackupAntelopeAccountScreen",
        "BackupAntelopePrivateKeyScreen",

        // ── PIN entry / setup ───────────────────────────────────────────────
        "UnlockPinScreen",
        "UnlockPinScreenV2",
        "SetupPinScreen",
    )

    /** Returns `true` if the given [screenClassName] is in the protected registry. */
    fun isSecure(screenClassName: String): Boolean =
        screenClassName in secureScreenClassNames
}
