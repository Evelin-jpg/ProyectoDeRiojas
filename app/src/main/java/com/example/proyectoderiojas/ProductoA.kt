package com.example.proyectoderiojas

import com.google.gson.annotations.SerializedName

data class ProductoA (
    @SerializedName("idProducto")
    val idProducto: Int,
    val clave: String,
    val nombre: String,
    val precio: String,
    val cantidad: String,
    val status: Int
)