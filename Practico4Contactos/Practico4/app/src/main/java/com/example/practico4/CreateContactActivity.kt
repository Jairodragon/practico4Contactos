package com.example.practico4

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.practico4.api.ApiClient
import com.example.practico4.api.ApiService
import com.example.practico4.databinding.CreatecontactBinding
import com.example.practico4.models.Contact
import com.example.practico4.models.Email
import com.example.practico4.models.Phone
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateContactActivity : AppCompatActivity() {

    private lateinit var binding: CreatecontactBinding
    private lateinit var apiService: ApiService
    private var selectedImageUri: Uri? = null
    private val phones = mutableListOf<Phone>()
    private val emails = mutableListOf<Email>()

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            binding.contactImageView.setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreatecontactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getClient().create(ApiService::class.java)

        binding.addImageButton.setOnClickListener {
            openGallery()
        }

        binding.saveContactButton.setOnClickListener {
            saveContact()
        }

        binding.addPhoneButton.setOnClickListener {
            addPhoneField()
        }

        binding.addEmailButton.setOnClickListener {
            addEmailField()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun addPhoneField() {
        val phoneView = LayoutInflater.from(this).inflate(R.layout.phone_item, binding.phoneContainer, false) as LinearLayout
        binding.phoneContainer.addView(phoneView)
    }

    private fun addEmailField() {
        val emailView = LayoutInflater.from(this).inflate(R.layout.email_item, binding.emailContainer, false) as LinearLayout
        binding.emailContainer.addView(emailView)
    }

    private fun saveContact() {
        val name = binding.contactName.text.toString()
        val lastName = binding.contactLastName.text.toString()
        val company = binding.companyEditText.text.toString()
        val address = binding.addressEditText.text.toString()
        val city = binding.cityEditText.text.toString()
        val state = binding.stateEditText.text.toString()

        if (name.isBlank() || lastName.isBlank()) {
            Toast.makeText(this, "Nombre y apellido son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener números de teléfono y etiquetas
        phones.clear()
        for (i in 0 until binding.phoneContainer.childCount) {
            val phoneView = binding.phoneContainer.getChildAt(i) as LinearLayout
            val phoneEditText = phoneView.findViewById<EditText>(R.id.phoneEditText)
            val phoneSpinner = phoneView.findViewById<Spinner>(R.id.phoneSpinner)

            val phoneLabel = phoneSpinner.selectedItem.toString()
            val phoneNumber = phoneEditText.text.toString()

            if (phoneNumber.isNotBlank()) {
                phones.add(Phone(id = 0, label = phoneLabel, number = phoneNumber))
            }
        }

        // Obtener correos electrónicos y etiquetas
        emails.clear()
        for (i in 0 until binding.emailContainer.childCount) {
            val emailView = binding.emailContainer.getChildAt(i) as LinearLayout
            val emailEditText = emailView.findViewById<EditText>(R.id.emailEditText)
            val emailSpinner = emailView.findViewById<Spinner>(R.id.emailSpinner)

            val emailLabel = emailSpinner.selectedItem.toString()
            val emailAddress = emailEditText.text.toString()

            if (emailAddress.isNotBlank()) {
                emails.add(Email(id = 0, label = emailLabel, email = emailAddress))
            }
        }

        val newContact = Contact(
            id = 0,
            name = name,
            last_name = lastName,
            company = company,
            address = address,
            city = city,
            state = state,
            profile_picture = selectedImageUri?.toString(),
            phones = phones,
            emails = emails
        )

        apiService.addContact(newContact).enqueue(object : Callback<Contact> {
            override fun onResponse(call: Call<Contact>, response: Response<Contact>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateContactActivity, "Contacto agregado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    showError("Error al agregar contacto")
                }
            }

            override fun onFailure(call: Call<Contact>, t: Throwable) {
                showError("Error de red: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
