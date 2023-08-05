package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import kotlin.contracts.contract

class LoginSharedPreference {
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var context: Context

    var PRIVATE_MODE: Int = 0

    companion object {
        val PREF_NAME = "LoginSharedPreference"
        val IS_LOGIN = "isLoggedIn"
        val KEY_EMAIL_OR_USERNAME = "emailOrUsername"

    }

    constructor(context: Context) {
        this.context = context
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun createLogInSession(emailOrUsername: String) {
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(KEY_EMAIL_OR_USERNAME, emailOrUsername)
        editor.commit()

    }

    fun checkLogIn() {
        if (!this.isLoggedIn()) {
            var i: Intent = Intent(context, SignInFragment()::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
    }

    fun logOutUser() {
        editor.clear()
        editor.commit()
        val i = Intent(context, SignInFragment()::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }

    private fun isLoggedIn(): Boolean {
        return pref.getBoolean(IS_LOGIN, false)
    }


}
