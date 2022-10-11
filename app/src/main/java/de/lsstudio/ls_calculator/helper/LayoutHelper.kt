package de.lsstudio.ls_calculator.helper

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.dolatkia.animatedThemeManager.ThemeManager
import de.lsstudio.ls_calculator.R
import de.lsstudio.ls_calculator.theme.MyAppTheme

@RequiresApi(Build.VERSION_CODES.N)
class LayoutHelper {

    companion object {

        @RequiresApi(Build.VERSION_CODES.N)
        fun createAllerDialog(layout: View, setUp: (AlertDialog) -> Unit) {
            val alertBuilder = AlertDialog.Builder(layout.context)

            alertBuilder.setView(layout)

            val alertDialog = alertBuilder.create()

            setUp(alertDialog)

            //Set Background Transparent
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            alertDialog.show()
        }

        fun createYesNoDialog(context: Context, text: String, yesFunction: () -> Unit, noFunction: () -> Unit = {}) {
            val layout = LayoutInflater.from(context).inflate(R.layout.popup_yes_no, null)

            createAllerDialog(layout) { alertDialog ->

                val nameText = layout.findViewById<TextView>(R.id.popupQuestionNameText)
                val yesBtn = layout.findViewById<Button>(R.id.popupYesButton)
                val noBtn = layout.findViewById<Button>(R.id.popupNoButton)

                //Set color by theme
                val myAppTheme = ThemeManager.instance.getCurrentTheme() as MyAppTheme

                layout.findViewById<CardView>(R.id.popupYesNoCard).setCardBackgroundColor(myAppTheme.main1Color())
                layout.findViewById<TextView>(R.id.popupQuestionNameText).setTextColor(myAppTheme.textColor())
                yesBtn.backgroundTintList = ColorStateList.valueOf(myAppTheme.main2Color())
                yesBtn.setTextColor(myAppTheme.textColor())
                noBtn.backgroundTintList = ColorStateList.valueOf(myAppTheme.main2Color())
                noBtn.setTextColor(myAppTheme.textColor())

                nameText.text = text

                yesBtn.setOnClickListener {
                    yesFunction()

                    alertDialog.dismiss()
                }

                noBtn.setOnClickListener {
                    noFunction()

                    alertDialog.dismiss()
                }
            }
        }

    }

}