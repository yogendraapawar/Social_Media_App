package com.example.myapplication.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.bottomnavigation.CreateFragment
import com.example.myapplication.bottomnavigation.HomeFragment
import com.example.myapplication.bottomnavigation.ProfileFragment
import com.example.myapplication.bottomnavigation.SearchFragment
import com.example.myapplication.bottomnavigation.ExploreFragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    // Binding object to access views in layout
    private lateinit var binding: ActivityMainBinding

    // The bottom navigation view
    private lateinit var bottomNavigation: BottomNavigationView

    // Count to keep track of number of back button presses
    private var prevBackStackSize = 0

    private var count = 1
    private val frag = CreateFragment()

    private var backPressed = false
    private val MAX_IMAGE_COUNT = 5
    private val IMAGE_REQUEST_CODE = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using the binding object
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Access the bottom navigation view and create object
        bottomNavigation = binding.bottomnavigation

        // Replace the initial fragment with the home fragment
        replaceFragment(HomeFragment(), "home")

        // Set the listener for when a bottom navigation item is selected
        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment(), "home")
                    true
                }

                R.id.search -> {
                    replaceFragment(SearchFragment(), "search")
                    true
                }

                R.id.createpost -> {
                    //pickImagesIntent()
                    requestImagePermission()
//                    replaceFragment(CreateFragment(), "createpost")
                    false
                }

                R.id.Explore -> {
                    replaceFragment(ExploreFragment(), "workout")
                    true
                }

                R.id.profile -> {
                    replaceFragment(ProfileFragment(), "profile")
                    true
                }

                else -> false
            }
        }
        prevBackStackSize = supportFragmentManager.backStackEntryCount
        // Create a callback for when the back button is pressed

        supportFragmentManager.addOnBackStackChangedListener {
            val stackSize = supportFragmentManager.backStackEntryCount

            if (stackSize < prevBackStackSize) {
                if (count == 0) {
                    finishAffinity()
                }
                backPressed = true
                // A fragment has been popped from the back stack
                // Perform any desired actions or update UI accordingly

                count--
                val backStackCount = supportFragmentManager.backStackEntryCount
                if (backStackCount > 0) {
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
                    checkBottomNavIcon(currentFragment)

                } else {
                    finishAffinity()
                }
            } else if (stackSize > prevBackStackSize) {
                if (count < 3) {
                    count++
                }
            }
            prevBackStackSize = stackSize
        }

        // Add the callback to the back button dispatcher
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun requestImagePermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    IMAGE_REQUEST_CODE
                )
            }
            else -> {
                pickImagesIntent()
            }
        }
    }


    private fun checkBottomNavIcon(currentFragment: Fragment?) {
        val menu = bottomNavigation.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            menuItem.isChecked = false
        }
        when (currentFragment) {
            is HomeFragment -> menu.findItem(R.id.home).isChecked = true
            is SearchFragment -> menu.findItem(R.id.search).isChecked = true
            is ProfileFragment -> menu.findItem(R.id.profile).isChecked = true
            is ExploreFragment -> menu.findItem(R.id.Explore).isChecked = true
            is CreateFragment -> menu.findItem(R.id.createpost).isChecked = true
            // Add more cases for other fragments
            // Use the menu item ID corresponding to each fragment's icon
        }
    }

    private fun pickImagesIntent() {
        val intent = Intent().apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            action = Intent.ACTION_GET_CONTENT
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        resultLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            IMAGE_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    pickImagesIntent()
                } else {
                    Toast.makeText(
                        this,
                        "Please grant permission for picking image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {

                    val bundle = Bundle()
                    if (data.clipData != null) {
                        //multiple images
                        val count = minOf(data.clipData?.itemCount ?: 0, MAX_IMAGE_COUNT)

                        bundle.putInt("count", count)
                        for (i in 0 until count) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            bundle.putString("image$i", imageUri.toString())
                        }

                        frag.arguments = bundle
                    } else {
                        val imageUri = data.data
                        if (imageUri != null) {


                            bundle.putInt("count", 1)
                            bundle.putString("image0", imageUri.toString())
                            frag.arguments = bundle
                        }
                    }
                    replaceFragment(frag, "createpost")
                    val menu = bottomNavigation.menu
                    menu.findItem(R.id.createpost).isChecked = true

                } else {
                    val current =
                        supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)

                    val currentId = current.id
                    bottomNavigation.selectedItemId = currentId
                }


            }
        }


    // Function to replace the current fragment with a new one
    private fun replaceFragment(fragment: Fragment, name: String, onSuccess: (() -> Unit)? = null) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(name)
            .commit()
        onSuccess?.invoke()
    }


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //showing dialog and then closing the application..
            if (count == 0) {
                finishAffinity()
            } else {
                supportFragmentManager.popBackStack()

            }
        }
    }


}
