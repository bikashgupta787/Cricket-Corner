package com.example.ecommerceapp.Fragment.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecommerceapp.Adapter.HomeViewPagerAdapter
import com.example.ecommerceapp.Fragment.categories.AccessoryFragment
import com.example.ecommerceapp.Fragment.categories.ChairFragment
import com.example.ecommerceapp.Fragment.categories.CupboardFragment
import com.example.ecommerceapp.Fragment.categories.FurnitureFragment
import com.example.ecommerceapp.Fragment.categories.MainCategory
import com.example.ecommerceapp.Fragment.categories.TableFragment
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment:Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf<Fragment>(
            MainCategory(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        binding.viewpagerHome.isUserInputEnabled = false

        val viewPager2Adapter = HomeViewPagerAdapter(categoriesFragments,childFragmentManager,lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout,binding.viewpagerHome) {tab,position->
            when(position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Bat"
                2 -> tab.text = "Ball"
                3 -> tab.text = "Kits"
                4 -> tab.text = "Accessory"
                5 -> tab.text = "Jersey"
            }
        }.attach()
    }
}