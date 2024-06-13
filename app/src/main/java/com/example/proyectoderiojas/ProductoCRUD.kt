package com.example.proyectoderiojas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class ProductoCRUD : ComponentActivity() {
    private lateinit var editTextClave: EditText
    private lateinit var editTextNombre: EditText
    private lateinit var editTextPrecio: EditText
    private lateinit var editTextCantidad: EditText
    private lateinit var editTextStatus: EditText
    private lateinit var btnAddProducto: Button
    private lateinit var btnUpdateProducto: Button
    private lateinit var btnDeleteProducto: Button
    private lateinit var btnGetAllProductos: Button
    private lateinit var apiService: ApiService
    private lateinit var editTextId: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto)

        editTextClave = findViewById(R.id.editTextClave)
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextPrecio = findViewById(R.id.editTextPrecio)
        editTextCantidad = findViewById(R.id.editTextCantidad)
        btnAddProducto = findViewById(R.id.btnAddProducto)
        btnUpdateProducto = findViewById(R.id.btnUpdateProducto)
        btnDeleteProducto = findViewById(R.id.btnDeleteProducto)
        btnGetAllProductos = findViewById(R.id.btnGetAllProductos)
        editTextId = findViewById(R.id.editTextId)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.88:7093/") // Asegúrate de que la URL base sea correcta
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        btnAddProducto.setOnClickListener {
            val producto = ProductoA(
                idProducto = 0,
                clave = editTextClave.text.toString(),
                nombre = editTextNombre.text.toString(),
                precio = editTextPrecio.text.toString(),
                cantidad = editTextCantidad.text.toString(),
                status = 1 // Asumimos 1 como estado activo
            )
            addProducto(producto)
        }

        btnGetAllProductos.setOnClickListener {
            val intent = Intent(this@ProductoCRUD, MostrarProducto::class.java)
            startActivity(intent)
        }

        btnUpdateProducto.setOnClickListener {
            val idString = editTextId.text.toString()
            val clave = editTextClave.text.toString()
            val nombre = editTextNombre.text.toString()
            val precio = editTextPrecio.text.toString()
            val cantidad = editTextCantidad.text.toString()

            // Validar ID
            val id = try {
                idString.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this@ProductoCRUD, "ID inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar precio y cantidad
            val precioVal = try {
                precio.toDouble()
            } catch (e: NumberFormatException) {
                Toast.makeText(this@ProductoCRUD, "Precio inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cantidadVal = try {
                cantidad.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this@ProductoCRUD, "Cantidad inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto Producto
            val producto = ProductoA(id, clave, nombre, precio, cantidad, 1)
            updateProducto(id, producto)
        }



        btnDeleteProducto.setOnClickListener {
            val idStr = editTextId.text.toString().trim()
            if (idStr.isNotEmpty()) {
                val id = idStr.toIntOrNull()
                if (id != null) {
                    deleteProducto(id)
                } else {
                    Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Métodos CRUD aquí (addProducto, getAllProductos, getProducto, updateProducto, deleteProducto)

    private fun addProducto(producto: ProductoA) {
        val call = apiService.addProducto(producto)
        call.enqueue(object : Callback<List<ProductoA>> {
            override fun onResponse(call: Call<List<ProductoA>>, response: Response<List<ProductoA>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductoCRUD, "Producto añadido", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ProductoCRUD, "Error al añadir producto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ProductoA>>, t: Throwable) {
                Toast.makeText(this@ProductoCRUD, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateProducto(id: Int, producto: ProductoA) {
        val call = apiService.updateProducto(id, producto)
        call.enqueue(object : Callback<ProductoA> {
            override fun onResponse(call: Call<ProductoA>, response: Response<ProductoA>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductoCRUD, "Producto modificado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ProductoCRUD, "Error al modificar el producto", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ProductoA>, t: Throwable) {
                Toast.makeText(this@ProductoCRUD, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteProducto(id: Int) {
        val call = apiService.inactivarProducto(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductoCRUD, "Producto inactivado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ProductoCRUD, "Error al inactivar el producto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ProductoCRUD, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun unsafeOkHttpClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            val hostnameVerifier = HostnameVerifier { _, _ -> true }

            return OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier(hostnameVerifier)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}