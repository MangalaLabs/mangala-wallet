package com.mangala.wallet.features.addressbook.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.core.ai.domain.model.action.ActionHandler
import com.mangala.wallet.core.ai.domain.model.action.QuickActionProvider
import com.mangala.wallet.core.ai.domain.model.dialog.DialogProvider
import com.mangala.wallet.core.ai.domain.model.factory.MessageFactory
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionPlugin
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandlerPlugin
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRendererPlugin
import com.mangala.wallet.core.ai.domain.model.navigation.NavigationHandler
import com.mangala.wallet.core.ai.domain.model.renderer.MessageRenderer
import com.mangala.wallet.features.addressbook.data.local.blockchain.BlockchainLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.blockchain.BlockchainLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.local.contact.ContactLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.contact.ContactLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.local.contact.DeleteSocialProfilesByContactIdUseCase
import com.mangala.wallet.features.addressbook.data.local.contact.WalletAddressLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.contact.WalletAddressLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.local.group.GroupLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.group.GroupLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.local.group.GroupWalletLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.group.GroupWalletLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.local.note.AddressNoteHistoryLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.note.AddressNoteHistoryLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.local.note.AddressNoteLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.note.AddressNoteLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.local.note.NoteTemplateLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.note.NoteTemplateLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.local.setting.SettingsLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.setting.SettingsLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.local.setting.SubscriptionLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.setting.SubscriptionLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.local.tag.TagLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.tag.TagLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.local.transaction.AddressBookRecentTxRemoteKeyLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.transaction.AddressBookRecentTxRemoteKeyLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.local.transaction.TransactionLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.transaction.TransactionLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.repository.avatar.AvatarHistoryRepositoryImpl
import com.mangala.wallet.features.addressbook.data.repository.avatar.AvatarRepositoryImpl
import com.mangala.wallet.features.addressbook.data.repository.blockchain.BlockchainTypeRepositoryImpl
import com.mangala.wallet.features.addressbook.data.repository.contact.ContactRepositoryImpl
import com.mangala.wallet.features.addressbook.data.repository.contact.WalletAddressRepositoryImpl
import com.mangala.wallet.features.addressbook.data.repository.group.GroupRepositoryImpl
import com.mangala.wallet.features.addressbook.data.repository.group.GroupWalletRepositoryImpl
import com.mangala.wallet.features.addressbook.data.repository.note.AddressNoteRepositoryImpl
import com.mangala.wallet.features.addressbook.data.repository.note.NoteTemplateRepositoryImpl
import com.mangala.wallet.features.addressbook.data.repository.setting.SettingsRepositoryImpl
import com.mangala.wallet.features.addressbook.data.repository.setting.SubscriptionRepositoryImpl
import com.mangala.wallet.features.addressbook.data.repository.tag.TagRepositoryImpl
import com.mangala.wallet.features.addressbook.data.repository.transaction.TransactionRepositoryImpl
import com.mangala.wallet.features.addressbook.domain.functioncalling.AddressBookConfirmationRendererPlugin
import com.mangala.wallet.features.addressbook.domain.functioncalling.AddressBookFunctions
import com.mangala.wallet.features.addressbook.domain.functioncalling.AddressBookFunctionsHandler
import com.mangala.wallet.features.addressbook.domain.functioncalling.AddressBookMessageFactoryImpl
import com.mangala.wallet.features.addressbook.domain.functioncalling.add.AddContactHandler
import com.mangala.wallet.features.addressbook.domain.functioncalling.delete.DeleteContactHandler
import com.mangala.wallet.features.addressbook.domain.functioncalling.edit.EditContactHandler
import com.mangala.wallet.features.addressbook.domain.functioncalling.edit.EditContactNameHandler
import com.mangala.wallet.features.addressbook.domain.functioncalling.find.FindContactHandler
import com.mangala.wallet.features.addressbook.domain.functioncalling.list.ListContactsHandler
import com.mangala.wallet.features.addressbook.domain.model.AvatarPickerContract
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarHistoryRepository
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarRepository
import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupWalletRepository
import com.mangala.wallet.features.addressbook.domain.repository.note.AddressNoteRepository
import com.mangala.wallet.features.addressbook.domain.repository.note.NoteTemplateRepository
import com.mangala.wallet.features.addressbook.domain.repository.setting.SettingsRepository
import com.mangala.wallet.features.addressbook.domain.repository.setting.SubscriptionRepository
import com.mangala.wallet.features.addressbook.domain.repository.tag.ObservableTagRepository
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository
import com.mangala.wallet.features.addressbook.domain.repository.transaction.TransactionRepository
import com.mangala.wallet.features.addressbook.domain.usecase.avatar.AvatarUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.AddBlockchainTypeUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.AddTokenInformationUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.DeleteBlockchainTypeUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.GetAllBlockchainTypesUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.GetAvailableContactsForBlockchainUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.GetBlockchainTypeByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.GetTokensForBlockchainUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.UpdateBlockchainTypeUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.clipboard.CopyToClipboardUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.AddImportantDateUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.CountAllContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.CreateContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.CreateContactWithAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.DeleteContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.DeleteImportantDateUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.FilterContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.FindContactByEmailUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.FindContactByPhoneNumberUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetAllContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactDetailByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactsWithMultipleBlockchainsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactsWithWalletAddressPaginatedUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetImportantDatesByContactIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetRecentContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.IsContactFavoriteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.NotifyContactsChangedUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.RemoveFavoriteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.SearchContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdateContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdateImportantDateUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdateLastViewedAtUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdatePhoneNumberUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdatePhysicalAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdateRelatedNameUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.AddEmailAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.DeleteEmailAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.GetEmailAddressesByContactIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.InsertEmailAddressesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.InsertImportantDatesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.InsertSocialProfilesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.UpdateEmailAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.favorite.AddFavoriteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.favorite.GetFavoriteContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.phone.AddPhoneNumberUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.phone.DeletePhoneNumberUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.phone.GetPhoneNumbersByContactIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.phone.InsertPhoneNumbersBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.physical_address.AddPhysicalAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.physical_address.DeletePhysicalAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.physical_address.GetPhysicalAddressesByContactIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.physical_address.InsertPhysicalAddressesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.related.AddRelatedNameUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.related.DeleteRelatedNameUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.related.GetRelatedNamesByContactIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.related.InsertRelatedNamesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.wallet_address.InsertWalletAddressesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.AddContactToGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.BatchAssignGroupsToContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.ContactInGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.CountContactsByGroupIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.CreateGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.DeleteGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetAllGroupsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetContactAddressByGroupIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetContactsByGroupIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetContactsWithAddressesByGroupIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetGroupByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetGroupDetailByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetGroupModelByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetGroupsByContactIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.InsertGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.RemoveContactFromGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.UpdateGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.AddAddressNoteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.CreateNoteTemplateUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.DeleteAddressNoteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.DeleteNoteTemplateUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.GetAddressNoteByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.GetAddressNoteHistoryUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.GetAddressNotesUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.GetAllNoteTemplatesUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.GetNoteTemplateByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.SearchAddressNotesUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.UpdateAddressNoteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.UpdateNoteTemplateUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.setting.CancelSubscriptionUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.setting.CheckPremiumAccessUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.setting.CreateSubscriptionUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.setting.GetCurrentUserSettingUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.setting.GetUserSubscriptionUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.setting.RenewSubscriptionUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.setting.SaveUserSettingUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.setting.UpdateCurrentSettingUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.setting.UpgradeSubscriptionUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.AssignTagToContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.BatchAssignTagsToContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.CreateTagUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.DeleteTagUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.GetActiveTagsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.GetMostUsedTagsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.GetTagsForContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.RemoveTagFromContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.RestoreTagUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.SearchTagsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.UpdateTagUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.AddWalletAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.CountWalletAddressesForContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.DeleteAllWalletAddressesForContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.DeleteWalletAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetContactWalletByWalletIdsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetDefaultWalletAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesByNetworkUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesForContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesWithBlockchainByContactIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.MarkWalletAddressAsPrimaryUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.SetWalletAddressAsDefaultUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.UpdateWalletAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.ValidateAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.VerifyWalletAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.ClearAddressBookDataUseCaseImpl
import com.mangala.wallet.domain.reset.usecases.ClearAddressBookDataUseCase
import com.mangala.wallet.features.addressbook.presentation.StartScreenModel
import com.mangala.wallet.features.addressbook.presentation.blockchain.BlockchainScreenModel
import com.mangala.wallet.features.addressbook.presentation.contact.BatchTagManagementScreenModel
import com.mangala.wallet.features.addressbook.presentation.contact.ContactTagSelectionScreenModel
import com.mangala.wallet.features.addressbook.presentation.contact.create.CreateTagBottomSheet
import com.mangala.wallet.features.addressbook.presentation.contact.ContactScreenModel
import com.mangala.wallet.features.addressbook.presentation.contact.detail.ContactDetailScreenModel
import com.mangala.wallet.features.addressbook.presentation.contact.list.ContactListScreen
import com.mangala.wallet.features.addressbook.presentation.contact.list.ContactListScreenModel
import com.mangala.wallet.features.addressbook.presentation.contact.list.FilterTestScreenModel
import com.mangala.wallet.features.addressbook.presentation.contact.recent.TransactionDetailScreenModel
import com.mangala.wallet.features.addressbook.presentation.group.create.CreateGroupScreenModel
import com.mangala.wallet.features.addressbook.presentation.group.create.WalletAddressBottomSheetViewModel
import com.mangala.wallet.features.addressbook.presentation.group.detail.GroupDetailScreenModelNew
import com.mangala.wallet.features.addressbook.presentation.note.AddressNoteDetailScreenModel
import com.mangala.wallet.features.addressbook.presentation.note.AddressNoteEditScreenModel
import com.mangala.wallet.features.addressbook.presentation.note.AddressNoteListScreenModel
import com.mangala.wallet.features.addressbook.presentation.note.NoteTemplateScreenModel
import com.mangala.wallet.features.addressbook.presentation.plugins.AddressBookActionHandler
import com.mangala.wallet.features.addressbook.presentation.plugins.AddressBookDialogProvider
import com.mangala.wallet.features.addressbook.presentation.plugins.AddressBookMessageRenderer
import com.mangala.wallet.features.addressbook.presentation.plugins.AddressBookNavigationHandler
import com.mangala.wallet.features.addressbook.presentation.plugins.AddressBookQuickActionProvider
import com.mangala.wallet.features.addressbook.presentation.privacy.PrivacyModeViewModel
import com.mangala.wallet.features.addressbook.presentation.security.AnalyticsTracker
import com.mangala.wallet.features.addressbook.presentation.security.ContextAwareSecureAuthPolicyProvider
import com.mangala.wallet.features.addressbook.presentation.security.DefaultAnalyticsTracker
import com.mangala.wallet.features.addressbook.presentation.security.DefaultSecureAuthAnalytics
import com.mangala.wallet.features.addressbook.presentation.security.DefaultSecureAuthPolicyProvider
import com.mangala.wallet.features.addressbook.presentation.security.DefaultSecureAuthSessionManager
import com.mangala.wallet.features.addressbook.presentation.security.SecureAuthAnalytics
import com.mangala.wallet.features.addressbook.presentation.security.SecureAuthFlowCoordinator
import com.mangala.wallet.features.addressbook.presentation.security.SecureAuthPolicyProvider
import com.mangala.wallet.features.addressbook.presentation.security.SecureAuthSessionManager
import com.mangala.wallet.features.addressbook.presentation.security.SystemTimeProvider
import com.mangala.wallet.features.addressbook.presentation.security.TimeProvider
import com.mangala.wallet.features.addressbook.presentation.tag.AddressSelectionScreen
import com.mangala.wallet.features.addressbook.presentation.tag.AddressSelectionViewModel
import com.mangala.wallet.features.addressbook.presentation.tag.QuickTagCreationScreenModel
import com.mangala.wallet.features.addressbook.presentation.tag.add.AddTagScreenModel
import com.mangala.wallet.features.addressbook.presentation.tag.detail.DetailTagScreen
import com.mangala.wallet.features.addressbook.presentation.tag.detail.DetailTagScreenModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Duration.Companion.minutes

// Removed QR module dependency - using existing ShowContactQrScreen

val addressBookModule = module {
    includes(
        addressBookPlatformSpecificModule(),
        contactQrModule,
        qrModule,
        contactEditRefactoredModule,
    )

    // Validation
    single {
        com.mangala.wallet.features.addressbook.domain.validation.WalletAddressValidator(
            bitcoinValidator = getOrNull(),
            antelopeValidator = getOrNull()
        )
    }
    single { com.mangala.wallet.features.addressbook.domain.validation.DomainNameResolver() }
    single { 
        com.mangala.wallet.features.addressbook.domain.validation.ComprehensiveAddressValidator(
            walletValidator = get(),
            domainResolver = get(),
            getAccountsByQueryUseCase = getOrNull() // Inject for internal error handling
        ) 
    }
    
    // New validation system components
    single { com.mangala.wallet.features.addressbook.domain.validation.ValidationConfig() }
    factory { (scope: kotlinx.coroutines.CoroutineScope) ->
        com.mangala.wallet.features.addressbook.domain.validation.UnifiedValidationOrchestrator(
            comprehensiveValidator = get(),
            scope = scope,
            config = get()
        )
    }

    // Clipboard functionality
    factory { CopyToClipboardUseCase(get()) }

    // Data Sources
    single<BlockchainLocalDataSource> { BlockchainLocalDataSourceImpl(get()) }
    single<WalletAddressLocalDataSource> { WalletAddressLocalDataSourceImpl(get()) }
    single<ContactLocalDataSource> { ContactLocalDataSourceImpl(get()) }
    single<GroupLocalDataSource> { GroupLocalDataSourceImpl(get()) }
    single<TransactionLocalDataSource> { TransactionLocalDataSourceImpl(get()) }
    single<AddressBookRecentTxRemoteKeyLocalDataSource>{
        AddressBookRecentTxRemoteKeyLocalDataSourceImpl(get())
    }

    // Repositories
    single<BlockchainRepository> { BlockchainTypeRepositoryImpl(get()) }
    single<WalletAddressRepository> { WalletAddressRepositoryImpl(get()) }
    single<GroupRepository> {
        GroupRepositoryImpl(
            localDataSource = get(),
            databaseWrapper = get()
        )
    }
    singleOf(::ContactRepositoryImpl) bind ContactRepository::class
    singleOf(::TransactionRepositoryImpl) bind TransactionRepository::class
    single<AvatarHistoryRepository> { AvatarHistoryRepositoryImpl(get()) }

    // Use Cases - WalletAddress
    factory { GetWalletAddressesForContactUseCase(repository = get()) }
    factory { SetWalletAddressAsDefaultUseCase(get()) }
    factory { CountWalletAddressesForContactUseCase(get()) }
    factory { GetDefaultWalletAddressUseCase(get()) }
    factory { GetWalletAddressesByNetworkUseCase(get()) }
    factory { AddWalletAddressUseCase(get()) }
    factory { UpdateWalletAddressUseCase(get()) }
    factory { DeleteWalletAddressUseCase(get()) }
    factory { DeleteAllWalletAddressesForContactUseCase(get()) }
    
    factory { com.mangala.wallet.features.addressbook.domain.usecase.pin.CheckPinExistsUseCase(get()) }

    // Use Cases - Blockchain
    factory { GetBlockchainTypeByIdUseCase(get()) }
    factory { AddBlockchainTypeUseCase(get()) }
    factory { UpdateBlockchainTypeUseCase(get()) }
    factory { DeleteBlockchainTypeUseCase(get()) }
    factory { GetWalletAddressesWithBlockchainByContactIdUseCase(get()) }
    factory { MarkWalletAddressAsPrimaryUseCase(get()) }
    factory { VerifyWalletAddressUseCase(get()) }
    factory { ValidateAddressUseCase(get()) }
    factory { GetAllBlockchainTypesUseCase(get()) }
    
    // Blockchain network mapping
    factory { com.mangala.wallet.features.addressbook.domain.mapper.BlockchainNetworkMapper() }
    factory { com.mangala.wallet.features.addressbook.domain.usecase.blockchain.GetHardcodedBlockchainTypesUseCase(get(), get()) }
    factory { GetTokensForBlockchainUseCase(get()) }
    factory { AddTokenInformationUseCase(get()) }

    // Screen Models
    factory { BlockchainScreenModel(get(), get(), get(), get(), get()) }

    factory { StartScreenModel() }

    // Data sources
    single<TagLocalDataSource> { TagLocalDataSourceImpl(get()) }

    // Repositories
    single<ObservableTagRepository> { TagRepositoryImpl(get(), get(), get()) }
    single<TagRepository> { get<ObservableTagRepository>() }

    // Use cases
    factory { GetActiveTagsUseCase(get()) }
    factory { CreateTagUseCase(get()) }
    factory { UpdateTagUseCase(get()) }
    factory { DeleteTagUseCase(get()) }
    factory { RestoreTagUseCase(get()) }
    factory { AssignTagToContactUseCase(get()) }
    factory { RemoveTagFromContactUseCase(get()) }
    factory { GetTagsForContactUseCase(get()) }
    factory { SearchTagsUseCase(get()) }
    factory { BatchAssignTagsToContactUseCase(get()) }
    factory { GetMostUsedTagsUseCase(get()) }
    
    factoryOf(::ClearAddressBookDataUseCaseImpl) bind ClearAddressBookDataUseCase::class

    // Data sources
    single<SubscriptionLocalDataSource> {
        SubscriptionLocalDataSourceImpl(get())
    }

    // Repositories
    single<SubscriptionRepository> {
        SubscriptionRepositoryImpl(
            localDataSource = get()
        )
    }


    // Use cases
    factory { GetUserSubscriptionUseCase(repository = get()) }
    factory { CreateSubscriptionUseCase(repository = get()) }
    factory { UpgradeSubscriptionUseCase(repository = get()) }
    factory { RenewSubscriptionUseCase(repository = get()) }
    factory { CancelSubscriptionUseCase(repository = get()) }
    factory { CheckPremiumAccessUseCase(repository = get()) }
    factory { (selectedContactId: String) ->
        BatchTagManagementScreenModel(
            selectedContactId,
            get(),
            get()
        )
    }
    factory { (contactId: String) ->
        ContactTagSelectionScreenModel(
            contactId,
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { QuickTagCreationScreenModel(get(), get(), get()) }

    factory { AddContactToGroupUseCase(get()) }
    factory { ContactInGroupUseCase(get()) }
    factory { DeleteGroupUseCase(get()) }
    factory { GetAllGroupsUseCase(get()) }
    factory { GetContactsByGroupIdUseCase(get()) }
    factory { GetGroupByIdUseCase(get()) }
    factory { GetGroupDetailByIdUseCase(get()) }
    factory { GetGroupsByContactIdUseCase(get()) }
    factory { CountContactsByGroupIdUseCase(get()) }
    factory { InsertGroupUseCase(get()) }
    factory { RemoveContactFromGroupUseCase(get()) }
    factory { UpdateGroupUseCase(get(), get()) }
    factory { GetContactAddressByGroupIdUseCase(get()) }
    factory { GetContactsWithAddressesByGroupIdUseCase(get()) }


    // Contact Basic Operations
    factory { GetContactByIdUseCase(get()) }
    factory { GetContactDetailByIdUseCase(get()) }
    factoryOf(::GetAllContactsUseCase)
    factory { SearchContactsUseCase(get()) }
    factory { CreateContactUseCase(get()) }
    factory { CreateContactWithAddressUseCase(get(), get()) }
    factory { UpdateContactUseCase(get()) }
    factory { DeleteContactUseCase(get()) }
    factory { UpdateLastViewedAtUseCase(get()) }
    factory { CountAllContactsUseCase(get()) }
    factory { GetContactsWithMultipleBlockchainsUseCase(get(), get()) }

// Phone Number Operations
    factory { GetPhoneNumbersByContactIdUseCase(get()) }
    factory { AddPhoneNumberUseCase(get()) }
    factory { UpdatePhoneNumberUseCase(get()) }
    factory { DeletePhoneNumberUseCase(get()) }
    factory { FindContactByPhoneNumberUseCase(get()) }

// Email Address Operations
    factory { GetEmailAddressesByContactIdUseCase(get()) }
    factory { AddEmailAddressUseCase(get()) }
    factory { UpdateEmailAddressUseCase(get()) }
    factory { DeleteEmailAddressUseCase(get()) }
    factory { FindContactByEmailUseCase(get()) }

// Physical Address Operations
    factory { GetPhysicalAddressesByContactIdUseCase(get()) }
    factory { AddPhysicalAddressUseCase(get()) }
    factory { UpdatePhysicalAddressUseCase(get()) }
    factory { DeletePhysicalAddressUseCase(get()) }

// Related Name Operations
    factory { GetRelatedNamesByContactIdUseCase(get()) }
    factory { AddRelatedNameUseCase(get()) }
    factory { UpdateRelatedNameUseCase(get()) }
    factory { DeleteRelatedNameUseCase(get()) }

// Important Date Operations
    factory { GetImportantDatesByContactIdUseCase(get()) }
    factory { AddImportantDateUseCase(get()) }
    factory { UpdateImportantDateUseCase(get()) }
    factory { DeleteImportantDateUseCase(get()) }

// Favorite Operations
    factory { IsContactFavoriteUseCase(get()) }
    factory { AddFavoriteUseCase(get()) }
    factory { RemoveFavoriteUseCase(get()) }
    factoryOf(::GetFavoriteContactsUseCase)

// Recent Contacts
    factoryOf(::GetRecentContactsUseCase)


    // Data Sources
    single<AddressNoteLocalDataSource> {
        AddressNoteLocalDataSourceImpl(get())
    }

    single<AddressNoteHistoryLocalDataSource> {
        AddressNoteHistoryLocalDataSourceImpl(get())
    }

    single<NoteTemplateLocalDataSource> {
        NoteTemplateLocalDataSourceImpl(get())
    }

    // Repositories
    single<AddressNoteRepository> {
        AddressNoteRepositoryImpl(get(), get())
    }

    single<NoteTemplateRepository> {
        NoteTemplateRepositoryImpl(get())
    }

    // Use Cases - Address Notes
    factory { GetAddressNotesUseCase(get()) }
    factory { GetAddressNoteByIdUseCase(get()) }
    factory { AddAddressNoteUseCase(get()) }
    factory { UpdateAddressNoteUseCase(get()) }
    factory { DeleteAddressNoteUseCase(get()) }
    factory { SearchAddressNotesUseCase(get()) }
    factory { GetAddressNoteHistoryUseCase(get()) }

    // Use Cases - Note Templates
    factory { GetAllNoteTemplatesUseCase(get()) }
    factory { GetNoteTemplateByIdUseCase(get()) }
    factory { CreateNoteTemplateUseCase(get()) }
    factory { UpdateNoteTemplateUseCase(get()) }
    factory { DeleteNoteTemplateUseCase(get()) }
    factory { DeleteSocialProfilesByContactIdUseCase(get()) }

    // Screen Models
    factory {
        AddressNoteListScreenModel(
            getAddressNotesUseCase = get(),
            searchAddressNotesUseCase = get(),
            deleteAddressNoteUseCase = get()
        )
    }

    factory {
        AddressNoteDetailScreenModel(
            getAddressNoteByIdUseCase = get(),
            getAddressNoteHistoryUseCase = get(),
            addAddressNoteUseCase = get(),
            updateAddressNoteUseCase = get()
        )
    }

    factory {
        AddressNoteEditScreenModel(
            getAddressNoteByIdUseCase = get(),
            addAddressNoteUseCase = get(),
            updateAddressNoteUseCase = get()
        )
    }

    factory {
        NoteTemplateScreenModel(
            getAllNoteTemplatesUseCase = get(),
            getNoteTemplateByIdUseCase = get(),
            createNoteTemplateUseCase = get(),
            updateNoteTemplateUseCase = get(),
            deleteNoteTemplateUseCase = get()
        )
    }

    factory { InsertPhysicalAddressesBatchUseCase(get()) }
    factory { InsertImportantDatesBatchUseCase(get()) }
    factory { InsertSocialProfilesBatchUseCase(get()) }
    factory { InsertPhoneNumbersBatchUseCase(get()) }
    factory { InsertEmailAddressesBatchUseCase(get()) }
    factory { InsertRelatedNamesBatchUseCase(get()) }
    factory { InsertWalletAddressesBatchUseCase(get()) }
    factory { BatchAssignGroupsToContactUseCase(get()) }
    factory { FilterContactsUseCase(get(), get(), get()) }
    factory { NotifyContactsChangedUseCase(get()) }
    factory { (contactId: String) ->
        ContactDetailScreenModel(
            contactId = contactId,
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }

    // Unified ContactScreenModel - handles both create and edit modes
    factory<ContactScreenModel> { params ->
        val contactId = params.getOrNull<String>()
        ContactScreenModel(
            contactId = contactId,
            getAllBlockchainsUseCase = get(),
            getActiveTagsUseCase = get(),
            createTagUseCase = get(),
            getAllGroupsUseCase = get(),
            createContactUseCase = get(),
            updateContactUseCase = get(),
            getContactByIdUseCase = get(),
            getContactDetailByIdUseCase = get(),
            batchAssignTagsToContactUseCase = get(),
            batchAssignGroupsToContactUseCase = get(),
            addFavoriteUseCase = get(),
            removeFavoriteUseCase = get(),
            insertWalletAddressesBatchUseCase = get(),
            insertEmailAddressesBatchUseCase = get(),
            insertPhoneNumbersBatchUseCase = get(),
            insertPhysicalAddressesBatchUseCase = get(),
            insertSocialProfilesBatchUseCase = get(),
            insertRelatedNamesBatchUseCase = get(),
            insertImportantDatesBatchUseCase = get(),
            validateAccountUseCase = get(),
            isValidBitcoinAddressUseCase = getOrNull() ?: Unit,
            evmAddressValidator = getOrNull() ?: Unit,
            exchangeAddressDetector = get(),
            copyToClipboardUseCase = get(),
            comprehensiveValidator = get(), // Added required ComprehensiveAddressValidator
            // ADD FACADE USE CASES FOR FULL CRUD
            communicationUseCase = get(),
            walletUseCase = get()
        )
    }

    // Privacy Mode
    single {
        PrivacyModeViewModel(
            getCurrentUserSettingUseCase = get(),
            updateCurrentSettingUseCase = get(),
            settingsRepository = get()
        )
    }

    factoryOf(::ContactListScreenModel)

    factory { GetGroupModelByIdUseCase(get()) }

    factory { (groupId: String) ->
        GroupDetailScreenModelNew(
            getGroupUseCase = get(),
            getGroupModelByIdUseCase = get(),
            deleteGroupUseCase = get(),
            copyToClipboardUseCase = get(),
            privacyModeViewModel = get()
        )
    }


    factory { (transactionId: String) ->
        TransactionDetailScreenModel(
            transactionId = transactionId,
            repository = get(),
            getBlockchainExplorerLinkUseCase = get(),
        )
    }

    // No need to explicitly register the new screen in DI since it's instantiated directly
    // where needed with the required parameters

    factory { (tagId: String) ->
        DetailTagScreenModel(
            tagId = tagId,
            tagRepository = get(),
            getContactUseCase = get(),
            deleteTagUseCase = get(),
            removeTagFromContactUseCase = get(),
            privacyModeViewModel = get()
        )
    }

    factory {
        FilterTestScreenModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }


    factory { (tagId: String?) ->
        AddTagScreenModel(
            tagId,
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
    factory {
        DetailTagScreen(
            get()
        )
    }

    factory { GetCurrentUserSettingUseCase(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<SettingsLocalDataSource> { SettingsLocalDataSourceImpl(get()) }

//    single<TwoFactorAuthService> { DefaultTwoFactorAuthService(get()) }
//    single<SecureAuthResultHandler> { DefaultSecureAuthResultHandler() }
    single<TimeProvider> { SystemTimeProvider() }
    single<AnalyticsTracker> { DefaultAnalyticsTracker() }

    // Policy providers
    single<SecureAuthPolicyProvider> {
        // Use ContextAwareSecureAuthPolicyProvider to check contact security level
        ContextAwareSecureAuthPolicyProvider(
            defaultProvider = DefaultSecureAuthPolicyProvider(),
            getContactByIdUseCase = get()
            // coroutineScope has default value in constructor, no need to provide
        )
    }

    // Main coordinator
    single { SecureAuthFlowCoordinator(get(), get(), get(), get(), get(), get()) }

    single<SecureAuthSessionManager> {
        DefaultSecureAuthSessionManager(5.minutes, get(), get())
    }

    single<SecureAuthAnalytics> { DefaultSecureAuthAnalytics(get(), get()) }

    factory { SaveUserSettingUseCase(get()) }

    factory { UpdateCurrentSettingUseCase(get()) }

    // Privacy Mode Authentication
    factory {
        com.mangala.wallet.features.addressbook.presentation.privacy.AddressRevealAuthenticator(
            get(), get()
        )
    }

    factory {
        com.mangala.wallet.features.addressbook.presentation.privacy.AddressRevealUseCase(
            get()
        )
    }

    single<GroupWalletLocalDataSource> {
        GroupWalletLocalDataSourceImpl(get())
    }
    single<GroupWalletRepository> {
        GroupWalletRepositoryImpl(get())
    }
    factory { GetAvailableContactsForBlockchainUseCase(get()) }
    factory { CreateGroupUseCase(get(), get()) }
    factory { GetGroupUseCase(get(), get()) }
    factory { GetContactsWithWalletAddressPaginatedUseCase(get()) }

    // ViewModel cho AddContactBottomSheet
    factory { (groupId: String?) ->
        CreateGroupScreenModel(
            getBlockchainTypesUseCase = get(),
            createGroupUseCase = get(),
            getAvailableContactsForBlockchainUseCase = get(),
            updateGroupUseCase = get(),
            getGroupUseCase = get(),
            groupId = groupId,
            avatarUseCase = get(),
            getContactWalletByWalletIdsUseCase = get(),
            groupWalletRepository = get(),
            getContactsWithWalletAddressPaginatedUseCase = get()
        )
    }


    // Repository
    single<AvatarRepository> { AvatarRepositoryImpl(get()) }

    // UseCase
    factory { AvatarUseCase(get(), get()) }

    single<FunctionPlugin>(named("AddressBookFunctions")) { AddressBookFunctions() }
    single<FunctionHandlerPlugin>(named("AddressBookFunctionHandlers")) {
        AddressBookFunctionsHandler(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    single<ConfirmationRendererPlugin>(named("AddressBookConfirmationRenderer")) { AddressBookConfirmationRendererPlugin() }

    single { AddContactHandler(get()) }
    single { EditContactHandler(get(), get()) }
    single { EditContactNameHandler(get(), get()) }
    single { FindContactHandler(get()) }
    single { ListContactsHandler(get(), get()) }
    single { DeleteContactHandler(get(), get()) }

    single<MessageFactory> { AddressBookMessageFactoryImpl() }

    single<MessageRenderer> { AddressBookMessageRenderer() }
    single<ActionHandler> { AddressBookActionHandler() }
    single<DialogProvider> { AddressBookDialogProvider() }
    single<NavigationHandler> { AddressBookNavigationHandler() }
    single<QuickActionProvider> { AddressBookQuickActionProvider() }
    
    // Validators
    single { com.mangala.wallet.features.addressbook.domain.validation.ExchangeAddressDetector() }

    factory { (avatarPickerContract: AvatarPickerContract) ->
        com.mangala.wallet.features.addressbook.presentation.avatar.AvatarPickerViewModel(
            avatarPickerContract = avatarPickerContract,
            avatarHistoryRepository = get(),
            avatarRepository = get()
        )
    }

    factory { (tagId: String?, initialSelectedContactIds: List<String>) ->
        AddressSelectionViewModel(
            tagId = tagId,
            initialSelectedContactIds = initialSelectedContactIds,
            getAllContactsUseCase = get(),
            addContactToTagUseCase = get(),
            removeContactFromTagUseCase = get(),
            tagRepository = get(),
        )
    }

    factory { GetContactWalletByWalletIdsUseCase(get()) }

    // ViewModel for WalletAddressBottomSheet with pagination support
    factory { (blockchainId: String, getAvailableContactsForBlockchainUseCase: GetAvailableContactsForBlockchainUseCase, onWalletsSelected: (List<String>) -> Unit, initialSelectedWalletIds: List<String>) ->
        WalletAddressBottomSheetViewModel(
            blockchainId = blockchainId,
            getAvailableContactsForBlockchainUseCase = getAvailableContactsForBlockchainUseCase,
            onWalletsSelected = onWalletsSelected,
            initialSelectedWalletIds = initialSelectedWalletIds
        )
    }
}

val addressBookScreenModule = screenModule {
    register<SharedScreen.ContactListScreen> {
        ContactListScreen()
    }
    // Register ContactsScreen to support send flow
    register<SharedScreen.ContactsScreen> {
        ContactListScreen(
            chainId = it.blockchainUid
        )
    }
    register<SharedScreen.AddressSelectionScreen> {
        AddressSelectionScreen(
            tagId = it.tagId,
            initialSelectedContactIds = it.initialSelectedContactIds,
            onApplySelections = it.onApplySelections
        )
    }
    register<SharedScreen.CreateTagBottomSheet> {
        CreateTagBottomSheet(
            onTagCreated = it.onTagCreated
        )
    }
    
    // Register unified ContactScreen
    register<SharedScreen.ContactScreen> {
        com.mangala.wallet.features.addressbook.presentation.contact.ContactScreen(
            contactId = it.contactId,
            prefilledName = it.prefilledName,
            prefilledAddress = it.prefilledAddress,
            prefilledBlockchain = it.prefilledBlockchain,
            onBackClick = it.onBackClick,
            onSaveSuccess = it.onSaveSuccess
        )
    }
}