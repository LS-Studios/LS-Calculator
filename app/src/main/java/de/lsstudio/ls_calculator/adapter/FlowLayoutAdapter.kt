package de.lsstudio.ls_calculator.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.core.view.allViews
import com.dolatkia.animatedThemeManager.ThemeManager
import com.nex3z.flowlayout.FlowLayout
import de.lsstudio.ls_calculator.R
import de.lsstudio.ls_calculator.theme.MyAppTheme

class FlowLayoutAdapter(private val context: Context, private val flowLayout: FlowLayout, private val brighter: Boolean = false) {

    fun addItem (name: String, onClick: (View) -> Unit = {}, onLongClick: (View) -> Unit = {}) {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_custom_row, null)

        val btn = view.findViewById<Button>(R.id.customBtn)

        val myTheme = ThemeManager.instance.getCurrentTheme() as MyAppTheme

        btn.setTextColor(myTheme.textColor())

        if (brighter)
            btn.backgroundTintList = ColorStateList.valueOf(myTheme.main1Color())
        else
            btn.backgroundTintList = ColorStateList.valueOf(myTheme.main2Color())

        btn.text = name

        btn.setOnClickListener {
            onClick(it)
        }

        btn.setOnLongClickListener {
            onLongClick(it)

            return@setOnLongClickListener true
        }

        flowLayout.addView(view)
    }

    fun removeItem (name: String) {
        for (view in flowLayout.allViews) {
            if (view is Button && view.text == name) {
                flowLayout.removeView(view)
                break
            }
        }
    }
}