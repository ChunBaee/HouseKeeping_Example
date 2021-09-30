package com.solie.housekeeping_example.dialog

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.solie.housekeeping_example.R
import com.solie.housekeeping_example.databinding.DialogSelectMonthBinding
import com.solie.housekeeping_example.util.FirebaseData
import com.solie.housekeeping_example.util.HouseKeepViewModel

class SelectDialog : DialogFragment(), FirebaseData {
    private lateinit var binding : DialogSelectMonthBinding
    private lateinit var monthList: Array<String>

    private val viewModel : HouseKeepViewModel by viewModels({requireActivity()})
    var myPosition : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DialogSelectMonthBinding.inflate(inflater, container, false)

        setSpinner()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.selectBtn.setOnClickListener {
            viewModel.updateNewMonth(monthList[myPosition])
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        context?.dialogResize(this@SelectDialog, 0.8f, 0.6f)
    }

    fun Context.dialogResize(dialogFragment : SelectDialog, width : Float, height : Float) {
        val windowMananger = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if(Build.VERSION.SDK_INT < 30) {
            val display = windowMananger.defaultDisplay
            val size = Point()
            display.getSize(size)

            val window = dialogFragment.dialog?.window

            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()
            window?.setLayout(x, y)
        } else {
            val rect = windowMananger.currentWindowMetrics.bounds
            val window = dialogFragment.dialog?.window

            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }

    private fun setSpinner() {
        monthList  = resources.getStringArray(R.array.month_list)
        val monthListAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,monthList)
        binding.selectSpinner.adapter = monthListAdapter
        binding.selectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                myPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }
}