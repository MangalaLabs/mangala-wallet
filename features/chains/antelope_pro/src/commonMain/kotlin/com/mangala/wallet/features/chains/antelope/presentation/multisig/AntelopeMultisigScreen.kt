package com.mangala.wallet.features.chains.antelope.presentation.multisig

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.features.chains.antelope.presentation.backupaccount.keycert.BackupWithKeyCertScreen
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.list.ProposalTableScreen
import com.mangala.wallet.features.chains.antelope.presentation.permission.PermissionScreen
import com.mangala.wallet.ui.component.MaxSizeColumn

class AntelopeMultisigScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val proposalTableScreen = ProposalTableScreen()
        val permissionScreen = PermissionScreen("partneracc11", "active")
        val backupWithKeyCertScreen = BackupWithKeyCertScreen()

        MaxSizeColumn(Modifier.background(Colors.cloudGray)
            .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Button(onClick = {
                navigator.push(permissionScreen)
            }) {
                Text("Account permission")
            }

            Button(onClick = {
                navigator.push(proposalTableScreen)
            }) {
                Text("Proposal table")
            }

            Button(onClick = {
                navigator.push(backupWithKeyCertScreen)
            }) {
                Text("Backup with key cert")
            }
        }
    }
}