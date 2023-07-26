package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.activities.MainActivity
import com.example.myapplication.R.*
import com.example.myapplication.Schema.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class SignInFragment : Fragment() {
    private lateinit var username: String
    private lateinit var input: String
    private lateinit var password: String
    private lateinit var databasePassword:String
    private  lateinit var query:com.google.firebase.firestore.Query
    private val db= Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(layout.fragment_sign_in, container, false)

        val usersReference=db.collection("users")

        view.findViewById<Button>(R.id.sign_in_button).setOnClickListener{

            input=view.findViewById<TextInputEditText>(R.id.email_text).text.toString().trim()
            password=view.findViewById<TextInputEditText >(R.id.password_text).text.toString().trim()

            val pref=requireActivity().getSharedPreferences("LOG_IN", Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putBoolean("isLoggedIn", true)
            editor.putString("emailOrUsername", input)
            editor.apply()
            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(isEmail(input)){
                query=usersReference.whereEqualTo("email",input)
            }else{
                query=usersReference.whereEqualTo("username", input)
            }


            query.get().addOnSuccessListener{
                querySnapShot->
                if(querySnapShot.isEmpty){
                    Toast.makeText(context, "Account doesn't exist", Toast.LENGTH_SHORT).show()
                }else{
                    for(document in querySnapShot){
                        val user=document.toObject<User>()
                        Log.d(TAG, user.email.toString())
                        databasePassword=user.password.toString()
                    }
                    if(databasePassword.equals(password)){
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }else{
                        Toast.makeText(context, "Wrong Password", Toast.LENGTH_SHORT).show()
                    }
                }

            }.addOnFailureListener{
                exception->
                Toast.makeText(context, "Error logging in", Toast.LENGTH_SHORT).show()
            }



        }

        view.findViewById<TextView>(R.id.sign_up_text).setOnClickListener{
            replaceFragment(SignUpFragment())
        }


        return view
    }

    private fun isEmail(input: String): Boolean {
        for (i in input.indices) {
            if (input[i] == '@') return true
        }
        return false
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.introContainer, fragment)
            .addToBackStack(null)
            .commit()
    }


}