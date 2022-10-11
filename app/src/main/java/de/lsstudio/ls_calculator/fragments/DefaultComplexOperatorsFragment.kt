package de.lsstudio.ls_calculator.fragments

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment
import de.lsstudio.ls_calculator.R
import de.lsstudio.ls_calculator.databinding.FragmentDefaultComplexOperatorsBinding
import de.lsstudio.ls_calculator.theme.MyAppTheme

class DefaultComplexOperatorsFragment: ThemeFragment() {

    lateinit var binding: FragmentDefaultComplexOperatorsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDefaultComplexOperatorsBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun syncTheme(appTheme: AppTheme) {
        val myTheme = appTheme as MyAppTheme

        repeat(10) {
            val id = resources.getIdentifier("funValBtn${it+1}", "id", requireContext().packageName)
            val btn = binding.root.findViewById<View>(id) as Button

            btn.setTextColor(myTheme.textColor())
            btn.backgroundTintList =
                ColorStateList.valueOf(myTheme.main2Color())
        }
    }

}