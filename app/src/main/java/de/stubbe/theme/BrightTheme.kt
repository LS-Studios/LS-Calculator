package de.stubbe.theme

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import de.stubbe.R

@RequiresApi(Build.VERSION_CODES.M)
class BrightTheme(private val context: Context): MyAppTheme {
    override fun main1Color(): Int {
        return context.getColor(R.color.bright_main_1)
    }

    override fun main2Color(): Int {
        return context.getColor(R.color.bright_main_2)
    }

    override fun main3Color(): Int {
        return context.getColor(R.color.bright_main_3)
    }

    override fun main4Color(): Int {
        return context.getColor(R.color.bright_main_4)
    }

    override fun textColor(): Int {
        return context.getColor(R.color.bright_text)
    }

    override fun dividerColor(): Int {
        return context.getColor(R.color.bright_divider)
    }

    override fun id(): Int {
        return 0
    }
}

