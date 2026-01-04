package com.mangala.wallet.features.nft_base.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.chains.BlockSyncerPlugin
import com.mangala.wallet.features.nft_base.data.repository.NftMetadataRepositoryImpl
import com.mangala.wallet.features.nft_base.data.repository.NftRepositoryImpl
import com.mangala.wallet.features.nft_base.domain.plugins.NftBlockSyncerPlugin
import com.mangala.wallet.features.nft_base.domain.repository.NftMetadataRepository
import com.mangala.wallet.features.nft_base.domain.repository.NftRepository
import com.mangala.wallet.features.nft_base.domain.usecases.DeleteNftByIdUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.GetNftBalanceUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.GetNftByTokenIdUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.GetNftFavoriteStatusUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.ImportNftUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.SendNftUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.UpdateNftFavoriteStatusUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.ClearNFTDataUseCaseImpl
import com.mangala.wallet.domain.reset.usecases.ClearNFTDataUseCase
import com.mangala.wallet.features.nft_base.presentation.NftMainScreen
import com.mangala.wallet.features.nft_base.presentation.NftScreenModel
import com.mangala.wallet.features.nft_base.presentation.details.NftDetailsScreen
import com.mangala.wallet.features.nft_base.presentation.details.NftDetailsScreenModel
import com.mangala.wallet.features.nft_base.presentation.import.ImportNftScreen
import com.mangala.wallet.features.nft_base.presentation.import.ImportNftScreenModel
import com.mangala.wallet.features.nft_base.presentation.send.SendNftScreenModel
import com.mangala.wallet.features.nft_base.presentation.send.confirmation.BaseSendNftConfirmationScreenModel
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.utils.di.IGNORE_UNKNOWN_KEY_JSON
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val nftBaseModule = module {

    single<com.mangala.wallet.features.nft_base.data.local.NftCollectionLocalDataSource> {
        com.mangala.wallet.features.nft_base.data.local.NftCollectionLocalDataSourceImpl(
            get()
        )
    }

    single<NftRepository> { NftRepositoryImpl(get(), get(), get(named(IGNORE_UNKNOWN_KEY_JSON))) }
    single<NftMetadataRepository> { NftMetadataRepositoryImpl(get()) }

    factory { GetNftBalanceUseCase(get(), get()) }
    factory { DeleteNftByIdUseCase(get()) }
    factory { ImportNftUseCase(get(), get(), get(), get()) }
    factory { GetNftByTokenIdUseCase(get(), get()) }
    factory { SendNftUseCase(
        estimateGasUseCase = get(),
        getSelectedWalletUseCase = get(),
        getAccountByIdUseCase = get(),
        generateHDKeyUseCase = get(),
        getNonceUseCase = get(),
        sendRawTransactionUseCase = get(),
        saveTransactionHistoryUseCase = get(),
        blockSyncer = get(),
        parsingJson = get(named(IGNORE_UNKNOWN_KEY_JSON))
    ) }
    factory { GetNftFavoriteStatusUseCase(get()) }
    factory { UpdateNftFavoriteStatusUseCase(get()) }
    factoryOf(::NftBlockSyncerPlugin) bind BlockSyncerPlugin::class
    
    // Reset use cases
    factoryOf(::ClearNFTDataUseCaseImpl) bind ClearNFTDataUseCase::class

    factory { (accountId: String, collectionContractAddress: String, tokenId: String) ->
        NftDetailsScreenModel(
            accountId = accountId,
            collectionContractAddress = collectionContractAddress,
            tokenId = tokenId,
            getNftByTokenIdUseCase = get(),
            getNftFavoriteStatusUseCase = get(),
            updateNftFavoriteStatusUseCase = get(),
            getSelectedNetworkUseCase = get()
        )
    }

    factory { NftScreenModel(get(), get(), get()) }

    factory { (accountId: String, collectionContractAddress: String, tokenId: String) ->
        SendNftScreenModel(
            accountId = accountId,
            collectionContractAddress = collectionContractAddress,
            tokenId = tokenId,
            getNftByTokenIdUseCase = get(),
            getContactsUseCase = get(),
            createContactUseCase = get(),
            getSelectedNetworkUseCase = get()
        )
    }
    factoryOf(::ImportNftScreenModel)
}

val nftBaseScreenModule = screenModule {
    register<SharedScreen.NftScreen> {
        NftMainScreen()
    }

    register<SharedScreen.ImportNftScreen> {
        ImportNftScreen()
    }

    register<SharedScreen.NftDetailsScreen> {
        NftDetailsScreen(
            accountId = it.accountId,
            collectionContractAddress = it.collectionContractAddress,
            tokenId = it.tokenId
        )
    }
}

inline operator fun <reified T> ParametersHolder.component6(): T = elementAt(5, T::class)
