package com.mangala.eticket.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.eticket.ETicketSharedScreen
import com.mangala.eticket.data.local.ETicketDatabaseWrapper
import com.mangala.eticket.data.local.cache.RemotePagingKeyLocalDataSource
import com.mangala.eticket.data.local.cache.RemotePagingKeyLocalDataSourceImpl
import com.mangala.eticket.data.local.event.EventListLocalDataSource
import com.mangala.eticket.data.local.event.EventListLocalDataSourceImpl
import com.mangala.eticket.data.local.user.login.AuthenticationLocalDataSource
import com.mangala.eticket.data.local.user.login.AuthenticationLocalDataSourceImpl
import com.mangala.eticket.data.remote.AuthDataSource
import com.mangala.eticket.data.remote.CategoriesDataSource
import com.mangala.eticket.data.remote.EventsDataSource
import com.mangala.eticket.data.remote.UserDataSource
import com.mangala.eticket.data.remote.favourite.UserEventFavouriteDataSource
import com.mangala.eticket.domain.repository.CategoriesRepository
import com.mangala.eticket.domain.repository.CategoriesRepositoryImpl
import com.mangala.eticket.domain.repository.event.EventsRepository
import com.mangala.eticket.domain.repository.event.EventsRepositoryImpl
import com.mangala.eticket.domain.repository.UsersRepository
import com.mangala.eticket.domain.repository.UsersRepositoryImpl
import com.mangala.eticket.domain.usecases.event.GetEventUseCase
import com.mangala.eticket.domain.repository.auth.AuthRepository
import com.mangala.eticket.domain.repository.auth.AuthRepositoryImpl
import com.mangala.eticket.domain.repository.favourite.UserEventFavouriteRepository
import com.mangala.eticket.domain.repository.favourite.UserEventFavouriteRepositoryImpl
import com.mangala.eticket.domain.usecases.auth.GetAuthLoginDataUseCase
import com.mangala.eticket.domain.repository.ticket.TicketPurchaseRepository
import com.mangala.eticket.domain.repository.ticket.TicketPurchaseRepositoryImpl
import com.mangala.eticket.domain.usecases.auth.LoginUseCase
import com.mangala.eticket.domain.usecases.auth.SaveAuthEntityUseCase
import com.mangala.eticket.domain.usecases.category.GetCategoriesUseCase
import com.mangala.eticket.domain.usecases.event.GetEventsUseCase
import com.mangala.eticket.domain.usecases.favourite.ListUserEventFavouriteUseCase
import com.mangala.eticket.domain.usecases.ticket.TicketPreparePurchaseUseCase
import com.mangala.eticket.domain.usecases.ticket.TicketPurchaseUseCase
import com.mangala.eticket.domain.usecases.user.CheckUserHasAccountUseCase
import com.mangala.eticket.domain.usecases.user.RegisterUserUseCase
import com.mangala.eticket.domain.usecases.wallet.GetCurrentAddressUseCase
import com.mangala.eticket.domain.usecases.reset.ClearETicketDataUseCaseImpl
import com.mangala.wallet.domain.reset.usecases.ClearETicketDataUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import com.mangala.eticket.presentation.category.CategoryScreen
import com.mangala.eticket.presentation.category.CategoryScreenModel
import com.mangala.eticket.presentation.booking.BookingScreenModel
import com.mangala.eticket.presentation.booking.ConfirmationScreenModel
import com.mangala.eticket.presentation.booking.TicketSelection
import com.mangala.eticket.presentation.event.EventDetailScreenModel
import com.mangala.eticket.presentation.event.EventScreenUiModel
import com.mangala.eticket.presentation.event.list.EventListScreenModel
import com.mangala.eticket.presentation.favourite.UserEventFavouriteScreenModel
import com.mangala.eticket.presentation.home.ETicketHomeScreen
import com.mangala.eticket.presentation.home.ETicketHomeScreenModel
import com.mangala.eticket.presentation.onboard.ETicketOnboardScreenModel
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import org.koin.dsl.module

fun eTicketModule() = module {
    single { CategoriesDataSource(get()) }
    single { EventsDataSource(get()) }
    single { AuthDataSource(get()) }
    single { UserDataSource(get()) }
    single { UserEventFavouriteDataSource(get()) }

    factory<UsersRepository> { UsersRepositoryImpl(get()) }
    factory { CheckUserHasAccountUseCase(get()) }
    factory { RegisterUserUseCase(get()) }
    factory<AuthenticationLocalDataSource> { AuthenticationLocalDataSourceImpl(get()) }
    factory<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    factory { GetCurrentCurrencyCodeUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { SaveAuthEntityUseCase(get()) }
    factory { ETicketDatabaseWrapper(get()) }

    factory {
        ETicketOnboardScreenModel(
            getSelectedWalletUseCase = get(),
            generateHDKeyUseCase = get(),
            deriveAddressUseCase = get(),
            checkUserHasAccountUseCase = get(),
            getSelectedNetworkUseCase = get(),
            registerUserUseCase = get(),
            signPersonalMessageUseCase = get(),
            loginUseCase = get(),
            secureStorage = get()
        )
    }

    factory<EventsRepository> { EventsRepositoryImpl(get(), get(), get()) }
    factory { GetEventUseCase(get()) }
    factory { (id: String) ->
        EventDetailScreenModel(
            getEventDetailUseCase = get(),
            getCurrentCurrencyCodeUseCase = get(),
            eventId = id
        )
    }
    factory { TicketPreparePurchaseUseCase(get()) }
    factory { TicketPurchaseUseCase(get()) }
    factory<TicketPurchaseRepository> { TicketPurchaseRepositoryImpl(get()) }

    factory {
            (
                selectedTickets: List<TicketSelection>,
                totalAmount: Double,
                isSendAnotherWallet: Boolean,
                walletAnotherAddress: String,
            ),
        ->
        ConfirmationScreenModel(
            selectedTickets = selectedTickets,
            totalAmount = totalAmount,
            isSendAnotherWallet = isSendAnotherWallet,
            walletAnotherAddress = walletAnotherAddress,
            ticketPurchaseUseCase = get(),
            ticketPreparePurchaseUseCase = get()
        )
    }

    factory { (ticketTypes: List<EventScreenUiModel.TicketTypeUiModel>) ->
        BookingScreenModel(ticketTypes)
    }

    factory { GetAuthLoginDataUseCase(get()) }
    factory { GetCurrentAddressUseCase(
        getSelectedNetworkUseCase = get(),
        getSelectedWalletUseCase = get(),
        deriveAddressUseCase = get(),
        generateHDKeyUseCase = get()
    ) }

    factory { ETicketHomeScreenModel() }
    factory { GetCategoriesUseCase(get()) }
    factory<CategoriesRepository> { CategoriesRepositoryImpl(get()) }
    factory {CategoryScreenModel(get())}

    factory { GetEventsUseCase(get()) }
    factory { (categoryId: String?) -> EventListScreenModel(get(), categoryId) }

    factory<RemotePagingKeyLocalDataSource> { RemotePagingKeyLocalDataSourceImpl(get()) }
    factory<EventListLocalDataSource> { EventListLocalDataSourceImpl(get()) }

    factory<UserEventFavouriteRepository> { UserEventFavouriteRepositoryImpl(get()) }
    factory{ ListUserEventFavouriteUseCase(get()) }
    factory { UserEventFavouriteScreenModel(get()) }
    
    // Reset use cases
    factoryOf(::ClearETicketDataUseCaseImpl) bind ClearETicketDataUseCase::class
}

val eTicketScreenModule = screenModule {
    register<ETicketSharedScreen.ETicketHomeScreen> {
        ETicketHomeScreen()
    }
    register<ETicketSharedScreen.CategoryScreen> {
        CategoryScreen()
    }
}