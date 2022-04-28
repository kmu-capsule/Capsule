package com.example.capsule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore

class PrivacyActivity : AppCompatActivity() {
    private val userName: EditText by lazy {
        findViewById(R.id.PrivacyUserName)
    }
    private val userEmail: EditText by lazy {
        findViewById(R.id.PrivacyEmail)
    }
    private val backButton: Button by lazy {
        findViewById(R.id.btn_SettingBack)
    }

    private lateinit var auth: FirebaseAuth
    private val db : FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)

        backButton.setOnClickListener {
            finish()
        }
        auth = Firebase.auth
        val uid = auth.currentUser?.uid ?: "ProfileTest"

        db.document("users/${uid}").get().addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val document = task.result
                val email = document.data?.get("Email")
                val nickname = document.data?.get("Nickname")
                // 개인정보에 이메일, 닉네임 보여줌
                userEmail.setText(email.toString())
                userName.setText(nickname.toString())
            }
        }

    }
}