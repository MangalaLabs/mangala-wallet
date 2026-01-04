package com.mangala.wallet.features.chains.antelope_base.presentation.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AntelopeDataFieldValidationUtilsTest {

    @Test
    fun `Given no precision, when validating symbol, then return correct exception`() {
        val symbol = "EOS"
        val precision = ""

        val result = AntelopeDataFieldValidator.SymbolValidator.getValidationResult("$precision,$symbol")

        assertTrue(result.exceptionOrNull() is AntelopeDataFieldValidator.DataFieldException.TwoFieldsException)
        val exception = result.exceptionOrNull() as AntelopeDataFieldValidator.DataFieldException.TwoFieldsException
        assertTrue(exception.field1Exception is AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException.MissingSymbolPrecision)
        assertNull(exception.field2Exception)
    }

    @Test
    fun `Given no symbol, when validating precision, then return correct exception`() {
        val symbol = ""
        val precision = "4"

        val result = AntelopeDataFieldValidator.SymbolValidator.getValidationResult("$precision,$symbol")

        assertTrue(result.exceptionOrNull() is AntelopeDataFieldValidator.DataFieldException.TwoFieldsException)
        val exception = result.exceptionOrNull() as AntelopeDataFieldValidator.DataFieldException.TwoFieldsException
        assertNull(exception.field1Exception)
        assertTrue(exception.field2Exception is AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException.MissingSymbolName)
    }

    @Test
    fun `Given both no precision and symbol, when validating symbol, then return correct exception`() {
        val symbol = ""
        val precision = ""

        val result = AntelopeDataFieldValidator.SymbolValidator.getValidationResult("$precision,$symbol")

        assertTrue(result.exceptionOrNull() is AntelopeDataFieldValidator.DataFieldException.TwoFieldsException)
        val exception = result.exceptionOrNull() as AntelopeDataFieldValidator.DataFieldException.TwoFieldsException
        assertTrue(exception.field1Exception is AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException.MissingSymbolPrecision)
        assertTrue(exception.field2Exception is AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException.MissingSymbolName)
    }

    @Test
    fun `Given too large precision, when validating symbol, then return correct exception`() {
        val symbol = "EOS"
        val precision = "19"

        val result = AntelopeDataFieldValidator.SymbolValidator.getValidationResult("$precision,$symbol")

        assertTrue(result.exceptionOrNull() is AntelopeDataFieldValidator.DataFieldException.TwoFieldsException)
        val exception = result.exceptionOrNull() as AntelopeDataFieldValidator.DataFieldException.TwoFieldsException
        assertTrue(exception.field1Exception is AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException.InvalidSymbolPrecision)
        assertNull(exception.field2Exception)
    }

    @Test
    fun `Given too small precision, when validating symbol, then return correct exception`() {
        val symbol = "EOS"
        val precision = "-1"

        val result = AntelopeDataFieldValidator.SymbolValidator.getValidationResult("$precision,$symbol")

        assertTrue(result.exceptionOrNull() is AntelopeDataFieldValidator.DataFieldException.TwoFieldsException)
        val exception = result.exceptionOrNull() as AntelopeDataFieldValidator.DataFieldException.TwoFieldsException
        assertTrue(exception.field1Exception is AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException.InvalidSymbolPrecision)
        assertNull(exception.field2Exception)
    }

    @Test
    fun `Given too long symbol name, when validating symbol, then return correct exception`() {
        val symbol = "EOSEOSEOSEOSEOSEOSEOS"
        val precision = "4"

        val result = AntelopeDataFieldValidator.SymbolValidator.getValidationResult("$precision,$symbol")

        assertTrue(result.exceptionOrNull() is AntelopeDataFieldValidator.DataFieldException.TwoFieldsException)
        val exception = result.exceptionOrNull() as AntelopeDataFieldValidator.DataFieldException.TwoFieldsException
        assertNull(exception.field1Exception)
        assertTrue(exception.field2Exception is AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException.InvalidSymbolValue)
    }

    @Test
    fun `Given name with 13 characters that when serialized does match original, when validating name, then return success`() {
        val name = "zzzzzzzzzzzza"

        val result = AntelopeDataFieldValidator.NameValidator(false).getValidationResult(name)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `Given name with 13 characters that when serialized does not match original, when validating name, then return correct exception`() {
        val name = "createaccount"

        val result = AntelopeDataFieldValidator.NameValidator(false).getValidationResult(name)

        val exception = result.exceptionOrNull() as AntelopeDataFieldValidator.NameValidator.InvalidNameException.NameDoesNotMatchSerializedName

        assertEquals("createaccound", exception.serializedName)
    }
}