package com.example.quizyourself

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_result.*

class Result : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        var username = intent.getStringExtra(QuestionList.USER_NAME)
        var corrAns = intent.getIntExtra(QuestionList.CORRECT_ANS,0)
        var totalQues = intent.getIntExtra(QuestionList.TOTAL_QUES,0)
        tv_username.text = username
        tv_score.text="Your Score is $corrAns/$totalQues"
        tv_finish.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}