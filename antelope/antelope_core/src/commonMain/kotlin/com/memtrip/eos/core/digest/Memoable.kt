/*
 * Copyright (c) 2000-2021 The Legion of the Bouncy Castle Inc.
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the MIT License.
 * See https://www.bouncycastle.org/licence.html for license details.
 */

// ------------------------------------------------------------------
// MODIFICATION NOTICE:
// Modified by Mangala Wallet
// Description: Adapted from BouncyCastle for Kotlin Multiplatform compatibility.
// ------------------------------------------------------------------
package com.memtrip.eos.core.digest

interface Memoable {
    /**
     * Produce a copy of this object with its configuration and in its current state.
     *
     *
     * The returned object may be used simply to store the state, or may be used as a similar object
     * starting from the copied state.
     */
    fun copy(): Memoable

    /**
     * Restore a copied object state into this object.
     *
     *
     * Implementations of this method *should* try to avoid or minimise memory allocation to perform the reset.
     *
     * @param other an object originally [copied][.copy] from an object of the same type as this instance.
     * @throws ClassCastException if the provided object is not of the correct type.
     * @throws MemoableResetException if the **other** parameter is in some other way invalid.
     */
    fun reset(other: Memoable)
}
