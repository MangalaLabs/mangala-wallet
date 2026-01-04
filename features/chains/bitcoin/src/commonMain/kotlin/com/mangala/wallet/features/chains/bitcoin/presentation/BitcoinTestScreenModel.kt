package com.mangala.wallet.features.chains.bitcoin.presentation

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.wallet.usecases.RestoreWalletUseCase
import com.mangala.wallet.features.chains.bitcoin.data.remote.electrum.ElectrumConnectionManager
import com.mangala.wallet.features.chains.bitcoin.domain.model.utxo.BitcoinUtxo
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.balance.GetBitcoinBalanceUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.transaction.SendBitcoinTransactionUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.utxo.GetBitcoinAddressUtxoUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.utils.getChainHash
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.launch
import fr.acinq.bitcoin.Bitcoin
import fr.acinq.bitcoin.ByteVector
import fr.acinq.bitcoin.ByteVector32
import fr.acinq.bitcoin.Crypto
import fr.acinq.bitcoin.Script
import fr.acinq.bitcoin.sat
import fr.acinq.lightning.blockchain.fee.FeeratePerKw

class BitcoinTestScreenModel(
    private val electrumConnectionManager: ElectrumConnectionManager,
    private val restoreWalletUseCase: RestoreWalletUseCase,
    private val getBitcoinBalanceUseCase: GetBitcoinBalanceUseCase,
    private val getBitcoinAddressUtxoUseCase: GetBitcoinAddressUtxoUseCase,
    private val sendBitcoinTransactionUseCase: SendBitcoinTransactionUseCase,
) : BaseScreenModel() {

    private lateinit var utxos: List<BitcoinUtxo>

    init {
        screenModelScope.launch {
            electrumConnectionManager.startConnectionLoop(BlockchainType.BitcoinTestnet4)

//            electrumBalanceManager.startMonitoring(
//                setOf(addressToElectrumScriptHash("tb1qupjzemazcxylfs54ypzvd989shpxy405x37qpw"))
//            )
//
//            electrumBalanceManager.walletBalance.collectLatest {
//                println("BitcoinTestScreenModel balance: $it")
//            }
        }
    }

    fun restoreTestWallet() {
        screenModelScope.launch {
            restoreWalletUseCase(
                "gossip fork trial dog typical expose loan hawk front purity buffalo beyond".split(
                    " "
                ),
                "Test",
                blockchainType = BlockchainType.BitcoinTestnet4
            )
        }
    }

    fun getUtxos() {
        screenModelScope.launch {
            val response = getBitcoinAddressUtxoUseCase(
                "tb1qupjzemazcxylfs54ypzvd989shpxy405x37qpw",
                BlockchainType.BitcoinTestnet4
            )

            println("BitcoinTestScreenModel utxos: $response")

            utxos = response
        }
    }

    fun sendTransaction() {
        screenModelScope.launch {
            val response = sendBitcoinTransactionUseCase(
                accountId = "0x045631067cee893f6e946eda5666c65017e819f3063dcae62afa82831330f8b3e3303f32d972b9c8696835d907c8cba3a9f181209ddb65d74d9937008596b925f5",
                recipientAddress = "tb1qr435v927uzf9xrkgfde60am0av7k9arlj9g4nj",
                amount = 1000.sat(),
                feeRate = FeeratePerKw.MinimumFeeratePerKw,
                utxos = utxos,
                changeAddress = "tb1qupjzemazcxylfs54ypzvd989shpxy405x37qpw",
                blockchainType = BlockchainType.BitcoinTestnet4,
            )
        }
    }

    private fun addressToElectrumScriptHash(address: String): String {
        // 1. Convert address to scriptPubKey (List<ScriptElt>)
        val scriptElements = Bitcoin.addressToPublicKeyScript(getChainHash(BlockchainType.BitcoinTestnet4), address).right.orEmpty()

        // 2. Serialize the script elements to binary
        val scriptBytes = Script.write(scriptElements)

        // 3. Create a ByteVector from the script bytes
        val scriptByteVector = ByteVector(scriptBytes)

        // 4. Calculate SHA256 hash of the script
        val scriptHash = Crypto.sha256(scriptByteVector)

        // 5. Reverse the bytes (Electrum expects reversed byte order)
        val reversedHash = ByteVector32(scriptHash.reversedArray())

        // 6. Convert to hex string
        return reversedHash.toHex()
    }
}