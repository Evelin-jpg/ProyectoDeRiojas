package com.example.proyectoderiojas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        val btn: Button = findViewById(R.id.btnMembresia)
        btn.setOnClickListener {
            val intent: Intent = Intent(this, MembresiaCRUD::class.java)
            startActivity(intent)
        }

        // Botón Producto
        val btnProducto: Button = findViewById(R.id.btnProducto)
        btnProducto.setOnClickListener {
            val intent = Intent(this, ProductoCRUD::class.java)
            startActivity(intent)
        }

        // Botón Socio
        val btnSocio: Button = findViewById(R.id.btnSocio)
        btnSocio.setOnClickListener {
            val intent = Intent(this, SocioCRUD::class.java)
            startActivity(intent)
        }



        val tvGoLogin = findViewById<Button>(R.id.tv_go_to_login)
        tvGoLogin.setOnClickListener {
            goToLogin()
        }


    }

    private fun goToLogin() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

}



