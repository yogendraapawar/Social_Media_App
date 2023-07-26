package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.R

class Authentication : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        val r = Runnable {
            val pref =  getSharedPreferences("LOG_IN", MODE_PRIVATE)
            val check=pref.getBoolean("isLoggedIn", false)
            val i: Intent
            if(check){
                i= Intent(this, MainActivity::class.java)
                startActivity(i)
            }else{
                loadFragment(IntroFragment())
            }
        }
        Handler(Looper.getMainLooper()).postDelayed(r, 1000)






    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.introContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}