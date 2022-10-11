package de.lsstudio.ls_calculator.helper

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.lsstudio.ls_calculator.classes.Custom
import de.lsstudio.ls_calculator.classes.History
import de.lsstudio.ls_calculator.theme.BrightTheme
import de.lsstudio.ls_calculator.theme.DarkTheme
import de.lsstudio.ls_calculator.theme.MyAppTheme

class LocalDataHelper() {

    companion object {

        val LOCAL_DATA = "local_data"
        val HISTORY = "history"
        val CUSTOMS = "customs"
        val THEME = "theme"

        fun getHistory(context: Context): List<History> {
            val sharedPreferences = context.getSharedPreferences(LOCAL_DATA, Activity.MODE_PRIVATE)

            val gson = Gson()
            val itemType = object : TypeToken<List<History>>() {}.type

            return gson.fromJson(sharedPreferences.getString(HISTORY, "[]"), itemType)
        }

        fun saveHistory(context: Context, newHistory: List<History>) {
            val sharedPreferences = context.getSharedPreferences(LOCAL_DATA, Activity.MODE_PRIVATE)
            val sharedPreferencesEdit = sharedPreferences.edit()

            sharedPreferencesEdit.putString(HISTORY, Gson().toJson(newHistory))

            sharedPreferencesEdit.commit()
        }

        fun getCustoms(context: Context): List<Custom> {
            val sharedPreferences = context.getSharedPreferences(LOCAL_DATA, Activity.MODE_PRIVATE)

            val gson = Gson()
            val itemType = object : TypeToken<List<Custom>>() {}.type

            return gson.fromJson(sharedPreferences.getString(CUSTOMS, "[]"), itemType)
        }

        fun saveCustoms(context: Context, newCustoms: List<Custom>) {
            val sharedPreferences = context.getSharedPreferences(LOCAL_DATA, Activity.MODE_PRIVATE)
            val sharedPreferencesEdit = sharedPreferences.edit()

            sharedPreferencesEdit.putString(CUSTOMS, Gson().toJson(newCustoms))

            sharedPreferencesEdit.commit()
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun getTheme(context: Context): MyAppTheme {
            val sharedPreferences = context.getSharedPreferences(LOCAL_DATA, Activity.MODE_PRIVATE)

            return when (sharedPreferences.getInt(THEME, 0)) {
                0 -> BrightTheme(context)
                1 -> DarkTheme(context)
                else -> BrightTheme(context)
            }
        }

        fun saveTheme(context: Context, newTheme: Int) {
            val sharedPreferences = context.getSharedPreferences(LOCAL_DATA, Activity.MODE_PRIVATE)
            val sharedPreferencesEdit = sharedPreferences.edit()

            sharedPreferencesEdit.putInt(THEME, newTheme)

            sharedPreferencesEdit.commit()
        }

    }

}