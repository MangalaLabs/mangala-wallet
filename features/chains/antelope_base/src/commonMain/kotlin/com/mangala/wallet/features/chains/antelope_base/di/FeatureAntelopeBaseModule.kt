package com.mangala.wallet.features.chains.antelope_base.di

import com.mangala.wallet.core.ai.domain.AddressValidator
import com.mangala.wallet.features.chains.antelope_base.data.local.account.AccountLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.account.AccountLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.account.key.AccountKeyLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.account.key.AccountKeyLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.account.permission.AccountPermissionLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.account.permission.AccountPermissionLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.account.token.AntelopeAccountTokenBalanceLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.account.token.AntelopeAccountTokenBalanceLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.AntelopeActionsLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.AntelopeActionsLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.abis.AntelopeActionAbiLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.abis.AntelopeActionAbiLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.actiontrace.AntelopeActionTraceCacheMetadataLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.actiontrace.AntelopeActionTraceCacheMetadataLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.actiontrace.AntelopeActionTraceLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.actiontrace.AntelopeActionTraceLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.cache.AntelopeRemoteKeyLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.cache.AntelopeRemoteKeyLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.esr.anchorlink.AnchorLinkSessionLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.esr.anchorlink.AnchorLinkSessionLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.powerup.AntelopePowerUpLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.powerup.AntelopePowerUpLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.proposal.ProposalLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.proposal.ProposalLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.ram.AntelopeRamMarketLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.ram.AntelopeRamMarketLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexfund.AntelopeRexFundLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexfund.AntelopeRexFundLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexpool.AntelopeRexPoolLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexpool.AntelopeRexPoolLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexqueue.AntelopeRexQueueLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexqueue.AntelopeRexQueueLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount.CreateAccountApi
import com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount.CreateAccountRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.remote.esr.EsrApi
import com.mangala.wallet.features.chains.antelope_base.data.remote.esr.EsrRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.remote.esr.anchorlink.AnchorLinkRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.remote.esr.anchorlink.AnchorLinkWebSocket
import com.mangala.wallet.features.chains.antelope_base.data.repository.account.AccountRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.account.permission.AccountPermissionRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.account.token.AntelopeAccountTokenBalanceRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.actions.ActionsRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.actions.abis.AntelopeActionAbiRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.createaccount.CreateAccountRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.esr.EsrRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.esr.anchorlink.AnchorLinkRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.key.AntelopeKeyRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.notification.AntelopeNotificationRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.powerup.PowerUpRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.proposal.ProposalRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.ram.AntelopeRamMarketRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.rex.rexfund.AntelopeRexFundRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.rex.rexpool.AntelopeRexPoolRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.rex.rexqueue.AntelopeRexQueueRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountPermissionRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AntelopeKeyRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.actions.ActionsRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.actions.abis.ActionAbiRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.createaccount.CreateAccountRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.esr.EsrRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.esr.anchorlink.AnchorLinkRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.notification.AntelopeNotificationRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.powerup.PowerUpRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.proposal.ProposalRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.ram.AntelopeRamMarketRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.rex.AntelopeRexFundRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.rex.AntelopeRexPoolRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.rex.AntelopeRexQueueRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.token.AntelopeAccountTokenBalanceRepository
import com.mangala.wallet.domain.reset.usecases.ClearAntelopeImportedAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ClearAntelopeImportedAccountUseCaseImpl
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ClearAntelopeCacheDataUseCaseImpl
import com.mangala.wallet.domain.reset.usecases.ClearAntelopeCacheDataUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.CreateFreeJungleTestnetAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountBalanceRefresher
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CheckAccountAvailableToCreateUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CheckAccountNotExistsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CheckPublicKeyLinkedToAccountNameUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CountImportedAntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.DeleteAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GenerateRandomAccountNameUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountByNameUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountNameHashUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPrivateKeyUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountWithBalanceInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsByQueryUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAllAntelopeAccountsBalancesUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAntelopeAccountBalanceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ListAccountPublicKeysUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.RefreshAccountBalanceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountPermissionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.UpdateAccountStatusUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ValidateAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.createimport.GetAccountsByAuthorizersUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.keycert.CreateKeyCertUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.keycert.DecryptKeyCertUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.payment.GetAccountPaymentUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.payment.SaveAccountPaymentUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetActionAbiByContractAndActionNameUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetActionsAbiUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetActionsByContractUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetActionsPagingUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetActionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetFlattenedActionAbiArrayElementUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.esr.ResolveEsrUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.esr.anchorlink.AnchorLinkSessionManager
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.esr.anchorlink.DecodeAnchorLinkSealedMessageUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.esr.anchorlink.SaveAndConnectAnchorLinkSessionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.GetTableByScopeUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.GetTableRowsDataProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.GetTableRowsMultisigsProposalsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.ApproveProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.CancelProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.CreateProposeTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.DecoderProposalTransactionUseCaseV2
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.DeleteProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.ExecuteProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GenerateApproveProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GenerateCancelProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GenerateCreateProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GenerateExecuteProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GenerateUnApproveProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetAccountWeightUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetContractNamesUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetExecutableStatusProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetProposalDetailUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetProposalDraftUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetRequestApprovalProposalDetailUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.ListProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.SaveNewProposalDraftUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.UnApproveProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.UpdateProposalOnSubmitSuccessUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.notification.RegisterAntelopeNotificationUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.notification.UnregisterAntelopeNotificationUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.DeleteAccountPermissionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.GenerateKeyAndUpdateAccountPermissionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.LinkAuthUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.MultiSignAccountCheckingUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.UnLinkAuthUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.UpdateAccountPermissionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamPriceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rentViaRex.GenerateRentViaRexUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rentViaRex.GetRexRateUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rentViaRex.RentViaRexUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.GetSampleUsageUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.delegate.GetDelegateRateUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup.GeneratePowerUpSignRequestUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup.GetPowerUpRateUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup.GetTableRowsPowerUpUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup.PowerUpUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex.CalculateRexPriceInEosUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex.DelegateAndUnDelegateBandwidthUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex.GenerateDelegateBandwidthUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex.GenerateUnDelegateBandwidthUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex.GetRexBalanceInNativeCoinUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex.GetRexFundInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex.GetRexPoolInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex.GetRexQueueInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.send.AntelopeSendCryptoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.send.GenerateSendAssetSignRequestUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.token.GetAntelopeAccountCryptoBalanceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.token.GetAntelopeAccountTokenBalanceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.ResourceProviderRequestTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndComputeTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushResourceProvidedTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.giftram.GenerateGiftRamSignRequestUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.giftram.GiftRamUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.utils.AntelopeAccountValidator
import com.mangala.wallet.remote.di.provideKtorfit
import com.mangala.wallet.remote.di.provideSocketHttpClient
import com.mangala.wallet.utils.di.IGNORE_UNKNOWN_KEY_JSON
import com.mangala.wallet.utils.di.LOCAL_CACHE_JSON
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val featureAntelopeBaseCommonModule = module {
    // Domain
    factoryOf(::CreateFreeJungleTestnetAccountUseCase)
    factoryOf(::ValidateAccountUseCase)
    factoryOf(::GenerateRandomAccountNameUseCase)
    factoryOf(::CheckPublicKeyLinkedToAccountNameUseCase)
    factoryOf(::SaveAccountUseCase)
    factoryOf(::SignTransactionUseCase)
    factoryOf(::GetAccountsUseCase)
    factoryOf(::DeleteAccountUseCase)
    factoryOf(::GetAccountByNameUseCase)
    factoryOf(::GetAccountPermissionsUseCase)
    factoryOf(::CalculateRexPriceInEosUseCase)
    factoryOf(::GetRexBalanceInNativeCoinUseCase)
    factoryOf(::GetRexQueueInfoUseCase)
    factoryOf(::GetRexFundInfoUseCase)
    factoryOf(::GetRexPoolInfoUseCase)
    factoryOf(::GetAccountWithBalanceInfoUseCase)
    factoryOf(::GetAllAntelopeAccountsBalancesUseCase)
    factoryOf(::GetAntelopeAccountBalanceUseCase)
    factoryOf(::GetRamPriceUseCase)
    factoryOf(::RentViaRexUseCase)
    factoryOf(::GenerateRentViaRexUseCase)
    factoryOf(::GetAntelopeAccountTokenBalanceUseCase)
    factoryOf(::GetAntelopeAccountCryptoBalanceUseCase)
    factoryOf(::GetActionsUseCase)
    factoryOf(::GetActionsPagingUseCase)
    factoryOf(::CheckAccountNotExistsUseCase)
    factoryOf(::GetAccountsByQueryUseCase)
    factoryOf(::CheckAccountAvailableToCreateUseCase)
    factoryOf(::GetAccountPrivateKeyUseCase)
    factoryOf(::CreateProposeTransactionUseCase)
    factoryOf(::ApproveProposalUseCase)
    factoryOf(::UnApproveProposalUseCase)
    factoryOf(::ExecuteProposalUseCase)
    factoryOf(::CancelProposalUseCase)
    factoryOf(::GenerateCreateProposalUseCase)
    factoryOf(::GenerateApproveProposalUseCase)
    factoryOf(::GenerateExecuteProposalUseCase)
    factoryOf(::GenerateUnApproveProposalUseCase)
    factoryOf(::GenerateCancelProposalUseCase)
    factoryOf(::ApproveProposalUseCase)
    factoryOf(::CancelProposalUseCase)
    factoryOf(::ExecuteProposalUseCase)
    factoryOf(::UnApproveProposalUseCase)
    factoryOf(::GetContractNamesUseCase)
    factoryOf(::GetExecutableStatusProposalUseCase)
    factoryOf(::GetAccountWeightUseCase)
    factoryOf(::GetAccountInfoUseCase)
    factoryOf(::GetActionsAbiUseCase)
    factoryOf(::GetActionsByContractUseCase)
    factoryOf(::GetActionAbiByContractAndActionNameUseCase)
    factoryOf(::GetActionAbi)
    factoryOf(::GetFlattenedActionAbiArrayElementUseCase)
    factoryOf(::DecoderProposalTransactionUseCaseV2)
    factoryOf(::GetTableRowsDataProposalUseCase)
    factoryOf(::GetProposalDetailUseCase)
    factoryOf(::GetTableByScopeUseCase)
    factoryOf(::GetTableRowsMultisigsProposalsUseCase)
    factoryOf(::GetRequestApprovalProposalDetailUseCase)
    factoryOf(::ListAccountPublicKeysUseCase)
    factoryOf(::GetTableRowsPowerUpUseCase)
    factoryOf(::PowerUpUseCase)
    factoryOf(::GeneratePowerUpSignRequestUseCase)
    factoryOf(::GenerateDelegateBandwidthUseCase)
    factoryOf(::GenerateUnDelegateBandwidthUseCase)
    factoryOf(::GetSampleUsageUseCase)
    factoryOf(::GetPowerUpRateUseCase)
    factoryOf(::GetDelegateRateUseCase)
    factoryOf(::SignAndPushTransactionUseCase)
    factoryOf(::SignAndComputeTransactionUseCase)
    factoryOf(::ResourceProviderRequestTransactionUseCase)
    factoryOf(::SignAndPushResourceProvidedTransactionUseCase)
    factoryOf(::UpdateAccountStatusUseCase)
    factoryOf(::GetAccountsByAuthorizersUseCase)
    factoryOf(::AntelopeSendCryptoUseCase)
    factoryOf(::GenerateSendAssetSignRequestUseCase)
    factoryOf(::DelegateAndUnDelegateBandwidthUseCase)
    factoryOf(::ResolveEsrUseCase)
    factoryOf(::GetAccountPaymentUseCase)
    factoryOf(::SaveAccountPaymentUseCase)
    factoryOf(::RefreshAccountBalanceUseCase)
    factoryOf(::SaveAndConnectAnchorLinkSessionUseCase)
    factoryOf(::DecodeAnchorLinkSealedMessageUseCase)
    factoryOf(::GetRexRateUseCase)
    factoryOf(::CreateKeyCertUseCase)
    factoryOf(::DecryptKeyCertUseCase)
    factoryOf(::GenerateKeyAndUpdateAccountPermissionUseCase)
    factoryOf(::SaveAccountPermissionUseCase)
    factoryOf(::GetAccountNameHashUseCase)

    factoryOf(::UpdateAccountPermissionUseCase)
    factoryOf(::DeleteAccountPermissionUseCase)
    factoryOf(::LinkAuthUseCase)
    factoryOf(::UnLinkAuthUseCase)
    factoryOf(::MultiSignAccountCheckingUseCase)
    factoryOf(::RegisterAntelopeNotificationUseCase)
    factoryOf(::UnregisterAntelopeNotificationUseCase)
    factoryOf(::ListProposalUseCase)
    factoryOf(::SaveNewProposalDraftUseCase)
    factoryOf(::GetProposalDraftUseCase)
    factoryOf(::DeleteProposalUseCase)
    factoryOf(::UpdateProposalOnSubmitSuccessUseCase)
    factoryOf(::GenerateGiftRamSignRequestUseCase)
    factoryOf(::GiftRamUseCase)
    factoryOf(::ClearAntelopeImportedAccountUseCaseImpl) bind ClearAntelopeImportedAccountUseCase::class
    factoryOf(::ClearAntelopeCacheDataUseCaseImpl) bind ClearAntelopeCacheDataUseCase::class
    factoryOf(::CountImportedAntelopeAccount)

    single { AccountBalanceRefresher(get()) }
    single { AnchorLinkSessionManager(get(), get(), get()) }

    factory<AccountRepository> { AccountRepositoryImpl(get(), get()) }
    factory<AccountPermissionRepository> { AccountPermissionRepositoryImpl(get(), get()) }
    factory<AntelopeKeyRepository> { AntelopeKeyRepositoryImpl(get()) }
    factory<AntelopeRexQueueRepository> { AntelopeRexQueueRepositoryImpl(get(), get(), get()) }
    factory<AntelopeRexFundRepository> { AntelopeRexFundRepositoryImpl(get(), get()) }
    factory<AntelopeRexPoolRepository> { AntelopeRexPoolRepositoryImpl(get(), get()) }
    factory<AntelopeRamMarketRepository> { AntelopeRamMarketRepositoryImpl(get(), get()) }
    factory<AntelopeAccountTokenBalanceRepository> {
        AntelopeAccountTokenBalanceRepositoryImpl(
            get(), get()
        )
    }
    factory<ActionAbiRepository> { AntelopeActionAbiRepositoryImpl(get(), get()) }
    factory<PowerUpRepository> { PowerUpRepositoryImpl(get(), get()) }
    factory<CreateAccountRepository> { CreateAccountRepositoryImpl(get()) }
    factory<EsrRepository> { EsrRepositoryImpl(get()) }
    factory<AnchorLinkRepository> { AnchorLinkRepositoryImpl(get(), get()) }
    single<ActionsRepository> { ActionsRepositoryImpl(get(), get(), get(), get(), get(), get(), get(), get(), get(named(IGNORE_UNKNOWN_KEY_JSON))) }
    factory<AntelopeNotificationRepository> { AntelopeNotificationRepositoryImpl(get()) }
    single<ProposalRepository> { ProposalRepositoryImpl(get(named(LOCAL_CACHE_JSON)), get(), get(), get()) }

    // Data
    single<AccountLocalDataSource> { AccountLocalDataSourceImpl(get()) }
    single<AccountPermissionLocalDataSource> { AccountPermissionLocalDataSourceImpl(get()) }
    single<AccountKeyLocalDataSource> { AccountKeyLocalDataSourceImpl(get()) }
    single<AntelopePowerUpLocalDataSource> { AntelopePowerUpLocalDataSourceImpl(get()) }
    single<AntelopeRexQueueLocalDataSource> { AntelopeRexQueueLocalDataSourceImpl(get()) }
    single<AntelopeRexFundLocalDataSource> { AntelopeRexFundLocalDataSourceImpl(get()) }
    single<AntelopeRexPoolLocalDataSource> { AntelopeRexPoolLocalDataSourceImpl(get()) }
    single<AntelopeRamMarketLocalDataSource> { AntelopeRamMarketLocalDataSourceImpl(get()) }
    single<AnchorLinkSessionLocalDataSource> { AnchorLinkSessionLocalDataSourceImpl(get()) }
    single<AntelopeAccountTokenBalanceLocalDataSource> {
        AntelopeAccountTokenBalanceLocalDataSourceImpl(
            get()
        )
    }
    single<AntelopeActionsLocalDataSource> { AntelopeActionsLocalDataSourceImpl(get()) }
    single<AntelopeActionTraceCacheMetadataLocalDataSource> { AntelopeActionTraceCacheMetadataLocalDataSourceImpl(get()) }
    single<AntelopeActionTraceLocalDataSource> { AntelopeActionTraceLocalDataSourceImpl(get()) }

    single<ProposalLocalDataSource> { ProposalLocalDataSourceImpl(get()) }
    single<AntelopeRemoteKeyLocalDataSource> {
        AntelopeRemoteKeyLocalDataSourceImpl(get())
    }

    single<AntelopeActionAbiLocalDataSource> { AntelopeActionAbiLocalDataSourceImpl(get()) }
    single<EsrApi> {
        provideKtorfit(
            baseUrl = "https://myapp.com/",
            enableNetworkLogs = true,
            username = "",
            password = "",
            forceJsonBody = false,
            httpClientEngine = get()
        ).create()
    }
    factory { EsrRemoteDataSource(get()) }
    single<CreateAccountApi> {
        provideKtorfit(
            baseUrl = "https://asia-southeast1-mangala-wallet-cb7c1.cloudfunctions.net/eos-account-testnet/",
            enableNetworkLogs = true,
            username = "",
            password = "",
            forceJsonBody = false,
            httpClientEngine = get(),
        ).create()
    }
    factory<AnchorLinkRemoteDataSource> { AnchorLinkRemoteDataSource(get()) }
    single {
        AnchorLinkWebSocket(
            provideSocketHttpClient(
                httpClientEngine = get(),
            )
        )
    }
    factory { CreateAccountRemoteDataSource(get()) }
    single<AddressValidator> { AntelopeAccountValidator(get(), get()) }


    // for eos account
//    single<EosAccountApi> {
//        provideKtorfit(
//            EOS_ACCOUNT_BASE_URL,
//            true,
//            "",
//            "",
//            get()
//        ).create()
//    }
}
