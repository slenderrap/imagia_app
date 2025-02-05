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
    private var usuariCreat: Boolean =false
        set(value) {
            if (value){
                binding.validatebtn.visibility = View.VISIBLE
                ChangeToNotEditable()
            }
            field = value
        }
    private var usuariValidat: Boolean =false
        set(value) {
            if (value){

            }
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogingBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.validatebtn.visibility = View.INVISIBLE


        binding.createbtn.setOnClickListener {
            registrarUsuario(binding.userTextView.text.toString(),binding.mailTextView.text.toString(),
                binding.passwordEditTextView.text.toString(),binding.telephoneTextView.text.toString(),
                binding.nicknameTextView.text.toString())

        }

        binding.validatebtn.setOnClickListener {
            validarUsuario(binding.userTextView.text.toString())

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
                    runOnUiThread {
                        Log.d("REGISTER_RESPONSE", responseBody ?: "")
                        usuariCreat=true
                    }
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
                Log.e("REGISTER_EXCEPTION", "Error", e)
            }
        }.start()
        Toast.makeText(baseContext,usuariCreat.toString(),Toast.LENGTH_SHORT).show()

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
                val responseBody = response.body?.string()
                if (response.isSuccessful) {
                    runOnUiThread {
                        Log.d("REGISTER_RESPONSE", responseBody ?: "")
                        usuariValidat=true
                }
                } else {
                    val jsonObject = JSONObject(responseBody)
                    val message = jsonObject.getString("message")
                    Log.e("VALIDATE_ERROR", "Error: ${response.code} \n" +
                            "Message: $message")
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

    fun ChangeToNotEditable(){
        binding.userInputLayout.isEnabled=false
        binding.passwordInputLayout.isEnabled=false
        binding.mailInputLayout.isEnabled=false
        binding.nicknameInputLayout.isEnabled=false
        binding.telephoneInputLayout.isEnabled=false
    }











}