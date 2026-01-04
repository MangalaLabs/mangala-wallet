package com.mangala.wallet.features.conversationui.domain.validation

import com.mangala.wallet.core.ai.domain.AddressValidationResult
import com.mangala.wallet.core.ai.domain.AddressValidator
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AddressValidatorRegistry(private val validators: List<AddressValidator>) {

    fun getValidator(networkName: String): AddressValidator? {
        return validators.find { it.canValidate(networkName) }
    }
}