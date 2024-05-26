package com.example.ecommerceapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerceapp.Activity.ShoppingActivity
import com.example.ecommerceapp.R
import com.example.ecommerceapp.ViewModel.IntroductionViewModel
import com.example.ecommerceapp.ViewModel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.example.ecommerceapp.ViewModel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import com.example.ecommerceapp.databinding.FragmentIntroBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class IntroFragment : Fragment(R.layout.fragment_intro) {

    private lateinit var binding: FragmentIntroBinding
    private val viewModel by viewModels<IntroductionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect{
                when(it){
                    SHOPPING_ACTIVITY -> {
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                    }
                }
                    ACCOUNT_OPTIONS_FRAGMENT->{
                        findNavController().navigate(it)
                    }

                    else -> Unit
                }
            }
        }
        binding.btnStart.setOnClickListener{
            viewModel.startButtonClick()
            findNavController().navigate(R.id.action_introFragment_to_accountOptionsFragment)
        }
    }

}