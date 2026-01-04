/*
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mangala.antelope.base.core.error


/**
 * Error class is used when there is an exception while attempting to process anything inside the
 * Eosio-java library
 */
open class EosioError : Exception {
    /**
     * Create an EosioError with a null message and original exception.
     */
    constructor() : super()

    /**
     * Construct an EosioError with the given message.
     *
     * @param message - Message text for the exception.
     */
    constructor(message: String) : super(message)

    /**
     * Construct an EosioError with the given message and original exception.
     *
     * @param message - Message text for the exception.
     * @param exception - Original root exception for the error.
     */
    constructor(message: String, exception: Exception) : super(message, exception)

    /**
     * Construct an EosioError with the given original exception.
     *
     * @param exception - Original root exception for the error.
     */
    constructor(exception: Exception) : super(exception)

    /**
     * Construct a JSON formatted string describing the error code and reason.
     *
     * @return A JSON formatted string
     */
//    fun asJsonString(): String {
//        val errInfo = JsonObject()
//        errInfo.addProperty("errorCode", this.javaClass.getSimpleName())
//        errInfo.addProperty("reason", this.getLocalizedMessage())
//        val err = JsonObject()
//        err.addProperty("errorType", "EosioError")
//        err.add("errorInfo", errInfo)
//        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
//        return gson.toJson(err)
//    }
}
