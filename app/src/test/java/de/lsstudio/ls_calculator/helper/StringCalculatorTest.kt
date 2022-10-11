package de.lsstudio.ls_calculator.helper
import junit.framework.TestCase
import org.junit.Test
import java.lang.reflect.Method

class StringCalculatorTest : TestCase() {

    /** [StringCalculator.Operators] tests */
    @Test
    fun testOperatorPositionWithHighestPriority() {
        val method: Method = StringCalculator.Operators.Operator::class.java.getMethod("operatorPositionWithHighestPriority", MutableList::class.java)

        assertEquals(1, method.invoke(StringCalculator.Operators, mutableListOf("1", "+", "3")))
        assertEquals(3, method.invoke(StringCalculator.Operators, mutableListOf("2", "-", "3", "×", "5")))
        assertEquals(5, method.invoke(StringCalculator.Operators, mutableListOf("(", "7", "+", "4", ")", "÷", "2")))
        assertEquals(2, method.invoke(StringCalculator.Operators, mutableListOf("2", "^", "ln", "4")))
    }

    @Test
    fun testGetOperatorBySign() {
        val method: Method = StringCalculator.Operators.Operator::class.java.getMethod("getOperatorBySign", String::class.java)

        assertEquals(StringCalculator.Operators.Add, method.invoke(StringCalculator.Operators, "+"))
        assertEquals(StringCalculator.Operators.Multiply, method.invoke(StringCalculator.Operators, "×"))
        assertEquals(StringCalculator.Operators.Sine, method.invoke(StringCalculator.Operators, "sin"))
        assertEquals(StringCalculator.Operators.Tangent, method.invoke(StringCalculator.Operators, "tan"))
    }

    @Test
    fun testIsRightAndLeftOperator() {
        val method: Method = StringCalculator.Operators.Operator::class.java.getMethod("isRightAndLeftOperator", Char::class.java)

        assertEquals(true, method.invoke(StringCalculator.Operators, "-".first()))
        assertEquals(true, method.invoke(StringCalculator.Operators, "÷".first()))
        assertEquals(false, method.invoke(StringCalculator.Operators, "ln".first()))
        assertEquals(false, method.invoke(StringCalculator.Operators, "acos".first()))
    }


    /** [StringCalculator.Variables] tests */
    @Test
    fun testIsVariable() {
        val method: Method = StringCalculator.Variables.Variable::class.java.getMethod("isVariable", String::class.java)

        assertEquals(true, method.invoke(StringCalculator.Variables, "e"))
        assertEquals(false, method.invoke(StringCalculator.Variables, "2"))
    }

    @Test
    fun testGetVariableValue() {
        val method: Method = StringCalculator.Variables.Variable::class.java.getMethod("getVariableValue", String::class.java)

        assertEquals(3.141592, method.invoke(StringCalculator.Variables, "π"))
        assertEquals(2.718281, method.invoke(StringCalculator.Variables, "e"))
        assertEquals(0.0, method.invoke(StringCalculator.Variables, "7"))
    }


    /** [StringCalculator.Calculator] tests */
    @Test
    fun testCalculation() {
        val method: Method = StringCalculator.Calculator::class.java.getMethod("calculate", String::class.java)
        method.isAccessible = true

        assertEquals("3", method.invoke(StringCalculator, "1 + 2"))
        assertEquals("4", method.invoke(StringCalculator, "2 ^ 2"))
        assertEquals("9", method.invoke(StringCalculator, "(((9)))"))
        assertEquals("2", method.invoke(StringCalculator, "2!"))
        assertEquals("6", method.invoke(StringCalculator, "6 + ln(1)"))
        assertEquals("-4", method.invoke(StringCalculator, "((-6)+2)"))
        assertEquals("10", method.invoke(StringCalculator, "5 - (-5)"))
        assertEquals("12", method.invoke(StringCalculator, "(4 + 2) × 2"))
        assertEquals("8", method.invoke(StringCalculator, "8+ln(14-13)"))
        assertEquals("-63", method.invoke(StringCalculator, "(10 - 12 × (8 ÷ 2) - 30) + 5"))
    }

    @Test
    fun testCalculateValues() {
        val privateMethod: Method = StringCalculator.Calculator::class.java.getDeclaredMethod("calculateValues", MutableList::class.java, Int::class.java)
        privateMethod.isAccessible = true

        assertEquals(3.0, privateMethod.invoke(StringCalculator, mutableListOf("1", "+", "2"), 1))
        assertEquals(4.0, privateMethod.invoke(StringCalculator, mutableListOf("10", "-", "6"), 1))
        assertEquals(16.0, privateMethod.invoke(StringCalculator, mutableListOf("4", "×", "4"), 1))
        assertEquals(2.0, privateMethod.invoke(StringCalculator, mutableListOf("8", "÷", "4"), 1))
    }

    @Test
    fun testSplitStringCalculation() {
        val method: Method = StringCalculator.Calculator::class.java.getMethod("splitStringCalculation", String::class.java)

        assertEquals(mutableListOf("-45"), method.invoke(StringCalculator, "(-45)"))
        assertEquals(mutableListOf("2", "+", "3"), method.invoke(StringCalculator, "2+3"))
        assertEquals(mutableListOf("(", "2", "-", "3", ")", "×", "6"), method.invoke(
            StringCalculator, "(2-3)×6"))
        assertEquals(mutableListOf("2", "-", "-3", "-", "(", "2", "×", "4"), method.invoke(
            StringCalculator, "2-(-3)-(2×4)"))
    }

    @Test
    fun testGetCalculationInsideBracketsWithString() {
        val privateMethod: Method = StringCalculator.Calculator::class.java.getDeclaredMethod("getCalculationInsideBrackets", String::class.java, Int::class.java)
        privateMethod.isAccessible = true

        assertEquals("4-2", privateMethod.invoke(StringCalculator, "(4-2)×8", 1))
        assertEquals("4-2", privateMethod.invoke(StringCalculator, "(4-2)×8", 2))
        assertEquals("2×4+6", privateMethod.invoke(StringCalculator, "8-(-9)-(2×4+6)", 8))
        assertEquals("2×4+6", privateMethod.invoke(StringCalculator, "8-(-9)-(2×4+6)", 10))
    }

    @Test
    fun testGetCalculationInsideBracketsWithList() {
        val privateMethod: Method = StringCalculator.Calculator::class.java.getDeclaredMethod("getCalculationInsideBrackets", MutableList::class.java, Int::class.java)
        privateMethod.isAccessible = true

        assertEquals(mutableListOf("4", "-", "2"), privateMethod.invoke(StringCalculator, mutableListOf("(", "4", "-", "2", ")", "×", "8"), 1))
        assertEquals(mutableListOf("4", "-", "2"), privateMethod.invoke(StringCalculator, mutableListOf("(", "4", "-", "2", ")", "×", "8"), 2))
        assertEquals(mutableListOf("2", "×", "4", "+", "6"), privateMethod.invoke(StringCalculator, mutableListOf("8", "-", "-9", "-", "(", "2", "×", "4", "+", "6", ")"), 5))
        assertEquals(mutableListOf("2", "×", "4", "+", "6"), privateMethod.invoke(StringCalculator, mutableListOf("8", "-", "-9", "-", "(", "2", "×", "4", "+", "6", ")"), 7))
    }

    @Test
    fun testGetNumber() {
        val method: Method = StringCalculator.Calculator::class.java.getMethod("getNumber", String::class.java, Int::class.java)
        method.isAccessible = true

        assertEquals("245", method.invoke(StringCalculator, "245+2", 1))
        assertEquals("55", method.invoke(StringCalculator, "2+55+3", 2))
        assertEquals("444", method.invoke(StringCalculator, "84×3+(444-2)", 6))
        assertEquals("32", method.invoke(StringCalculator, "(-32)-4", 2))
    }

    @Test
    fun testGetOperator() {
        val privateMethod: Method = StringCalculator.Calculator::class.java.getDeclaredMethod("getOperator", String::class.java, Int::class.java)
        privateMethod.isAccessible = true

        assertEquals("+", privateMethod.invoke(StringCalculator, "4+2", 1))
        assertEquals("-", privateMethod.invoke(StringCalculator, "7×8-1", 3))
        assertEquals("×", privateMethod.invoke(StringCalculator, "84×(3+200)-2", 2))
        assertEquals("ln", privateMethod.invoke(StringCalculator, "2+ln2", 3))
    }

    @Test
    fun testGetVariable() {
        val method: Method = StringCalculator.Calculator::class.java.getMethod("getVariable", String::class.java, Int::class.java)

        assertEquals("e", method.invoke(StringCalculator, "2e+1", 1))
        assertEquals("π", method.invoke(StringCalculator, "2π", 1))
    }

    @Test
    fun testGetListOfLettersNextToEachOther() {
        val privateMethod: Method = StringCalculator.Calculator::class.java.getDeclaredMethod("getListOfLettersNextToEachOther", String::class.java, Int::class.java)
        privateMethod.isAccessible = true

        assertEquals(Pair("e-ln", 1), privateMethod.invoke(StringCalculator, "2+3e-ln2", 4))
        assertEquals(Pair("-sin", 0), privateMethod.invoke(StringCalculator, "4-sin8", 1))
    }

    @Test
    fun testIsAnyOperator() {
        val method: Method = StringCalculator.Calculator::class.java.getMethod("isAnyOperator", String::class.java, Int::class.java)

        assertEquals(true, method.invoke(StringCalculator, "×", 0))
        assertEquals(true, method.invoke(StringCalculator, "ln", 0))
        assertEquals(false, method.invoke(StringCalculator, "7", 0))
        assertEquals(false, method.invoke(StringCalculator, "a", 0))
    }

    @Test
    fun testReplaceElements() {
        val privateMethod: Method = StringCalculator.Calculator::class.java.getDeclaredMethod("replaceElements", MutableList::class.java, IntRange::class.java, Any::class.java)
        privateMethod.isAccessible = true

        assertEquals(mutableListOf("25", "3"), privateMethod.invoke(StringCalculator, mutableListOf("1", "2", "3"), 0..1, "25"))
        assertEquals(mutableListOf("8"), privateMethod.invoke(StringCalculator, mutableListOf("1", "2", "3"), 0..2, "8"))
        assertEquals(mutableListOf("2", "÷", "3"), privateMethod.invoke(StringCalculator, mutableListOf("(", "2", "+", "5", ")", "÷", "3"), 0..4, "2"))
        assertEquals(mutableListOf("1"), privateMethod.invoke(StringCalculator, mutableListOf("14647"), 0..0, "1"))
    }
}