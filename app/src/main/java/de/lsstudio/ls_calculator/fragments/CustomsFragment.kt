package de.lsstudio.ls_calculator.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import de.lsstudio.ls_calculator.MainActivity
import de.lsstudio.ls_calculator.adapter.FlowLayoutAdapter
import de.lsstudio.ls_calculator.classes.Custom
import de.lsstudio.ls_calculator.classes.CustomType
import de.lsstudio.ls_calculator.databinding.FragmentCustomsBinding
import de.lsstudio.ls_calculator.helper.LayoutHelper
import de.lsstudio.ls_calculator.helper.LocalDataHelper
import de.lsstudio.ls_calculator.helper.StringCalculator


class CustomsFragment(): Fragment() {

    companion object {
        var addCustoms: (Custom, Boolean, Boolean) -> Unit = { custom: Custom, saveLocal: Boolean, isParameter: Boolean ->}
        var removeCustoms: (Custom) -> Unit = {}
        lateinit var mainActivity: MainActivity
        var functionParameters = mutableListOf<String>()
    }

    lateinit var binding: FragmentCustomsBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomsBinding.inflate(layoutInflater)
        val flowLayout = binding.customFlowLayout

        var flowLayoutAdapter = FlowLayoutAdapter(requireContext(), flowLayout)

        addCustoms = fun (newCustom: Custom, saveLocal: Boolean, isParameter: Boolean) {
            flowLayoutAdapter = FlowLayoutAdapter(requireContext(), flowLayout, isParameter)
            flowLayoutAdapter.addItem(newCustom.name,
            {
                when (newCustom.customType) {
                    CustomType.Variable -> mainActivity.addStringAtSelection(newCustom.variable!!.sign)
                    CustomType.Function -> {
                        var functionBody = newCustom.function!!.sign + "("
                        val parameters = newCustom.function.parameters
                        for (i in parameters.indices) {
                            functionBody +=
                                if (i != parameters.lastIndex) {
                                    ","
                                } else {
                                    ")"
                                }
                        }

                        val selectStart = newCustom.function.sign.length + 1

                        mainActivity.addStringAtSelectionAndSelectOffset(functionBody, selectStart)
                    }
                }
            },
            {
                if (!isParameter) {
                    val customTypeName = when (newCustom.customType) {
                        CustomType.Variable -> "value"
                        CustomType.Function -> "function"
                    }

                    LayoutHelper.createYesNoDialog(
                        mainActivity,
                        "Do you want to delete this custom $customTypeName?",
                        {
                            removeCustoms(newCustom)
                        })
                } else {
                    LayoutHelper.createYesNoDialog(
                        mainActivity,
                        "Do you want to delete this parameter?",
                        {
                            removeCustoms(newCustom)

                            //Remove from fragment list
                            for (i in functionParameters.toMutableList().indices) {
                                if (functionParameters[i] == newCustom.name) {
                                    functionParameters.removeAt(i)
                                    break
                                }
                            }

                            //Remove from activity list
                            for (i in MainActivity.functionParameters.toMutableList().indices) {
                                if (MainActivity.functionParameters[i] == newCustom.name) {
                                    val newArray = MainActivity.functionParameters.toMutableList()
                                    newArray.removeAt(i)
                                    MainActivity.functionParameters = newArray.toTypedArray()
                                    break
                                }
                            }
                        })
                }
            })

            if (saveLocal) {
                val newCustoms = LocalDataHelper.getCustoms(requireContext()).toMutableList()
                newCustoms.add(newCustom)
                LocalDataHelper.saveCustoms(requireContext(), newCustoms)
            }
        }

        removeCustoms = fun (customToRemove: Custom) {
            flowLayoutAdapter.removeItem(customToRemove.name)

            val newCustoms = LocalDataHelper.getCustoms(requireContext()).toMutableList()

            for (i in newCustoms.toMutableList().indices) {
                if (newCustoms[i].name == customToRemove.name) {
                    newCustoms.removeAt(i)
                    break
                }
            }

            LocalDataHelper.saveCustoms(requireContext(), newCustoms)
        }

        val customList = LocalDataHelper.getCustoms(requireContext()).toMutableList()

        customList.sortBy { custom -> custom.name.length }

        customList.forEach { custom ->
            addCustoms(custom, false, false)
        }

        StringCalculator.Functions.values().forEach { enumFunction ->
            addCustoms(
                Custom(
                    enumFunction.function.sign,
                    CustomType.Function,
                    null,
                    enumFunction.function
            ), false, false)
        }

        functionParameters.forEach { parameter ->
            addCustoms(Custom(
                parameter,
                CustomType.Variable,
                StringCalculator.Variable(
                    parameter,
                    0.0
                ),
                null), false, true)
        }

        return binding.root
    }


}