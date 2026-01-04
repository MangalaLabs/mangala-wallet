package com.mangala.wallet.features.chains.antelope.create_account.presentation.iap

import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.IapProductAlreadyOwnedDialog
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource

data class IapCreateAccountUiModel(
    val accountType: AccountNameType,
    val blockchainType: BlockchainType?,
    val currentStep: CreateAccountStep,
    val iapProductAlreadyOwnedDialog: IapProductAlreadyOwnedDialog? = null,
    val purchaseToken: String? = null,
    val purchaseId: String? = null,
    val isPinSetUp: Boolean
) {
    val paymentStepStatus: CreateAccountStepStatus = if (currentStep is CreateAccountStep.Payment) {
        if (currentStep.isPaymentPending) {
            CreateAccountStepStatus.IN_PROGRESS
        } else if (currentStep.error != null) {
            CreateAccountStepStatus.FAILED
        } else if (purchaseToken == null) {
            CreateAccountStepStatus.DEFAULT
        } else {
            CreateAccountStepStatus.DONE
        }
    } else {
        CreateAccountStepStatus.DONE
    }

    val createAccountStepStatus: CreateAccountStepStatus = if (currentStep is CreateAccountStep.CreateAccount) {
        if (currentStep.error != null) {
            CreateAccountStepStatus.FAILED
        } else {
            CreateAccountStepStatus.IN_PROGRESS
        }
    } else if (currentStep is CreateAccountStep.Payment) {
        CreateAccountStepStatus.NOT_SELECTED
    } else {
        CreateAccountStepStatus.DONE
    }

    val exploreMangalaOrSetupPinStepStatus: CreateAccountStepStatus =
        if (currentStep is CreateAccountStep.ExploreMangalaOrSetupPin) {
            if (isPinSetUp.not()) {
                CreateAccountStepStatus.IN_PROGRESS_PROMPT
            } else if (currentStep.enableProceed) {
                CreateAccountStepStatus.DONE_FOCUSED
            } else {
                CreateAccountStepStatus.DEFAULT
            }
        } else {
            CreateAccountStepStatus.NOT_SELECTED
        }

    val allStepStatuses =
        listOf(paymentStepStatus, createAccountStepStatus, exploreMangalaOrSetupPinStepStatus)

    val mainButtonTextRes = if (paymentStepStatus == CreateAccountStepStatus.FAILED || createAccountStepStatus == CreateAccountStepStatus.FAILED) {
            WrappedStringResource.StringRes(MR.strings.all_retry)
        } else if (isPinSetUp) {
            WrappedStringResource.StringRes(MR.strings.message_step_3_create_account_payment_step_explore)
        } else {
            WrappedStringResource.StringRes(MR.strings.message_step_3_create_account_payment_step_setup_pin)
        }

    val accountTypeName = when (blockchainType) {
        BlockchainType.EosJungleTestnet, BlockchainType.Eos -> if (accountType == AccountNameType.Premium) {
            WrappedStringResource.StringRes(MR.strings.message_step_3_create_account_payment_introduction_premium_account_name, blockchainType.name)
        } else {
            WrappedStringResource.PlainString(blockchainType.name)
        }
        else -> WrappedStringResource.PlainString(blockchainType?.name.orEmpty())
    }

    val mainButtonEnabled = when (currentStep) {
        is CreateAccountStep.CreateAccount -> when (createAccountStepStatus) {
            CreateAccountStepStatus.IN_PROGRESS -> false
            else -> true
        }

        is CreateAccountStep.Payment -> {
            when (paymentStepStatus) {
                CreateAccountStepStatus.IN_PROGRESS -> false
                else -> true
            }
        }

        is CreateAccountStep.ExploreMangalaOrSetupPin -> currentStep.enableProceed
    }

    val paymentStepText = when (paymentStepStatus) {
        CreateAccountStepStatus.IN_PROGRESS -> MR.strings.message_step_3_create_account_payment_step_payment_processing
        else -> MR.strings.message_step_3_create_account_payment_step_payment
    }
    val createAccountStepText = when (createAccountStepStatus) {
        CreateAccountStepStatus.IN_PROGRESS -> MR.strings.message_step_3_create_account_payment_step_creating
        else -> MR.strings.message_step_3_create_account_payment_step_create
    }
}

sealed interface CreateAccountStep {
    val error: WrappedStringResource?

    data class Payment(
        override val error: WrappedStringResource? = null,
        val isPaymentPending: Boolean = false
    ) : CreateAccountStep

    data class CreateAccount(
        override val error: WrappedStringResource? = null,
    ) : CreateAccountStep

    data class ExploreMangalaOrSetupPin(
        override val error: WrappedStringResource? = null,
        val enableProceed: Boolean = false
    ) : CreateAccountStep
}

enum class CreateAccountStepStatus(val isInFocus: Boolean) {
    DEFAULT(true),
    NOT_SELECTED(false),
    IN_PROGRESS(true),
    IN_PROGRESS_PROMPT(true), // for set up PIN
    DONE(false),
    DONE_FOCUSED(true),
    FAILED(true);
}