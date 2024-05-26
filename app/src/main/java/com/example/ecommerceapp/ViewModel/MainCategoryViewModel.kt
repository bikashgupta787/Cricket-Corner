package com.example.ecommerceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.example.ecommerceapp.Data.Product
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<com.example.ecommerceapp.Utils.Resource<List<Product>>>(com.example.ecommerceapp.Utils.Resource.Unspecified())
     val specialProducts:StateFlow<com.example.ecommerceapp.Utils.Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts = MutableStateFlow<com.example.ecommerceapp.Utils.Resource<List<Product>>>(com.example.ecommerceapp.Utils.Resource.Unspecified())
    val bestDealsProducts:StateFlow<com.example.ecommerceapp.Utils.Resource<List<Product>>> = _bestDealsProducts

    private val _bestProducts = MutableStateFlow<com.example.ecommerceapp.Utils.Resource<List<Product>>>(com.example.ecommerceapp.Utils.Resource.Unspecified())
    val bestProducts:StateFlow<com.example.ecommerceapp.Utils.Resource<List<Product>>> = _bestProducts

    private val pagingInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
    }
    fun fetchSpecialProducts(){
        viewModelScope.launch {
            _specialProducts.emit(com.example.ecommerceapp.Utils.Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category","Special Products").get().addOnSuccessListener { result ->
                val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(com.example.ecommerceapp.Utils.Resource.Success(specialProductsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(com.example.ecommerceapp.Utils.Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestDeals(){
        viewModelScope.launch {
            _bestDealsProducts.emit(com.example.ecommerceapp.Utils.Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category","Best deals").get().addOnSuccessListener { result ->
                val bestDealsProducts = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealsProducts.emit(com.example.ecommerceapp.Utils.Resource.Success(bestDealsProducts))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsProducts.emit(com.example.ecommerceapp.Utils.Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts(){
        if (!pagingInfo.isPagingEnd){
        viewModelScope.launch {
            _bestProducts.emit(com.example.ecommerceapp.Utils.Resource.Loading())
        }
        firestore.collection("Products").limit(pagingInfo.bestProductsPage * 10).get()
            .addOnSuccessListener { result ->
                val bestProducts = result.toObjects(Product::class.java)
                pagingInfo.isPagingEnd = bestProducts == pagingInfo.oldBestProducts
                pagingInfo.oldBestProducts = bestProducts
                viewModelScope.launch {
                    _bestProducts.emit(com.example.ecommerceapp.Utils.Resource.Success(bestProducts))
                }
                pagingInfo.bestProductsPage++
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(com.example.ecommerceapp.Utils.Resource.Error(it.message.toString()))
                }
            }
    }
    }

    internal data class PagingInfo(
         var bestProductsPage: Long = 1,
         var oldBestProducts: List<Product> = emptyList(),
         var isPagingEnd : Boolean = false
    )

}

