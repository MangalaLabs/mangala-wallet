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

package com.mangala.antelope.base.core.error.serializationProvider

/**
 * Error class is used when there is an exception while attempting to call deserializeTransaction()
 * of Serialization Provider
 */
class DeserializeTransactionError : SerializationProviderError {
    constructor()
    constructor(message: String) : super(message)
    constructor(
        message: String,
        exception: Exception
    ) : super(message, exception)

    constructor(exception: Exception) : super(exception)
}
