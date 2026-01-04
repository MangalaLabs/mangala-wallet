package com.mangala.wallet.features.chains.antelope.di

//import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.step.three.CreateProposalStepThreeScreenModel
import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse
import com.mangala.wallet.domain.portfolio.usecases.PortfolioWalletProvider
import com.mangala.wallet.features.chains.antelope.domain.usecase.account.AntelopePortfolioWalletProvider
import com.mangala.wallet.features.chains.antelope.domain.usecase.account.ImportAccountFromKeyCertUseCase
import com.mangala.wallet.features.chains.antelope.domain.usecase.account.ImportAccountUseCase
import com.mangala.wallet.features.chains.antelope.domain.usecase.account.UpdatePurchasedAccountsStateUseCase
import com.mangala.wallet.features.chains.antelope.presentation.backupaccount.BackupAntelopeAccountScreen
import com.mangala.wallet.features.chains.antelope.presentation.backupaccount.BackupAntelopeAccountScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.backupaccount.backupPrivateKey.BackupAntelopePrivateKeyScreen
import com.mangala.wallet.features.chains.antelope.presentation.backupaccount.backupPrivateKey.BackupAntelopePrivateKeyScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.backupaccount.guideBackupAccount.GuideBackupAccountScreen
import com.mangala.wallet.features.chains.antelope.presentation.backupaccount.guideBackupAccount.GuideBackupAccountScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.backupaccount.keycert.BackupWithKeyCertScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.backupaccount.selectPermissionToBackup.SelectPermissionToBackupScreen
import com.mangala.wallet.features.chains.antelope.presentation.backupaccount.selectPermissionToBackup.SelectPermissionToBackupScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.esr.EsrScreen
import com.mangala.wallet.features.chains.antelope.presentation.esr.EsrScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.giftram.GiftRamScreen
import com.mangala.wallet.features.chains.antelope.presentation.giftram.GiftRamScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.importaccount.Step1ImportAccountPrivateKeyScreen
import com.mangala.wallet.features.chains.antelope.presentation.importaccount.Step1ImportAccountPrivateKeyScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.importaccount.keycert.ImportAccountByKeyCertScreen
import com.mangala.wallet.features.chains.antelope.presentation.importaccount.keycert.ImportAccountByKeyCertScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.importaccount.step2.Step2ImportAccountSelectAccountScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.manageaccount.ManageAntelopeAccountScreen
import com.mangala.wallet.features.chains.antelope.presentation.manageaccount.ManageAntelopeAccountScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.multisig.AntelopeMultisigScreen
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.CreateNewProposalScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.action.MultisigProposalActionScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.approver.MultisigProposalApproverScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.selectAccPer.SelectAccountPermissionScreen
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.selectAccPer.SelectAccountPermissionScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.ProposalDetailScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.list.ProposalTableScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.list.ProposalsByProposerScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.NetAndCpuScreen
import com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.NetAndCpuScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.rentviarex.RentViaRexScreen
import com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.rentviarex.RentViaRexScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.stakeforresource.StakeForResourceScreen
import com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.stakeforresource.StakeForResourceScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.permission.PermissionScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.permission.createcustom.CreatePermissionScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.permission.detail.PermissionDetailScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.permission.linkauth.LinkAuthScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.permission.list.PermissionListScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.permission.unlinkauth.UnLinkAuthScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.permission.updatepermission.UpdatePermissionScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.powerup.PowerUpScreen
import com.mangala.wallet.features.chains.antelope.presentation.powerup.PowerUpScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.proposal.ProposalScreenModal
import com.mangala.wallet.features.chains.antelope.presentation.proposal.approvals.detail.ApprovalProposalDetailScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.proposal.expiredProposal.detail.ExpiredProposalDetailScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail.MyProposalDetailScreen
import com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail.MyProposalDetailScreenModel
import com.mangala.wallet.features.chains.antelope.pro.importaccount.presentation.ImportPrivateKeyScreen
import com.mangala.wallet.features.chains.antelope.pro.importaccount.presentation.ImportPrivateKeyScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.resources.VaultaResourcesScreenModel
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.ParametersHolder
import org.koin.dsl.module

val antelopeModule = module {
//    factory { CreateAccountScreenModel() }
    factory { (accountName: String, isCpu: Boolean) ->
        PowerUpScreenModel(
            accountName = accountName,
            isCpu = isCpu,
            getSelectedNetworkUseCase = get(),
            getPowerUpRateUseCase = get(),
            powerUpUseCase = get(),
            getAccountWithBalanceInfoUseCase = get()
        )
    }
    factory { ImportAccountUseCase(get(), get(), get(), get()) }
    factory { ImportAccountFromKeyCertUseCase(get(), get(), get(), get(), get()) }
    factory { UpdatePurchasedAccountsStateUseCase(get(), get(), get(), get(), get(), get()) }

    factory { (privateKey: String?) ->
        Step1ImportAccountPrivateKeyScreenModel(privateKey, get(), get())
    }
    factory { (privateKey: String, accountByAuthorizer: ArrayList<AntelopeAccountByAuthorizer>) ->
        Step2ImportAccountSelectAccountScreenModel(
            privateKey,
            accountByAuthorizer,
            importAccountUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getIsPinSetupUseCase = get(),
            updateAccountStatusUseCase = get()
        )
    }
    factory { (accountName: String, blockchainUid: String?) ->
        BackupAntelopeAccountScreenModel(
            accountName = accountName,
            blockchainUid = blockchainUid,
            getAccountPermissionsUseCase = get(),
            getAccountPrivateKeyUseCase = get(),
            getSelectedNetworkUseCase = get()
        )
    }
    factory { (accountName: String, isStakeRex: Boolean, isCpu: Boolean) ->
        StakeForResourceScreenModel(
            accountName = accountName,
            isStakeRex = isStakeRex,
            isCpu = isCpu,
            getSelectedNetworkUseCase = get(),
            getAccountWithBalanceInfoUseCase = get(),
            delegateAndUnDelegateBandwidthUseCase = get()
        )
    }
    factory { (isCpu: Boolean) ->
        NetAndCpuScreenModel(
            isCpu = isCpu,
            getPowerUpRateUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getDelegateRateUseCase = get(),
            getRexRateUseCase = get()
        )
    }
    factory { (accountName: String, isCpu: Boolean) ->
        RentViaRexScreenModel(
            accountName = accountName,
            isCpu = isCpu,
            getSelectedNetworkUseCase = get(),
            rentViaRexUseCase = get(),
            getAccountWithBalanceInfoUseCase = get(),
            getRexRateUseCase = get()
        )
    }

    factory { GuideBackupAccountScreenModel() }

    factory { (accountName: String) ->
        SelectPermissionToBackupScreenModel(
            getAccountPermissionsUseCase = get(),
            accountName = accountName
        )
    }

    factory { (accountName: String, permissionName: String) ->
        BackupAntelopePrivateKeyScreenModel(
            getAccountPrivateKeyUseCase = get(),
            listAccountPublicKeysUseCase = get(),
            clipboardFactory = get(),
            accountName = accountName,
            permissionName = permissionName
        )
    }

    factory {
        ManageAntelopeAccountScreenModel(
            getAntelopeAccountsUseCase = get(),
            deleteAccountUseCase = get(),
            toastFactory = get()
        )
    }

    factory { (esrUri: String) ->
        EsrScreenModel(
            esrUri = esrUri,
            decodeEsrUseCase = get(),
            resolveEsrUseCase = get(),
            getAccountsUseCase = get(),
            getAccountPermissionsUseCase = get()
        )
    }

    factory { (keyCert: String) ->
        ImportAccountByKeyCertScreenModel(
            keyCert = keyCert,
            decryptKeyCertUseCase = get(),
            importAccountFromKeyCertUseCase = get(),
            generateKeyAndUpdateAccountPermissionUseCase = get(),
            getIsPinSetupUseCase = get()
        )
    }

    factory {
        BackupWithKeyCertScreenModel(get(), get(), get(), get())
    }
    factory { (proposalName: String?, accountName: String?) ->
        CreateNewProposalScreenModel(
            proposalName = proposalName,
            accountName = accountName,
            getSelectedNetworkUseCase = get(),
            createProposeTransactionUseCase = get(),
            getBlockchainExplorerLinkUseCase = get(),
            saveNewProposalDraftUseCase = get(),
            getProposalDraftUseCase = get(),
            getAccountsUseCase = get(),
            getAccountPermissionsUseCase = get(),
            deleteProposalUseCase = get(),
            updateProposalOnSubmitSuccessUseCase = get()
        )
    }
    factory { (actionData: MultisigAction?) ->
        MultisigProposalActionScreenModel(actionData, get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
    }
    factory { (action: List<MultisigAction>, approvers: Map<MultisigActionAuthorization, List<MultisigActionAuthorization>>) ->
        MultisigProposalApproverScreenModel(action, approvers, get())
    }
    factory { ProposalTableScreenModel(get(), get()) }
    factory { (proposer: String, proposal: GetMultisigProposalTableRowResponse.ProposalRow) ->
        ProposalDetailScreenModel(
            proposerAccountName = proposer,
            proposal = proposal,
            getSelectedNetworkUseCase = get(),
            approveProposalUseCase = get(),
            cancelProposalUseCase = get(),
            executeProposalUseCase = get(),
            unApproveProposalUseCase = get(),
            getTableRowsDataProposalUseCase = get(),
            decoderProposalTransactionUseCase = get(),
            getAccountsUseCase = get(),
            getAccountPermissionsUseCase = get()
        )
    }
    factory { (proposer: String) ->
        ProposalsByProposerScreenModel(
            proposer = proposer,
            get()
        )
    }

    factory { (accountName: String) -> PermissionDetailScreenModel(accountName, get(), get()) }

    factory { (accountName: String, accountPermission: String) ->
        PermissionListScreenModel(
            get(),
            get(),
            accountName,
            accountPermission,
            get()
        )
    }
    factory { (account: String, currentPermission: String) ->
        CreatePermissionScreenModel(
            account,
            currentPermission,
            get(),
            get()
        )
    }
    factory { (account: String, currentPermission: String) ->
        LinkAuthScreenModel(
            account,
            currentPermission,
            get(),
            get()
        )
    }
    factory { (account: String, currentPermission: String) ->
        UnLinkAuthScreenModel(
            account,
            currentPermission,
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { UpdatePermissionScreenModel(get()) }
    factory { (account: String, permission: String) ->
        PermissionScreenModel(
            account,
            permission,
            get(),
            get()
        )
    }
    factoryOf(::ProposalScreenModal)
    factory { (proposer: String, proposalName: String, chainId: String?) ->
        MyProposalDetailScreenModel(
            proposerAccountName = proposer,
            proposalName = proposalName,
            chainId = chainId,
            getProposalDetailUseCase = get(),
            decoderProposalTransactionUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getRequestApprovalProposalDetailUseCase = get(),
            getAccountsUseCase = get(),
            getAccountPermissionsUseCase = get(),
            approveProposalUseCase = get(),
            cancelProposalUseCase = get(),
            executeProposalUseCase = get(),
            unApproveProposalUseCase = get(),
            getExecutableStatusProposalUseCase = get(),
            getAccountWeightUseCase = get()
        )
    }
    factory { ApprovalProposalDetailScreenModel() }
    factory { (proposer: String, proposalName: String) ->
        ExpiredProposalDetailScreenModel(
            proposer,
            proposalName,
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
    factory { (initialProposerName: String, initialProposerPermission: String) ->
        SelectAccountPermissionScreenModel(
            initialProposerName = initialProposerName,
            initialProposerPermissionName = initialProposerPermission,
            getAccountsUseCase = get(),
            getAccountPermissionsUseCase = get()
        )
    }
    factory { (accountName: String) ->
        GiftRamScreenModel(
            accountName = accountName,
            giftRamUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getAccountInfoUseCase = get(),
            validateAccountUseCase = get(),
            checkAccountNotExistsUseCase = get()
        )
    }
    factory { 
        ImportPrivateKeyScreenModel(
            getAccountsByAuthorizersUseCase = get(),
            importAccountUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getIsPinSetupUseCase = get(),
            updateAccountStatusUseCase = get(),
            buildEnvironmentProvider = get(),
            saveSelectedNetworkUseCase = get()
        )
    }
    factory { VaultaResourcesScreenModel() }
    single<PortfolioWalletProvider> { AntelopePortfolioWalletProvider(get()) }
}

val antelopeScreenModule = screenModule {
    register<SharedScreen.AntelopeImportAccountScreen> {
        Step1ImportAccountPrivateKeyScreen(it.privateKey)
    }
    register<SharedScreen.ImportPrivateKeyScreen> { _ ->
        ImportPrivateKeyScreen()
    }
    register<SharedScreen.PowerUpScreen> {
        PowerUpScreen(it.accountName, it.isCpu)
    }
    register<SharedScreen.NetAndCpuScreen> {
        NetAndCpuScreen(it.accountName, it.isCpu)
    }

    register<SharedScreen.BackupAntelopeAccountScreen> {
        BackupAntelopeAccountScreen(it.accountName, it.blockchainUid)
    }
    register<SharedScreen.StakeForResourceScreen> {
        StakeForResourceScreen(
            it.accountName,
            it.isStakeRex,
            it.isCpu
        )
    }
    register<SharedScreen.RentViaRexScreen> {
        RentViaRexScreen(
            it.accountName,
            it.isCpu
        )
    }

    register<SharedScreen.GuideBackupAntelopeAccountScreen> {
        GuideBackupAccountScreen(it.accountName)
    }

    register<SharedScreen.SelectPermissionToBackupScreen> {
        SelectPermissionToBackupScreen(it.accountName)
    }

    register<SharedScreen.BackupAntelopePrivateKeyScreen> {
        BackupAntelopePrivateKeyScreen(it.accountName, it.permissionName)
    }

    register<SharedScreen.ManageAntelopeAccountScreen> {
        ManageAntelopeAccountScreen()
    }

    register<SharedScreen.EsrScreen> {
        EsrScreen(it.esrUri)
    }

    register<SharedScreen.AntelopeMulitsigScreen> {
        AntelopeMultisigScreen()
    }

    register<SharedScreen.ImportAccountByKeyCertScreen> {
        ImportAccountByKeyCertScreen(it.keyCert)
    }

    register<SharedScreen.SelectAccountPermissionScreen> {
        SelectAccountPermissionScreen()
    }
    register<SharedScreen.MyProposalDetailScreen> {
        MyProposalDetailScreen(
            proposalName = it.proposalName,
            submitter = it.submitter,
            chainId = it.chainId
        )
    }
    register<SharedScreen.GiftRamScreen> {
        GiftRamScreen(it.accountName)
    }
}

inline operator fun <reified T> ParametersHolder.component6(): T = elementAt(5, T::class)