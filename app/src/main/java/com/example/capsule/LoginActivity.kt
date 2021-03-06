package com.example.capsule

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(), TextWatcher {
    private lateinit var auth: FirebaseAuth

    private val emailText: EditText by lazy {
        findViewById(R.id.loginEmail)
    }
    private val passwordText: EditText by lazy {
        findViewById(R.id.loginPassword)
    }
    private val autoLoginCheck: AppCompatCheckBox by lazy {
        findViewById(R.id.autoLoginCheckbox)
    }

    private val loginButton: AppCompatButton by lazy {
        findViewById(R.id.loginButton)
    }
    private val signupButton: AppCompatButton by lazy {
        findViewById(R.id.signupButton)
    }
    private val passwordEye: ImageButton by lazy {
        findViewById(R.id.passwordEyeButton)
    }

    private var isPasswordShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        setPasswordShowingState()
        emailText.addTextChangedListener(this)
        passwordText.addTextChangedListener(this)
        setAutoLoginCheck()
        initLoginButton()
        initSignupButton()
    }

    private fun initSignupButton() {
        // 회원가입 페이지로 이동!
        signupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setPasswordShowingState() {
        passwordEye.setOnClickListener {
            // 비밀번호가 보이지 않는 상태. eye_open
            if (!isPasswordShowing) {
                isPasswordShowing = true
                passwordEye.background = getDrawable(R.drawable.eye_close)
                passwordText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                // 비밀번호가 보이는 상태. eye_close
                isPasswordShowing = false
                passwordEye.background = getDrawable(R.drawable.eye_open)
                passwordText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }

    private fun setAutoLoginCheck() {
        autoLoginCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                Toast.makeText(this, "체크", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "해제", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initAlertDialog() {
        val dialogView = View.inflate(this, R.layout.alertdialog_view, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val dialogOkButton: AppCompatButton = dialogView.findViewById(R.id.okButton)
        dialogOkButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setError() {
        emailText.background = getDrawable(R.drawable.edittext_alert_background)
        passwordText.background = getDrawable(R.drawable.edittext_alert_background)
    }

    private fun initLoginButton() {
        loginButton.setOnClickListener {
            val emailFromUser = emailText.text.toString()
            val passWordFromUser = passwordText.text.toString()

            auth.signInWithEmailAndPassword(emailFromUser, passWordFromUser)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        initAlertDialog()
                        setError()
                    }
                }

//            if (emailFromUser == TEST_EMAIL && passWordFromUser == TEST_PASSWORD) {
//                // 다음
//            } else {
//                initAlertDialog()
//                setError()
//            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // TODO("Not yet implemented")
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val emailFromUser = emailText.text.toString()
        val passWordFromUser = passwordText.text.toString()
        if (!emailFromUser.isNullOrBlank() && !passWordFromUser.isNullOrBlank()) {
            loginButton.background = getDrawable(R.drawable.activate_button_background)
            loginButton.isEnabled = true
        } else if (emailFromUser.isNullOrBlank()) {
            emailText.background = getDrawable(R.drawable.edittext_background)
            loginButton.isEnabled = false
        } else if (passWordFromUser.isNullOrBlank()) {
            passwordText.background = getDrawable(R.drawable.edittext_background)
            loginButton.isEnabled = false
        } else {
            loginButton.background = getDrawable(R.drawable.inactivate_button_background)
            loginButton.isEnabled = false
        }
    }

    override fun afterTextChanged(p0: Editable?) {
        // TODO("Not yet implemented")
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean { // 현재 포커스된 뷰의 영역이 아닌 다른 곳을 클릭 시 키보드를 내리고 포커스 해제
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm != null) imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        private const val TEST_EMAIL = "test@test.com"
        private const val TEST_PASSWORD = "test"
    }
}
