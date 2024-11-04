package com.example.practico4.models

data class Email(
    val id: Int,
    val email: String,
    val label: String // "Persona", "Trabajo", "Universidad" o personalizada
)
