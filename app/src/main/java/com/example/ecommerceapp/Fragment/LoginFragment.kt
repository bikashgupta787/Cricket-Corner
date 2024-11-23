package com.example.ecommerceapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecommerceapp.Activity.ShoppingActivity
import com.example.ecommerceapp.R
import com.example.ecommerceapp.Utils.Resource
import com.example.ecommerceapp.ViewModel.LoginViewModel
import com.example.ecommerceapp.databinding.FragmentLoginBinding
import com.example.ecommerceapp.dialog.setupBottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding:FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonLogin.setOnClickListener{
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString().trim()
                viewModel.login(email,password)
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            setupBottomSheetDialog { email->
                viewModel.resetPswd(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPswd.collect{
                when(it){
                    is Resource.Loading->{
                    }
                    is Resource.Success->{
                        Snackbar.make(requireView(),"Reset link sent to email",Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error->{
                        Snackbar.make(requireView(),"Error: ${it.message}",Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect{
                when(it){
                    is Resource.Loading->{
                        binding.buttonLogin.visibility = View.VISIBLE
                    }
                    is Resource.Success->{
                        binding.buttonLogin.visibility = View.VISIBLE
                        Intent(requireActivity(),ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error-> {
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                        binding.buttonLogin.visibility = View.VISIBLE
                    }
                    else -> Unit
                }
            }
        }
    }

}