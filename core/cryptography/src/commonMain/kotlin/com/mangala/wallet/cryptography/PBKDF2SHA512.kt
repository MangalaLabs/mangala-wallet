package com.mangala.wallet.cryptography

expect fun pbkdf2sha512(password: String, salt: String, rounds: Int, derivedKeyLength: Int): ByteArray