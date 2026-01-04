package com.mangala.wallet.features.contacts.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.contacts.data.local.ContactLocalDataSource
import com.mangala.wallet.features.contacts.data.local.ContactLocalDataSourceImpl
import com.mangala.wallet.features.contacts.domain.repository.ContactRepository
import com.mangala.wallet.features.contacts.data.repository.ContactRepositoryImpl
import com.mangala.wallet.features.contacts.domain.usecases.CreateContactUseCase
import com.mangala.wallet.features.contacts.domain.usecases.CreateContactsUseCase
import com.mangala.wallet.features.contacts.domain.usecases.DeleteContactUseCase
import com.mangala.wallet.features.contacts.domain.usecases.GetAllContactUseCase
import com.mangala.wallet.features.contacts.domain.usecases.GetContactByIdUseCase
import com.mangala.wallet.features.contacts.domain.usecases.GetContactsByBlockchainUidUseCase
import com.mangala.wallet.features.contacts.domain.usecases.UpdateContactUseCase
import com.mangala.wallet.features.contacts.presentation.ContactsScreen
import com.mangala.wallet.features.contacts.presentation.ContactsScreenModel
import com.mangala.wallet.features.contacts.presentation.addcontact.AddContactScreen
import com.mangala.wallet.features.contacts.presentation.addcontact.AddContactScreenModel
import com.mangala.wallet.features.contacts.presentation.contactdetail.ContactDetailScreen
import com.mangala.wallet.features.contacts.presentation.contactdetail.ContactDetailScreenModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val settingsContactModule = module {
    single<ContactLocalDataSource> { ContactLocalDataSourceImpl(get()) }

    factory { CreateContactUseCase(get()) }
    factory { CreateContactsUseCase(get()) }
    factory { GetAllContactUseCase(get()) }
    factory { GetContactByIdUseCase(get()) }
    factory { GetContactsByBlockchainUidUseCase(get()) }
    factory { UpdateContactUseCase(get()) }
    factory { DeleteContactUseCase(get()) }
    factory { (contactId: Long) -> ContactDetailScreenModel(contactId, get()) }

    single<ContactRepository> { ContactRepositoryImpl(get()) }

    factory { (blockchainUid: String?) ->
        ContactsScreenModel(
            blockchainUid = blockchainUid,
            getAllContactsUseCase = get(),
            deleteContactUseCase = get()
        )
    }

    factory { (contactName: String, address: String, blockchainUid: String) ->
        AddContactScreenModel(
            contactName = contactName,
            address = address,
            blockchainUid = blockchainUid,
            antelopeValidateAccountUseCase = get(),
            createContactUseCase = get(),
            updateContactUseCase = get(),
            checkAccountNotExistsUseCase = get(),
            buildEnvironmentProvider = get()
        )
    }
}

val settingsContactScreenModule = screenModule {
    register<SharedScreen.ContactDetailScreen> {
        ContactDetailScreen(
            contactId = it.contactId,)
    }
    register<SharedScreen.AddContactScreen> {
        AddContactScreen(
            id = it.id,
            name = it.name,
            address = it.address,
            blockchainUid = it.blockchainUid,
            isEdit = it.isEdit,
        )
    }
    register<SharedScreen.ContactsScreen> {
        ContactsScreen(
            blockchainUid =  it.blockchainUid,
            onSelectContact = it.onSelectContact,
        )
    }
}