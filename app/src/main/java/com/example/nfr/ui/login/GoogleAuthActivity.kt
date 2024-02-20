package com.example.nfr.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nfr.MainActivity
import com.example.nfr.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthActivity : AppCompatActivity() {

    // Declaraciones de variables
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity

    // ResultLauncher para iniciar la actividad de inicio de sesión de Google
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            manageResults(task)
        } else {
            // Si el usuario cancela la actividad, regresa a LoginActivity
            navigateBackToLogin()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de Firebase Auth y configuración del cliente de inicio de sesión de Google
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_web_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Iniciar el proceso de inicio de sesión con Google
        googleSignIn()
    }

    // Método para iniciar la actividad de inicio de sesión de Google
    private fun googleSignIn() {
        val signInClient = googleSignInClient.signInIntent
        launcher.launch(signInClient)
    }

    // Método para manejar los resultados de la actividad de inicio de sesión de Google
    private fun manageResults(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

            if (account != null) {
                // Si la autenticación con Google es exitosa, se llama al método para mostrar la pantalla principal
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Si el inicio de sesión es exitoso, finaliza esta actividad para evitar que el usuario retroceda
                        showHome()
                        finish()
                    }
                }
            } else {
                // Manejar el caso en que la autenticación con Google falle
                // Por ejemplo, mostrar un diálogo de error o informar al usuario
                showErrorDialog()
            }
        } catch (e: ApiException) {
            // Error al iniciar sesión con Google
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
            // Manejar el error, por ejemplo, mostrar un diálogo de error o informar al usuario
            showErrorDialog()
        }
    }

    // Método para navegar de regreso a LoginActivity si el usuario cancela la actividad de inicio de sesión
    private fun navigateBackToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Método para mostrar un diálogo de error en caso de que falle la autenticación con Google
    private fun showErrorDialog() {
        // Aquí puedes implementar la lógica para mostrar un diálogo de error al usuario
        // Por ejemplo, utilizando AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Failed to sign in with Google.")
        builder.setPositiveButton("OK", null)
        builder.show()
    }
    private fun showHome() {
        val homeIntent = Intent(this, MainActivity::class.java)
        startActivity(homeIntent)
    }

    companion object {
        private const val TAG = "GoogleAuthActivity"
    }
}
