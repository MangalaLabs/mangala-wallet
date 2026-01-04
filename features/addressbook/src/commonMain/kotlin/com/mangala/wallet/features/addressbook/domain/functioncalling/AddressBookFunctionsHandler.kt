package com.mangala.wallet.features.addressbook.domain.functioncalling

import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandler
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandlerPlugin
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandlerRegistry
import com.mangala.wallet.features.addressbook.domain.functioncalling.add.AddContactHandler
import com.mangala.wallet.features.addressbook.domain.functioncalling.delete.DeleteContactHandler
import com.mangala.wallet.features.addressbook.domain.functioncalling.edit.EditContactHandler
import com.mangala.wallet.features.addressbook.domain.functioncalling.edit.EditContactNameHandler
import com.mangala.wallet.features.addressbook.domain.functioncalling.find.FindContactHandler
import com.mangala.wallet.features.addressbook.domain.functioncalling.list.ListContactsHandler

class AddressBookFunctionsHandler(
    private val addContactHandler: AddContactHandler,
    private val editContactHandler: EditContactHandler,
    private val findContactHandler: FindContactHandler,
    private val listContactsHandler: ListContactsHandler,
    private val deleteContactHandler: DeleteContactHandler,
    private val editContactNameHandler: EditContactNameHandler
) : FunctionHandlerPlugin {

    override fun registerTo(registry: FunctionHandlerRegistry) {
        getFunctionHandlers().forEach {
            registry.registerHandler(it)
        }
    }

    override fun getFunctionHandlers(): List<FunctionHandler> {
        return listOf(addContactHandler, editContactHandler, findContactHandler, listContactsHandler, deleteContactHandler, editContactNameHandler)
    }
}