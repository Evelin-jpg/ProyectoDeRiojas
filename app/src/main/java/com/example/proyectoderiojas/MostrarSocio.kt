package com.example.proyectoderiojas

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class MostrarSocio : ComponentActivity() {
    private lateinit var listViewSocios: ListView
    private lateinit var btnIrCrud: Button
    private lateinit var apiService: ApiService
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mostrar_socio)

        listViewSocios = findViewById(R.id.listViewSocios)
        btnIrCrud = findViewById(R.id.btnIrCrud)
        btnVolver = findViewById(R.id.btnVolver)

        // Configurar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.88:7093/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Obtener todos los socios
        obtenerTodosLosSocios()

        // Configurar el botón para ir a la actividad CRUD
        btnIrCrud.setOnClickListener {
            val intent = Intent(this, SocioCRUD::class.java)
            startActivity(intent)
        }
        btnVolver.setOnClickListener {
            val intent = Intent(this@MostrarSocio, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun obtenerTodosLosSocios() {
        val call = apiService.getAllSocios()
        call.enqueue(object : Callback<List<SocioA>> {
            override fun onResponse(call: Call<List<SocioA>>, response: Response<List<SocioA>>) {
                if (response.isSuccessful) {
                    val socio = response.body() ?: emptyList()
                    val adapter = ArrayAdapter(
                        this@MostrarSocio,
                        android.R.layout.simple_list_item_1,
                        socio.map { socio ->
                            "ID: ${socio.idSocio}, Nombre: ${socio.nombre}, Telefono: ${socio.telefono}, FechaIngreso: ${socio.fechaIngreso}"
                        }
                    )
                    listViewSocios.adapter = adapter
                } else {
                    Toast.makeText(this@MostrarSocio, "Error al obtener los socios", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<SocioA>>, t: Throwable) {
                Toast.makeText(this@MostrarSocio, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun unsafeOkHttpClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

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
