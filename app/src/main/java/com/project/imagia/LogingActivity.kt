package com.project.imagia

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.imagia.databinding.ActivityLogingBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LogingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogingBinding
    private val username : String = ""
    private val password : String = ""
    private val mail : String = ""
    private val nickname : String = ""
    private val telephone : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogingBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.validatebtn.visibility = View.INVISIBLE


        binding.createbtn.setOnClickListener {

            registrarUsuario("Juan1","","123456","601076940","Juanito1")
            binding.validatebtn.visibility = View.VISIBLE
        }

        binding.validatebtn.setOnClickListener {
            validarUsuario("Juan1")

        }

    }
    fun registrarUsuario(usuario: String, email: String, contrasenya: String, telefon: String, nickname: String) {
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("username", usuario)
            put("email", email)
            put("contrasenya", contrasenya)
            put("telefon", telefon)
            put("nickname", nickname)
        }

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = json.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://imagia5.ieti.site/api/usuaris/registrar")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        Toast.makeText(baseContext,"Creant usuari",Toast.LENGTH_SHORT).show()
        Thread {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("REGISTER_RESPONSE", responseBody ?: "")
                } else {
                    // Manejo del error
                    val responseBody = response.body?.string()

                    // Intentamos parsear el cuerpo del mensaje como JSON
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val message = jsonObject.getString("message")
                        Log.e("REGISTER_ERROR", "Error: ${response.code} \nMessage: $message")
                    } catch (e: Exception) {
                        Log.e("REGISTER_ERROR", "Error parsing JSON: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("REGISTER_EXCEPTION", "Errorb", e)
            }
        }.start()
    }

    private fun validarUsuario(username: String) {
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("username", username)
        }

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = json.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://imagia5.ieti.site/api/usuaris/validar")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        Thread {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("VALIDATE_RESPONSE", responseBody ?: "")
                } else {
                    Log.e("VALIDATE_ERROR", "Error: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("VALIDATE_EXCEPTION", "Error", e)
            }
        }.start()
    }

    fun validarSms(username: String, sms: String) {
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("username", username)
            put("sms", sms)
        }

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = json.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://imagia5.ieti.site/api/usuaris/sms")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        Thread {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("SMS_RESPONSE", responseBody ?: "")
                } else {
                    Log.e("SMS_ERROR", "Error: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("SMS_EXCEPTION", "Error", e)
            }
        }.start()
    }











}