package com.mangala.wallet.model.blockchain

data class Blockchain(
    val type: BlockchainType,
    val name: String,
    val eip3091url: String?
) {

    val uid: String
        get() = type.uid

    override fun equals(other: Any?): Boolean =
        other is Blockchain && other.type == type

//    override fun hashCode(): Int =
//        Objects.hash(type, name)

}