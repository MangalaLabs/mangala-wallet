package com.mangala.wallet.features.addressbook.domain.functioncalling

import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRenderer
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRendererPlugin
import com.mangala.wallet.features.addressbook.domain.functioncalling.add.AddContactConfirmationRenderer
import com.mangala.wallet.features.addressbook.domain.functioncalling.delete.DeleteContactConfirmationRenderer
import com.mangala.wallet.features.addressbook.domain.functioncalling.edit.EditContactConfirmationRenderer
import com.mangala.wallet.features.addressbook.domain.functioncalling.edit.EditContactNameConfirmationRenderer

class AddressBookConfirmationRendererPlugin : ConfirmationRendererPlugin {
    
    override fun getRenderers(): List<ConfirmationRenderer> {
        return listOf(
            AddContactConfirmationRenderer(),
            EditContactConfirmationRenderer(),
            DeleteContactConfirmationRenderer(),
            EditContactNameConfirmationRenderer()
        )
    }
}