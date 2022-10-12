package de.lsstudio.ls_calculator.helper

import java.lang.Exception
import kotlin.math.*
import de.lsstudio.ls_calculator.helper.StringCalculator.Functions.Companion.isAnyFunction
import de.lsstudio.ls_calculator.helper.StringCalculator.Functions.Companion.getFunction
import de.lsstudio.ls_calculator.helper.StringCalculator.Functions.Companion.getFunctionBySign
import de.lsstudio.ls_calculator.helper.StringCalculator.Operators.Companion.getOperator
import de.lsstudio.ls_calculator.helper.StringCalculator.Operators.Companion.getOperatorBySign
import de.lsstudio.ls_calculator.helper.StringCalculator.Operators.Companion.isAnyOperator
import de.lsstudio.ls_calculator.helper.StringCalculator.Operators.Companion.isRightAndLeftOperator
import de.lsstudio.ls_calculator.helper.StringCalculator.Variables.Companion.getVariable
import de.lsstudio.ls_calculator.helper.StringCalculator.Variables.Companion.isAnyVariable

/**
 * This is a calculator that allows you to pass a [String] as input.
 *
 * To use it, call the [calculate] function and pass the [String] as a parameter.
 *
 * In case you want to debug what's happening, call the calculate function with the debug boolean set to true
 *
 * - Features:
 *      - You can add custom operators statically
 *      - You can add custom variables statically as well as dynamically
 *      - You can add custom functions statically as well dynamically
 *      - Brackets are supported
 *      - An icon for the app is included
 *      - Landscape as well as portrait mode is supported
 *      - When in portrait mode to access the scientific calculator features
 *        pull the white bar above the numerical buttons down
 *      - When in landscape mode the scientific features will be located on the left side
 *      - A save function is included which allows saving the result as a variable "Ans"
 *      - You can view the local history by clicking on the button at the top right corner
 *      - Invalid letters get removed
 *      - All methods are unit tested in [StringCalculatorTests]
 */
class StringCalculator(newAdditionalVariables: List<Variable> = mutableListOf(), newAdditionalFunctions: List<Function> = mutableListOf()) {

    /** Enum with valid operators
     *
     * You can also add custom operators by define the sign and the calculation
     *
     */

    enum class Operators {
        //You can not create own operators which contain any number or bracket

        //Left and right operators
        Add("+", Double::plus, 0),
        Subtract("-", Double::minus, 0),
        Multiply("×", Double::times, 1),
        Divide("÷", Double::div, 1),
        Power("^", Double::pow, 2),

        //Left operators
        Faculty("!", { a ->
            var factorial: Long = 1
            for (i in a.toLong() downTo 2) {
                factorial *= i
            }
            factorial.toDouble()
        }, CalculationTyp.Left, 4),

        //Right operators
        SquareRoot(
            "√", { a -> sqrt(a) },
            CalculationTyp.Right, 3
        ),
        LogarithmBaseE(
            "ln", { a -> ln(a) },
            CalculationTyp.Right, 3
        ),
        LogarithmBase10(
            "log", { a -> log10(a) },
            CalculationTyp.Right, 3
        ),
        Sine(
            "sin", { a -> sin(a) },
            CalculationTyp.Right, 3
        ),
        ArcSine(
            "asin", { a -> asin(a) },
            CalculationTyp.Right, 3
        ),
        Cosine(
            "cos", { a -> cos(a) },
            CalculationTyp.Right, 3
        ),
        ArcCosine(
            "acos", { a -> acos(a) },
            CalculationTyp.Right, 3
        ),
        Tangent(
            "tan", { a -> tan(a) },
            CalculationTyp.Right, 3
        ),
        ArcTangent(
            "atan", { a -> atan(a) },
            CalculationTyp.Right, 3
        );

        //Input sign
        var sign: String = ""

        //Functions to calculate with two number operation
        var calculationTwoNumber: ((Double, Double) -> Double) = { num1: Double, num2: Double -> num1 + num2 }

        //Functions to calculate with one number operation
        var calculationOneNumber: ((Double) -> Double) = { num: Double -> num }

        //Priority of the operator, 0 is the lowest
        var priority = 0

        //Enum which defines where the number to handle is positioned
        enum class CalculationTyp {
            Left,
            LeftAndRight,
            Right;
        }

        var calculationTyp = CalculationTyp.LeftAndRight

        constructor(sign: String, calculation: (Double, Double) -> Double, priority: Int) {
            this.sign = sign
            this.calculationTwoNumber = calculation
            this.priority = priority
            this.calculationTyp = CalculationTyp.LeftAndRight
        }

        constructor(
            sign: String,
            calculationOneNumber: (Double) -> Double,
            calculationTyp: CalculationTyp,
            priority: Int
        ) {
            this.sign = sign
            this.calculationOneNumber = calculationOneNumber
            this.priority = priority
            this.calculationTyp = calculationTyp
        }

        companion object {
            /**Check if any valid operator is contained in [String] */
            fun isAnyOperator(parentString: String, posInParent: Int): Boolean {
                return getOperatorBySign(getOperator(parentString, posInParent)) != null
            }

            /**Returns an operator based on one index contained in it */
            fun getOperator(string: String, index: Int): String {
                val letters = getListOfLettersNextToEachOther(string, index, true)

                val possibleOperators = mutableListOf<String>()
                var operator = ""

                if (letters.first.isNotEmpty()) {
                    if (isRightAndLeftOperator(letters.first[letters.second])) {
                        operator = letters.first[letters.second].toString()
                    } else {
                        //Add operators that containing the char at the given index
                        for (s in getAllOperatorSigns()) {
                            if (s.contains(letters.first[letters.second])) {
                                possibleOperators.add(s)
                            }
                        }
                        //Loop though possible operators and find the corresponding
                        for (s in possibleOperators) {
                            if (letters.first == s) {
                                operator = s
                                break
                            }
                        }
                    }
                }

                return operator
            }

            /**Loops through the operators and return the operator that is containing the given sign */
            fun getOperatorBySign(sign: String): Operators? {
                for (operation in values()) {
                    if (operation.sign == sign) {
                        return operation
                    }
                }
                return null
            }

            /**Returns all signs of operators  */
            fun getAllOperatorSigns(): MutableList<String> {
                val list = mutableListOf<String>()

                for (operator in values()) {
                    list.add(operator.sign)
                }
                return list
            }

            /**Check if the given string is a operator which takes a variable on the left as well as on the right side */
            fun isRightAndLeftOperator(char: Char): Boolean {
                for (operator in values()) {
                    if (char == operator.sign.first() && operator.calculationTyp == CalculationTyp.LeftAndRight) {
                        return true
                    }
                }
                return false
            }
        }
    }

    /** Enum with valid functions
     *
     * You can also add custom functions by define the sign, parameters and the calculation
     *
     */

    enum class Functions {
        PQ(Function("pq", arrayOf("p", "q"), "-p÷2+√(p^2÷2-q)"));

        //Input sign
        var function: Function

        constructor(function: Function) {
            this.function = function
        }

        companion object {
            /**Loops through the operators and return the operator that is containing the given sign */
            fun getFunctionBySign(sign: String): Function? {
                for (function in values()) {
                    if (function.function.sign == sign) {
                        return function.function
                    }
                }
                for (function in additionalFunctions) {
                    if (function.sign == sign) {
                        return function
                    }
                }

                return null
            }

            /**Check if any valid functionn is contained in [String] */
            fun isAnyFunction(parentString: String, posInParent: Int): Boolean {
                return getFunctionBySign(getFunction(parentString, posInParent)) != null
            }

            /**Returns an operator based on one index contained in it */
            fun getFunction(string: String, index: Int): String {
                val letters = getListOfLettersNextToEachOther(string, index)

                val possibleFunctions = mutableListOf<String>()
                var function = ""

                if (letters.first.isNotEmpty()) {
                    //Add functions that containing the char at the given index
                    for (s in getAllFunctionSigns()) {
                        if (s.contains(letters.first[letters.second])) {
                            possibleFunctions.add(s)
                        }
                    }
                    //Loop though possible functions and find the corresponding
                    for (s in possibleFunctions) {
                        if (letters.first == s) {
                            function = s
                            break
                        }
                    }
                }

                return function
            }

            /**Returns all signs of operators  */
            fun getAllFunctionSigns(): MutableList<String> {
                val list = mutableListOf<String>()

                for (function in additionalFunctions) {
                    list.add(function.sign)
                }
                for (function in values()) {
                    list.add(function.function.sign)
                }

                return list
            }
        }
    }

    /** Class for custom functions */

    data class Function(val sign: String, val parameters: Array<String>, val calculation: String)

    /** Enum with valid variables
     *
     * You can also add custom variables by define the sign and the related value
     */

    enum class Variables {
        Pi(Variable("π", 3.141592)),
        Euler(Variable("e", 2.718281)),
        Percent(Variable("%", 0.01)),
        Ans(Variable("Ans", 0.0));

        var variable: Variable

        constructor(variable: Variable) {
            this.variable = variable
        }

        companion object {
            /**Check if the given char is a variable */
            fun isVariable(string: String): Boolean {
                for (variable in values()) {
                    if (variable.variable.sign == string) {
                        return true
                    }
                }
                for (variable in additionalVariables) {
                    if (variable.sign == string) {
                        return true
                    }
                }

                return false
            }

            /**Check if any valid variable is contained in [String] */
            fun isAnyVariable(parentString: String, posInParent: Int): Boolean {
                return getVariableBySign(getVariable(parentString, posInParent)) != null
            }

            /**Returns a variable based on one index contained in it */
            fun getVariable(string: String, index: Int): String {
                val letters = getListOfLettersNextToEachOther(string, index)

                val possibleVariables = mutableListOf<String>()
                var variable = ""

                if (letters.first.isNotEmpty()) {
                    if (Variables.isVariable(letters.first[letters.second].toString())) {
                        variable = letters.first[letters.second].toString()
                    } else {
                        //Add variables that containing the char at the given index
                        for (s in Variables.getAllVariableSigns()) {
                            if (s.contains(letters.first[letters.second])) {
                                possibleVariables.add(s)
                            }
                        }
                        //Loop though possible variables and find the corresponding
                        for (s in possibleVariables) {
                            if (letters.first == s) {
                                variable = s
                                break
                            }
                        }
                    }
                }

                return variable
            }

            /**Loops through the values and return the value that is containing the given sign */
            fun getVariableBySign(sign: String): Double? {
                for (variable in values()) {
                    if (variable.variable.sign == sign) {
                        return variable.variable.value
                    }
                }
                for (variable in additionalVariables) {
                    if (variable.sign == sign) {
                        return variable.value
                    }
                }

                return null
            }

            /**Return a list of all variables signs */
            fun getAllVariableSigns(): MutableList<String> {
                val variableSignList = mutableListOf<String>()

                for (variable in values()) {
                    variableSignList.add(variable.variable.sign)
                }
                for (variable in additionalVariables) {
                    variableSignList.add(variable.sign)
                }

                return variableSignList
            }
        }
    }

    /** Class for custom variables */

    data class Variable(val sign: String, var value: Double)

    companion object {
        private var additionalVariables = mutableListOf<Variable>()
        private var additionalFunctions = mutableListOf<Function>()

        /**Returns an [String] of letters which are around the given index next to each other */
        private fun getListOfLettersNextToEachOther(string: String, index: Int, withNonLetterOperators: Boolean = false): Pair<String, Int> {
            var leftLetters = ""
            var rightLetters = ""

            var letters = ""
            var fixedIndex = index

            //Only check when the character at the index is no bracket or number
            if (!string[index].toString().matches(Regex("[()0-9.]")) && (withNonLetterOperators ||
                        Operators.getAllOperatorSigns().find { sign -> !sign.matches(Regex("^[a-zA-Z]*\$")) && sign.first() == string[index] } == null)) {

                //Loop down until a bracket or number is found
                for (char in index downTo 0) {
                    if (!string[char].toString().matches(Regex("[()0-9.]")) && (withNonLetterOperators ||
                                Operators.getAllOperatorSigns().find { sign -> !sign.matches(Regex("^[a-zA-Z]*\$")) && sign.first() == string[char] } == null))
                        leftLetters += string[char]
                    else {
                        fixedIndex -= char + 1
                        break
                    }
                }
                leftLetters = leftLetters.reversed()

                //Loop up until a bracket or number is found
                for (char in index + 1 until string.length) {
                    if (!string[char].toString().matches(Regex("[()\\d.]")) && (withNonLetterOperators ||
                                Operators.getAllOperatorSigns().find { sign -> !sign.matches(Regex("^[a-zA-Z]*\$")) && sign.first() == string[char] } == null))
                        rightLetters += string[char]
                    else
                        break
                }

                letters = (leftLetters + rightLetters)
            }

            return Pair(letters, fixedIndex)
        }

        /**Returns a range of the connected letters at the given index next to each other */
        fun getRangeOfLettersNextToEachOther(string: String, index: Int): IntRange {
            var startIndex = index+1
            var endIndex = index

            //Only check when the character at the index is no bracket or number
            if (!string[index].toString().matches(Regex("[()0-9.]")) &&
                Operators.getAllOperatorSigns().find { sign -> !sign.matches(Regex("^[a-zA-Z]*\$")) && sign.first() == string[index] } == null) {

                //Loop down until a bracket or number is found
                for (char in index downTo 0) {
                    if (!string[char].toString().matches(Regex("[()0-9.]")) &&
                        Operators.getAllOperatorSigns().find { sign -> !sign.matches(Regex("^[a-zA-Z]*\$")) && sign.first() == string[char] } == null)
                        startIndex--
                    else
                        break
                }

                //Loop up until a bracket or number is found
                for (char in index + 1 until string.length) {
                    if (!string[char].toString().matches(Regex("[()0-9.]")) &&
                        Operators.getAllOperatorSigns().find { sign -> !sign.matches(Regex("^[a-zA-Z]*\$")) && sign.first() == string[char] } == null)
                        endIndex++
                    else
                        break
                }
            }

            return IntRange(startIndex, endIndex)
        }
    }

    private var debug = false
    var errorMessage = "Math Error!"

    init {
        additionalVariables = newAdditionalVariables.toMutableList()
        additionalFunctions = newAdditionalFunctions.toMutableList()
    }

    /**Main functions to calculate */
    fun calculate(calculationString: String): String {
        try {
            //Remove any spaces
            val fixedString: String = calculationString.replace(Regex("\\s"), "")

            //Return 0.0 if the calculation is empty
            if (fixedString.isEmpty())
                return ""

            //List that holds the split calculation
            val calculationCharList = mutableListOf<String>()

            //Add characters to list separated by operators
            calculationCharList.addAll(splitStringCalculation(fixedString))

            debug("Splitlist", mutableListOf(Pair("List", calculationCharList.toString())))

            //Check if there is a valid calculation
            if (calculationCharList.size != 1) {
                //Calculate if there is a valid calculation

                //Return number without dot if there is no other value behind the dot than 0
                var result = calculateSplitList(calculationCharList).toString()
                var resultWithoutDot = ""

                var reachedDot = false
                var isNull = true

                for (char in result) {
                    if (reachedDot && char != '0') {
                        isNull = false
                    }

                    if (char == '.') {
                        reachedDot = true
                    }

                    if (!reachedDot) {
                        resultWithoutDot += char
                    }
                }

                if (isNull) {
                    result = resultWithoutDot
                }

                if (result == "NaN")
                    result = errorMessage

                return result
            } else {
                //Return the input
                return calculationCharList[0]
            }
        } catch (e: Exception) {
            if (debug)
                e.printStackTrace()
            debug(errorMessage, mutableListOf(Pair("Error", e.toString())))

            return errorMessage
        }
    }

    fun calculate(calculationString: String, debug: Boolean): String {
        this.debug = debug

        return calculate(calculationString)
    }

    /**Calculates step by step the calculation stored in a list with help of recursion */
    private fun calculateSplitList(list: MutableList<String>): Double {
        var result: Double

        //Represents the list with remaining calculations
        val listToEdit: MutableList<String> = list.toMutableList()

        //Calculate calculations in brackets
        while (listToEdit.contains("(")) {
            val index = listToEdit.indexOf("(")

            //Get the calculation of the first bracket
            val bracketCalculation = getCalculationInsideBrackets(listToEdit, index)

            //Calculate the bracket calculation
            val bracketResult = calculateSplitList(bracketCalculation)

            //Replace the bracket with the result
            replaceElements(listToEdit, index..(index + bracketCalculation.size + 1), bracketResult.toString())
        }

        //Get the index of the first operator which has the highest priority
        val index = operatorPositionWithHighestPriorityOrFunction(listToEdit)

        //Do the calculation in case the list is not replaced with bracket result
        result = if (listToEdit.size > 1)
            calculateValues(listToEdit, index)

        //Set the result to the first number of list in case the list was replaced with bracket result
        else
            listToEdit[0].toDouble()

        //Check if there are any other calculations left
        if (listToEdit.size > 1) calculateSplitList(listToEdit).also { result = it }

        return result
    }

    /**Calculates the given numbers by an operator or function */
    private fun calculateValues(list: MutableList<String>, indexOfOperatorOrFunction: Int): Double {
        var result = 0.0

        //Get the calculation from the operation and calculate the result
        val operation = getOperatorBySign(list[indexOfOperatorOrFunction])
        val function = getFunctionBySign(list[indexOfOperatorOrFunction])

        if (operation != null) {
            debug(listOf(Pair("Calculation type", "Operation")))

            when (operation.calculationTyp) {
                Operators.CalculationTyp.LeftAndRight -> {
                    //Check if input is valid left right operation and if not return the number
                    if (indexOfOperatorOrFunction - 1 == -1 || getNumber(
                            list[indexOfOperatorOrFunction - 1],
                            0
                        ) == ""
                    ) {
                        if (list[indexOfOperatorOrFunction] == "-")
                            result =
                                (list[indexOfOperatorOrFunction] + list[indexOfOperatorOrFunction + 1]).toDouble()
                        else if (isRightAndLeftOperator(list[indexOfOperatorOrFunction].first()))
                            result = list[indexOfOperatorOrFunction + 1].toDouble()

                        //Replace
                        replaceElements(
                            list,
                            (indexOfOperatorOrFunction)..(indexOfOperatorOrFunction + 1),
                            result.toString()
                        )

                        return result
                    }

                    val value1 = list[indexOfOperatorOrFunction - 1].toDouble()
                    val value2 = list[indexOfOperatorOrFunction + 1].toDouble()

                    //Calculates the result with left and right number by operator
                    result = operation.calculationTwoNumber(value1, value2)

                    debug(
                        "Calculation", listOf(
                            Pair("Typ", operation.name), Pair("Expression", "$value1 ${operation.sign} $value2"),
                            Pair("Sub result", "$result")
                        )
                    )

                    //Replace the calculated calculation with the result
                    replaceElements(
                        list,
                        (indexOfOperatorOrFunction - 1)..(indexOfOperatorOrFunction + 1),
                        result.toString()
                    )
                }

                Operators.CalculationTyp.Left -> {
                    val value: Double

                    //Try assign the value and return an error if no valid value typed in
                    try {
                        value = list[indexOfOperatorOrFunction - 1].toDouble()
                    } catch (e: Exception) {
                        error("No valid input! A value is missing.")
                    }

                    //Calculates the result with right number by operator
                    result = operation.calculationOneNumber(value)

                    debug(
                        "Calculation", listOf(
                            Pair("Typ", operation.name), Pair("Expression", "${operation.sign} $value"),
                            Pair("Sub result", "$result")
                        )
                    )

                    //Replace the calculated calculation with the result
                    replaceElements(
                        list,
                        (indexOfOperatorOrFunction - 1)..(indexOfOperatorOrFunction),
                        result.toString()
                    )
                }

                Operators.CalculationTyp.Right -> {
                    val value: Double

                    //Try assign the value and return an error if no valid value typed in
                    try {
                        value = list[indexOfOperatorOrFunction + 1].toDouble()
                    } catch (e: Exception) {
                        error("No valid input! A value is missing.")
                    }

                    //Calculates the result with right number by operator
                    result = operation.calculationOneNumber(value)

                    debug(
                        "Calculation", listOf(
                            Pair("Typ", operation.name), Pair("Expression", "${operation.sign} $value"),
                            Pair("Sub result", "$result")
                        )
                    )

                    //Replace the calculated calculation with the result
                    replaceElements(
                        list,
                        (indexOfOperatorOrFunction)..(indexOfOperatorOrFunction + 1),
                        result.toString()
                    )
                }
            }
        } else if (function != null) {
            debug(listOf(Pair("Calculation type", "Functions")))

            val inputParameters = getFunctionParametersFromBody(list[indexOfOperatorOrFunction + 1], 0, function)

            //Calculates the result with the given parameter
            var calculation = function.calculation

            val functionParameters = function.parameters

            var i = 0

            while (i < calculation.length) {
                if (isAnyOperator(calculation, i)) {
                    i += getOperator(calculation, i).length
                }
                if (isAnyFunction(calculation, i)) {
                    i += getFunction(calculation, i).length
                }
                if (isAnyVariable(calculation, i)) {
                    i += getVariable(calculation, i).length
                }

                for (parameter in functionParameters) {
                    val foundParameter = getParameter(calculation, i, functionParameters)
                    if (parameter == foundParameter) {
                        calculation = calculation.replaceRange(
                            i,
                            i + foundParameter.length,
                            inputParameters[functionParameters.indexOf(parameter)]
                        )
                    }
                }

                i++
            }

            debug(
                "Functions calculation replacement", listOf(
                    Pair("Calculation without parameter values", function.calculation),
                    Pair("Calculation with parameter values", calculation)
                )
            )

            result = calculate(calculation).toDouble()

            debug(
                "Calculation", listOf(
                    Pair("Expression", "${function.sign} ${list[indexOfOperatorOrFunction + 1]}"),
                    Pair("Sub result", "$result")
                )
            )

            //Replace the calculated calculation with the result
            replaceElements(
                list,
                (indexOfOperatorOrFunction)..(indexOfOperatorOrFunction + 1),
                result.toString()
            )
        }

        result = String.format("%.10f", result).replace(",", ".").toDouble()

        return result
    }

    /**Split the given string to a list with negative as well as positive numbers, [Variables] and [Operators] */
    fun splitStringCalculation(string: String): MutableList<String> {
        val splitList: MutableList<String> = mutableListOf()
        var i = 0

        fun addOperator() {
            debug(mutableListOf(Pair("${string[i]}", "Operator")))

            val operator = getOperator(string, i)

            //Add multiplication if no operator is set for example {2 sin(2)}
            if (i - 1 >= 0 && (getOperatorBySign(operator)!!.calculationTyp == Operators.CalculationTyp.Right &&
                        !isAnyOperator(string, i - 1))
            )
                splitList.add(Operators.Multiply.sign)

            splitList.add(operator)

            i += operator.length - 1
        }

        fun addFunction() {
            debug(mutableListOf(Pair("${string[i]}", "Functions")))

            val function = getFunction(string, i)

            //Add multiplication if no operator is set for example {2 add(2, 2)}
            if (i - 1 >= 0 && !isAnyOperator(string, i - 1))
                splitList.add(Operators.Multiply.sign)

            splitList.add(function)

            i += function.length

            var functionParameter = ""

            if (string[i] == '(') {
                val functionObj = getFunctionBySign(function)!!
                val inputParameters = getFunctionParametersFromBody(string, i, functionObj)

                var parameterBody = inputParameters[0]
                for (index in 1..inputParameters.lastIndex) {
                    parameterBody += "," + inputParameters[index]
                }
                parameterBody = "($parameterBody)"

                //Calculate sub calculation values
                for (parameterIndex in inputParameters.indices) {
                    val value = calculate(inputParameters[parameterIndex])

                    functionParameter +=
                        if (parameterIndex != inputParameters.lastIndex) {
                            "$value,"
                        } else {
                            value
                        }
                }

                //Add calculated parameters to function sing
                functionParameter = "($functionParameter)"

                debug(mutableListOf(Pair("Calculated parameters", functionParameter)))

                splitList.add(functionParameter)

                i += parameterBody.length - 1
            } else {
                functionParameter += "(0"

                for (parameter in getFunctionBySign(function)!!.parameters) {
                    functionParameter += ",0"
                }

                functionParameter += ")"

                splitList.add(functionParameter)

                i += functionParameter.length - 1
            }
        }

        fun addBracket() {
            debug(mutableListOf(Pair("${string[i]}", "Bracket")))

            val bracketCalculation = getCalculationInsideBrackets(string, i)
            val signs = bracketCalculation.replace(Regex("[0-9]"), "")

            //Check if the bracket calculation is a negative number
            if (string.length > i + 1) {
                if (signs.length <= 1 && string[i + 1].toString().contains("-")) {
                    if (i - 1 >= 0 && (!isAnyOperator(string, i - 1) || string[i - 1].toString()
                            .matches(Regex("[()]")))
                    ) {
                        splitList.add(Operators.Multiply.sign)
                    }

                    splitList.add(bracketCalculation)
                }
                //Check if the bracket calculation is only one positive number
                else if (signs.isEmpty()) {
                    if (i - 1 >= 0 && (!isAnyOperator(string, i - 1) || string[i - 1].toString()
                            .matches(Regex("[()]")))
                    ) {
                        splitList.add(Operators.Multiply.sign)
                    }

                    splitList.add(bracketCalculation)
                }

                //Else add the separated bracket content
                else {
                    //Add multiplication if no operator is set
                    if (i - 1 >= 0 && (!isAnyOperator(string, i - 1) || string[i - 1].toString()
                            .matches(Regex("[()]")))
                    ) {
                        splitList.add(Operators.Multiply.sign)
                    }

                    splitList.addAll(mutableListOf("(", ")"))
                    splitList.addAll(splitList.size - 1, splitStringCalculation(bracketCalculation))
                }
            } else {
                //Add multiplication if no operator is set
                if (i - 1 >= 0 && (!isAnyOperator(string, i - 1) || string[i - 1].toString().matches(Regex("[()]")))) {
                    splitList.add(Operators.Add.sign)
                }

                splitList.add("0")
            }

            i += bracketCalculation.length + 1
        }

        fun addNumber() {
            debug(mutableListOf(Pair("${string[i]}", "Number")))

            //Add multiplication if no operator is set
            if (i - 1 >= 0 && (!isAnyOperator(string, i - 1) || string[i - 1].toString().matches(Regex("[()]")))) {
                splitList.add(Operators.Multiply.sign)
            }

            val h = getNumber(string, i)

            splitList.add(h)
            i += h.length - 1
        }

        fun addVariable() {
            debug(mutableListOf(Pair("${string[i]}", "Variable")))

            val variable = getVariable(string, i)

            //Add multiplication if no operator is set
            if (i - 1 >= 0 && (!isAnyOperator(string, i - 1) || string[i - 1].toString().matches(Regex("[()]")))) {
                splitList.add(Operators.Multiply.sign)
            }

            splitList.add(Variables.getVariableBySign(variable).toString())

            i += variable.length - 1
        }

        while (i < string.length) {
            //Char at i is an operator
            if (isAnyOperator(string, i)) {
                addOperator()
            }

            // Char at i is a function
            else if (isAnyFunction(string, i)) {
                addFunction()
            }

            //Char at i is a variable
            else if (isAnyVariable(string, i)) {
                addVariable()
            }

            //Char at i is a bracket
            else if (string[i].toString().matches(Regex("[()]"))) {
                addBracket()
            }

            //Char at i is a number
            else if (string[i].toString().matches(Regex("[0-9]"))) {
                addNumber()
            }

            i++
        }

        //Remove all operators and brackets if there is no value to it
        if (splitList.isNotEmpty()) {
            var charactersToRemove = 0
            for (index in splitList.lastIndex downTo 0) {
                if (splitList[index].contains(Regex("[0-9]")) ||
                    getOperatorBySign(
                        getOperator(
                            splitList[index],
                            0
                        )
                    )?.calculationTyp == Operators.CalculationTyp.Left
                ) {
                    if (charactersToRemove > 0) {
                        for (l in 1..charactersToRemove) splitList.removeLast()
                    }
                    break
                } else
                    charactersToRemove++

            }
        }

        //Turn - - to + and + - to  - and - + to - and + + to +
        var splitListString = splitList.joinToString(separator = "|").trim()
        splitListString = splitListString.replace("-|-", "+")
        splitListString = splitListString.replace("+|-", "-")
        splitListString = splitListString.replace("-|+", "-")

        return splitListString.split("|").map { it.trim() }.toMutableList()
    }

    /**Get the calculation inside a bracket with an [MutableList] of strings */
    fun getCalculationInsideBrackets(list: MutableList<String>, index: Int): MutableList<String> {
        val returnListLeft = mutableListOf<String>()
        val returnListRight = mutableListOf<String>()

        //Used to know when bracket ends and do not end earlier when other brackets contained in bracket
        var bracketAmount = 0

        //Loop down and add the bracket content to the left list
        for (char in index downTo 0) {
            if (list[char].contains(")"))
                bracketAmount++

            if (list[char].contains("(") && bracketAmount == 0) {
                break
            } else if (list[char].contains("(")) {
                bracketAmount--
            }

            returnListLeft.add(list[char])
        }
        returnListLeft.reverse()

        //Loop up and add the bracket content to the right list
        for (char in index + 1 until list.size) {
            if (list[char].contains("("))
                bracketAmount++

            if (list[char].contains(")") && bracketAmount == 0) {
                break
            } else if (list[char].contains(")")) {
                bracketAmount--
            }

            returnListRight.add(list[char])
        }

        return (returnListLeft + returnListRight).toMutableList()
    }

    /**Get the calculation inside a bracket with [String] */
    fun getCalculationInsideBrackets(string: String, index: Int): String {
        var returnStringLeft = ""
        var returnStringRight = ""

        //Used to know when bracket ends and do not end earlier when other brackets contained in bracket
        var bracketAmount = 0

        //Loop down and add the bracket content to the left string
        for (char in index downTo 0) {
            if (string[char].toString().contains(")"))
                bracketAmount++

            if (string[char].toString().contains("(") && bracketAmount == 0) {
                break
            } else if (string[char].toString().contains("(")) {
                bracketAmount--
            }

            returnStringLeft += string[char]
        }
        returnStringLeft = returnStringLeft.reversed()

        //Loop up and add the bracket content to the right string
        for (char in index + 1 until string.length) {
            if (string[char].toString().contains("("))
                bracketAmount++

            if (string[char].toString().contains(")") && bracketAmount == 0) {
                break
            } else if (string[char].toString().contains(")")) {
                bracketAmount--
            }

            returnStringRight += string[char]
        }

        return returnStringLeft + returnStringRight
    }

    /**Get the calculation range from a bracket with [String] */
    fun getCalculationRangeFromBrackets(string: String, index: Int): IntRange {
        var bracketStart = index
        var bracketEnd = index+1

        //Used to know when bracket ends and do not end earlier when other brackets contained in bracket
        var bracketAmount = 0

        //Loop down and add decrease start index value
        for (char in index downTo 0) {
            if (string[char].toString().contains(")"))
                bracketAmount++

            if (string[char].toString().contains("(") && bracketAmount == 0) {
                break
            } else if (string[char].toString().contains("(")) {
                bracketAmount--
            }

            bracketStart--
        }

        //Loop up and add increase the end index value
        for (char in index + 1 until string.length) {
            if (string[char].toString().contains("("))
                bracketAmount++

            if (string[char].toString().contains(")") && bracketAmount == 0) {
                break
            } else if (string[char].toString().contains(")")) {
                bracketAmount--
            }

            bracketEnd++
        }

        return IntRange(bracketStart, bracketEnd)
    }

    /**Returns a hole number based on one index contained in it */
    fun getNumber(string: String, index: Int): String {
        var numberLeft = ""
        var numberRight = ""

        //Loop down and add all characters that are a number to the left string
        for (char in index downTo 0) {
            if (string[char].toString().matches(Regex("[0-9.]")))
                numberLeft += string[char]
            else
                break
        }
        numberLeft = numberLeft.reversed()

        //Loop up and add all characters that are a number to the right string
        for (char in index + 1 until string.length) {
            if (string[char].toString().matches(Regex("[0-9.]")))
                numberRight += string[char]
            else
                break
        }
        return (numberLeft + numberRight).replace(Regex("\\s"), "")
    }

    /**Returns a list of parameters of a passed function and function body */
    private fun getFunctionParametersFromBody(
        string: String,
        startIndex: Int,
        function: Function
    ): MutableList<String> {
        val funParameters = function.parameters
        val inputParameters = mutableListOf<String>()

        var i = startIndex

        i++ //To avoid first bracket

        //Get parameters of function
        for (parameterIndex in funParameters.indices) {
            //Get parameter if not the last one ending by a comma
            if (parameterIndex != funParameters.lastIndex) {
                var parameter = ""
                var reachedParameterEnd = false
                var commasToIgnore = 0

                while (!reachedParameterEnd) {
                    if (isAnyFunction(string, i)) {
                        val funAtI = getFunctionBySign(getFunction(string, i))
                        commasToIgnore += funAtI!!.parameters.size - 1
                        parameter += funAtI.sign
                        i += funAtI.sign.length
                        continue
                    }

                    if (string[i] == ',') {
                        if (commasToIgnore == 0)
                            reachedParameterEnd = true
                        else {
                            commasToIgnore--
                            parameter += string[i]
                        }
                    } else {
                        parameter += string[i]
                    }

                    i++
                }

                inputParameters.add(parameter)
            }
            //Get last parameter ending by a bracket
            else {
                var parameter = ""
                var reachedParameterEnd = false
                var bracketsToIgnore = 0

                while (!reachedParameterEnd) {
                    if (isAnyFunction(string, i)) {
                        val funAtI = getFunctionBySign(getFunction(string, i))
                        bracketsToIgnore += 1
                        parameter += funAtI!!.sign
                        i += funAtI.sign.length
                        continue
                    }

                    if (string[i] == ')') {
                        if (bracketsToIgnore == 0)
                            reachedParameterEnd = true
                        else {
                            bracketsToIgnore--
                            parameter += string[i]
                        }
                    } else {
                        parameter += string[i]
                    }

                    i++
                }

                inputParameters.add(parameter)
            }
        }

        return inputParameters
    }

    /**Returns an variable based on one index contained in it */
    fun getParameter(string: String, index: Int, parameters: Array<String>): String {
        val letters = getListOfLettersNextToEachOther(string, index)

        val possibleParameters = mutableListOf<String>()
        var parameter = ""

        if (letters.first.isNotEmpty()) {
            //Check if single
            if (Variables.isVariable(letters.first[letters.second].toString())) {
                parameter = letters.first[letters.second].toString()
            } else {
                //Add parameters that containing the char at the given index
                for (s in parameters) {
                    if (s.contains(letters.first[letters.second])) {
                        possibleParameters.add(s)
                    }
                }
                //Loop though possible parameters and find the corresponding
                for (s in possibleParameters) {
                    if (letters.first.contains(s)) {
                        parameter = s
                        break
                    }
                }
            }
        }

        return parameter
    }

    /**Loops through the given list and find the operator with the highest priority or a function */
    fun operatorPositionWithHighestPriorityOrFunction(list: MutableList<String>): Int {
        //Start checking for functions
        for (index in list.indices) {
            //Check if char at index is an operator. Do check to be sure not take a negative number as operator
            if (isAnyFunction(list[index], if (list[index].length > 1) 1 else 0)) {
                val function = getFunctionBySign(list[index])

                //Check if the priority is higher and override old one
                if (function != null) {
                    return index
                }
            }
        }

        //If no functions look for the operator with the highest priority
        var operatorIndex = 0
        var currentHighestPriority = -1

        for (index in list.indices) {
            //Check if char at index is an operator. Do check to be sure not take a negative number as operator
            if (isAnyOperator(list[index], if (list[index].length > 1) 1 else 0)) {
                val operator = getOperatorBySign(list[index])

                //Check if the priority is higher and override old one
                if (operator?.priority!! > currentHighestPriority || currentHighestPriority == -1) {
                    currentHighestPriority = operator.priority
                    operatorIndex = index
                }
            }
        }
        return operatorIndex
    }

    /**Replaces a range of elements with one element of typ [E] */
    private fun <E> replaceElements(list: MutableList<E>, indexBounds: IntRange, element: E): MutableList<E> {
        val indexesToRemove: MutableList<Int> = indexBounds.toMutableList()
        val firstIndex = indexBounds.first

        //region For debug

        val oldList = list.toMutableList()
        if (oldList[oldList.size - 1].toString() == "0" && oldList[oldList.size - 2].toString() == "+") {
            for (i in 0..1) oldList.removeAt(oldList.size - 1)
        }

        //endregion

        //Remove the first index
        indexesToRemove.removeAt(0)

        //Removes all elements except for the first one
        for ((a, index) in indexesToRemove.withIndex()) {
            if (list.size > index - a) {
                list.removeAt(index - a)
            }
        }

        //Set the element to the first index
        list[firstIndex] = element

        //region For debug
        val newList = list.toMutableList()
        if (newList[newList.size - 1].toString() == "0" && newList[newList.size - 2].toString() == "+") {
            for (i in 0..1) newList.removeAt(newList.size - 1)
        }
        //endregion

        debug("Replace", listOf(Pair("Before", "$oldList"), Pair("After", "$newList")))
        return newList
    }

    /**Custom debug functions */
    private fun debug(title: String, content: List<Pair<String, String>>) {
        if (debug) {
            println("---------------$title---------------")
            debug(content)

            println()
        }
    }

    private fun debug(content: List<Pair<String, String>>) {
        if (debug) {
            for (c in content) {
                println("${c.first} -> ${c.second}")
            }

            println()
        }
    }
}

