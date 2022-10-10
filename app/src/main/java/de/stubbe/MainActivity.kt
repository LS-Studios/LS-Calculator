package de.stubbe

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeActivity
import com.dolatkia.animatedThemeManager.ThemeManager
import de.stubbe.adapter.HistoryRecycleAdapter
import de.stubbe.databinding.ActivityMainBinding
import de.stubbe.helper.LayoutHelper
import de.stubbe.helper.StringCalculator
import de.stubbe.theme.BrightTheme
import de.stubbe.theme.DarkTheme
import de.stubbe.theme.MyAppTheme


@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : ThemeActivity() {

    lateinit var binding: ActivityMainBinding;

    var history = mutableListOf<Pair<Pair<String, String>, Double>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myTheme = ThemeManager.instance.getCurrentTheme() as MyAppTheme

        if (myTheme is BrightTheme)
            setTheme(R.style.Theme_MyTaschenrechner_Bright)
        else
            setTheme(R.style.Theme_MyTaschenrechner_Dark)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar: Toolbar = binding.toolBar
        toolBar.title = ""

        setSupportActionBar(toolBar)

        //Disable the soft keyboard when clicking on the calculation
        val calculationTextView: EditText = binding.calculationText
        calculationTextView.showSoftInputOnFocus = false
    }

    fun updateMenuItems(menu: Menu) {
        repeat(3) {
            val item = menu.getItem(it)
            val s = SpannableString(item.title)

            val myTheme = ThemeManager.instance.getCurrentTheme() as MyAppTheme

            s.setSpan(ForegroundColorSpan(myTheme.textColor()), 0, s.length, 0)
            item.title = s
        }

        val item = menu.getItem(3)

        val myTheme = ThemeManager.instance.getCurrentTheme() as MyAppTheme

        val text = if (myTheme is BrightTheme) {
            "Change to dark theme"
        } else
            "Change to bright theme"

        val s = SpannableString(text)

        s.setSpan(ForegroundColorSpan(myTheme.textColor()), 0, s.length, 0)
        item.title = s
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.option_menue, menu)

        updateMenuItems(menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.historyButton -> showHistory()
            R.id.themeButton -> {
                val myTheme = ThemeManager.instance.getCurrentTheme()

                if (myTheme is BrightTheme)
                    ThemeManager.instance.changeTheme(DarkTheme(this), binding.toolBar)
                else
                    ThemeManager.instance.changeTheme(BrightTheme(this), binding.toolBar)

                updateMenuItems(binding.toolBar.menu)

                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        recreate()
                    },
                    500
                )
            }
        }
        return true
    }

    override fun getStartTheme(): AppTheme {
        return DarkTheme(this)
    }

    override fun syncTheme(appTheme: AppTheme) {
        val myTheme = appTheme as MyAppTheme

        if (myTheme is BrightTheme)
            setTheme(R.style.Theme_MyTaschenrechner_Bright)
        else
            setTheme(R.style.Theme_MyTaschenrechner_Dark)

        val window = this.window
        window.statusBarColor = myTheme.main1Color()
        window.navigationBarColor = myTheme.main4Color()

        binding.rootLayout.background = ColorDrawable(myTheme.main3Color())
        binding.appBarLayout.background = ColorDrawable(myTheme.main1Color())
        binding.calculationText.setTextColor(myTheme.textColor())
        binding.resultText.setTextColor(myTheme.textColor())
        binding.dragBar?.background = ColorDrawable(myTheme.main2Color())

        binding.calculationAreaACBtn.setTextColor(getColor(R.color.dark_text))
        binding.calculationAreaACBtn.backgroundTintList =
            ColorStateList.valueOf(getColor(R.color.green))

        binding.calculationAreaCalculateBtn.setTextColor(getColor(R.color.dark_text))
        binding.calculationAreaCalculateBtn.backgroundTintList =
            ColorStateList.valueOf(getColor(R.color.blue))

        binding.calculationAreaCommaBtn.setTextColor(myTheme.textColor())
        binding.calculationAreaCommaBtn.backgroundTintList =
            ColorStateList.valueOf(myTheme.main2Color())

        binding.calculationAreaDELBtn.setTextColor(myTheme.textColor())
        binding.calculationAreaDELBtn.backgroundTintList =
            ColorStateList.valueOf(myTheme.main2Color())

        repeat(10) {
            val id = resources.getIdentifier("calculationAreaNumberBtn$it", "id", packageName)
            val btn = findViewById<View>(id) as Button

            btn.setTextColor(myTheme.textColor())
            btn.backgroundTintList =
                ColorStateList.valueOf(myTheme.main2Color())
        }

        repeat(14) {
            val id = resources.getIdentifier("funValBtn${it+1}", "id", packageName)
            val btn = findViewById<View>(id) as Button

            btn.setTextColor(myTheme.textColor())
            btn.backgroundTintList =
                ColorStateList.valueOf(myTheme.main2Color())
        }

        repeat(6) {
            val id = resources.getIdentifier("calculationAreaOperationBtn${it+1}", "id", packageName)
            val btn = findViewById<View>(id) as Button

            btn.setTextColor(getColor(R.color.dark_text))
            btn.backgroundTintList =
                ColorStateList.valueOf(getColor(R.color.blue))
        }
    }

    //Method to show an dialog view which contains the current history
    private fun showHistory() {
        val historyView = layoutInflater.inflate(R.layout.history_card_dialogue, null)

        LayoutHelper.createAllerDialog(historyView) { dialog ->
            val historyRecycler = historyView.findViewById<RecyclerView>(R.id.historyRecycler)

            val historyRecyclerAdapter = HistoryRecycleAdapter(history, dialog, findViewById<View?>(android.R.id.content).rootView)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
            historyRecycler.layoutManager = layoutManager
            historyRecycler.adapter = historyRecyclerAdapter

            val closeHistoryButton = historyView.findViewById<Button>(R.id.closeHistoryButton)
            closeHistoryButton.setOnClickListener {
                dialog.dismiss()
            }

            val deleteAllHistoryButton = historyView.findViewById<Button>(R.id.deleteAllHistoryButton)

            if (history.isEmpty()) {
                deleteAllHistoryButton.isEnabled = false
                deleteAllHistoryButton.alpha = 0.5f
            }


            deleteAllHistoryButton.setOnClickListener {
                history.clear()
                historyRecyclerAdapter.notifyDataSetChanged()

                deleteAllHistoryButton.isEnabled = false
                deleteAllHistoryButton.alpha = 0.5f

                dialog.dismiss()
            }
        }
    }

    //Method to scroll the scroll views to the right
    private fun scrollToRight() {
        val calculationTextView: EditText = findViewById(R.id.calculationText)

        val cursorPosition = calculationTextView.selectionStart

        val calculationTextScroll = binding.calculationTextScroll

        if (cursorPosition == calculationTextView.text.length) {
            calculationTextScroll.post(Runnable {
                calculationTextScroll.fullScroll(View.FOCUS_RIGHT)
            })

            val resultTextViewScroll = binding.resultTextScroll

            calculationTextScroll.post(Runnable {
                resultTextViewScroll.fullScroll(View.FOCUS_RIGHT)
            })
        }
    }

    //region Methods to calculate the result
    fun calculate(view: View) {
        val calculationTextView: EditText = binding.calculationText

        val calculationText: String = calculationTextView.text.toString()

        val resultTextView: TextView = binding.resultText

        val result = StringCalculator.calculate(calculationText)

        //Only show the result when the calculation is valid
        if (result != StringCalculator.errorMessage) {
            calculationTextView.setText(result)

            calculationTextView.setSelection(calculationTextView.text.lastIndex + 1)

            //Set Ans to the result if the result is not empty and add the calculation to the history
            if (result.isNotEmpty()) {
                history.add(
                    Pair(
                        Pair(calculationText, result),
                        StringCalculator.Variables.Ans.value
                    )
                )
                StringCalculator.Variables.Ans.value = result.toDouble()
            }

            scrollToRight()

            resultTextView.text = result
        }
    }

    private fun subCalculate(view: View) {
        val calculationTextView: EditText = binding.calculationText

        val calculationText: String = calculationTextView.text.toString()

        val resultTextView: TextView = binding.resultText

        val result = StringCalculator.calculate(calculationText)
        resultTextView.text = result

        //Color the color of the result red if its an invalid calculation
        if (result == StringCalculator.errorMessage)
            resultTextView.setTextColor(getColor(R.color.red))
        else {
            val myTheme = ThemeManager.instance.getCurrentTheme() as MyAppTheme
            resultTextView.setTextColor(myTheme.textColor())
        }
    }
    //endregion

    //region Methods to add Numbers, Dots, Variables, Ans and Brackets
    fun addSign(view: View) {
        val button: Button = view as Button

        addCharAtSelection(button.text.toString())

        scrollToRight()

        subCalculate(view)
    }

    fun addDot(view: View) {
        val calculationTextView: EditText = binding.calculationText

        var calculationText: String = calculationTextView.text.toString()

        val cursorPosition = calculationTextView.selectionStart

        //Add a dot when there is a number and no dot before the cursor position
        if (calculationText.isNotEmpty() && !StringCalculator.getNumber(calculationText, cursorPosition-1).contains(".") &&
            calculationText[cursorPosition-1].toString().matches(Regex("[0-9]")) &&
            !StringCalculator.isAnyOperator(calculationTextView.text.toString(), calculationTextView.text.lastIndex))
        {
            addCharAtSelection(".")
        }

        scrollToRight()
    }

    fun addAns(view: View) {
        addCharAtSelection("Ans")

        val calculationTextView: EditText = binding.calculationText

        val resultTextView: TextView = binding.resultText

        //Check if Ans is is only variable in the calculation
        if (calculationTextView.text.matches((Regex("Ans"))))
            resultTextView.text = StringCalculator.Variables.Ans.value.toString()
    }

    fun addBracket(view: View) {
        val calculationTextView: EditText = binding.calculationText

        var calculationText: String = calculationTextView.text.toString()

        val cursorPosition= calculationTextView.selectionStart

        //Returns the bracket to place based on the opened and closed ones in the calculation
        fun getBracketToPlace(): String {
            var bracketAmmount = 0

            for (char in calculationText) {
                if (char == "(".first()) {
                    bracketAmmount++
                } else if (char == ")".first()) {
                    bracketAmmount--
                }
            }

            return if (bracketAmmount > 0)
                ")"
            else
                "("
        }

        if (calculationText.isNotEmpty()) {
            if (cursorPosition== 0)
                addCharAtSelection("(")
            else if (calculationText[cursorPosition-1].toString().contains("("))
                addCharAtSelection("(")
            else
                addCharAtSelection(getBracketToPlace())
        }
        else {
            addCharAtSelection("(")
        }

        scrollToRight()
    }
    //endregion

    //region Methods to add operators
    fun addLeftAndRightOperator(view: View) {
        val calculationTextView: EditText = binding.calculationText

        val button: Button = view as Button

        var calculationText: String = calculationTextView.text.toString()

        //Delete last operator if there is one
        if (calculationText.isNotEmpty() && calculationTextView.selectionStart> 0) {
            if (calculationTextView.text[calculationTextView.selectionStart
                        - 1].toString().contains("(") && button.text == "-") {
                if (StringCalculator.Operators.isRightAndLeftOperator(calculationTextView.text[calculationTextView.selectionStart- 1])) {
                    deleteChar(view)
                }
                addCharAtSelection(button.text.toString())

                subCalculate(view)
                scrollToRight()
            }
            else if (!calculationTextView.text[calculationTextView.selectionStart
                        - 1].toString().contains("(")) {
                if (StringCalculator.Operators.isRightAndLeftOperator(calculationTextView.text[calculationTextView.selectionStart- 1])) {
                    deleteChar(view)
                }
                addCharAtSelection(button.text.toString())

                subCalculate(view)
                scrollToRight()
            }
        } else if (button.text == "-") {
            addCharAtSelection(button.text.toString())

            subCalculate(view)
            scrollToRight()
        }
    }

    fun addLeftOperator(view: View) {
        val calculationTextView: EditText = binding.calculationText

        val button: Button = view as Button

        var calculationText: String = calculationTextView.text.toString()

        //Delete last operator if there is one
        if (calculationText.isNotEmpty()) {
            addCharAtSelection(button.text.toString())

            subCalculate(view)
            scrollToRight()
        } else {
            addCharAtSelection(button.text.toString())
        }
    }

    fun addRightOperator(view: View) {
        val calculationTextView: EditText = binding.calculationText

        val button: Button = view as Button

        var calculationText: String = calculationTextView.text.toString()

        //Delete last operator if there is one
        if (calculationText.isNotEmpty()) {
            addCharAtSelection(button.text.toString())

            subCalculate(view)
            scrollToRight()
        } else {
            addCharAtSelection(button.text.toString())
        }
    }
    //endregion

    // Method to add an character at the selected cursor position
    private fun addCharAtSelection(stringToAdd: String) {
        val calculationTextView: EditText = binding.calculationText

        val cursorPosition = calculationTextView.selectionStart

        val oldString = calculationTextView.text.toString()
        var leftSide = oldString.substring(0, cursorPosition)
        var rightSide = oldString.substring(cursorPosition)

        calculationTextView.setText(String.format("%s%s%s", leftSide, stringToAdd, rightSide))
        calculationTextView.setSelection(cursorPosition
                +stringToAdd.length)

    }

    //region Methods to delete
    fun deleteChar(view: View) {
        val calculationTextView: EditText = binding.calculationText

        var calculationText: String = calculationTextView.text.toString()

        if (calculationText.isNotEmpty()) {
            val cursorPosition = calculationTextView.selectionStart

            val selectionEnd = calculationTextView.selectionEnd

            //Cursor is just at one position
            if (cursorPosition == selectionEnd && cursorPosition-1 >= 0) {
                val variable = StringCalculator.getVariable(calculationText, cursorPosition-1)
                //Check if the char to delete is a variable and if so remove the hole variable
                calculationText = calculationText.removeRange(cursorPosition - 1, selectionEnd)

                calculationTextView.setText(calculationText)

                calculationTextView.setSelection(cursorPosition - 1)
            }
            //There is an selection
            else if (cursorPosition != selectionEnd) {
                calculationText = calculationText.removeRange(cursorPosition, selectionEnd)

                calculationTextView.setText(calculationText)

                calculationTextView.setSelection(cursorPosition)
            }
        }

        subCalculate(view)
    }

    fun deleteAll(view: View) {
        val calculationTextView: EditText = binding.calculationText

        val resultTextView: TextView = binding.resultText

        calculationTextView.setText("")
        resultTextView.text = ""

        calculationTextView.setSelection(0)
    }
    //endregion
}