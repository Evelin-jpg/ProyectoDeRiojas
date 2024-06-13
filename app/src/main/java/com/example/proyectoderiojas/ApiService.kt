package com.example.proyectoderiojas

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("api/Usuario/autenticar")
   fun autenticarUsuario(
        @Query("nombre") nombre: String,
        @Query("contrasena") contrasena: String
    ): Call<ResponseBody>

    // CRUD Membresia
    @POST("api/Membresia")
    fun addMembresia(@Body membresia: MembresiaA): Call<List<MembresiaA>>

    @GET("api/Membresia/GetActiveMembresias")
    fun getAllMembresias(): Call<List<MembresiaA>>

    @PUT("api/Membresia/{id}")
    fun updateMembresia(@Path("id") id: Int, @Body membresia: MembresiaA): Call<MembresiaA>

    @PUT("api/Membresia/inactivar/{id}")
    fun inactivarMembresia(@Path("id") id: Int): Call<ResponseBody>

    // CRUD Producto
    @POST("api/Producto")
    fun addProducto(@Body producto: ProductoA): Call<List<ProductoA>>

    @GET("api/Producto/GetActiveProductos")
    fun getAllProductos(): Call<List<ProductoA>>

    @PUT("api/Producto/{id}")
    fun updateProducto(@Path("id") id: Int, @Body producto: ProductoA): Call<ProductoA>

    @PUT("api/Producto/inactivar/{id}")
    fun inactivarProducto(@Path("id") id: Int): Call<ResponseBody>

    // CRUD Socio
    @POST("api/Socio")
    fun addSocio(@Body socioA: SocioA): Call<List<SocioA>>

    @GET("api/Socio/GetActiveSocios")
    fun getAllSocios(): Call<List<SocioA>>

    @PUT("api/Socio/{id}")
    fun updateSocio(@Path("id") id: Int, @Body socioA: SocioA): Call<SocioA>

    @PUT("api/Socio/inactivar/{id}")
    fun inactivarSocio(@Path("id") id: Int): Call<ResponseBody>
}





