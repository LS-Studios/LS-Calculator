package de.lsstudio.ls_calculator

import android.annotation.SuppressLint
import android.content.Intent
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeActivity
import com.dolatkia.animatedThemeManager.ThemeManager
import com.nex3z.flowlayout.FlowLayout
import de.lsstudio.ls_calculator.adapter.CustomPagerAdapter
import de.lsstudio.ls_calculator.adapter.FlowLayoutAdapter
import de.lsstudio.ls_calculator.adapter.HistoryRecycleAdapter
import de.lsstudio.ls_calculator.classes.Custom
import de.lsstudio.ls_calculator.classes.CustomType
import de.lsstudio.ls_calculator.classes.EditTextAutoSizeUtility
import de.lsstudio.ls_calculator.classes.History
import de.lsstudio.ls_calculator.databinding.ActivityMainBinding
import de.lsstudio.ls_calculator.fragments.CustomsFragment
import de.lsstudio.ls_calculator.helper.LayoutHelper
import de.lsstudio.ls_calculator.helper.LocalDataHelper
import de.lsstudio.ls_calculator.helper.StringCalculator
import de.lsstudio.ls_calculator.theme.BrightTheme
import de.lsstudio.ls_calculator.theme.DarkTheme
import de.lsstudio.ls_calculator.theme.MyAppTheme


@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : ThemeActivity() {

    companion object {
        var functionName: String = ""
        var functionParameters: Array<String> = arrayOf()
    }

    lateinit var binding: ActivityMainBinding;

    lateinit var calculator: StringCalculator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateCalculatorValues()

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

        val pagerAdapter = CustomPagerAdapter(this)
        val viewPager = binding.viewPager
        viewPager.adapter = pagerAdapter

        EditTextAutoSizeUtility.setupAutoResize(binding.calculationText, this)

        if (intent.getIntExtra("state", 0) == 0) {
            binding.resultText.visibility = View.VISIBLE
            binding.calculationAreaCalculateBtn.isEnabled = true
        } else {
            binding.resultText.visibility = View.GONE
            binding.calculationAreaCalculateBtn.isEnabled = false
        }
    }

    fun updateMenuItems(menu: Menu, itemCount: Int) {
        repeat(itemCount-1) {
            val item = menu.getItem(it)
            val s = SpannableString(item.title)

            val myTheme = ThemeManager.instance.getCurrentTheme() as MyAppTheme

            s.setSpan(ForegroundColorSpan(myTheme.textColor()), 0, s.length, 0)
            item.title = s
        }

        val item = menu.getItem(itemCount-1)

        val myTheme = ThemeManager.instance.getCurrentTheme() as MyAppTheme

        val text = if (myTheme is BrightTheme) {
            "Change to dark theme"
        } else
            "Change to bright theme"

        var s = SpannableString(text)

        if (intent.getIntExtra("state", 0) == 1)
            s = SpannableString(item.title)

        s.setSpan(ForegroundColorSpan(myTheme.textColor()), 0, s.length, 0)
        item.title = s
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater

        val state = intent.getIntExtra("state", 0)

        if (state == 0) {
            menuInflater.inflate(R.menu.main_options_menu, menu)
            updateMenuItems(menu, 4)
        }
        else {
            menuInflater.inflate(R.menu.create_function_options_menu, menu)
            updateMenuItems(menu, 2)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.historyButton -> showHistory()
            R.id.themeButton -> changeTheme()
            R.id.newVariableButton -> createNewVariable()
            R.id.newFunctionButton -> createNewFunction()
            R.id.cancel -> {
                functionName = ""
                functionParameters = arrayOf()

                finish()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("state", 0)
                startActivity(intent)
            }
            R.id.completeFunction -> {
                val newCustoms = LocalDataHelper.getCustoms(this).toMutableList()
                newCustoms.add(
                    Custom(
                        functionName,
                        CustomType.Function,
                        null,
                        StringCalculator.Function(
                            functionName,
                            functionParameters,
                            binding.calculationText.text.toString()
                        )
                    )
                )
                LocalDataHelper.saveCustoms(this, newCustoms)

                functionParameters.forEach { parameter ->
                    CustomsFragment.functionParameters.remove(parameter)
                    CustomsFragment.removeCustoms(
                        Custom(
                            parameter,
                            CustomType.Variable,
                            StringCalculator.Variable(
                                parameter,
                                0.0
                            ),
                            null
                        )
                    )
                }

                updateCalculatorValues()

                finish()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("state", 0)
                startActivity(intent)
            }
            R.id.editValues -> {
                createFunctionPopUp(functionName, functionParameters) { name, parameters ->
                    functionName = name
                    functionParameters = parameters
                }
            }
        }
        return true
    }

    override fun getStartTheme(): AppTheme {
        return LocalDataHelper.getTheme(this)
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
            ColorStateList.valueOf(getColor(R.color.green))

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

        repeat(4) {
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

    fun updateCalculatorValues() {
        val variables = mutableListOf<StringCalculator.Variable>()
        val functions = mutableListOf<StringCalculator.Function>()
        functions.addAll(StringCalculator.Functions.values().map { enumFunction -> enumFunction.function })

        val customs = LocalDataHelper.getCustoms(this)
        customs.forEach { custom ->
            when (custom.customType) {
                CustomType.Variable -> variables.add(custom.variable!!)
                CustomType.Function -> functions.add(custom.function!!)
            }
        }

        calculator = StringCalculator(variables, functions)
    }

    //Method to show an dialog view which contains the current history
    @SuppressLint("NotifyDataSetChanged")
    private fun showHistory() {
        val historyView = layoutInflater.inflate(R.layout.history_card_dialogue, null)

        LayoutHelper.createAllerDialog(historyView) { dialog ->
            val historyRecycler = historyView.findViewById<RecyclerView>(R.id.historyRecycler)

            val historyRecyclerAdapter = HistoryRecycleAdapter(applicationContext, dialog, findViewById<View?>(android.R.id.content).rootView)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
            historyRecycler.layoutManager = layoutManager
            historyRecycler.adapter = historyRecyclerAdapter

            val myTheme = ThemeManager.instance.getCurrentTheme() as MyAppTheme

            val nameText = historyView.findViewById<TextView>(R.id.historyNameText)
            nameText.setTextColor(myTheme.textColor())

            val card = historyView.findViewById<CardView>(R.id.historyCard)
            card.setCardBackgroundColor(myTheme.main2Color())

            val closeHistoryButton = historyView.findViewById<Button>(R.id.closeHistoryButton)
            closeHistoryButton.backgroundTintList = ColorStateList.valueOf(myTheme.main1Color())
            closeHistoryButton.setTextColor(myTheme.textColor())

            closeHistoryButton.setOnClickListener {
                dialog.dismiss()
            }

            val deleteAllHistoryButton = historyView.findViewById<Button>(R.id.deleteAllHistoryButton)
            deleteAllHistoryButton.backgroundTintList = ColorStateList.valueOf(myTheme.main1Color())
            deleteAllHistoryButton.setTextColor(myTheme.textColor())

            if (LocalDataHelper.getHistory(applicationContext).isEmpty()) {
                deleteAllHistoryButton.isEnabled = false
                deleteAllHistoryButton.alpha = 0.5f
            }

            deleteAllHistoryButton.setOnClickListener {
                LocalDataHelper.saveHistory(applicationContext, listOf())
                historyRecyclerAdapter.notifyDataSetChanged()

                deleteAllHistoryButton.isEnabled = false
                deleteAllHistoryButton.alpha = 0.5f

                dialog.dismiss()
            }
        }
    }

    private fun changeTheme() {
        val myTheme = ThemeManager.instance.getCurrentTheme()

        if (myTheme is BrightTheme) {
            ThemeManager.instance.changeTheme(DarkTheme(this), binding.toolBar)
            LocalDataHelper.saveTheme(this, 1)
        }
        else {
            ThemeManager.instance.changeTheme(BrightTheme(this), binding.toolBar)
            LocalDataHelper.saveTheme(this, 0)
        }

        updateMenuItems(binding.toolBar.menu, 4)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                recreate()
            },
            500
        )
    }

    private fun createNewVariable() {
        val view = layoutInflater.inflate(R.layout.popup_custom_variable, null)

        LayoutHelper.createAllerDialog(view) { dialog ->
            val card = view.findViewById<CardView>(R.id.popupCustomVariableCard)
            val titleText = view.findViewById<TextView>(R.id.popupCustomVariableTitleText)
            val nameText = view.findViewById<TextView>(R.id.popupCustomVariableNameNameText)
            val nameEditText = view.findViewById<EditText>(R.id.popupCustomVariableNameEditText)
            val valueText = view.findViewById<TextView>(R.id.popupCustomVariableValueNameText)
            val valueEditText = view.findViewById<EditText>(R.id.popupCustomVariableValueEditText)
            val confirmBtn = view.findViewById<Button>(R.id.popupCustomVariableConfirmBtn)

            val myTheme = ThemeManager.instance.getCurrentTheme() as MyAppTheme

            card.setCardBackgroundColor(myTheme.main2Color())

            titleText.setTextColor(myTheme.textColor())

            nameText.setTextColor(myTheme.textColor())

            nameEditText.setTextColor(myTheme.textColor())
            nameEditText.backgroundTintList = ColorStateList.valueOf(myTheme.textColor())

            valueText.setTextColor(myTheme.textColor())

            valueEditText.setTextColor(myTheme.textColor())
            valueEditText.backgroundTintList = ColorStateList.valueOf(myTheme.textColor())

            confirmBtn.setTextColor(myTheme.textColor())
            confirmBtn.backgroundTintList = ColorStateList.valueOf(myTheme.main1Color())

            confirmBtn.setOnClickListener {
                if(!checkIfNameValid(nameEditText.text.toString())) {
                    return@setOnClickListener
                }

                if (nameEditText.text.toString().contains(" ")) {
                    Toast.makeText(this, "Please enter a variable name that do not contain spaces!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                //Add variable to local data
                val newCustoms = LocalDataHelper.getCustoms(this).toMutableList()
                val newCustom = Custom(
                    nameEditText.text.toString(),
                    CustomType.Variable,
                    StringCalculator.Variable(nameEditText.text.toString(), valueEditText.text.toString().toDouble()),
                    null
                )
                newCustoms.add(newCustom)
                LocalDataHelper.saveCustoms(this, newCustoms)

                CustomsFragment.addCustoms(newCustom, false, false)

                updateCalculatorValues()

                dialog.dismiss()
            }
        }
    }

    private fun createNewFunction() {
        createFunctionPopUp("", arrayOf()) { name, parameters ->
            functionName = name
            functionParameters = parameters

            finish()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("state", 1)
            startActivity(intent)
        }
    }

    fun createFunctionPopUp(nameOfFunction: String, parameterList: Array<String>, onContinue: (String, Array<String>) -> Unit) {
        val view = layoutInflater.inflate(R.layout.popup_custom_function, null)

        LayoutHelper.createAllerDialog(view) { dialog ->
            val card = view.findViewById<CardView>(R.id.popupCustomFunctionCard)
            val titleText = view.findViewById<TextView>(R.id.popupCustomFunctionTitleText)
            val nameText = view.findViewById<TextView>(R.id.popupCustomFunctionNameNameText)
            val nameEditText = view.findViewById<EditText>(R.id.popupCustomFunctionNameEditText)
            val parameterText =
                view.findViewById<TextView>(R.id.popupCustomFunctionParameterNameText)
            val parameterEditText =
                view.findViewById<EditText>(R.id.popupCustomFunctionParameterEditText)
            val addParameterBtn =
                view.findViewById<Button>(R.id.popupCustomFunctionAddParameterBtn)
            val cancelBtn = view.findViewById<Button>(R.id.popupCustomFunctionCancelBtn)
            val continueBtn = view.findViewById<Button>(R.id.popupCustomFunctionContinueBtn)
            val parameterFlowLayout =
                view.findViewById<FlowLayout>(R.id.customParameterFlowLayout)

            nameEditText.setText(nameOfFunction)

            val parameterAdapter = FlowLayoutAdapter(this, parameterFlowLayout, true)
            val parameters = parameterList.toMutableList()

            fun addParameter(name: String, addToList: Boolean) {
                parameterAdapter.addItem(
                    name,
                    {},
                    {
                        LayoutHelper.createYesNoDialog(
                            this,
                            "Do you want to delete this parameter?",
                            {
                                parameterAdapter.removeItem(name)

                                CustomsFragment.removeCustoms(
                                    Custom(
                                        name,
                                        CustomType.Variable,
                                        StringCalculator.Variable(
                                            name,
                                            0.0
                                        ),
                                        null)
                                )

                                //Remove from fragment list
                                for (i in CustomsFragment.functionParameters.toMutableList().indices) {
                                    if (CustomsFragment.functionParameters[i] == name) {
                                        CustomsFragment.functionParameters.removeAt(i)
                                        break
                                    }
                                }

                                //Remove from activity list
                                for (i in functionParameters.toMutableList().indices) {
                                    if (functionParameters[i] == name) {
                                        val newArray = functionParameters.toMutableList()
                                        newArray.removeAt(i)
                                        functionParameters = newArray.toTypedArray()
                                        break
                                    }
                                }

                                CustomsFragment.functionParameters.remove(name)
                            })
                    })
                parameterEditText.setText("")

                if (addToList)
                    parameters.add(name)
            }

            parameters.forEach { addParameter(it, false) }

            val myTheme = ThemeManager.instance.getCurrentTheme() as MyAppTheme

            card.setCardBackgroundColor(myTheme.main2Color())

            titleText.setTextColor(myTheme.textColor())

            nameText.setTextColor(myTheme.textColor())

            nameEditText.setTextColor(myTheme.textColor())
            nameEditText.backgroundTintList = ColorStateList.valueOf(myTheme.textColor())

            parameterText.setTextColor(myTheme.textColor())

            parameterEditText.setTextColor(myTheme.textColor())
            parameterEditText.backgroundTintList = ColorStateList.valueOf(myTheme.textColor())

            addParameterBtn.setTextColor(myTheme.textColor())
            addParameterBtn.backgroundTintList = ColorStateList.valueOf(myTheme.main1Color())

            addParameterBtn.setOnClickListener {
                if (parameterEditText.text.toString().isEmpty()) {
                    Toast.makeText(this, "Please enter a name for the parameter!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (parameters.find { parameter -> parameter == parameterEditText.text.toString() } != null) {
                    Toast.makeText(this, "Parameter with that name already exist! ", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if(!checkIfNameValid(parameterEditText.text.toString())) {
                    return@setOnClickListener
                }

                //Add variable to the flow layut fragment
                CustomsFragment.functionParameters.add(parameterEditText.text.toString())
                CustomsFragment.addCustoms(
                    Custom(
                        parameterEditText.text.toString(),
                        CustomType.Variable,
                        StringCalculator.Variable(
                            parameterEditText.text.toString(),
                            0.0
                        ),
                        null),
                false, true)

                addParameter(parameterEditText.text.toString(), true)
            }

            cancelBtn.setTextColor(myTheme.textColor())
            cancelBtn.backgroundTintList = ColorStateList.valueOf(myTheme.main1Color())

            cancelBtn.setOnClickListener {
                parameters.forEach { parameter ->
                    CustomsFragment.functionParameters.remove(parameter)
                    CustomsFragment.removeCustoms(
                        Custom(
                            parameter,
                            CustomType.Variable,
                            StringCalculator.Variable(
                                parameter,
                                0.0
                            ),
                            null)
                    )
                }

                dialog.dismiss()
            }

            continueBtn.setTextColor(myTheme.textColor())
            continueBtn.backgroundTintList = ColorStateList.valueOf(myTheme.main1Color())

            continueBtn.setOnClickListener {
                if (nameEditText.text.toString().isEmpty()) {
                    Toast.makeText(this, "Please enter a name for the function!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (nameEditText.text.toString().contains(" ")) {
                    Toast.makeText(this, "Please enter a function name that do not contain spaces!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                onContinue(nameEditText.text.toString(), parameters.toTypedArray())

                dialog.dismiss()
            }
        }
    }

    //Check if a variable, operator or function with that name already exist
    private fun checkIfNameValid(name: String): Boolean {
        val variables = mutableListOf<StringCalculator.Variable>()
        val functions = mutableListOf<StringCalculator.Function>()
        val operators = mutableListOf<String>()
        variables.addAll(StringCalculator.Variables.values().map { enumVariable -> enumVariable.variable })
        functions.addAll(StringCalculator.Functions.values().map { enumFunction -> enumFunction.function })
        operators.addAll(StringCalculator.Operators.values().map { enumOperator -> enumOperator.sign })

        val customs = LocalDataHelper.getCustoms(this)
        customs.forEach { custom ->
            when (custom.customType) {
                CustomType.Variable -> variables.add(custom.variable!!)
                CustomType.Function -> functions.add(custom.function!!)
            }
        }
        if (variables.find { variable -> variable.sign == name } != null) {
            Toast.makeText(this, "Variable with that name already exist!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (functions.find { function -> function.sign == name } != null) {
            Toast.makeText(this, "Function with that name already exist!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (operators.find { operator -> operator == name } != null) {
            Toast.makeText(this, "Operator with that name already exist!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    //region Methods to calculate the result
    fun calculate(view: View) {
        val calculationTextView: EditText = binding.calculationText

        val calculationText: String = calculationTextView.text.toString()

        val resultTextView: TextView = binding.resultText

        val result = calculator.calculate(calculationText)

        //Only show the result when the calculation is valid
        if (result != calculator.errorMessage) {
            calculationTextView.setText(result)

            calculationTextView.setSelection(calculationTextView.text.lastIndex + 1)

            //Set Ans to the result if the result is not empty and add the calculation to the history
            if (result.isNotEmpty()) {
                val newHistory = LocalDataHelper.getHistory(this).toMutableList()
                newHistory.add(
                    History(
                        calculationText,
                        result.toDouble()
                    )
                )
                LocalDataHelper.saveHistory(this, newHistory)
                StringCalculator.Variables.Ans.variable.value = result.toDouble()
            }

            resultTextView.text = result
        }
    }

    private fun subCalculate() {
        val calculationTextView: EditText = binding.calculationText

        val calculationText: String = calculationTextView.text.toString()

        val resultTextView: TextView = binding.resultText

        val result = calculator.calculate(calculationText)

        resultTextView.text = result

        //Color the color of the result red if its an invalid calculation
        if (result == calculator.errorMessage)
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

        addStringAtSelection(button.text.toString())

        subCalculate()
    }

    fun addDot(view: View) {
        val calculationTextView: EditText = binding.calculationText

        var calculationText: String = calculationTextView.text.toString()

        val cursorPosition = calculationTextView.selectionStart

        //Add a dot when there is a number and no dot before the cursor position
        if (calculationText.isNotEmpty() && !calculator.getNumber(calculationText, cursorPosition-1).contains(".") &&
            calculationText[cursorPosition-1].toString().matches(Regex("[0-9]")) &&
            !StringCalculator.Operators.isAnyOperator(calculationTextView.text.toString(), calculationTextView.text.lastIndex))
        {
            addStringAtSelection(".")
        }

        subCalculate()
    }

    fun addAns(view: View) {
        addStringAtSelection("Ans")

        val calculationTextView: EditText = binding.calculationText

        val resultTextView: TextView = binding.resultText

        //Check if Ans is is only variable in the calculation
        if (calculationTextView.text.matches((Regex("Ans"))))
            resultTextView.text = StringCalculator.Variables.Ans.variable.value.toString()

        subCalculate()
    }

    fun addBracket(view: View) {
        val calculationTextView: EditText = binding.calculationText

        addStringAtSelectionAndSelectOffset("()", 1)

        subCalculate()
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
                addStringAtSelection(button.text.toString())

                subCalculate()
            }
            else if (!calculationTextView.text[calculationTextView.selectionStart
                        - 1].toString().contains("(")) {
                if (StringCalculator.Operators.isRightAndLeftOperator(calculationTextView.text[calculationTextView.selectionStart- 1])) {
                    deleteChar(view)
                }
                addStringAtSelection(button.text.toString())

                subCalculate()
            }
        } else if (button.text == "-") {
            addStringAtSelection(button.text.toString())

            subCalculate()
        }
    }

    fun addLeftOperator(view: View) {
        val calculationTextView: EditText = binding.calculationText

        val button: Button = view as Button

        var calculationText: String = calculationTextView.text.toString()

        //Delete last operator if there is one
        if (calculationText.isNotEmpty()) {
            addStringAtSelection(button.text.toString())

            subCalculate()
        } else {
            addStringAtSelection(button.text.toString())
        }
    }

    fun addRightOperator(view: View) {
        val button: Button = view as Button

        val operator = button.text.toString()

        addStringAtSelectionAndSelectOffset("$operator()", operator.length+1)

        subCalculate()
    }

    //endregion

    // Method to add a string at the selected cursor position
    fun addStringAtSelection(stringToAdd: String) {
        val calculationTextView: EditText = binding.calculationText

        val cursorPosition = calculationTextView.selectionStart

        val oldString = calculationTextView.text.toString()
        var leftSide = oldString.substring(0, cursorPosition)
        var rightSide = oldString.substring(cursorPosition)

        calculationTextView.setText(String.format("%s%s%s", leftSide, stringToAdd, rightSide))
        calculationTextView.setSelection(cursorPosition
                +stringToAdd.length)

        subCalculate()
    }

    //Method to add a string at the selected cursor position and select something in range starting at the added string start
    fun addStringAtSelectionAndSelectOffset(stringToAdd: String, offsetFromCurrentSelection: Int) {
        val calculationTextView: EditText = binding.calculationText

        val cursorPosition = calculationTextView.selectionStart

        val oldString = calculationTextView.text.toString()
        var leftSide = oldString.substring(0, cursorPosition)
        var rightSide = oldString.substring(cursorPosition)

        calculationTextView.setText(String.format("%s%s%s", leftSide, stringToAdd, rightSide))

        calculationTextView.setSelection(cursorPosition+offsetFromCurrentSelection)

        println(cursorPosition)

        subCalculate()
    }

    //region Methods to delete
    fun deleteChar(view: View) {
        deleteChar(-1)
    }

    //region Methods to delete
    fun deleteChar(pos: Int = -1) {
        val calculationTextView: EditText = binding.calculationText

        var calculationText: String = calculationTextView.text.toString()

        if (calculationText.isNotEmpty()) {
            var cursorPosition = calculationTextView.selectionStart

            var selectionEnd = calculationTextView.selectionEnd

            if (pos != -1) {
                cursorPosition = pos
                selectionEnd = pos
            }

            //Cursor is just at one position
            if (cursorPosition == selectionEnd && cursorPosition-1 >= 0) {
                //Left char is bracket or ,
                if (calculationText[cursorPosition-1].toString().matches(Regex("[,()]"))) {
                    var bracketLength = calculator.getCalculationRangeFromBrackets(calculationText, cursorPosition-2)

                    if (calculationText[cursorPosition-1].toString() == "(")
                        bracketLength = calculator.getCalculationRangeFromBrackets(calculationText, cursorPosition-1)

                    //Remove brackets
                    calculationText =
                        calculationText.removeRange(bracketLength.first, bracketLength.last+1)

                    calculationTextView.setText(calculationText)

                    calculationTextView.setSelection(bracketLength.first)

                    //Delete function or operator if there left value is letter
                    try {
                        if (calculationText[calculationTextView.selectionStart-1].toString().matches(Regex("[a-zA-Z]")))
                            deleteChar()
                    } catch (e: Exception) {

                    }
                }

                //Check if the char to delete is letter and if so remove the hole word
                else if (calculationText[cursorPosition-1].toString().matches(Regex("[a-zA-Z]"))) {
                    val wordRange = StringCalculator.getRangeOfLettersNextToEachOther(calculationText, cursorPosition - 1)

                    val operator = StringCalculator.Operators.getOperator(calculationText, cursorPosition-1)
                    val function = StringCalculator.Functions.getFunction(calculationText, cursorPosition-1)

                    calculationText =
                        calculationText.removeRange(wordRange.first, wordRange.last+1)

                    calculationTextView.setText(calculationText)

                    calculationTextView.setSelection(wordRange.first)

                    if ((operator != "" || function != "") && (calculationText.length > calculationTextView.selectionStart && calculationText[calculationTextView.selectionStart] == '('))
                        deleteChar(calculationTextView.selectionStart+1)
                }

                //Left char is a anything else
                else {
                    calculationText =
                        calculationText.removeRange(cursorPosition-1, selectionEnd)

                    calculationTextView.setText(calculationText)

                    calculationTextView.setSelection(cursorPosition - 1)
                }
            }
            //There is a selection
            else if (cursorPosition != selectionEnd) {
                    calculationText = calculationText.removeRange(cursorPosition, selectionEnd)

                    calculationTextView.setText(calculationText)

                    calculationTextView.setSelection(cursorPosition)

                    subCalculate()

                    return
                }
        }

        subCalculate()
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