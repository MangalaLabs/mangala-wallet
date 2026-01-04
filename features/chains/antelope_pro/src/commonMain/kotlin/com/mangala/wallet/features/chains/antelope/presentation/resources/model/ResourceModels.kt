package com.mangala.wallet.features.chains.antelope.presentation.resources.model

data class ResourceUiState(
    val playerInfo: PlayerInfo,
    val powerLevel: Int,
    val cpuResource: CpuResource,
    val netResource: NetResource,
    val ramResource: RamResource,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class PlayerInfo(
    val name: String,
    val walletAddress: String,
    val eosBalance: Double
)

sealed class Resource {
    abstract val name: String
    abstract val type: String
    abstract val rarity: ResourceRarity
    abstract val current: Number
    abstract val max: Number
    abstract val unit: String
    abstract val percentage: Int
    abstract val statusEffects: List<StatusEffect>
}

data class CpuResource(
    override val name: String,
    override val type: String,
    override val rarity: ResourceRarity,
    override val current: Int,
    override val max: Int,
    override val unit: String,
    override val percentage: Int,
    val regenTime: String,
    override val statusEffects: List<StatusEffect>
) : Resource()

data class NetResource(
    override val name: String,
    override val type: String,
    override val rarity: ResourceRarity,
    override val current: Int,
    override val max: Int,
    override val unit: String,
    override val percentage: Int,
    val regenTime: String,
    override val statusEffects: List<StatusEffect>
) : Resource()

data class RamResource(
    override val name: String,
    override val type: String,
    override val rarity: ResourceRarity,
    override val current: Double,
    override val max: Double,
    override val unit: String,
    override val percentage: Int,
    val price: Double,
    val priceChange: Double,
    override val statusEffects: List<StatusEffect>
) : Resource()

enum class ResourceRarity {
    EPIC,
    LEGENDARY
}

data class StatusEffect(
    val name: String,
    val type: StatusEffectType
)

enum class StatusEffectType {
    BUFF,
    DEBUFF
}