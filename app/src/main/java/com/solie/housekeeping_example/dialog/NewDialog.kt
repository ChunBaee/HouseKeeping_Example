package com.solie.housekeeping_example.dialog

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.solie.housekeeping_example.databinding.DialogInputBinding
import com.solie.housekeeping_example.util.FirebaseData

class NewDialog : DialogFragment(), FirebaseData {

    private lateinit var binding : DialogInputBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DialogInputBinding.inflate(inflater, container, false)

        binding.inputBtn.setOnClickListener {
            if(binding.inputEdt.text.isEmpty()) {
                Toast.makeText(requireContext(), "Check Your Input Again", Toast.LENGTH_SHORT).show()
            } else {
                upLoadInput()
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        context?.dialogResize(this@NewDialog,0.9f, 0.2f)
    }

    fun Context.dialogResize(dialogFragment : NewDialog, width : Float, height : Float) {
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

    private fun upLoadInput() {
        val inputCost = mutableMapOf<String, Any>()
        inputCost["Cost"] = binding.inputEdt.text.toString().toInt()
        fireStoreCost.set(inputCost)
        targetFragment!!.onActivityResult(0, 0, null)
        dismiss()
    }

}