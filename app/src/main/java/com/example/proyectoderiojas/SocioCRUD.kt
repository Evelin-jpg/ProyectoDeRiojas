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

class SocioCRUD : ComponentActivity() {
    private lateinit var editTextNombre: EditText
    private lateinit var editTextTelefono: EditText
    private lateinit var editTextFechaIngreso: EditText
    private lateinit var btnAddSocio: Button
    private lateinit var btnUpdateSocio: Button
    private lateinit var btnDeleteSocio: Button
    private lateinit var btnGetAllSocios: Button
    private lateinit var apiService: ApiService
    private lateinit var editTextId: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socio)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextTelefono = findViewById(R.id.editTextTelefono)
        editTextFechaIngreso = findViewById(R.id.editTextFechaIngreso)
        btnAddSocio = findViewById(R.id.btnAddSocio)
        btnUpdateSocio = findViewById(R.id.btnUpdateSocio)
        btnDeleteSocio = findViewById(R.id.btnDeleteSocio)
        btnGetAllSocios = findViewById(R.id.btnGetAllSocios)
        editTextId = findViewById(R.id.editTextId)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.7.138:7093/") // Asegúrate de que la URL base sea correcta
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        btnAddSocio.setOnClickListener {
            val socio = SocioA(
                idSocio = 0,
                nombre = editTextNombre.text.toString(),
                telefono = editTextTelefono.text.toString(),
                fechaIngreso = editTextFechaIngreso.text.toString(),
                status = 1 // Asumimos 1 como estado activoz
            )
            addSocio(socio)
        }

        btnGetAllSocios.setOnClickListener {
            val intent = Intent(this@SocioCRUD, MostrarSocio::class.java)
            startActivity(intent)
        }

        btnUpdateSocio.setOnClickListener {
            val idString = editTextId.text.toString()
            val nombre = editTextNombre.text.toString()
            val telefono = editTextTelefono.text.toString()
            val fechaIngreso = editTextFechaIngreso.text.toString()

            // Validar ID
            val id = try {
                idString.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this@SocioCRUD, "ID inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto Socio
            val socio = SocioA(id, nombre, telefono, fechaIngreso, 1)
            updateSocio(id, socio)
        }

        btnDeleteSocio.setOnClickListener {
            val idStr = editTextId.text.toString().trim()
            if (idStr.isNotEmpty()) {
                val id = idStr.toIntOrNull()
                if (id != null) {
                    deleteSocio(id)
                } else {
                    Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Métodos CRUD aquí (addSocio, getAllSocios, getSocio, updateSocio, deleteSocio)

    private fun addSocio(socio: SocioA) {
        val call = apiService.addSocio(socio)
        call.enqueue(object : Callback<List<SocioA>> {
            override fun onResponse(call: Call<List<SocioA>>, response: Response<List<SocioA>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SocioCRUD, "Socio añadido correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SocioCRUD, "Error al añadir socio", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<SocioA>>, t: Throwable) {
                Toast.makeText(this@SocioCRUD, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateSocio(id: Int, socio: SocioA) {
        val call = apiService.updateSocio(id, socio)
        call.enqueue(object : Callback<SocioA> {
            override fun onResponse(call: Call<SocioA>, response: Response<SocioA>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SocioCRUD, "Socio modificado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SocioCRUD, "Error al modificar el socio", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SocioA>, t: Throwable) {
                Toast.makeText(this@SocioCRUD, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteSocio(id: Int) {
        val call = apiService.inactivarSocio(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SocioCRUD, "Socio inactivado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SocioCRUD, "Error al inactivar el socio", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@SocioCRUD, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
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
