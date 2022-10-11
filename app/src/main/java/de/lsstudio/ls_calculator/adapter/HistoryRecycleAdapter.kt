package de.lsstudio.ls_calculator.adapter

import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dolatkia.animatedThemeManager.ThemeManager
import de.lsstudio.ls_calculator.R
import de.lsstudio.ls_calculator.helper.LocalDataHelper
import de.lsstudio.ls_calculator.helper.StringCalculator
import de.lsstudio.ls_calculator.theme.MyAppTheme

class HistoryRecycleAdapter(private val context: Context, private val dialog: AlertDialog, private val parentView: View):
    RecyclerView.Adapter<HistoryRecycleAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val historyItemCard: CardView = view.findViewById(R.id.historyItemCard)
        val calculationOfHistory: TextView = view.findViewById(R.id.calculationOfHistory)
        val resultOfHistory: TextView = view.findViewById(R.id.resultOfHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_data_set, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyList = LocalDataHelper.getHistory(context).reversed()
        holder.calculationOfHistory.text = historyList[position].calculation
        holder.resultOfHistory.text = historyList[position].result.toString()

        val myTheme = ThemeManager.instance.getCurrentTheme() as MyAppTheme

        holder.historyItemCard.setCardBackgroundColor(myTheme.main1Color())

        holder.calculationOfHistory.setTextColor(myTheme.textColor())
        holder.resultOfHistory.setTextColor(myTheme.textColor())

        //Go back to values of selected history
        holder.itemView.setOnClickListener {
            val calculationTextView: EditText =  parentView.findViewById(R.id.calculationText)

            val resultTextView: TextView = parentView.findViewById(R.id.resultText)

            calculationTextView.setText(historyList[position].calculation)
            resultTextView.text = historyList[position].result.toString()

            StringCalculator.Variables.Ans.variable.value = historyList[position].result

            calculationTextView.setSelection(calculationTextView.text.length)

            dialog.cancel()
        }
    }

    override fun getItemCount(): Int {
        return LocalDataHelper.getHistory(context).size
    }
}