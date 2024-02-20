package com.example.nfr.ui.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nfr.MainActivity
import com.example.nfr.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var signInButton: Button
    private lateinit var logInButton: Button
    private lateinit var registerPhoneButton: Button
    private lateinit var registerGoogleButton: Button

    private lateinit var emailText: TextInputEditText
    private lateinit var passwordText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Obtener referencias a los elementos de la interfaz de usuario
        emailText = findViewById(R.id.emailText)
        passwordText = findViewById(R.id.passwordText)
        signInButton = findViewById(R.id.registerButton)
        logInButton = findViewById(R.id.logInButton)
        registerPhoneButton = findViewById(R.id.registerPhoneButton)
        registerGoogleButton = findViewById(R.id.registerGoogleButton)

        // Verificar si el usuario ya está autenticado
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Si el usuario ya está autenticado, ir directamente a MainActivity
            showHome(currentUser.email ?: "")
            finish() // Cerrar LoginActivity para que el usuario no pueda volver atrás
        } else {
            // Si el usuario no está autenticado, continuar con la inicialización de LoginActivity
            setup()
        }
    }



    private fun setup(){
        title = "Authentication"

        signInButton.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Verificar si la contraseña cumple con los criterios
                val passwordPattern = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}\$")
                if (!passwordPattern.matches(password)) {
                    // Si la contraseña no cumple con los criterios, mostrar un error
                    showAlert(getString(R.string.password_error), getString(R.string.password_requirements_error))
                    return@setOnClickListener
                }

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                            showAlert(getString(R.string.email_verification_sent), getString(R.string.confirm_email_account))
                        } else {
                            showAlert(getString(R.string.error_creating_account), getString(R.string.error_creating_account_message))
                        }
                    }
            } else {
                showAlert(getString(R.string.empty_fields), getString(R.string.complete_all_fields))
            }

        }

        logInButton.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Verificar si la contraseña cumple con los criterios
                val passwordPattern = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}\$")
                if (!passwordPattern.matches(password)) {
                    // Si la contraseña no cumple con los criterios, mostrar un error
                    showAlert(getString(R.string.password_error), getString(R.string.password_requirements_error))
                    return@setOnClickListener
                }

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            if (user?.isEmailVerified == true) {
                                showHome(user.email ?: "")
                            } else {
                                showToast(getString(R.string.verify_email))
                            }
                        } else {
                            showAlert(getString(R.string.error_logging_in), getString(R.string.incorrect_email_password))
                        }
                    }

            } else {
                showAlert(getString(R.string.empty_fields), getString(R.string.complete_all_fields))
            }
        }


        registerPhoneButton.setOnClickListener{
            // Iniciar la actividad para el registro por teléfono
            val phoneIntent = Intent(this, PhoneAuthActivity::class.java)
            startActivity(phoneIntent)
        }

        registerGoogleButton.setOnClickListener {
            // Iniciar la actividad para el registro con Google
            val googleIntent = Intent(this, GoogleAuthActivity::class.java)
            startActivity(googleIntent)
        }
    }

    // Mostrar un cuadro de diálogo de alerta
    private fun showAlert(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog = builder.create()
        dialog.show()
    }

    // Mostrar la pantalla principal (MainActivity)
    private fun showHome(email: String) {
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }

    // Mostrar un mensaje emergente
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
