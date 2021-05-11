package com.example.quizyourself.JoinQuiz

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.quizyourself.Constants.*
import com.example.quizyourself.Data.QuizData
import com.example.quizyourself.Data.QuizResultData
import com.example.quizyourself.MainPage
import com.example.quizyourself.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_quiz_submitted.*

class QuizSubmitted : AppCompatActivity() {
    lateinit var quizID : String
    lateinit var quizQuestion : ArrayList<Pair<String,Any>>
    lateinit var userMail : String
    lateinit var firestore: FirebaseFirestore
    var quizDetails : QuizResultData?=null
    var totalScore = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_submitted)
        var actionbar = supportActionBar
        actionbar?.title=""
        actionbar?.setDisplayHomeAsUpEnabled(true)
        //Init
        firestore = FirebaseFirestore.getInstance()

        //Get quiz Id
        val i = intent
        quizID=i.getStringExtra(ConstantsPutExtra.QUIZ_ID)!!
        quizQuestion= i.getStringArrayListExtra(ConstantsPutExtra.QUIZ_QUESTIONS) as ArrayList<Pair<String, Any>>
        //for((k,v) in quizQuestion)
       // Log.d("AFTERANS","$k : $v")
        //Get user mail from sharedpref
        userMail=getUserMailFromSharedPref()

        //cal score
        calculateScore()
        uploadedAnsToFirestore()
    }

    private fun calculateScore() {
        var pos = -1
        for (eachQuesPair in quizQuestion){
            pos++
            val eachQuesDetailMap = eachQuesPair.second as MutableMap<String,Any>
            if(eachQuesDetailMap[ConstantAnsweredInfo.CHOOSEN_OPTION]==eachQuesDetailMap[ConstantsQuestionInfo.QUES_CORR_OPT]){
                eachQuesDetailMap.put(ConstantAnsweredInfo.SCORE,"1")
                quizQuestion[pos]=eachQuesPair.copy(second = eachQuesDetailMap)
                totalScore++
            }
            else
            {
                eachQuesDetailMap.put(ConstantAnsweredInfo.SCORE,"0")
            }
        }

    }

    private fun getUserMailFromSharedPref(): String{
        val sharedpref = getSharedPreferences(Constants.ROOT_SHAREDPREFERENCES, Context.MODE_PRIVATE)
        return sharedpref.getString(Constants.EMAIL_SHAREDPREF,"")!!
    }

    private fun uploadedAnsToFirestore() {
        //Add user who attemped quiz
        firestore.collection(ConstantsFireStore.QUIZ_DATA_ROOT).document(quizID).update(
            mapOf(
                "${ConstantsQuizInfo.ATTEMPTED_BY}.$userMail" to totalScore
            )
        )
        //Add this quiz to joined quiz of user data
        firestore.collection(ConstantsFireStore.USER_DATA_ROOT).document(userMail).update(
            Constants.JOINED_QUIZES,FieldValue.arrayUnion(quizID)
        )
        firestore.collection(ConstantsFireStore.QUIZ_DATA_ROOT).document(quizID).get().addOnSuccessListener {
            if(it.exists()){
               quizDetails= it.toObject(QuizResultData::class.java)
               quizDetails?.QUIZ_RESULT=quizQuestion.toMap()
                quizDetails?.TOTAL_SCORE=totalScore.toString()
                //Add result to joined quizes
                firestore.collection(ConstantsFireStore.QUIZ_RESULT_ROOT).document(quizID).set(quizDetails!!).addOnCompleteListener{
                    if(it.isSuccessful){
                        lottie_uploaded_quizFinished.visibility= View.VISIBLE
                        lottie_loading_quizFinished.visibility=View.GONE
                        ll_success_quizSubmitted.visibility=View.VISIBLE
                    }
                }
            }
            else
            {
                Toast.makeText(this,"Doesn't exist",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,MainPage::class.java))
        finishAffinity()
    }
    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}