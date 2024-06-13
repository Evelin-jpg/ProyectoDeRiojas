package com.example.proyectoderiojas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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


class MembresiaCRUD : AppCompatActivity() {
    private lateinit var editTextNumero: EditText
    private lateinit var editTextTipo: EditText
    private lateinit var editTextPremia: EditText
    private lateinit var btnAddMembresia: Button
    private lateinit var btnGetAllMembresias: Button
    private lateinit var btnUpdateMembresia: Button
    private lateinit var btnDeleteMembresia: Button
    private lateinit var apiService: ApiService
    private lateinit var editTextId: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membresia_crud)

        editTextNumero = findViewById(R.id.editTextNumero)
        editTextTipo = findViewById(R.id.editTextTipo)
        editTextPremia = findViewById(R.id.editTextPremia)
        btnAddMembresia = findViewById(R.id.btnAddMembresia)
        btnGetAllMembresias = findViewById(R.id.btnGetAllMembresias)
        btnUpdateMembresia = findViewById(R.id.btnUpdateMembresia)
        btnDeleteMembresia = findViewById(R.id.btnDeleteMembresia)
        editTextId = findViewById(R.id.editTextId)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.88:7093/") // Asegúrate de que la URL base sea correcta
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        btnAddMembresia.setOnClickListener {
            val membresia = MembresiaA(
                idMembresia = 0,
                numero = editTextNumero.text.toString(),
                tipo = editTextTipo.text.toString(),
                premia = editTextPremia.text.toString().toInt(),
                status = 1 // Asumimos 1 como estado activo
            )
            addMembresia(membresia)
        }

        btnGetAllMembresias.setOnClickListener {
            val intent = Intent(this@MembresiaCRUD, MostrarMembresia::class.java)
            startActivity(intent)
        }

        btnUpdateMembresia.setOnClickListener {
            val idString = editTextId.text.toString()
            val numero = editTextNumero.text.toString()
            val tipo = editTextTipo.text.toString()
            val premiaString = editTextPremia.text.toString()

            // Validar ID
            val id = try {
                idString.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this@MembresiaCRUD, "ID inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar premia
            val premia = try {
                premiaString.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this@MembresiaCRUD, "Premia inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto Membresia
            val membresia = MembresiaA(id, numero, tipo, premia, 1) // Establecer el status a 1 por defecto
            updateMembresia(id, membresia)
        }


        btnDeleteMembresia.setOnClickListener {
            val idStr = editTextId.text.toString().trim()
            if (idStr.isNotEmpty()) {
                val id = idStr.toIntOrNull()
                if (id != null) {
                    deleteMembresia(id)
                } else {
                    Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Métodos CRUD aquí (addMembresia, getAllMembresias, getMembresia, updateMembresia, deleteMembresia)

    private fun addMembresia(membresia: MembresiaA) {
        val call = apiService.addMembresia(membresia)
        call.enqueue(object : Callback<List<MembresiaA>> {
            override fun onResponse(call: Call<List<MembresiaA>>, response: Response<List<MembresiaA>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MembresiaCRUD, "Membresia añadida", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MembresiaCRUD, "Error al añadir membresia", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<MembresiaA>>, t: Throwable) {
                Toast.makeText(this@MembresiaCRUD, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateMembresia(id: Int, membresia: MembresiaA) {
        val call = apiService.updateMembresia(id, membresia)
        call.enqueue(object : Callback<MembresiaA> {
            override fun onResponse(call: Call<MembresiaA>, response: Response<MembresiaA>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MembresiaCRUD, "Membresia modificada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MembresiaCRUD, "Error al modificar la membresia", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MembresiaA>, t: Throwable) {
                Toast.makeText(this@MembresiaCRUD, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteMembresia(id: Int) {
        val call = apiService.inactivarMembresia(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MembresiaCRUD, "Membresia inactivada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MembresiaCRUD, "Error al inactivar la membresia", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MembresiaCRUD, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
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