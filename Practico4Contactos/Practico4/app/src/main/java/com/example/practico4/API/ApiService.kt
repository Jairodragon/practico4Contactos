package com.example.practico4.api

import com.example.practico4.models.Contact
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("personas")
    fun getContacts(): Call<List<Contact>>

    @POST("personas")
    fun addContact(@Body contact: Contact): Call<Contact>

    @PUT("personas/{id}")
    fun updateContact(@Path("id") id: Int, @Body contact: Contact): Call<Contact>

    @DELETE("personas/{id}")
    fun deleteContact(@Path("id") id: Int): Call<Void>

    @GET("search")
    fun searchContacts(@Query("q") query: String): Call<List<Contact>>

    @POST("personas/{id}/profile-picture")
    @Multipart
    fun uploadProfilePicture(@Path("id") id: Int, @Part image: MultipartBody.Part): Call<Void>
}
