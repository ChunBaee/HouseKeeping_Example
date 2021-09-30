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
import com.solie.housekeeping_example.databinding.DialogIncreaseBinding
import com.solie.housekeeping_example.util.FirebaseData
import java.text.DecimalFormat

class IncreaseDialog : DialogFragment(), FirebaseData {
    private lateinit var binding: DialogIncreaseBinding

    private var existCost: Int = 0
    private var existIncrease: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogIncreaseBinding.inflate(inflater, container, false)

        binding.increaseBtn.setOnClickListener {
            getExistData()
            Toast.makeText(requireContext(), "Update Increase", Toast.LENGTH_SHORT).show()

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        context?.dialogResize(this@IncreaseDialog, 0.9f, 0.4f)
    }

    fun Context.dialogResize(dialogFragment: IncreaseDialog, width: Float, height: Float) {
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
                fireStoreIncrease.get()
                    .addOnCompleteListener { task ->
                        existIncrease = if(DecimalFormat("00").format(binding.increaseDateSpinner.month + 1).let { task.result!!.get(it) }.toString() == "null") {0}
                        else{ DecimalFormat("00").format(binding.increaseDateSpinner.month + 1).let { task.result!!.get(it) }.toString().toInt()}
                        upLoadRTDB()
                    }
            }
    }

    private fun upLoadStore() {
        val StoreMap = mutableMapOf<String, Any>()
        val increaseMap = mutableMapOf<String, Any>()
        existCost += binding.increaseEdt.text.toString().toInt()
        existIncrease += binding.increaseEdt.text.toString().toInt()

        increaseMap[DecimalFormat("00").format(binding.increaseDateSpinner.month + 1)] = existIncrease
        StoreMap["Cost"] = existCost

        fireStoreIncrease.get().addOnSuccessListener { task ->
            if(task.exists()) {
                fireStoreIncrease.update(increaseMap)
            } else {
                fireStoreIncrease.set(increaseMap)
            }
        }
        fireStoreCost.update(StoreMap)
        targetFragment!!.onActivityResult(0, 0, null)
        dismiss()
    }

    private fun upLoadRTDB() {
        val DBmap = mutableMapOf<String, Any>()
        DBmap["Year"] = binding.increaseDateSpinner.year.toString()
        DBmap["Month"] = DecimalFormat("00").format(binding.increaseDateSpinner.month + 1)
        DBmap["Date"] = binding.increaseDateSpinner.dayOfMonth.toString()
        DBmap["Type"] = "Increase"
        DBmap["Reason"] = binding.increaseReasonTxt.text.toString()
        DBmap["Cost"] = binding.increaseEdt.text.toString()
        database.push().setValue(DBmap)
        upLoadStore()
    }

}