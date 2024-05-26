package com.example.ecommerceapp.Admin

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.ActivityAdminBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

class AdminActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAdminBinding.inflate(layoutInflater) }
    private var selectedImages = mutableListOf<Uri>()
    private var selectedColors = mutableListOf<Int>()
    private val productsStorage = Firebase.storage.reference
    private val firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonColorPicker.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle("Product color")
                .setPositiveButton("Select", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                       envelope?.let {
                           selectedColors.add(it.color)
                           updateColors()
                       }
                    }
                })
                .setNegativeButton("cancel") {colorPicker, _ ->
                    colorPicker.dismiss()
                }.show()

        }

        val selectedImagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
                if (result.resultCode == RESULT_OK){
                    val intent = result.data

                    //Multiple images selected
                    if (intent?.clipData !=null){
                        val count = intent.clipData?.itemCount?:0
                        (0 until count).forEach {
                            val imageUri = intent.clipData?.getItemAt(it)?.uri
                            imageUri?.let{
                                selectedImages.add(it)
                            }
                        }
                    } else{
                        val imageUri = intent?.data
                        imageUri?.let { selectedImages.add(it)}
                    }
                    updateImages()
                }

            }

        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            intent.type = "image/*"
            selectedImagesActivityResult.launch(intent)
        }


    }

    private fun updateImages() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }

    private fun updateColors() {
        var colors = ""
        selectedColors.forEach{
            colors = "$colors ${Integer.toHexString(it)}"
        }
        binding.tvSelectedColors.text = colors
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.saveProduct){
            val productValidation = validateInfo()
            if(!productValidation){
                Toast.makeText(this,"Check ur inputs",Toast.LENGTH_SHORT).show()
                return false
            }
            saveProduct()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validateInfo(): Boolean {
        if (binding.edPrice.text.toString().trim().isEmpty())
            return false

        if (binding.edName.text.toString().trim().isEmpty())
            return false

        if (binding.edCategory.text.toString().trim().isEmpty())
            return false

        if (selectedImages.isEmpty())
            return false

        return true
    }

    private fun saveProduct() {
        val name = binding.edName.text.toString().trim()
        val category = binding.edCategory.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val offerPercentage = binding.offerPercentage.text.toString().trim()
        val description = binding.edDescription.text.toString().trim()
        val sizes = getSizesList(binding.edSizes.text.toString().trim())
        val imagesByteArrays = getImagesBytesArrays()
        val images = mutableListOf<String>()

        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){ showLoading()}

            try {
                async{
                    imagesByteArrays.forEach{
                        val id = UUID.randomUUID().toString()
                        launch{
                            val imageStorage = productsStorage.child("products/images/$id")
                            val result = imageStorage.putBytes(it).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }
                }.await()
            }catch (e:java.lang.Exception){
                e.printStackTrace()
                withContext(Dispatchers.Main){ hideLoading()}
            }

            val product = AdminProduct(
                UUID.randomUUID().toString(),
                name,
                category,
                price.toFloat(),
                if (offerPercentage.isEmpty()) null else offerPercentage.toFloat(),
                if(description.isEmpty()) null else description,
                if (selectedColors.isEmpty()) null else selectedColors,
                sizes,
                images
            )

            firestore.collection("Products").add(product).addOnSuccessListener {
                hideLoading()
            }.addOnFailureListener{
                hideLoading()
                Log.e("Error",it.message.toString())
            }
        }
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun getImagesBytesArrays():List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach{
            val stream = ByteArrayOutputStream()
            val imageBmp = MediaStore.Images.Media.getBitmap(contentResolver,it)
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG,100,stream)){
                imagesByteArray.add(stream.toByteArray())
            }
        }
        return imagesByteArray
    }



    private fun getSizesList(sizesStr: String): List<String>? {
        if (sizesStr.isEmpty())
            return null
        val sizesList = sizesStr.split(",")
        return sizesList

    }
}