package com.mangala.wallet.features.chains.evmcompatible.spv.rlp

import com.mangala.wallet.features.chains.evmcompatible.core.toInt

class RLPList : RLPElement {
    private val elements = ArrayList<RLPElement>()
    override var rlpData: ByteArray? = null

    fun add(element: RLPElement) {
        elements.add(element)
    }

    fun valueElement(name: String): RLPElement? {
        for (rlpElement in elements) {
            if (rlpElement is RLPList && rlpElement[0].rlpData?.toString() == name) {
                return rlpElement[1]
            }
        }

        return null
    }

    override fun toString(): String {
        return elements.toString()
    }

    // delegate necessary methods to the underlying ArrayList instance
    operator fun get(index: Int): RLPElement {
        return elements[index]
    }

    operator fun set(index: Int, element: RLPElement): RLPElement {
        return elements.set(index, element)
    }

    fun addAll(elements: Collection<RLPElement>): Boolean {
        return this.elements.addAll(elements)
    }

    fun clear() {
        elements.clear()
    }

    fun remove(element: RLPElement): Boolean {
        return elements.remove(element)
    }

    fun removeAt(index: Int): RLPElement {
        return elements.removeAt(index)
    }

    fun size(): Int {
        return elements.size
    }
}
