package de.lsstudio.ls_calculator.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import de.lsstudio.ls_calculator.MainActivity
import de.lsstudio.ls_calculator.classes.Custom
import de.lsstudio.ls_calculator.fragments.CustomsFragment
import de.lsstudio.ls_calculator.fragments.DefaultComplexOperatorsFragment

class CustomPagerAdapter(val mainActivity: MainActivity) : FragmentStateAdapter(mainActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        if (position == 0) {
            return DefaultComplexOperatorsFragment()
        } else {
            val customsFragment = CustomsFragment()
            CustomsFragment.mainActivity = mainActivity;
            return customsFragment
        }
    }
}
