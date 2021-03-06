package com.argyleexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log

import com.argyle.ArgyleConfig

import kotlinx.android.synthetic.main.main_activity.*
import com.argyle.Argyle
import com.argyle.ArgyleErrorType

private const val TAG = "MainActivity"

private const val PLUGIN_KEY = "YOUR_PLUGIN_KEY"
private const val API_HOST = "https://api-sandbox.argyle.io/v1"

//  https://argyle.com/docs/pay-distributions-guide/link-integration
private const val YOUR_PD_CONFIG = "YOUR_PD_CONFIG"

private const val PREF_USER_TOKEN = "userToken"
private const val PREF_USER_ID = "userId"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        configureArgyleSdk(null)

        newWorkerButton?.setOnClickListener {
            Argyle.instance.config.userToken = null // nullify user token
            openSdk()
        }

        existingButton?.setOnClickListener {
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val token = preferences.getString(PREF_USER_TOKEN, null)
            Argyle.instance.config.userToken = token // change user token
            openSdk()
        }
    }

    private fun openSdk() {
        Argyle.instance.startSDK(this)
    }

    private fun configureArgyleSdk(token: String? = null) {
        val argyle = Argyle.instance

        Log.d(TAG, "openSdk with token : $token")

        val config = ArgyleConfig.Builder()

            .loginWith(PLUGIN_KEY, API_HOST, token)
//            .linkItems(arrayOf("uber"))
//            .payDistributionConfig(YOUR_PD_CONFIG)
//            .payDistributionItemsOnly(true)
//            .payDistributionUpdateFlow(true)
            .setCallbackListener(object : Argyle.ArgyleResultListener {

                override fun onTokenExpired(handler: (String) -> Unit) {
                    val token = "token"
                    handler(token)
                }

                override fun onAccountCreated(accountId: String, userId: String, linkItemId:String) {
                    Log.d(TAG, "onAccountCreated: accountId: $accountId workerId: $userId linkItemId: $linkItemId")
                }

                override fun onAccountConnected(accountId: String, userId: String, linkItemId:String) {
                    Log.d(TAG, "onAccountConnected: accountId: $accountId workerId: $userId linkItemId: $linkItemId")
                }

                override fun onAccountUpdated(accountId: String, userId: String, linkItemId:String) {
                    Log.d(TAG, "onAccountUpdated: accountId: $accountId workerId: $userId linkItemId: $linkItemId")
                }

                override fun onAccountRemoved(accountId: String, userId: String, linkItemId:String) {
                    Log.d(TAG, "onAccountRemoved: accountId: $accountId workerId: $userId linkItemId: $linkItemId")
                }

                override fun onAccountError(accountId: String, userId: String, linkItemId: String
                ) {
                    Log.d(TAG, "onAccountError: accountId: $accountId workerId: $userId linkItemId: $linkItemId")
                }

                override fun onError(error: ArgyleErrorType) {
                    Log.d(TAG, "onError: error: $error")
                }

                override fun onUserCreated(userToken: String, userId: String) {
                    val preferences =
                        PreferenceManager.getDefaultSharedPreferences(applicationContext)
                    val editor = preferences.edit()
                    editor.putString(PREF_USER_TOKEN, userToken)
                    editor.putString(PREF_USER_ID, userId)
                    editor.apply()
                    Log.d(
                        TAG,
                        "onUserCreated:  userId: $userId userToken: $userToken"
                    )
                }

                override fun onClose() {
                    Log.d(TAG, "onClose")
                }

                override fun onPayDistributionError(
                    accountId: String,
                    userId: String,
                    linkItemId: String
                ) {
                    Log.d(
                        TAG,
                        "onPayDistributionError: accountId: $accountId userId: $userId linkItemId: $linkItemId"
                    )
                }

                override fun onPayDistributionSuccess(
                    accountId: String,
                    userId: String,
                    linkItemId: String
                ) {
                    Log.d(
                        TAG,
                        "onPayDistributionSuccess: accountId: $accountId userId: $userId linkItemId: $linkItemId"
                    )
                }

            })
            .build()

        argyle.init(config)
    }

}
