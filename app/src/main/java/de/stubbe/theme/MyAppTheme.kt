package de.stubbe.theme

import com.dolatkia.animatedThemeManager.AppTheme

interface MyAppTheme : AppTheme {
    fun main1Color(): Int
    fun main2Color(): Int
    fun main3Color(): Int
    fun main4Color(): Int
    fun textColor(): Int
    fun dividerColor(): Int
}