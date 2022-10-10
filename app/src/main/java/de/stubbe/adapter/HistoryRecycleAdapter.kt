package de.stubbe.adapter

import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.stubbe.R
import de.stubbe.helper.StringCalculator

class HistoryRecycleAdapter(private val data: MutableList<Pair<Pair<String, String>, Double>>, private val dialog: AlertDialog, private val parentView: View):
    RecyclerView.Adapter<HistoryRecycleAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val calculationOfHistory: TextView = view.findViewById(R.id.calculationOfHistory)
        val resultOfHistory: TextView = view.findViewById(R.id.resultOfHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_data_set, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.calculationOfHistory.text = data[position].first.first
        holder.resultOfHistory.text = data[position].first.second

        //Go back to values of selected history
        holder.itemView.setOnClickListener {
            val calculationTextView: EditText =  parentView.findViewById(R.id.calculationText)

            val resultTextView: TextView = parentView.findViewById(R.id.resultText)

            calculationTextView.setText(data[position].first.first)
            resultTextView.text = data[position].first.second

            StringCalculator.Variables.Ans.value = data[position].second

            calculationTextView.setSelection(calculationTextView.text.length)

            dialog.cancel()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}