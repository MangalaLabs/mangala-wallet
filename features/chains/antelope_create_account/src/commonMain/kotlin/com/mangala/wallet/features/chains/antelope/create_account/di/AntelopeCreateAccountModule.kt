package com.mangala.wallet.features.chains.antelope.create_account.di

import cafe.adriel.voyager.core.registry.screenModule
import com.linh.antelope_qr.domain.model.CreateAccountForFriendRequest
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRendererPlugin
import com.mangala.wallet.features.chains.antelope.create_account.conversation.AntelopeCreateAccountConfirmationRendererPlugin
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.GetFirstUnassignedPurchaseUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CheckCreateByFriendAccountCreatedUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.ContinueCreateInAppPurchaseAccountUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAccountForFriendUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAccountWithInAppPurchaseUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAccountWithOwnAccountUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAndSaveAccountWithInAppPurchaseUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.GenerateCreateAccountSignRequestUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.GenerateCreateByFriendQrUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.GetAccountInAppPurchaseStatusUseCase
import com.mangala.wallet.features.chains.antelope.create_account.presentation.forfriend.CreateAccountForFriendScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.forfriend.CreateAccountForFriendScreenModel
import com.mangala.wallet.features.chains.antelope.create_account.presentation.iap.IapCreateAccountScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.iap.IapCreateAccountScreenModel
import com.mangala.wallet.features.chains.antelope.create_account.presentation.selectaccounttype.SelectAccountTypeScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.selectaccounttype.SelectAccountTypeScreenModel
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step1.Step1SelectAccountTypeScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step1.Step1SelectAccountTypeScreenModel
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step2.Step2SelectAccountNameScreenModel
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step2.Step2SelectAccountNameScreenV2
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.Step3AccountReadyToClaimScreenModel
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.Step3CreateAccountPaymentScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.Step3CreateAccountPaymentScreenModel
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.createbyfriend.CreateByFriendBottomSheetScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.createbyfriend.CreateByFriendBottomSheetScreenModel
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.selectaccountname.SelectAccountNameBottomSheetScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.selectpaymentaccount.SelectPaymentAccountScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.selectpaymentaccount.SelectPaymentAccountScreenModel
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step4.Step4CreatingAccountScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step4.Step4CreatingAccountScreenModel
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step5.Step5BackupOptionsScreenModel
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.named
import org.koin.dsl.module

val antelopeCreateAccountModule = module {
    factory {
        CreateAndSaveAccountWithInAppPurchaseUseCase(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            ensureAccountInPortfolioUseCase = get()
        )
    }
    factory {
        CreateAccountWithOwnAccountUseCase(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { GetFirstUnassignedPurchaseUseCase(get(), get(), get()) }
    factory { GenerateCreateAccountSignRequestUseCase(get()) }
    factory { GenerateCreateByFriendQrUseCase(get(), get(), get(), get()) }
    factory { CheckCreateByFriendAccountCreatedUseCase(get(), get(), get(), get()) }
    factory { CreateAccountForFriendUseCase(get(), get(), get(), get(), get(), get(), get()) }
    factory { CreateAccountWithInAppPurchaseUseCase(get(), get()) }
    factory { Step1SelectAccountTypeScreenModel(get(), get()) }
    factory { (initialAccountName: String, initialAccountSuffix: String?, accountNameType: AccountNameType) ->
        Step2SelectAccountNameScreenModel(
            initialAccountName = initialAccountName,
            initialAccountSuffix = initialAccountSuffix,
            accountNameType = accountNameType,
            validateAccountUseCase = get(),
            generateRandomAccountNameUseCase = get(),
            checkAccountAvailableToCreateUseCase = get(),
            getSelectedNetworkUseCase = get(),
            buildEnvironmentProvider = get()
        )
    }
    factory { (accountName: String, accountNameSuffix: String?, accountNameType: AccountNameType, eosOwnerPrivateKey: String?, eosActivePrivateKey: String?) ->
        Step3CreateAccountPaymentScreenModel(
            initialAccountName = accountName,
            initialAccountSuffix = accountNameSuffix,
            initialAccountNameType = accountNameType,
            eosOwnerPrivateKey = eosOwnerPrivateKey,
            eosActivePrivateKey = eosActivePrivateKey,
            createAndSaveAccountWithInAppPurchaseUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getAntelopeAccountsUseCase = get(),
            validateAccountUseCase = get(),
            getIsPinSetupUseCase = get(),
            createAndSaveAccountWithOwnAccount = get(),
            saveAccountUseCase = get(),
            generateAccountKeyPairsUseCase = get(),
            purchaseManager = get(),
            getAccountNameHashUseCase = get(),
            buildEnvironmentProvider = get()
        )
    }
    factory { (accountNameWithSuffix: String, accountNameTypeString: String, skipToCreateAccountStep: Boolean, retryCreateAccountName: Boolean, purchaseToken: String?, purchaseId: String?) ->
        IapCreateAccountScreenModel(
            accountNameWithSuffix = accountNameWithSuffix,
            accountNameTypeString = accountNameTypeString,
            skipToCreateAccountStep = skipToCreateAccountStep,
            retryCreateAccountName = retryCreateAccountName,
            purchaseToken = purchaseToken,
            purchaseId = purchaseId,
            mailToFactory = get(),
            purchaseManager = get(),
            saveAccountUseCase = get(),
            createAndSaveAccountWithInAppPurchaseUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getAccountNameHashUseCase = get(),
            continueCreateInAppPurchaseAccountUseCase = get(),
            getAccountInAppPurchaseStatusUseCase = get(),
            getIsPinSetupUseCase = get(),
            getFirstUnassignedPurchaseUseCase = get(),
        )
    }
    factory { (initialAccountName: String) ->
        SelectPaymentAccountScreenModel(
            initialAccountName = initialAccountName,
            getAccountsUseCase = get(),
            getCurrencyBalanceAntelopeUseCase = get(),
            getSelectedNetworkUseCase = get()
        )
    }
    factory { (accountName: String, eosOwnerPrivateKey: String?, eosActivePrivateKey: String?) ->
        CreateByFriendBottomSheetScreenModel(
            accountName = accountName,
            eosOwnerPrivateKey = eosOwnerPrivateKey,
            eosActivePrivateKey = eosActivePrivateKey,
            generateCreateByFriendQrUseCase = get(),
            checkCreateByFriendAccountCreatedUseCase = get()
        )
    }
    factory { (createAccountForFriendRequest: CreateAccountForFriendRequest) ->
        CreateAccountForFriendScreenModel(
            createAccountForFriendRequest,
            createAccountForFriendUseCase = get(),
            getAccountsUseCase = get()
        )
    }
    factory {
        ContinueCreateInAppPurchaseAccountUseCase(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { GetAccountInAppPurchaseStatusUseCase(get(), get()) }
    factoryOf(::SelectAccountTypeScreenModel)
    
    factory { (initialAccountName: String, initialAccountSuffix: String, initialAccountType: AccountNameType) ->
        Step3AccountReadyToClaimScreenModel(
            initialAccountName = initialAccountName,
            initialAccountSuffix = initialAccountSuffix,
            initialAccountType = initialAccountType,
            purchaseManager = get(),
            getSelectedNetworkUseCase = get(),
            saveAccountUseCase = get(),
            getAccountNameHashUseCase = get(),
            getFirstUnassignedPurchaseUseCase = get()
        )
    }
    
    factory { (accountName: String, accountSuffix: String, operationType: SharedScreen.Step4CreatingAccountScreen.AccountOperationType) ->
        Step4CreatingAccountScreenModel(
            accountName = accountName,
            accountSuffix = accountSuffix,
            operationType = operationType,
            createAndSaveAccountWithInAppPurchaseUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getFirstUnassignedPurchaseUseCase = get(),
            generateAccountKeyPairsUseCase = get(),
            updateAccountStatusUseCase = get()
        )
    }
    
    factory { (accountName: String, accountSuffix: String) ->
        Step5BackupOptionsScreenModel(
            accountName = accountName,
            accountSuffix = accountSuffix,
            completeOnboardingUseCase = get(),
            getIsPinSetupUseCase = get()
        )
    }
    
    factory { (accountName: String) ->
        com.mangala.wallet.features.chains.antelope.create_account.presentation.step6.VaultaBackupPrivateKeyScreenModel(
            clipboardFactory = get(),
            accountName = accountName,
            completeOnboardingUseCase = get(),
            getAccountPrivateKeyUseCase = get(),
            getAccountPermissionsUseCase = get(),
            listAccountPublicKeysUseCase = get(),
            getSelectedNetworkUseCase = get()
        )
    }
    single<ConfirmationRendererPlugin>(named("AntelopeCreateAccountConfirmationRenderer")) { AntelopeCreateAccountConfirmationRendererPlugin() }
}

val antelopeCreateAccountScreenModule = screenModule {
    register<SharedScreen.AntelopeCreateAccountStep1Screen> { Step1SelectAccountTypeScreen() }
    register<SharedScreen.AntelopeCreateAccountV2Screen> { Step2SelectAccountNameScreenV2() }
    register<SharedScreen.CreateAccountForFriendScreen> {
        CreateAccountForFriendScreen(
            it.accountName, it.activePublicKey, it.ownerPublicKey, it.blockchainUid
        )
    }
    register<SharedScreen.SelectPaymentAccountScreen> {
        SelectPaymentAccountScreen(it.initialAccountName, it.onSelectAccount)
    }
    register<SharedScreen.SelectAccountNameBottomSheetScreen> {
        SelectAccountNameBottomSheetScreen(
            it.initialAccountName,
            initialAccountSuffix = it.accountNameSuffix,
            it.accountType,
            it.onSelectAccountName
        )
    }
    register<SharedScreen.CreateByFriendBottomSheetScreen> {
        CreateByFriendBottomSheetScreen(
            accountName = it.accountName,
            eosOwnerPrivateKey = it.eosOwnerPrivateKey,
            eosActivePrivateKey = it.eosActivePrivateKey,
            onAccountCreated = it.onAccountCreated
        )
    }
    register<SharedScreen.SelectAccountTypeScreen> { provider ->
        SelectAccountTypeScreen(provider.accountType, provider.accountTypeSelected)
    }
    register<SharedScreen.IapCreateAccountScreen> {
        IapCreateAccountScreen(it.accountNameWithSuffix, it.accountNameType, it.skipToCreateAccountStep, it.retryCreateAccountName, it.purchaseToken, it.purchaseId)
    }
    register<SharedScreen.Step3CreateAccountPaymentScreen> {
        Step3CreateAccountPaymentScreen(
            initialAccountName = it.initialAccountName,
            initialAccountSuffix = it.initialAccountSuffix,
            initialAccountType = it.initialAccountType,
            eosOwnerPrivateKey = it.eosOwnerPrivateKey,
            eosActivePrivateKey = it.eosActivePrivateKey
        )
    }
    register<SharedScreen.Step2SelectAccountNameScreenV2> {
        Step2SelectAccountNameScreenV2()
    }
    register<SharedScreen.Step4CreatingAccountScreen> {
        Step4CreatingAccountScreen(
            accountName = it.accountName,
            accountSuffix = it.accountSuffix,
            operationType = it.operationType
        )
    }
}

inline operator fun <reified T> ParametersHolder.component6(): T = elementAt(5, T::class)
