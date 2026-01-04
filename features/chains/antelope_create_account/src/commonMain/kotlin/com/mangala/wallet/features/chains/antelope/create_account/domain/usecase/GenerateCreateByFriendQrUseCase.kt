package com.mangala.wallet.features.chains.antelope.create_account.domain.usecase

import com.linh.antelope_qr.domain.model.CreateAccountForFriendRequest
import com.linh.antelope_qr.domain.usecase.EncodeRequestToQrCodeUseCase
import com.mangala.wallet.antelope_key_manager.domain.model.AccountKeyPairs
import com.mangala.wallet.antelope_key_manager.domain.usecase.GenerateAccountKeyPairsUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase

class GenerateCreateByFriendQrUseCase(
    private val generateAccountKeyPairsUseCase: GenerateAccountKeyPairsUseCase,
    private val encodeRequestToQrCodeUseCase: EncodeRequestToQrCodeUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) {

    suspend operator fun invoke(accountName: String, eosAccountKeyPairs: AccountKeyPairs?): String {
        val keyPair = eosAccountKeyPairs ?: generateAccountKeyPairsUseCase()

        val request = CreateAccountForFriendRequest(
            accountName = accountName,
            activePublicKey = keyPair.activeKeyPair.publicKey.toString(),
            ownerPublicKey = keyPair.ownerKeyPair.publicKey.toString(),
            blockchainUid = getSelectedNetworkUseCase().blockChainUid
        )
        val encodedRequest = encodeRequestToQrCodeUseCase(request)

        saveAccountUseCase(
            accountName = accountName,
            activePrivateKey = keyPair.activeKeyPair,
            ownerPrivateKey = keyPair.ownerKeyPair,
            isTemp = true,
            createAccountState = AntelopeAccount.CreateAccountState.FRIEND_CREATE_ACCOUNT_PENDING
        )

        println("Request create account $accountName for friend active ${keyPair.activeKeyPair.publicKey} owner ${keyPair.ownerKeyPair.publicKey}")

        return encodedRequest
    }
}