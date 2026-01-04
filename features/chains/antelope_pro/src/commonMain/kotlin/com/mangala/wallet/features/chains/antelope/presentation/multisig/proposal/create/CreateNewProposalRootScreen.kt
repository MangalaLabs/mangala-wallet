package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.ui.utils.screenmodel.BaseSharedScreenModelScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.parameter.parametersOf
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class CreateNewProposalRootScreen(
    private val proposalName: String? = null,
    private val accountName: String? = null
): BaseSharedScreenModelScreen<CreateNewProposalScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_MULTISIG_CREATE_NEW_PROPOSAL_ROOT
    override val screenClassName: String = CreateNewProposalRootScreen::class.simpleName.orEmpty()

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun createScreenModel(): CreateNewProposalScreenModel {
        val navigator = LocalNavigator.currentOrThrow

        return navigator.getNavigatorScreenModel<CreateNewProposalScreenModel>(parameters = {
            parametersOf(proposalName, accountName)
        })
    }

    override fun getScreen(): Screen {
        return CreateNewProposalScreen()
    }
}