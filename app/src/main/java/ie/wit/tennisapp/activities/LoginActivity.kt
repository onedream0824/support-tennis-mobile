package ie.wit.tennisapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import ie.wit.tennisapp.R
import ie.wit.tennisapp.auth.LoginRegisterViewModel
import ie.wit.tennisapp.databinding.ActivityLoginBinding
import ie.wit.tennisapp.main.MainApp
import ie.wit.tennisapp.models.MemberModel
import timber.log.Timber

class LoginActivity() : AppCompatActivity(), View.OnClickListener {

    private lateinit var loginRegisterViewModel : LoginRegisterViewModel
    private lateinit var binding: ActivityLoginBinding
    var member = MemberModel()
    lateinit var app: MainApp
    private lateinit var auth: FirebaseAuth
    private lateinit var togglePasswordVisButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        binding.loginButton.setOnClickListener(this)
        binding.togglePasswordVisButton.setOnClickListener(this)

        togglePasswordVisButton = findViewById(R.id.togglePasswordVisButton)
        togglePasswordVisButton.setImageResource(R.drawable.ic_eye)

        auth = FirebaseAuth.getInstance()
    }

    private fun logIn(email: String, password: String) {
        Timber.d( "signIn:$email")
        if (!validateForm()) {
            return
        }

        loginRegisterViewModel.login(email, password)
    }

    public override fun onStart() {
        super.onStart()

        loginRegisterViewModel = ViewModelProvider(this).get(LoginRegisterViewModel::class.java)
        loginRegisterViewModel.liveFirebaseUser.observe(this, Observer {
            firebaseUser -> if (firebaseUser != null)
                startActivity(Intent(this, Home::class.java))
        })

        loginRegisterViewModel.firebaseAuthManager.errorStatus.observe(this, Observer
        { status -> checkStatus(status) })
    }

    private fun checkStatus(error:Boolean) {
        if (error)
            Toast.makeText(this,
                getString(R.string.auth_failed),
                Toast.LENGTH_LONG).show()
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.memberEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.memberEmail.error = "Required."
            valid = false
        } else {
            binding.memberEmail.error = null
        }

        val password = binding.memberPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.memberPassword.error = "Required."
            valid = false
        } else {
            binding.memberPassword.error = null
        }

        return valid
    }

    private fun togglePasswordVisibility() {
        val passwordEntry = findViewById<EditText>(R.id.memberPassword)
        if(togglePasswordVisButton.drawable.constantState == ContextCompat.getDrawable(this, R.drawable.ic_eye)?.constantState) {
            togglePasswordVisButton.setImageResource(R.drawable.ic_eye_slash)
            passwordEntry.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            togglePasswordVisButton.setImageResource(R.drawable.ic_eye)
            passwordEntry.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_cancel, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.loginButton -> logIn(binding.memberEmail.text.toString(), binding.memberPassword.text.toString())
            R.id.togglePasswordVisButton -> togglePasswordVisibility()
        }
    }
}

