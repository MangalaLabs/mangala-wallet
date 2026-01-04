package com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory

import com.mangala.antelope.base.api.model.ActionTrace
import com.mangala.antelope.base.domain.model.Transaction
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.common.test.utils.SharedFileReader
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActionDataSummaryHeaderUiModelTest {

    @Test
    fun `Given a list of ActionTrace for a buy RAM only transaction, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "yfxzodmw.sus"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_buy_ram.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val ramBuy = result[0] as ActionDataSummaryHeaderUiModel.RamBuy
        assertEquals(5143, ramBuy.ramBytesBought)
        assertEquals(Balance(0.005, "EOS"), ramBuy.ramFee)
        assertEquals(Balance(1.0, "EOS"), ramBuy.totalCost)
        assertEquals("0.199 EOS/ KB", ramBuy.pricePerKbFormatted)
        assertEquals(6916, ramBuy.newRamBalance)
        assertEquals(
            ActionDataSummaryHeaderUiModel.RamBuy.BuyRamType.BUY_FOR_SELF,
            ramBuy.buyRamType
        )
        assertEquals(currentAccountName, ramBuy.recipientAccount)
        assertEquals(currentAccountName, ramBuy.payerAccount)
        assertEquals(currentAccountName, ramBuy.currentAccountName)
    }

    @Test
    fun `Given a list of ActionTrace for a buy RAM for other, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "yfxzodmw.sus"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_buy_ram_for_others.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(2, result.size)
        val ramBuy = result[0] as ActionDataSummaryHeaderUiModel.RamBuy
        assertEquals(5152, ramBuy.ramBytesBought)
        assertEquals(Balance(0.005, "EOS"), ramBuy.ramFee)
        assertEquals(Balance(1.0, "EOS"), ramBuy.totalCost)
        assertEquals("0.199 EOS/ KB", ramBuy.pricePerKbFormatted)
        assertEquals(54385, ramBuy.newRamBalance)
        assertEquals(
            ActionDataSummaryHeaderUiModel.RamBuy.BuyRamType.BUY_FOR_OTHERS,
            ramBuy.buyRamType
        )
        assertEquals("harkonnenmgl", ramBuy.recipientAccount)
        assertEquals(currentAccountName, ramBuy.payerAccount)
        assertEquals(currentAccountName, ramBuy.currentAccountName)
    }

    @Test
    fun `Given a list of ActionTrace for a buyrambytes received from others, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "yfxzodmw.sus"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_buyrambytes_by_other.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val ramBuy = result[0] as ActionDataSummaryHeaderUiModel.RamBuy
        assertEquals(5174, ramBuy.ramBytesBought)
        assertEquals(Balance(0.005, "EOS"), ramBuy.ramFee)
        assertEquals(Balance(0.9981, "EOS"), ramBuy.totalCost)
        assertEquals("0.198 EOS/ KB", ramBuy.pricePerKbFormatted)
        assertEquals(19177, ramBuy.newRamBalance)
        assertEquals(
            ActionDataSummaryHeaderUiModel.RamBuy.BuyRamType.BOUGHT_BY_OTHERS,
            ramBuy.buyRamType
        )
        assertEquals(currentAccountName, ramBuy.recipientAccount)
        assertEquals("harkonnenmgl", ramBuy.payerAccount)
        assertEquals(currentAccountName, ramBuy.currentAccountName)
    }

    @Test
    fun `Given a list of ActionTrace for a buyRamBytes that does not have logbuyram transaction, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "acc.gm"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_buy_ram_bytes_legacy.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val ramBuy = result[0] as ActionDataSummaryHeaderUiModel.RamBuy
        assertEquals(1000000, ramBuy.ramBytesBought)
        assertEquals(Balance(0.3457, "EOS"), ramBuy.ramFee)
        assertEquals(Balance(69.1286, "EOS"), ramBuy.totalCost)
        assertEquals("0.071 EOS/ KB", ramBuy.pricePerKbFormatted)
        assertNull(ramBuy.newRamBalance)
        assertEquals(
            ActionDataSummaryHeaderUiModel.RamBuy.BuyRamType.BUY_FOR_SELF,
            ramBuy.buyRamType
        )
        assertEquals(currentAccountName, ramBuy.recipientAccount)
        assertEquals(currentAccountName, ramBuy.payerAccount)
        assertEquals(currentAccountName, ramBuy.currentAccountName)
    }

    @Test
    fun `Given a list of ActionTrace for a buyRam that does not have logbuyram transaction, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "acc.gm"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_buy_ram_legacy.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val ramBuy = result[0] as ActionDataSummaryHeaderUiModel.RamBuy
        assertNull(ramBuy.ramBytesBought)
        assertEquals(Balance(4.185, "EOS"), ramBuy.ramFee)
        assertEquals(Balance(837.0, "EOS"), ramBuy.totalCost)
        assertNull(ramBuy.pricePerKbFormatted)
        assertNull(ramBuy.newRamBalance)
        assertEquals(
            ActionDataSummaryHeaderUiModel.RamBuy.BuyRamType.BUY_FOR_SELF,
            ramBuy.buyRamType
        )
        assertEquals(currentAccountName, ramBuy.recipientAccount)
        assertEquals(currentAccountName, ramBuy.payerAccount)
        assertEquals(currentAccountName, ramBuy.currentAccountName)
    }

    @Test
    fun `Given a list of ActionTrace for a buyRam that does not have fee in logbuyram transaction, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "acc.gm"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_buyram_with_no_ramfee_in_logramchange.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val ramBuy = result[0] as ActionDataSummaryHeaderUiModel.RamBuy
        assertEquals(3999999, ramBuy.ramBytesBought)
        assertEquals(Balance(4.6007, "EOS"), ramBuy.ramFee)
        assertEquals(Balance(920.1370, "EOS"), ramBuy.totalCost)
        assertEquals("0.236 EOS/ KB", ramBuy.pricePerKbFormatted)
        assertEquals(30187743, ramBuy.newRamBalance)
        assertEquals(
            ActionDataSummaryHeaderUiModel.RamBuy.BuyRamType.BUY_FOR_SELF,
            ramBuy.buyRamType
        )
        assertEquals(currentAccountName, ramBuy.recipientAccount)
        assertEquals(currentAccountName, ramBuy.payerAccount)
        assertEquals(currentAccountName, ramBuy.currentAccountName)
    }

    @Test
    fun `Given a list of ActionTrace for a sellRam that does not have logsellram transaction, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "gm3tanzzgyge"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_sell_ram_legacy.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val ramSell = result[0] as ActionDataSummaryHeaderUiModel.RamSell
        assertEquals(484000, ramSell.ramBytesSold)
        assertNull(ramSell.newRamBalance)
        assertEquals(Balance(83.986, "EOS"), ramSell.totalReceived)
        assertEquals(Balance(0.42, "EOS"), ramSell.ramFee)
        assertEquals("0.178 EOS/ KB", ramSell.pricePerKbFormatted)
    }

    @Test
    fun `Given a list of ActionTrace for a buyRamBytes transaction, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "yfxzodmw.sus"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_buy_ram_bytes.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(2, result.size)
        val ramBuy = result[0] as ActionDataSummaryHeaderUiModel.RamBuy
        assertEquals(1023, ramBuy.ramBytesBought)
        assertEquals(Balance(0.001, "EOS"), ramBuy.ramFee)
        assertEquals(Balance(0.1986, "EOS"), ramBuy.totalCost)
        assertEquals("0.199 EOS/ KB", ramBuy.pricePerKbFormatted)
        assertEquals(14003, ramBuy.newRamBalance)
        assertEquals(
            ActionDataSummaryHeaderUiModel.RamBuy.BuyRamType.BUY_FOR_SELF,
            ramBuy.buyRamType
        )
        assertEquals(currentAccountName, ramBuy.recipientAccount)
        assertEquals(currentAccountName, ramBuy.payerAccount)
        assertEquals(currentAccountName, ramBuy.currentAccountName)
    }

    @Test
    fun `Given a list of ActionTrace for a sell RAM only transaction, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "yfxzodmw.sus"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_sell_ram.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val ramSell = result[0] as ActionDataSummaryHeaderUiModel.RamSell
        assertEquals(102, ramSell.ramBytesSold)
        assertEquals(6814, ramSell.newRamBalance)
        assertEquals(Balance(0.0197, "EOS"), ramSell.totalReceived)
        assertEquals(Balance(0.0001, "EOS"), ramSell.ramFee)
        assertEquals("0.198 EOS/ KB", ramSell.pricePerKbFormatted)
    }

    @Test
    fun `Given a list of ActionTrace for a transfer with resource provided RAM, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "yfxzodmw.sus"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json =
            SharedFileReader().loadJsonFile("action_history_transfer_token_with_buy_ram_from_fuel.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(2, result.size)
        val transfer = result[0] as ActionDataSummaryHeaderUiModel.TokenTransfer
        assertEquals(currentAccountName, transfer.senderAccount)
        assertEquals("harkonnenmgl", transfer.recipientAccount)
        assertEquals(Balance(1.0, "EOS"), transfer.quantity)
        assertEquals("", transfer.memo)
        assertTrue(transfer.isOutgoingTransaction)
    }

    @Test
    fun `Given a list of ActionTrace for an outgoing RAM transfer, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "harkonnenmgl"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_ram_transfer.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val transfer = result[0] as ActionDataSummaryHeaderUiModel.RamTransfer
        assertEquals(currentAccountName, transfer.senderAccount)
        assertEquals("mangalaprovn", transfer.recipientAccount)
        assertEquals(currentAccountName, transfer.currentAccountName)
        assertEquals(1024, transfer.ramBytes)
        assertEquals("1 KB", transfer.ramBytesFormatted)
        assertEquals(49233, transfer.newRamBalance)
        assertEquals("48.079 KB", transfer.formattedNewRamBalance)
        assertTrue(transfer.isOutgoingTransaction)
    }

    @Test
    fun `Given a list of ActionTrace for a receive RAM transfer tx, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "mangalaprovn"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_ram_transfer_receive.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val transfer = result[0] as ActionDataSummaryHeaderUiModel.RamTransfer
        assertEquals("harkonnenmgl", transfer.senderAccount)
        assertEquals(currentAccountName, transfer.recipientAccount)
        assertEquals(currentAccountName, transfer.currentAccountName)
        assertEquals(1024, transfer.ramBytes)
        assertEquals("1 KB", transfer.ramBytesFormatted)
        assertEquals(1513037, transfer.newRamBalance)
        assertEquals("1477.575 KB", transfer.formattedNewRamBalance)
        assertFalse(transfer.isOutgoingTransaction)
    }

    @Test
    fun `Given a list of ActionTrace for a contract call with resource provider, when calling getGroupedActionTraces, then return correct map`() {
        val currentAccountName = "g4ydkmjvgene"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json =
            SharedFileReader().loadJsonFile("action_history_contract_call_with_resource_provider.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(3, result.size)
        val resourceProviderFee =
            result.find { it is ActionDataSummaryHeaderUiModel.ResourceProviderFee } as ActionDataSummaryHeaderUiModel.ResourceProviderFee
        assertEquals("fuel.gm", resourceProviderFee.resourceProviderAccount)
        assertEquals("Fuel Transaction Fee | ref=teamgreymass", resourceProviderFee.memo)
        assertEquals(Balance(0.0062, "EOS"), resourceProviderFee.amountPaid)
        assertEquals("0.0062 EOS", resourceProviderFee.amountPaidFormatted)
        val contractCalls = result.filterIsInstance<ActionDataSummaryHeaderUiModel.ContractCall>()
        assertEquals(2, contractCalls.size)
        assertTrue(contractCalls.any { it.actionId == "drops:generate" })
        assertTrue(contractCalls.any { it.actionId == "drops:destroy" })
    }

    @Test
    fun `Given a list of ActionTrace for a create account with buy RAM, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "harkonnenmgl"
        val newAccountName = "qibgvn3v3iox"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_create_account.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(2, result.size)
        val ramBuy =
            result.find { it is ActionDataSummaryHeaderUiModel.RamBuy } as ActionDataSummaryHeaderUiModel.RamBuy
        assertEquals(1599, ramBuy.ramBytesBought)
        assertEquals(Balance(0.0015, "EOS"), ramBuy.ramFee)
        assertEquals(Balance(0.2898, "EOS"), ramBuy.totalCost)
        assertEquals("0.186 EOS/ KB", ramBuy.pricePerKbFormatted)
        assertEquals(1599, ramBuy.newRamBalance)
        assertEquals(
            ActionDataSummaryHeaderUiModel.RamBuy.BuyRamType.BUY_FOR_OTHERS,
            ramBuy.buyRamType
        )
        assertEquals(newAccountName, ramBuy.recipientAccount)
        assertEquals(currentAccountName, ramBuy.payerAccount)
        assertEquals(currentAccountName, ramBuy.currentAccountName)
        val accountCreate =
            result.find { it is ActionDataSummaryHeaderUiModel.CreateAccount } as ActionDataSummaryHeaderUiModel.CreateAccount
        assertEquals(newAccountName, accountCreate.newAccountName)
    }

    @Test
    fun `Given a list of ActionTrace for link auth, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "harkonnenmgl"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_link_auth.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val linkAuth = result[0] as ActionDataSummaryHeaderUiModel.LinkAuth
        assertEquals("createaccound", linkAuth.auth)
        assertEquals("eosio", linkAuth.contractName)
        assertEquals("ramtransfer", linkAuth.action)
        assertEquals("eosio:ramtransfer", linkAuth.actionId)
    }

    @Test
    fun `Given a list of ActionTrace for update auth, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "harkonnenmgl"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_update_auth.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val updateAuth = result[0] as ActionDataSummaryHeaderUiModel.UpdateAuth
        assertEquals("createaccound", updateAuth.permissionName)
    }

    @Test
    fun `Given a list of ActionTrace for rentcpu, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "harkonnenmgl"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_rent_cpu.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val rentViaRex = result[0] as ActionDataSummaryHeaderUiModel.RentViaRex
        assertEquals(currentAccountName, rentViaRex.from)
        assertEquals(currentAccountName, rentViaRex.receiver)
        assertEquals(Balance(0.0028, "EOS"), rentViaRex.amount)
        assertEquals("0.0028 EOS", rentViaRex.amountFormatted)
        assertEquals(
            ActionDataSummaryHeaderUiModel.ResourceRentType.RENT_FOR_SELF,
            rentViaRex.resourceRentType
        )
        assertEquals(
            ActionDataSummaryHeaderUiModel.RentViaRex.ResourceType.RENT_CPU,
            rentViaRex.resourceType
        )
    }

    @Test
    fun `Given a list of ActionTrace for rentnet, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "harkonnenmgl"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_rent_net.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val rentViaRex = result[0] as ActionDataSummaryHeaderUiModel.RentViaRex
        assertEquals(currentAccountName, rentViaRex.from)
        assertEquals(currentAccountName, rentViaRex.receiver)
        assertEquals(Balance(0.0028, "EOS"), rentViaRex.amount)
        assertEquals("0.0028 EOS", rentViaRex.amountFormatted)
        assertEquals(
            ActionDataSummaryHeaderUiModel.ResourceRentType.RENT_FOR_SELF,
            rentViaRex.resourceRentType
        )
        assertEquals(
            ActionDataSummaryHeaderUiModel.RentViaRex.ResourceType.RENT_NET,
            rentViaRex.resourceType
        )
    }

    @Test
    fun `Given a list of ActionTrace for powerup, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "harkonnenmgl"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_powerup.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val rentViaRex = result[0] as ActionDataSummaryHeaderUiModel.PowerUp
        assertEquals(Balance(0.2663, "EOS"), rentViaRex.amount)
        assertEquals("0.2663 EOS", rentViaRex.amountFormatted)
        assertEquals(currentAccountName, rentViaRex.receiver)
        assertEquals(currentAccountName, rentViaRex.currentAccountName)
        assertEquals(
            ActionDataSummaryHeaderUiModel.ResourceRentType.RENT_FOR_SELF,
            rentViaRex.powerUpType
        )
    }

    @Test
    fun `Given a list of ActionTrace for multisig propose, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "accpartner11"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_msig_propose.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val propose = result[0] as ActionDataSummaryHeaderUiModel.MsigPropose
        assertEquals(currentAccountName, propose.proposer)
        assertEquals("buyram1..22", propose.proposalName)
        assertEquals(2, propose.requestedPermissions.size)
        assertEquals(
            Transaction.Authorization("accpartner11", "active"),
            propose.requestedPermissions[0]
        )
        assertEquals(
            Transaction.Authorization("accpartner12", "active"),
            propose.requestedPermissions[1]
        )
        assertEquals("eosio", propose.actions[0].account)
        assertEquals("buyram", propose.actions[0].name)
        assertEquals(currentAccountName, propose.currentAccountName)
        assertEquals(
            "accpartner11@active, accpartner12@active",
            propose.formattedRequestedPermissions
        )
        assertEquals("eosio:buyram", propose.formattedActions)
        assertTrue(propose.isProposalFromCurrentAccount)
    }

    @Test
    fun `Given a list of ActionTrace for multisig exec, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "accpartner11"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_msig_exec.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val propose = result[0] as ActionDataSummaryHeaderUiModel.MsigExecute
        assertEquals(currentAccountName, propose.proposer)
        assertEquals("buyram123", propose.proposalName)
        assertEquals(currentAccountName, propose.currentAccountName)
        assertEquals(currentAccountName, propose.executer)
        assertTrue(propose.isMsigProposedBySelf)
    }

    @Test
    fun `Given a list of ActionTrace for multisig cancel, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "accpartner11"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_msig_cancel.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val propose = result[0] as ActionDataSummaryHeaderUiModel.MsigCancel
        assertEquals(currentAccountName, propose.proposer)
        assertEquals("buyram1", propose.proposalName)
        assertEquals(currentAccountName, propose.canceler)
        assertEquals(currentAccountName, propose.currentAccountName)
        assertTrue(propose.isMsigProposedBySelf)
    }

    @Test
    fun `Given a list of ActionTrace for multisig approve, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "accpartner11"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_msig_approve.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val propose = result[0] as ActionDataSummaryHeaderUiModel.MsigApproveToggle
        assertEquals(currentAccountName, propose.proposer)
        assertEquals("buyram1dd", propose.proposalName)
        assertEquals(
            Transaction.Authorization("accpartner11", "active"),
            propose.approvedPermission
        )
        assertEquals(currentAccountName, propose.currentAccountName)
        assertEquals("accpartner11@active", propose.formattedApprovedPermission)
        assertTrue(propose.isMsigProposedBySelf)
        assertTrue(propose.isApprove)
    }

    @Test
    fun `Given a list of ActionTrace for multisig unapprove, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "accpartner12"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_msig_unapprove.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val propose = result[0] as ActionDataSummaryHeaderUiModel.MsigApproveToggle
        assertEquals("accmultisig1", propose.proposer)
        assertEquals("dxzr2g", propose.proposalName)
        assertEquals(
            Transaction.Authorization("accpartner12", "active"),
            propose.approvedPermission
        )
        assertEquals(currentAccountName, propose.currentAccountName)
        assertEquals("accpartner12@active", propose.formattedApprovedPermission)
        assertFalse(propose.isMsigProposedBySelf)
        assertFalse(propose.isApprove)
    }

    @Test
    fun `Given a list of ActionTrace for delegatebw, when calling mapActionDataSummaryHeader, then return correct map`() {
        val currentAccountName = "harkonnenmgl"
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_delegatebw.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)
        val groupedActionTraces = actionTraces.getGroupedActionTraces(currentAccountName)

        val result = groupedActionTraces.toActionDataSummaryHeaderUiModels(currentAccountName)

        assertEquals(1, result.size)
        val propose = result[0] as ActionDataSummaryHeaderUiModel.DelegateBandwidth
        assertEquals(currentAccountName, propose.from)
        assertEquals(currentAccountName, propose.receiver)
        assertEquals(
            Balance(0.0529, "EOS"),
            propose.netDelegateAmount
        )
        assertEquals(
            "0.0529 EOS",
            propose.netDelegateAmountFormatted
        )
        assertEquals(
            Balance(0.0, "EOS"),
            propose.cpuDelegateAmount
        )
        assertNull(propose.cpuDelegateAmountFormatted)
        assertEquals(
            Balance(0.0529, "EOS"),
            propose.totalAmount
        )
        assertEquals(
            "0.0529 EOS",
            propose.totalAmountFormatted
        )
        assertEquals(ActionDataSummaryHeaderUiModel.ResourceRentType.RENT_FOR_SELF, propose.delegateType)
    }
}