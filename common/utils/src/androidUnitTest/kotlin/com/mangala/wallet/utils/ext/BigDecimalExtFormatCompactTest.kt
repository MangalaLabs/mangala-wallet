package com.mangala.wallet.utils.ext

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals

class BigDecimalExtFormatCompactTest {

    @Test
    fun `Given a BigDecimal value 500_when formatCompact called_then return as is`() {
        val bigDecimal = 500.toBigDecimal()
        val result = bigDecimal.formatCompact()

        assertEquals("500", result)
    }

    @Test
    fun `Given a BigDecimal value 1000_when formatCompact called_then return abbreviated value`() {
        val bigDecimal = 1000.toBigDecimal()
        val result = bigDecimal.formatCompact()

        assertEquals("1K", result)
    }

    @Test
    fun `Given a BigDecimal value 1500_when formatCompact called_then return abbreviated value`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = 1500.toBigDecimal()
        val result = bigDecimal.formatCompact()

        assertEquals("1.5K", result)
    }

    @Test
    fun `Given a BigDecimal value and locale has comma as decimal separator_when formatCompact called_then return abbreviated value`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = 1500.toBigDecimal()
        val result = bigDecimal.formatCompact()

        assertEquals("1,5K", result)
    }

    @Test
    fun `Given a BigDecimal value 1512_when formatCompact called_then return abbreviated value`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = 1512.toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1.51K", result)
    }

    @Test
    fun `Given a BigDecimal value 1512 and locale has comma as decimal separator_when formatCompact called_then return abbreviated value`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = 1512.toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1,51K", result)
    }

    @Test
    fun `Given a BigDecimal value 1516_when formatCompact called_then return abbreviated value rounded to 2 decimal digits`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = 1516.toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1.51K", result)
    }


    @Test
    fun `Given a BigDecimal value 1516 and locale has comma as separator_when formatCompact called_then return abbreviated value rounded to 2 decimal digits`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = 1516.toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1,51K", result)
    }

    @Test
    fun `Given a BigDecimal value 1516000_when formatCompact called_then return abbreviated value rounded to 2 decimal digits`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "1516000".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1.51M", result)
    }

    @Test
    fun `Given a BigDecimal value 1516000 and locale has comma as separator_when formatCompact called_then return abbreviated value rounded to 2 decimal digits`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = "1516000".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1,51M", result)
    }

    @Test
    fun `Given BigDecimal value 1041 point 5299_when formatCompact called_then return abbreviated value rounded to 2 decimal digits`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "1041.5299".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1.04K", result)
    }

    @Test
    fun `Given BigDecimal value 1041 point 5299 and locale has comma as separator_when formatCompact called_then return abbreviated value rounded to 2 decimal digits`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = "1041.5299".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1,04K", result)
    }

    @Test
    fun `Given a BigDecimal value 1516000000_when formatCompact called_then return abbreviated value rounded to 2 decimal digits`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "1516000000".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1.51B", result)
    }

    @Test
    fun `Given a BigDecimal value 1516000000 and locale has comma as separator_when formatCompact called_then return abbreviated value rounded to 2 decimal digits`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = "1516000000".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1,51B", result)
    }

    @Test
    fun `Given a BigDecimal value 39969142670_ when formatCompact_ then return abbreviated value`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = BigDecimal.parseString("39969142670")
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("39.96B", result)
    }

    @Test
    fun `Given a BigDecimal value 39969142670 and locale has comma as separator_ when formatCompact_ then return abbreviated value`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = BigDecimal.parseString("39969142670")
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("39,96B", result)
    }

    @Test
    fun `Given a BigDecimal value 1516000000000_when formatCompact called_then return abbreviated value rounded to 2 decimal digits`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "1516000000000".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1.51T", result)
    }


    @Test
    fun `Given a BigDecimal value 1516000000000 and locale has comma as separator_when formatCompact called_then return abbreviated value rounded to 2 decimal digits`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = "1516000000000".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1,51T", result)
    }


    @Test
    fun `Given a BigDecimal value 1516123000000000_when formatCompact called_then return abbreviated value rounded to 2 decimal digits`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "1516123000000000".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1516.12T", result)
    }

    @Test
    fun `Given a BigDecimal value 1516123000000000 and locale has comma as separator_when formatCompact called_then return abbreviated value rounded to 2 decimal digits`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = "1516123000000000".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("1516,12T", result)
    }

    @Test
    fun `Given a BigDecimal value 0 point 0000000000000011234_when formatCompact called_then return abbreviated value with ellipsis in middle`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.0000000000000011234".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("0.0...11234", result)
    }

    @Test
    fun `Given a BigDecimal value 0 point 0000000000000011234 and locale has comma as separator_when formatCompact called_then return abbreviated value with ellipsis in middle`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = "0.0000000000000011234".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("0,0...11234", result)
    }

    @Test
    fun `Given a BigDecimal value 0 point 01 formatCompact called_then return value as is`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.01".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("0.01", result)
    }

    @Test
    fun `Given a BigDecimal value 0 point 01 and locale has comma as separator_when formatCompact called_then return value with correct decimal separator`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = "0.01".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("0,01", result)
    }

    @Test
    fun `Given a BigDecimal value 0 point 00001 formatCompact called_then return value as is`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.00001".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("0.00001", result)
    }

    @Test
    fun `Given a BigDecimal value 0 point 00001 and locale has comma as separator_when formatCompact called_then return value with correct decimal separator`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = "0.00001".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("0,00001", result)
    }

    @Test
    fun `Given a BigDecimal value 0 point 000001 formatCompact called_then return value as is`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "1.0E-6".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("0.0...1", result)
    }


    @Test
    fun `Given a BigDecimal value 0 point 000001 and locale has comma as separator formatCompact called_then return value with correct decimal separator`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = "1.0E-6".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("0,0...1", result)
    }

    @Test
    fun `Given a BigDecimal value 0 point 632390881556703953 formatCompact called_then return value as is`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.632390881556703953".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("0.63239", result)
    }

    @Test
    fun `Given a BigDecimal value 0 point 632390881556703953 and locale has comma as separator formatCompact called_then return value as is`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = "0.632390881556703953".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2)

        assertEquals("0,63239", result)
    }

    // ===== NEW MODE TESTS: useScaleBasedCompactForSmallerThanOne = true =====

    @Test
    fun `Given small decimal with new mode_when digits equal decimalScale_then show as is`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.1".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 3, useScaleBasedCompactForSmallerThanOne = true)

        assertEquals("0.1", result)
    }

    @Test
    fun `Given small decimal with new mode_when digits equal decimalScale_then show as is with comma separator`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = "0.01".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 3, useScaleBasedCompactForSmallerThanOne = true)

        assertEquals("0,01", result)
    }

    @Test
    fun `Given small decimal with new mode_when digits equal decimalScale exactly_then show as is`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.001".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 3, useScaleBasedCompactForSmallerThanOne = true)

        assertEquals("0.001", result)
    }

    @Test
    fun `Given small decimal with new mode_when at threshold length_then show as is since no space saved`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.1234567".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 3, useScaleBasedCompactForSmallerThanOne = true)

        assertEquals("0.1234567", result)
    }

    @Test
    fun `Given small decimal with new mode_when at threshold length_then show as is with comma separator`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = "0.1234567".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 3, useScaleBasedCompactForSmallerThanOne = true)

        assertEquals("0,1234567", result)
    }

    @Test
    fun `Given very small decimal with new mode_when decimalScale 2_then use ellipsis format`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.0000000001234".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2, useScaleBasedCompactForSmallerThanOne = true)

        assertEquals("0.0...34", result)
    }

    @Test
    fun `Given very small decimal with new mode_when decimalScale 2_then use ellipsis format with comma separator`() {
        Locale.setDefault(Locale("vi", "vn"))
        val bigDecimal = "0.0000000001234".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2, useScaleBasedCompactForSmallerThanOne = true)

        assertEquals("0,0...34", result)
    }

    @Test
    fun `Given decimal with many trailing zeros in new mode_when within threshold_then show as is`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.123000000".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2, useScaleBasedCompactForSmallerThanOne = true)

        assertEquals("0.123", result)
    }

    @Test
    fun `Given decimal with trailing zeros in new mode_when digits equal scale_then show as is`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.120000".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2, useScaleBasedCompactForSmallerThanOne = true)

        assertEquals("0.12", result)
    }

    @Test
    fun `Given 0 point 1234 with new mode and decimalScale 3_then show as is since not worth compressing`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.1234".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 3, useScaleBasedCompactForSmallerThanOne = true)

        assertEquals("0.1234", result)
    }

    @Test
    fun `Given 0 point 12345678 with new mode and decimalScale 3_then compress since longer than threshold`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.12345678".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 3, useScaleBasedCompactForSmallerThanOne = true)

        assertEquals("0.1...678", result)
    }

    // ===== EDGE CASE TESTS =====

    @Test
    fun `Given decimal with decimalScale 1 in new mode_when more digits_then show first digit and last 1 digit`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.123456".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 1, useScaleBasedCompactForSmallerThanOne = true)

        assertEquals("0.1...6", result)
    }

    @Test
    fun `Given decimal with decimalScale 4 in new mode_when more digits_then show first and last 4 digits`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.123456789".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 4, useScaleBasedCompactForSmallerThanOne = true)

        assertEquals("0.1...6789", result)
    }

    @Test
    fun `Given very small decimal original mode_when many leading zeros_then compress leading zeros`() {
        Locale.setDefault(Locale.US)
        val bigDecimal = "0.000000000001234567".toBigDecimal()
        val result = bigDecimal.formatCompact(decimalScale = 2, useScaleBasedCompactForSmallerThanOne = false)

        assertEquals("0.0...34567", result)
    }
}