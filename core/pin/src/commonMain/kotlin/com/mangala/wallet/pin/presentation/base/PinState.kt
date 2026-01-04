package com.mangala.wallet.pin.presentation.base

enum class PinState(val state: String) {
    SETUP_PIN("setup_pin"), //The user is setting up a new PIN for the first time.
    CONFIRM_PIN("confirm_pin"), //The user needs to confirm the new PIN they just set up.
    INCORRECT_PIN("incorrect_pin"), //The user entered an incorrect PIN.
    CORRECT_PIN("correct_pin"), //The user entered the correct PIN and can proceed to the next screen.
    LOCKED("locked"), //The user entered an incorrect PIN too many times and the app is locked.
    EMPTY("empty"), //When the user has not entered any digits.
    UNLOCKING("unlocking"), //The app is in the process of unlocking after the user entered the correct PIN.
    EDIT_PIN("edit_pin"),
    FORGOT_PIN("forgot_pin"); //The user has forgotten their PIN and needs to reset it.

    companion object {
        fun fromString(state: String): PinState? {
            return values().find { it.state == state }
        }
    }
}