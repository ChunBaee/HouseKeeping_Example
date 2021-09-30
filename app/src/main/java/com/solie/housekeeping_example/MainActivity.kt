package com.solie.housekeeping_example

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.solie.housekeeping_example.databinding.ActivityMainBinding
import com.solie.housekeeping_example.dialog.SelectDialog
import com.solie.housekeeping_example.fragment.MainFragment
import com.solie.housekeeping_example.util.FirebaseData
import com.solie.housekeeping_example.util.HouseKeepViewModel
import com.solie.housekeeping_example.util.NetworkStatus

class MainActivity : AppCompatActivity(), FirebaseData {

    private lateinit var binding: ActivityMainBinding
    private val mainFragment = MainFragment()
    private val model : HouseKeepViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                HouseKeepViewModel() as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        networkCheck()
        setToolbar()
        setFragment()
    }

    private fun networkCheck() {
        val networkStatus = NetworkStatus(applicationContext).checkNetworkStatus()
        if (!networkStatus) {
            Toast.makeText(applicationContext, "인터넷에 연결되지 않았습니다.", Toast.LENGTH_LONG).show()
            finish()
        } else {
            userAuthCheck()
        }
    }

    private fun userAuthCheck() {
        if (currentUser == null) {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.MainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
    }

    private fun setFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.MainContainer, mainFragment).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.Select_Month -> {
                Toast.makeText(applicationContext, "다른월 설정", Toast.LENGTH_SHORT).show()
                val dialog = SelectDialog()
                dialog.show(supportFragmentManager, "SelectDialog")
            }
        }

        return super.onOptionsItemSelected(item)
    }
}