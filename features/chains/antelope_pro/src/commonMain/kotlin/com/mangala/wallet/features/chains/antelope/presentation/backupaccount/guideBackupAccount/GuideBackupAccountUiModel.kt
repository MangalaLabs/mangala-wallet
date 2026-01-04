package com.mangala.wallet.features.chains.antelope.presentation.backupaccount.guideBackupAccount

data class GuideBackupAccountUiModel(
    val currentStep: Int,
    val isEnableNextButton: Boolean = false
) {
    companion object {
        private const val STEP_COUNT = 3
    }
    val isFinalStep: Boolean
        get() = currentStep == STEP_COUNT

    val isFirstStep: Boolean
        get() = currentStep == 1
}
