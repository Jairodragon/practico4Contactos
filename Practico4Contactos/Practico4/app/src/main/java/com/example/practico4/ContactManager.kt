package com.example.practico4

import android.content.Context
import android.widget.Toast
import com.example.practico4.api.ApiService
import com.example.practico4.models.Contact
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactManager(private val context: Context, private val apiService: ApiService) {

    private var toast: Toast? = null

    fun guardarContacto(contacto: Contact) {
        apiService.addContact(contacto).enqueue(object : Callback<Contact> {
            override fun onResponse(call: Call<Contact>, response: Response<Contact>) {
                if (response.isSuccessful) {
                    mostrarToast("Contacto guardado exitosamente")
                } else {
                    mostrarToast("Error al guardar el contacto")
                }
            }

            override fun onFailure(call: Call<Contact>, t: Throwable) {
                mostrarToast("Fallo en la conexión: ${t.message}")
            }
        })
    }

    private fun mostrarToast(mensaje: String) {
        toast?.cancel()
        toast = Toast.makeText(context, mensaje, Toast.LENGTH_SHORT)
        toast?.show()
    }
}
