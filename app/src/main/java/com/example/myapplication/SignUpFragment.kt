package com.example.myapplication

import android.content.ContentValues.TAG
import android.os.Build.VERSION_CODES.N
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import com.example.myapplication.databinding.FragmentSignUpBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.example.myapplication.Schema.User
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {
    private lateinit var firstname: String
    private lateinit var lastname: String
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.signUpButton.setOnClickListener {
            username = binding.usernameText.text.toString().trim()
            email = binding.emailText.text.toString().trim()
            password = binding.passwordText.text.toString().trim()
            firstname  = binding.firstNameText.text.toString().trim()
            lastname  = binding.lastNameText.text.toString().trim()

            if (firstname.isEmpty()||lastname.isEmpty()||email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!isEmail(email)){
                Toast.makeText(context, "Enter valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val user = hashMapOf(
                "firstname" to firstname,
            "lastname" to lastname,
                "email" to email,
                "password" to password,
                "username" to username,
                "searchByUsername" to username.lowercase(),
                "searchByName" to "$firstname$lastname".lowercase()

            )

            doesExist("email", email) { emailExists ->
                if (!emailExists) {
                    doesExist("username", username) { usernameExists ->
                        if (!usernameExists) {
                            addUser(user)
                        }
                    }
                }
            }
        }

        binding.signInTextView.setOnClickListener {
            replaceFragment(SignInFragment())
        }

        return view
    }

    private fun doesExist(key: String, value: String, callback: (Boolean) -> Unit) {
        db.collection("users")
            .whereEqualTo(key, value)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val exists = !querySnapshot.isEmpty
                callback(exists)
                if (exists) {
                    Toast.makeText(context, "$key exists, use another $key", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error querying document", e)
                callback(false)
            }
    }

    private fun addUser(user: Any) {
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                replaceFragment(SignInFragment())
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.introContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
    private fun isEmail(input: String): Boolean {
        for (i in input.indices) {
            if (input[i] == '@') return true
        }
        return false
    }
}