package com.solie.housekeeping_example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.solie.housekeeping_example.util.FirebaseData

class LoginActivity : AppCompatActivity(), FirebaseData {
    private val REQUEST_SIGN_IN = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signIn()
    }

    private fun signIn() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setTheme(getSelectedTheme())
                .setLogo(getSelectedLogo())
                .setAvailableProviders(getProviders())
                .setTosAndPrivacyPolicyUrls("https://naver.com", "https://google.com")
                .setIsSmartLockEnabled(false)
                .build(), REQUEST_SIGN_IN
        )
    }

    private fun getSelectedTheme() : Int {
        return AuthUI.getDefaultTheme()
    }

    private fun getSelectedLogo() : Int {
        return AuthUI.NO_LOGO
    }

    private fun getProviders() : MutableList<AuthUI.IdpConfig> {
        var providers : MutableList<AuthUI.IdpConfig> = mutableListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        return providers
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_SIGN_IN) {
            if(resultCode == RESULT_OK) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            } else {
                signIn()
            }
        }
    }
}