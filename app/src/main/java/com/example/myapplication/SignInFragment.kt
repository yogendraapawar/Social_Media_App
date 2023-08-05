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
import android.widget.Toast
import com.example.myapplication.activities.MainActivity
import com.example.myapplication.Schema.User
import com.example.myapplication.databinding.FragmentSignInBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class SignInFragment : Fragment() {
    private lateinit var input: String
    private lateinit var password: String
    private lateinit var databasePassword: String
    private lateinit var query: com.google.firebase.firestore.Query
    private val db = Firebase.firestore
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        val view = binding.root
        val usersReference = db.collection("users")

        binding.signInButton.setOnClickListener {

            input = binding.emailText.text.toString().trim()
            password = binding.passwordText.text.toString().trim()

            val pref = requireActivity().getSharedPreferences("LOG_IN", Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putBoolean("isLoggedIn", true)
            editor.putString("emailOrUsername", input)
            editor.apply()

            if (input.isEmpty() || password.isEmpty()) {
                context?.showShortToast("Please fill in all fields")
                return@setOnClickListener
            }

            query = if (isEmail(input)) {
                usersReference.whereEqualTo("email", input)
            } else {
                usersReference.whereEqualTo("username", input)
            }


            query.get().addOnSuccessListener { querySnapShot ->
                if (querySnapShot.isEmpty) {
                    context?.showShortToast("Account doesn't exist")
                } else {
                    for (document in querySnapShot) {
                        val user = document.toObject<User>()
                        Log.d(TAG, user.email.toString())
                        databasePassword = user.password.toString()
                    }
                    if (databasePassword == password) {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        context?.showShortToast("Wrong password")
                    }
                }

            }.addOnFailureListener { exception ->
                context?.showShortToast("Error while logging in ($exception)")
            }

        }

        binding.signUpText.setOnClickListener {
            replaceFragment(SignUpFragment())
        }


        return view
    }

    private fun Context.showShortToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
