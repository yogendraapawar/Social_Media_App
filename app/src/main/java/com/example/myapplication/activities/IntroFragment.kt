package com.example.myapplication.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.myapplication.R
import com.example.myapplication.SignInFragment
import com.example.myapplication.SignUpFragment
import com.example.myapplication.bottomnavigation.HomeFragment


class IntroFragment : Fragment() {

    private lateinit var IntroSignInButton: Button
    private lateinit var IntroSignUpButton: Button
    private lateinit var IntroExploreButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_intro, container, false)
        IntroSignInButton=view.findViewById(R.id.intro_sign_in_button)
        IntroSignUpButton=view.findViewById(R.id.intro_sign_up_button)
        IntroExploreButton=view.findViewById(R.id.intro_explore_button)
        IntroSignInButton.setOnClickListener{
            loadFragment( SignInFragment())
        }
        IntroSignUpButton.setOnClickListener{
            loadFragment( SignUpFragment())
        }
        IntroExploreButton.setOnClickListener{
            loadFragment(HomeFragment())
        }

        return view
    }
    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.introContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

}