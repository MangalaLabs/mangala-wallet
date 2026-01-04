package com.mangala.wallet.pin.data

// Interface to store and retrieve the PIN from platform-specific storage
interface IPINStorage {
    fun storePIN(pin: PIN)
    fun retrievePIN(): PIN?
}

expect class PINStorage: IPINStorage {
    override fun storePIN(pin: PIN)
    override fun retrievePIN(): PIN?
}