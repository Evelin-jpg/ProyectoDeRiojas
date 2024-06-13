package com.example.proyectoderiojas

import com.google.gson.annotations.SerializedName

data class SocioA (
    @SerializedName("idSocio")
    val idSocio: Int,
    val nombre: String,
    val telefono: String,
    val fechaIngreso: String,
    val status: Int
)