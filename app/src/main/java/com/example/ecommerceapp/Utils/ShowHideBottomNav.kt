package com.example.ecommerceapp.Utils

import android.view.View
import androidx.fragment.app.Fragment
import com.example.ecommerceapp.Activity.ShoppingActivity
import com.example.ecommerceapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigation= (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigationView
    )
    bottomNavigation.visibility = View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigation= (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigationView
    )
    bottomNavigation.visibility = View.VISIBLE
}