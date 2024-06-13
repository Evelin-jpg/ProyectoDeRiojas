package com.example.proyectoderiojas

import com.google.gson.annotations.SerializedName

data class MembresiaA (
    @SerializedName("idMembresia")
    val idMembresia: Int,
    val numero: String,
    val tipo: String,
    val premia: Int,
    val status: Int
)
