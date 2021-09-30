package com.solie.housekeeping_example.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.solie.housekeeping_example.adapter.RecyclerAdapter
import com.solie.housekeeping_example.databinding.FragmentMainBinding
import com.solie.housekeeping_example.dialog.DecreaseDialog
import com.solie.housekeeping_example.dialog.IncreaseDialog
import com.solie.housekeeping_example.dialog.NewDialog
import com.solie.housekeeping_example.item.DBItem
import com.solie.housekeeping_example.item.WalletItem
import com.solie.housekeeping_example.util.FirebaseData
import com.solie.housekeeping_example.util.HouseKeepViewModel
import com.solie.housekeeping_example.util.RecyclerDecoration
import java.util.*

open class MainFragment : Fragment(), FirebaseData {
    private lateinit var binding: FragmentMainBinding
    private val adapter = RecyclerAdapter()
    private var getMonth: Int = 0

    private val viewModel: HouseKeepViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.btnIncrease.setOnClickListener {
            increaseDialog()
        }
        binding.btnDecrease.setOnClickListener {
            decreaseDialog()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.newMonth.observe(viewLifecycleOwner, Observer {
            getMonth = viewModel.newMonth.value!!.toInt()
            onResume()
        })
    }

    override fun onResume() {
        super.onResume()
        setBoard()
    }

    private fun setBoard() {
        fireStoreCost.get()
            .addOnSuccessListener { task ->
                if (task.exists()) {
                    getCosts()
                    setRecycler()
                } else {
                    getDialog()
                }
            }
    }

    private fun increaseDialog() {
        val dialog = IncreaseDialog()
        dialog.setTargetFragment(this@MainFragment, 0)
        dialog.show(requireActivity().supportFragmentManager, "IncreaseDialog")
    }

    private fun decreaseDialog() {
        val dialog = DecreaseDialog()
        dialog.setTargetFragment(this@MainFragment, 0)
        dialog.show(requireActivity().supportFragmentManager, "DecreaseDialog")
    }

    private fun getDialog() {
        val dialog = NewDialog()
        dialog.setTargetFragment(this@MainFragment, 0)
        dialog.show(requireActivity().supportFragmentManager, "InPutDialog")
    }

    private fun getCosts() {
        fireStoreCost
            .get()
            .addOnCompleteListener { task ->
                binding.MainMyWallet.text =
                    task.result!!.toObject(WalletItem::class.java)?.Cost.toString()
            }
        fireStoreIncrease
            .get()
            .addOnCompleteListener { task ->
                binding.MainIncrease.text = if (viewModel.newMonth.value?.let { task.result!!.get(it) }.toString() == "null"
                ) {
                    "0"
                } else {
                    viewModel.newMonth.value?.let { task.result!!.get(it) }.toString()
                }
            }
        fireStoreDecrease
            .get()
            .addOnCompleteListener { task ->
                binding.MainDecrease.text = if (viewModel.newMonth.value?.let { task.result!!.get(it) }.toString() == "null"
                ) {
                    "0"
                } else {
                    viewModel.newMonth.value?.let { task.result!!.get(it) }.toString()
                }
            }
    }

    private fun setRecycler() {
        binding.MainRecycler.addItemDecoration(RecyclerDecoration(5, 5, 0, 0))
        binding.MainRecycler.adapter = adapter
        binding.MainRecycler.setHasFixedSize(true)

        val list = mutableListOf<DBItem>()
        val keyList = mutableListOf<String>()

        database.orderByChild("Month").equalTo(viewModel.newMonth.value)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    keyList.clear()
                    for (datasnapshot in snapshot.children) {
                        val listItem = datasnapshot.getValue(DBItem::class.java)
                        if (listItem != null) {
                            list.add(listItem)
                            keyList.add(datasnapshot.key!!)
                        }
                    }
                    list.reverse()
                    keyList.reverse()
                    adapter.setRecycler(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        adapter.recyclerClickListener(object : RecyclerAdapter.RecyclerClickListener{
            override fun onClick(view: View, position: Int) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("You Really wanna Delete this \"${list[position].Reason}\" Memo wrote on ${list[position].Month} / ${list[position].Date} at ${list[position].Year}")
                builder.setPositiveButton("Yes", { dialog, which ->
                    changeDatabase(list, position, keyList)
                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                })
                builder.setNegativeButton("No", {dialog, whick ->
                    Toast.makeText(context, "Canceled Delete", Toast.LENGTH_SHORT).show()
                })
                builder.show()
            }
        })
    }

    private fun changeDatabase(list : MutableList<DBItem>, position : Int, keyList : MutableList<String>) {
        database.child(keyList[position]).setValue(null)
        when (list[position].Type) {
            "Increase" -> {
                val map = mutableMapOf<String, Any>()
                map["Cost"] = binding.MainMyWallet.text.toString().toInt() - list[position].Cost.toInt()
                fireStoreCost.update(map)

                val incMap = mutableMapOf<String, Any>()
                incMap[list[position].Month] = binding.MainIncrease.text.toString().toInt() - list[position].Cost.toInt()
                fireStoreIncrease.update(incMap)
                setBoard()
            }
            "Decrease" -> {
                val map = mutableMapOf<String, Any>()
                map["Cost"] = binding.MainMyWallet.text.toString().toInt() + list[position].Cost.toInt()
                fireStoreCost.update(map)

                val decMap = mutableMapOf<String, Any>()
                decMap[list[position].Month] = binding.MainDecrease.text.toString().toInt() - list[position].Cost.toInt()
                fireStoreDecrease.update(decMap)
                setBoard()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            0 -> setBoard()
        }
    }
}