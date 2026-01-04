package com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory

import com.mangala.antelope.base.api.model.ActionTrace
import com.mangala.wallet.common.test.utils.SharedFileReader
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CompositeActionGroupMapperTest {

    @Test
    fun `Given a list of ActionTrace for a buy RAM only transaction, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_buy_ram.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("yfxzodmw.sus")

        assertEquals(1, response.keys.size)
        assertEquals(CompositeActionGroup.BUY_RAM, response.keys.first())
        assertEquals(actionTraces.size, response[CompositeActionGroup.BUY_RAM]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for a buy RAM for other transaction, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_buy_ram_for_others.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("yfxzodmw.sus")

        assertEquals(2, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.BUY_RAM))
        assertTrue(response.keys.contains(CompositeActionGroup.RESOURCE_PROVIDER_FEE))
        assertEquals(CompositeActionGroup.BUY_RAM, response.keys.first())
        assertEquals(5, response[CompositeActionGroup.BUY_RAM]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for a buyrambytes received from others, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_buyrambytes_by_other.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("yfxzodmw.sus")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.BUY_RAM))
        assertEquals(3, response[CompositeActionGroup.BUY_RAM]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for a buyRamBytes transaction, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_buy_ram_bytes.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("yfxzodmw.sus")

        assertEquals(2, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.BUY_RAM))
        assertTrue(response.keys.contains(CompositeActionGroup.RESOURCE_PROVIDER_FEE))
        assertEquals(CompositeActionGroup.BUY_RAM, response.keys.first())
        assertEquals(6, response[CompositeActionGroup.BUY_RAM]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for a buyRamBytes that does not have logbuyram transaction, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_buy_ram_bytes_legacy.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("acc.gm")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.BUY_RAM))
        assertEquals(CompositeActionGroup.BUY_RAM, response.keys.first())
        assertEquals(3, response[CompositeActionGroup.BUY_RAM]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for a buyRam that does not have logbuyram transaction, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_buy_ram_legacy.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("acc.gm")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.BUY_RAM))
        assertEquals(CompositeActionGroup.BUY_RAM, response.keys.first())
        assertEquals(3, response[CompositeActionGroup.BUY_RAM]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for a sell RAM only transaction, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_sell_ram.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("yfxzodmw.sus")

        assertEquals(1, response.keys.size)
        assertEquals(CompositeActionGroup.SELL_RAM, response.keys.first())
        assertEquals(actionTraces.size, response[CompositeActionGroup.SELL_RAM]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for a transfer with resource provided RAM, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json =
            SharedFileReader().loadJsonFile("action_history_transfer_token_with_buy_ram_from_fuel.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("yfxzodmw.sus")

        assertEquals(2, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.TOKEN_TRANSFER))
        assertTrue(response.keys.contains(CompositeActionGroup.RESOURCE_PROVIDER_FEE))
        assertEquals(1, response[CompositeActionGroup.TOKEN_TRANSFER]!!.size)
        assertEquals(4, response[CompositeActionGroup.RESOURCE_PROVIDER_FEE]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for an outgoing RAM transfer, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_ram_transfer.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("harkonnenmgl")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.RAM_TRANSFER))
        assertEquals(3, response[CompositeActionGroup.RAM_TRANSFER]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for a receive RAM transfer tx, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_ram_transfer_receive.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("mangalaprovn")

        println(response)

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.RAM_TRANSFER))
        assertEquals(2, response[CompositeActionGroup.RAM_TRANSFER]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for a plain transfer tx, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_plain_transfer.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("mangalaprovn")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.TOKEN_TRANSFER))
        assertEquals(1, response[CompositeActionGroup.TOKEN_TRANSFER]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for a contract call with resource provider, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json =
            SharedFileReader().loadJsonFile("action_history_contract_call_with_resource_provider.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("mangalaprovn")

        assertEquals(2, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.RESOURCE_PROVIDER_FEE))
        assertTrue(response.keys.contains(CompositeActionGroup.CONTRACT_CALL))
        assertEquals(1, response[CompositeActionGroup.RESOURCE_PROVIDER_FEE]!!.size)
        assertEquals(7, response[CompositeActionGroup.CONTRACT_CALL]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for a create account with buy RAM, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_create_account.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("harkonnenmgl")

        assertEquals(2, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.CREATE_ACCOUNT))
        assertTrue(response.keys.contains(CompositeActionGroup.BUY_RAM))
        assertEquals(1, response[CompositeActionGroup.CREATE_ACCOUNT]!!.size)
        assertEquals(5, response[CompositeActionGroup.BUY_RAM]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for link auth, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_link_auth.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("harkonnenmgl")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.LINK_AUTH))
        assertEquals(1, response[CompositeActionGroup.LINK_AUTH]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for update auth, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_update_auth.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("harkonnenmgl")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.UPDATE_AUTH))
        assertEquals(1, response[CompositeActionGroup.UPDATE_AUTH]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for rentcpu, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_rent_cpu.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("harkonnenmgl")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.RENT_CPU))
        assertEquals(3, response[CompositeActionGroup.RENT_CPU]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for rentnet, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_rent_net.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("harkonnenmgl")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.RENT_NET))
        assertEquals(3, response[CompositeActionGroup.RENT_NET]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for powerup, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_powerup.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("harkonnenmgl")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.POWERUP))
        assertEquals(2, response[CompositeActionGroup.POWERUP]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for multisig propose, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_msig_propose.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("accpartner11")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.MSIG_PROPOSE))
        assertEquals(1, response[CompositeActionGroup.MSIG_PROPOSE]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for multisig exec, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_msig_exec.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("accpartner11")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.MSIG_EXEC))
        assertEquals(1, response[CompositeActionGroup.MSIG_EXEC]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for multisig cancel, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_msig_cancel.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("accpartner11")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.MSIG_CANCEL))
        assertEquals(1, response[CompositeActionGroup.MSIG_CANCEL]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for multisig approve, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_msig_approve.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("accpartner11")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.MSIG_APPROVE))
        assertEquals(1, response[CompositeActionGroup.MSIG_APPROVE]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for multisig unapprove, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_msig_unapprove.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("accpartner11")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.MSIG_UNAPPROVE))
        assertEquals(1, response[CompositeActionGroup.MSIG_UNAPPROVE]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for delegatebw, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_delegatebw.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("harkonnenmgl")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.DELEGATE_BANDWIDTH))
        assertEquals(2, response[CompositeActionGroup.DELEGATE_BANDWIDTH]!!.size)
    }

    @Test
    fun `Given a list of ActionTrace for a receiving powerup, when calling getGroupedActionTraces, then return correct map`() {
        val jsonInstance = Json { ignoreUnknownKeys = true }
        val json = SharedFileReader().loadJsonFile("action_history_powerup_receive.json")!!
        val actionTraces: List<ActionTrace> = jsonInstance.decodeFromString(json)

        val response = actionTraces.getGroupedActionTraces("vwmnsagsadgk")

        assertEquals(1, response.keys.size)
        assertTrue(response.keys.contains(CompositeActionGroup.CONTRACT_CALL))
        assertEquals(1, response[CompositeActionGroup.CONTRACT_CALL]!!.size)
    }
}