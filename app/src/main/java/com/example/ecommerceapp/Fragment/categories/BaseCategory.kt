package com.example.ecommerceapp.Fragment.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceapp.Adapter.BestProductAdapter
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.FragmentBaseCategoryBinding

open class BaseCategory:Fragment(R.layout.fragment_base_category) {
    private lateinit var binding:FragmentBaseCategoryBinding
    private lateinit var offerAdapter: BestProductAdapter
    private lateinit var bestProductAdapter:BestProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstance: Bundle?
    ): View? {
        binding =FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOfferRv()
        setupBestProductsRv()

    }

    private fun setupBestProductsRv() {
        bestProductAdapter = BestProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdapter
        }
    }

    private fun setupOfferRv() {
       offerAdapter  = BestProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
            adapter = offerAdapter
        }
    }

}