package com.example.proyectoderiojas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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

class MainActivity : AppCompatActivity() {
    private lateinit var editTextNombre: EditText
    private lateinit var editTextContrasena: EditText
    private lateinit var btn_go_to_menu: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextContrasena = findViewById(R.id.editTextContrasena)
        btn_go_to_menu = findViewById(R.id.btn_go_to_menu)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.88:7093/") // Asegúrate de que la URL base sea correcta
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        btn_go_to_menu.setOnClickListener {
            val nombre = editTextNombre.text.toString()
            val contrasena = editTextContrasena.text.toString()

            if (nombre.isNotEmpty() && contrasena.isNotEmpty()) {
                autenticarUsuario(nombre, contrasena)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun autenticarUsuario(nombre: String, contrasena: String) {
        val call = apiService.autenticarUsuario(nombre, contrasena)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Autenticación exitosa, maneja la respuesta aquí
                    Toast.makeText(this@MainActivity, "Credenciales válidas", Toast.LENGTH_SHORT).show()
                    try {
                        val intent = Intent(this@MainActivity, MenuActivity::class.java)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Error al iniciar la nueva actividad", Toast.LENGTH_SHORT).show()
                        Log.e("NAV_ERROR", "Error al iniciar la nueva actividad", e)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Código de respuesta: ${response.code()}")
                    Log.e("API_ERROR", "Mensaje de error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", "Error: ${t.message}", t)
            }
        })
    }


    private fun unsafeOkHttpClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
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
