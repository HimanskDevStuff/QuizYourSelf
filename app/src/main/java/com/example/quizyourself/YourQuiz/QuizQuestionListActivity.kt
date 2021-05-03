package com.example.quizyourself.YourQuiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizyourself.Constants.ConstantsFireStore
import com.example.quizyourself.Constants.ConstantsPutExtra
import com.example.quizyourself.Constants.ConstantsQuestionInfo
import com.example.quizyourself.Constants.ConstantsQuizInfo
import com.example.quizyourself.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_quiz_question_list.*

class QuizQuestionListActivity : AppCompatActivity() {
    lateinit var quizID:String
    var quizDetailsMap : Map<String,Any> = mutableMapOf()
    lateinit var firebase : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question_list)
        var actionBar = supportActionBar
        actionBar?.title = "Questions"
        actionBar?.setDisplayHomeAsUpEnabled(true)

        firebase= FirebaseFirestore.getInstance()

        //get quizId
        val i = intent
        quizID=i.getStringExtra(ConstantsPutExtra.QUIZ_ID)!!

        loadQuestions()

    }

    private fun loadQuestions() {
        firebase.collection(ConstantsFireStore.QUIZ_DATA_ROOT).document(quizID).get().addOnSuccessListener {
            if(it.exists()){
                quizDetailsMap=it.get(ConstantsQuizInfo.QUESTIONS) as Map<String, Any>
                // map to list so that we can display recyclerview

                Log.d("TAG",quizDetailsMap.toList().toString())
                val adap = QuizQuestionListAdapter(quizDetailsMap.toList())
                recyclerview_QuestionList.adapter=adap
                recyclerview_QuestionList.layoutManager=LinearLayoutManager(this)

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}