package com.mangala.wallet.features.chains.antelope_base.domain.usecase

import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ValidateAccountUseCase
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import junit.framework.TestCase.assertFalse
import org.junit.Test
import kotlin.test.assertTrue

class ValidateAccountUseCaseTest {

    val sut = ValidateAccountUseCase()

    @Test
    fun `Given standard account name with period with 12 characters, when validate account name, then return result with invalid character`() {
        val accountName = "abcdasdf.ere"

        val result = sut.validateAccountName(accountName, accountType = AccountNameType.Standard)

        assertFalse(result.containsOnlyValidCharacters)
        assertTrue(result.isValidLength)
        assertFalse(result.isValid)
        assertTrue(result.startsAndEndsCorrectly)
    }

    @Test
    fun `Given standard account name with number 0 with 11 characters, when validate account name, then return result with invalid character and invalid length`() {
        val accountName = "abcdasdf0er"

        val result = sut.validateAccountName(accountName, accountType = AccountNameType.Standard)

        assertFalse(result.containsOnlyValidCharacters)
        assertFalse(result.isValidLength)
        assertFalse(result.isValid)
        assertTrue(result.startsAndEndsCorrectly)
    }

    @Test
    fun `Given standard account name with valid characters with 11 characters, when validate account name, then return result with invalid length`() {
        val accountName = "abcdasdfere"

        val result = sut.validateAccountName(accountName, accountType = AccountNameType.Standard)

        assertTrue(result.containsOnlyValidCharacters)
        assertFalse(result.isValidLength)
        assertFalse(result.isValid)
        assertTrue(result.startsAndEndsCorrectly)
    }

    @Test
    fun `Given standard account name starts with period, when validate account name, then return result with invalid start`() {
        val accountName = ".abcdasdfere"

        val result = sut.validateAccountName(accountName, accountType = AccountNameType.Standard)

        assertFalse(result.startsAndEndsCorrectly)
        assertFalse(result.containsOnlyValidCharacters)
        assertTrue(result.isValidLength)
        assertFalse(result.isValid)
    }

    @Test
    fun `Given standard account name ends with period, when validate account name, then return result with invalid end`() {
        val accountName = "abcdasdfere."

        val result = sut.validateAccountName(accountName, accountType = AccountNameType.Standard)

        assertFalse(result.startsAndEndsCorrectly)
        assertFalse(result.containsOnlyValidCharacters)
        assertTrue(result.isValidLength)
        assertFalse(result.isValid)
    }

    @Test
    fun `Given standard account name starts with a number, when validate account name, then return result with invalid start`() {
        val accountName = "1bcdasdferee"

        val result = sut.validateAccountName(accountName, accountType = AccountNameType.Standard)

        assertFalse(result.startsAndEndsCorrectly)
        assertTrue(result.containsOnlyValidCharacters)
        assertTrue(result.isValidLength)
        assertFalse(result.isValid)
    }

    @Test
    fun `Given standard account name ends with a number, when validate account name, then return result with valid end`() {
        val accountName = "abcdasdfere5"

        val result = sut.validateAccountName(accountName, accountType = AccountNameType.Standard)

        assertTrue(result.startsAndEndsCorrectly)
        assertTrue(result.containsOnlyValidCharacters)
        assertTrue(result.isValidLength)
        assertTrue(result.isValid)
    }

    @Test
    fun `Given premium account name starts with period, when validate account name, then return result with invalid start`() {
        val accountName = ".abcdasp.man"

        val result = sut.validateAccountName(accountName, accountType = AccountNameType.Premium)

        assertFalse(result.startsAndEndsCorrectly)
        assertTrue(result.containsOnlyValidCharacters)
        assertTrue(result.isValidLength)
        assertFalse(result.isValid)
    }

    @Test
    fun `Given premium account name ends with period, when validate account name, then return result with invalid end`() {
        val accountName = "abdcdas.man."

        val result = sut.validateAccountName(accountName, accountType = AccountNameType.Premium)

        assertFalse(result.startsAndEndsCorrectly)
        assertTrue(result.containsOnlyValidCharacters)
        assertTrue(result.isValidLength)
        assertFalse(result.isValid)
    }

    @Test
    fun `Given premium account name starts with a number, when validate account name, then return result with invalid start`() {
        val accountName = "9basprem.man"

        val result = sut.validateAccountName(accountName, accountType = AccountNameType.Premium)

        assertFalse(result.startsAndEndsCorrectly)
        assertTrue(result.containsOnlyValidCharacters)
        assertTrue(result.isValidLength)
        assertFalse(result.isValid)
    }

    @Test
    fun `Given premium account name with valid characters and length, when validate account name, then return result valid`() {
        val accountName = "abcaprem.man"

        val result = sut.validateAccountName(accountName, accountType = AccountNameType.Premium)

        assertTrue(result.startsAndEndsCorrectly)
        assertTrue(result.containsOnlyValidCharacters)
        assertTrue(result.isValidLength)
        assertTrue(result.isValid)
    }


    @Test
    fun `Given premium account name with less than 12 characters including suffix, when validate account name, then return result valid`() {
        val accountName = "pre.man"

        val result = sut.validateAccountName(accountName, accountType = AccountNameType.Premium)

        assertTrue(result.startsAndEndsCorrectly)
        assertTrue(result.containsOnlyValidCharacters)
        assertTrue(result.isValidLength)
        assertTrue(result.isValid)
    }
}