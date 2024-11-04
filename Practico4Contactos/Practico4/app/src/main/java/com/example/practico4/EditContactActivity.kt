package com.example.practico4
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

class EditContactActivity : AppCompatActivity() {

    private lateinit var binding: CreatecontactBinding
    private lateinit var apiService: ApiService
    private var selectedImageUri: Uri? = null
    private val phones = mutableListOf<Phone>()
    private val emails = mutableListOf<Email>()
    private var contactId: Int = 0

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

        // Obtener datos del Intent
        val contact = intent.getSerializableExtra("contact") as Contact
        contactId = contact.id

        // Cargar los datos en los campos
        loadContactData(contact)

        binding.saveContactButton.setOnClickListener {
            updateContact()
        }

        binding.addPhoneButton.setOnClickListener {
            addPhoneField()
        }

        binding.addEmailButton.setOnClickListener {
            addEmailField()
        }
    }

    private fun loadContactData(contact: Contact) {
        binding.contactName.setText(contact.name)
        binding.contactLastName.setText(contact.last_name)
        binding.companyEditText.setText(contact.company)
        binding.addressEditText.setText(contact.address)
        binding.cityEditText.setText(contact.city)
        binding.stateEditText.setText(contact.state)
        selectedImageUri = Uri.parse(contact.profile_picture)
        binding.contactImageView.setImageURI(selectedImageUri)

        // Cargar teléfonos
        contact.phones?.forEach { phone ->
            val phoneView = LayoutInflater.from(this).inflate(R.layout.phone_item, binding.phoneContainer, false) as LinearLayout
            val phoneEditText = phoneView.findViewById<EditText>(R.id.phoneEditText)
            val phoneSpinner = phoneView.findViewById<Spinner>(R.id.phoneSpinner)
            phoneEditText.setText(phone.number)
            phoneSpinner.setSelection(getPhoneLabelIndex(phone.label))
            binding.phoneContainer.addView(phoneView)
        }

        // Cargar correos electrónicos
        contact.emails?.forEach { email ->
            val emailView = LayoutInflater.from(this).inflate(R.layout.email_item, binding.emailContainer, false) as LinearLayout
            val emailEditText = emailView.findViewById<EditText>(R.id.emailEditText)
            val emailSpinner = emailView.findViewById<Spinner>(R.id.emailSpinner)
            emailEditText.setText(email.email)
            emailSpinner.setSelection(getEmailLabelIndex(email.label))
            binding.emailContainer.addView(emailView)
        }
    }

    private fun getPhoneLabelIndex(label: String): Int {
        val labels = resources.getStringArray(R.array.phone_labels)
        return labels.indexOf(label)
    }

    private fun getEmailLabelIndex(label: String): Int {
        val labels = resources.getStringArray(R.array.email_labels)
        return labels.indexOf(label)
    }

    private fun addPhoneField() {
        val phoneView = LayoutInflater.from(this).inflate(R.layout.phone_item, binding.phoneContainer, false) as LinearLayout
        binding.phoneContainer.addView(phoneView)
    }

    private fun addEmailField() {
        val emailView = LayoutInflater.from(this).inflate(R.layout.email_item, binding.emailContainer, false) as LinearLayout
        binding.emailContainer.addView(emailView)
    }

    private fun updateContact() {
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

        val updatedContact = Contact(
            id = contactId,
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

        apiService.updateContact(contactId, updatedContact).enqueue(object : Callback<Contact> {
            override fun onResponse(call: Call<Contact>, response: Response<Contact>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditContactActivity, "Contacto actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    showError("Error al actualizar contacto")
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
