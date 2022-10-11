package de.lsstudio.ls_calculator.classes

import de.lsstudio.ls_calculator.helper.StringCalculator

data class Custom(val name: String, val customType: CustomType, val variable: StringCalculator.Variable?, val function: StringCalculator.Function?)