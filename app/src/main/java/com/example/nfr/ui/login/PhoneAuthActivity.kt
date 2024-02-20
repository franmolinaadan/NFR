package com.example.nfr.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.nfr.R
import com.example.nfr.ui.user.UserInfoFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : Activity() {

    // Declaraciones de variables
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var verficationButton: Button
    private lateinit var verficationButtonOTP: Button
    private lateinit var phoneText: TextInputEditText
    private lateinit var otpVerification: TextInputEditText
    private lateinit var countryCodePicker: CountryCodePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)

        // Inicialización de componentes de la vista
        initializeViews()

        // Configuración de callbacks y autenticación
        setupAuthentication()
    }

    // Inicializar componentes de la vista
    private fun initializeViews() {
        countryCodePicker = findViewById(R.id.login_countrycode)
        phoneText = findViewById(R.id.phoneText)
        otpVerification = findViewById(R.id.otpVerification)
        verficationButton = findViewById(R.id.verficationButton)
        verficationButtonOTP = findViewById(R.id.verficationButtonOTP)

        countryCodePicker.registerCarrierNumberEditText(phoneText)

        // Escuchador para el botón de verificación
        verficationButton.setOnClickListener {
            if (!countryCodePicker.isValidFullNumber()) {
                phoneText.setError("Phone number not valid")
            } else {
                val phoneNumber = countryCodePicker.fullNumberWithPlus
                startPhoneNumberVerification(phoneNumber)
            }
        }

        // Escuchador para el botón de verificación OTP
        verficationButtonOTP.setOnClickListener {
            val verificationCode = otpVerification.text.toString().trim()
            if (verificationCode.isNotEmpty()) {
                verifyPhoneNumberWithCode(storedVerificationId, verificationCode)
            } else {
                // Manejar el caso de código de verificación vacío
            }
        }
    }

    // Configuración de callbacks y autenticación
    private fun setupAuthentication() {
        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inicializar callbacks de verificación telefónica
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Callback invocado cuando la verificación se completa automáticamente
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // Callback invocado cuando la verificación falla
                handleVerificationFailure(e)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // Callback invocado cuando se envía el código de verificación al número de teléfono
                handleCodeSent(verificationId, token)
            }
        }

        // Verificar si el usuario ya está autenticado
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    // Método para iniciar la verificación del número de teléfono
    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Método para verificar el código de verificación
    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    // Método para manejar la autenticación con las credenciales del teléfono
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Autenticación exitosa, actualizar la interfaz de usuario
                    val user = task.result?.user
                    updateUI(user)
                } else {
                    // Autenticación fallida, mostrar un mensaje de error
                    showToast("Verificación fallida. Por favor, intenta de nuevo.")
                    updateUI(null)
                }
            }
    }

    // Método para manejar el caso de fallo en la verificación
    private fun handleVerificationFailure(e: FirebaseException) {
        Log.w(TAG, "onVerificationFailed", e)
        if (e is FirebaseAuthInvalidCredentialsException) {
            // Manejar el caso de credenciales inválidas
        } else if (e is FirebaseTooManyRequestsException) {
            // Manejar el caso de exceso de solicitudes
        } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
            // Manejar el caso de actividad faltante para Recaptcha
        }
        // Mostrar un mensaje y actualizar la interfaz de usuario
    }

    // Método para manejar el código enviado
    private fun handleCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
        storedVerificationId = verificationId
        resendToken = token
    }

    // Método para actualizar la interfaz de usuario según el estado de autenticación
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Si el usuario está autenticado, redirigir a MainActivity
            val userInfoIntent = Intent(this, UserInfoFragment::class.java)
            startActivity(userInfoIntent)
        } else {
            // Si el usuario no está autenticado, permitir que continúe el proceso de autenticación
        }
    }

    // Método para mostrar un mensaje de tostada
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
    }
}
