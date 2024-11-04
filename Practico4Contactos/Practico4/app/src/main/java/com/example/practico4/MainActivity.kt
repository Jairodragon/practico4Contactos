package com.example.practico4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practico4.api.ApiClient
import com.example.practico4.api.ApiService
import com.example.practico4.models.Contact
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ContactAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var addContactButton: Button
    private val apiService = ApiClient.getClient().create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        addContactButton = findViewById(R.id.addContactButton)

        adapter = ContactAdapter(listOf(), { contact -> editContact(contact) }, { contact -> deleteContact(contact) })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchContacts(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchContacts(newText) }
                return false
            }
        })

        addContactButton.setOnClickListener {
            startActivity(Intent(this, CreateContactActivity::class.java))
        }

        fetchContacts()
    }

    override fun onResume() {
        super.onResume()
        fetchContacts()
    }

    private fun fetchContacts() {
        apiService.getContacts().enqueue(object : Callback<List<Contact>> {
            override fun onResponse(call: Call<List<Contact>>, response: Response<List<Contact>>) {
                if (response.isSuccessful) {
                    response.body()?.let { contacts ->
                        adapter.setContacts(contacts)
                    }
                }
            }

            override fun onFailure(call: Call<List<Contact>>, t: Throwable) {
                // Manejar error de red
            }
        })
    }

    private fun searchContacts(query: String) {
        apiService.searchContacts(query).enqueue(object : Callback<List<Contact>> {
            override fun onResponse(call: Call<List<Contact>>, response: Response<List<Contact>>) {
                if (response.isSuccessful) {
                    response.body()?.let { contacts ->
                        adapter.setContacts(contacts)
                    }
                }
            }

            override fun onFailure(call: Call<List<Contact>>, t: Throwable) {
                // Manejar error de red
            }
        })
    }

    private fun editContact(contact: Contact) {
        val intent = Intent(this, CreateContactActivity::class.java).apply {
            putExtra("contact", contact)
        }
        startActivity(intent)
    }

    private fun deleteContact(contact: Contact) {
        apiService.deleteContact(contact.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    fetchContacts()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Manejar error de red
            }
        })
    }
}
