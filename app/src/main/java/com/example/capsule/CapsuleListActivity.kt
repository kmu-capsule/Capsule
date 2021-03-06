package com.example.capsule

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.protobuf.StringValue

class CapsuleListActivity : AppCompatActivity() {
    private var CapsuleList = ArrayList<CapsuleData>()    // recyclerView 에 띄워줄 데이터 저장

    private val mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = mDatabase.getReference("Users")

    private var auth = FirebaseAuth.getInstance()
    private var user = auth.currentUser    // 현재 로그인한 유저

    lateinit var date:String     // capsule date
    lateinit var title:String    // capsule title
    lateinit var photoUri: Uri   // capsule detectImage
    lateinit var capsuleContent: String  // capsule content
    private var capsuleKey: MutableList<String> = mutableListOf()   // capsule key 들만 모아놓은 배열

    private val backButton: Button by lazy {
        findViewById(R.id.btn_CapsuleListBack)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capsule_list)

        if (user != null) {
            var uid = user!!.uid
            // capsule key들만 모아놓은 배열 생성
            myRef.child(uid).get().addOnSuccessListener {
                it.children.forEach {
                    if (it.key.toString() != "Info") {
                        capsuleKey.add(it.key.toString())
                    }
                }
                initValue(uid)
            }
        }
        initBackButton()
    }

    private fun initBackButton() {
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun initValue(uid:String) {
        var cnt = 0
        for (capsule in capsuleKey) {
            // todo: .child(uid)로 수정
            myRef.child(uid).child(capsule).get().addOnSuccessListener {
                // capsule 별 date, title, detectImage 저장
                date = it.child("date").getValue<String>().toString()
                title = it.child("title").getValue<String>().toString()
                photoUri = it.child("detectImage").getValue<String>()!!.toUri()
                capsuleContent = it.child("content").getValue<String>().toString()
                // 캡슐 하나 저장 -> cnt+=1
                cnt+=1
                // CapsuleList에 유저의 모든 캡슐 정보 저장
                CapsuleList.add(CapsuleData(title,date,photoUri,capsuleContent))
                // capsule key 개수만큼 리스트에 저장되었으면 initRecycler 호출
                if (cnt == capsuleKey.size) {
                    initRecycler(uid)
                }
            }
        }
    }

    private fun initRecycler(uid:String) {
        // adaper 와 recyclerView 연결
        val recyclerView: RecyclerView by lazy {
            findViewById(R.id.CapsuleList)
        }

        val capsuleAdapter = CapsuleDataAdapter(this, CapsuleList, capsuleKey, uid)
        recyclerView.adapter = capsuleAdapter

        // 리사이클러뷰에 스와이프, 드래그 기능 달기
        val swipeHelperCallback = SwipeHelperCallback(capsuleAdapter).apply {
            // 스와이프한 뒤 고정시킬 위치 지정
            setClamp(resources.displayMetrics.widthPixels.toFloat() / 5)    // 1080 / 5
        }
        ItemTouchHelper(swipeHelperCallback).attachToRecyclerView(recyclerView)
    }
}