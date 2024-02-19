package com.example.nfr.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.nfr.MainActivity
import com.example.nfr.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import java.security.Provider

class LoginActivity : AppCompatActivity() {
    private lateinit var signInButton:Button
    private lateinit var logInButton:Button
    private lateinit var registerPhoneButton:Button

    private lateinit var emailText: TextInputEditText
    private lateinit var passwordText: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Obtener referencias a los EditText
        emailText = findViewById(R.id.emailText)
        passwordText = findViewById(R.id.passwordText)
        signInButton=findViewById(R.id.registerButton)
        logInButton=findViewById(R.id.logInButton)
        registerPhoneButton=findViewById(R.id.registerPhoneButton)


        setup()
    }
    private fun setup(){
        title = "Authentication"

        signInButton.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showHome(task.result?.user?.email ?: "")
                        } else {
                            showAlert()
                        }
                    }
            } else {
                showAlert()
            }
        }

        logInButton.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showHome(task.result?.user?.email ?: "")
                        } else {
                            showAlert()
                        }
                    }
            } else {
                showAlert()
            }
        }
        registerPhoneButton.setOnClickListener{
            val phoneIntent=Intent(this,PhoneAuthActivity::class.java)
            startActivity(phoneIntent)
        }
    }
    private fun showAlert(){
        val builder=AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario ")
        builder.setPositiveButton("Aceptar",null)
        val dialog:AlertDialog=builder.create()
        dialog.show()
    }
    private fun showHome(email:String){
        val homeIntent=Intent(this,MainActivity::class.java).apply {
            putExtra("email",email)
        }
        startActivity(homeIntent)
    }
}