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
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.solie.housekeeping_example.databinding.DialogDecreaseBinding
import com.solie.housekeeping_example.util.FirebaseData
import java.text.DecimalFormat

class DecreaseDialog : DialogFragment(), FirebaseData {
    private lateinit var binding: DialogDecreaseBinding

    private var existCost: Int = 0
    private var existDecrease: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogDecreaseBinding.inflate(inflater, container, false)


        binding.decreaseBtn.setOnClickListener {
            getExistData()
            Toast.makeText(requireContext(), "Update Decrease", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        context?.dialogResize(this@DecreaseDialog, 0.9f, 0.4f)
    }

    fun Context.dialogResize(dialogFragment: DecreaseDialog, width: Float, height: Float) {
        val windowMananger = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT < 30) {
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

    private fun getExistData() {
        fireStoreCost.get()
            .addOnCompleteListener { task ->
                existCost = task.result!!.get("Cost").toString().toInt()
                fireStoreDecrease.get()
                    .addOnCompleteListener { task ->
                        existDecrease = if (task.result?.get(DecimalFormat("00").format(binding.decreaseDateSpinner.month + 1)).toString() == "null") { 0 }
                            else { DecimalFormat("00").format(binding.decreaseDateSpinner.month + 1).let {task.result!!.get(it) }.toString().toInt() }
                        upLoadRTDB()
                    }
            }

    }

    private fun upLoadStore() {
        val StoreMap = mutableMapOf<String, Any>()
        val decreaseMap = mutableMapOf<String, Any>()
        existCost -= binding.decreaseEdt.text.toString().toInt()
        existDecrease += binding.decreaseEdt.text.toString().toInt()

        StoreMap["Cost"] = existCost
        decreaseMap[DecimalFormat("00").format(binding.decreaseDateSpinner.month + 1)] = existDecrease

        fireStoreDecrease.get().addOnSuccessListener { task ->
            if (task.exists()) {
                fireStoreDecrease.update(decreaseMap)
            } else {
                fireStoreDecrease.set(decreaseMap)
            }
        }
        fireStoreCost.update(StoreMap)
        targetFragment!!.onActivityResult(0, 0, null)
        dismiss()
    }

    private fun upLoadRTDB() {
        val DBmap = mutableMapOf<String, Any>()
        DBmap["Year"] = binding.decreaseDateSpinner.year.toString()
        DBmap["Month"] = DecimalFormat("00").format(binding.decreaseDateSpinner.month + 1)
        DBmap["Date"] = binding.decreaseDateSpinner.dayOfMonth.toString()
        DBmap["Type"] = "Decrease"
        DBmap["Reason"] = binding.decreaseReasonTxt.text.toString()
        DBmap["Cost"] = binding.decreaseEdt.text.toString()
        database.push().setValue(DBmap)
        upLoadStore()
    }

}