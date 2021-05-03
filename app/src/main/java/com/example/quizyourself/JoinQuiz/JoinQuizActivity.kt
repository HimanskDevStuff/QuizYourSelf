package com.example.quizyourself.JoinQuiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.view.View
import com.example.quizyourself.Constants.ConstantsFireStore
import com.example.quizyourself.Constants.ConstantsPutExtra
import com.example.quizyourself.Constants.ConstantsQuestionInfo
import com.example.quizyourself.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_join_quiz.*

class JoinQuizActivity : AppCompatActivity() {
    lateinit var quizID : String
    lateinit var firestore : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_quiz)
        var actionBar = supportActionBar
        actionBar?.title=""
        actionBar?.setDisplayHomeAsUpEnabled(true)

        quizID=""
        firestore= FirebaseFirestore.getInstance()
        btn_continue_to_quiz.setOnClickListener{
            quizID=et_quiz_id.text.toString()
            if (quizID.isEmpty())
            {
                tl_quiz_id.isErrorEnabled=true
                tl_quiz_id.error="Field can't be left blank"
            }
            else{
                lottie_loading_jointhequiz.visibility= View.VISIBLE
                tl_quiz_id.error=null
                tl_quiz_id.isErrorEnabled=false
                matchQuizIDToDb()
            }
        }

    }

    private fun matchQuizIDToDb() {
        firestore.collection(ConstantsFireStore.QUIZ_DATA_ROOT).get().addOnSuccessListener {
            if(!it.isEmpty){
                var check=true
                for (docSnap in it){
                    if(docSnap.id.equals(quizID)){
                        check=false
                        lottie_loading_jointhequiz.visibility=View.GONE
                        val i = Intent(this,StartingTheQuizActivity::class.java)
                        i.putExtra(ConstantsPutExtra.QUIZ_ID,quizID)
                        startActivity(i)
                    }
                }
                if(check){
                    lottie_loading_jointhequiz.visibility=View.GONE
                    tl_quiz_id.isErrorEnabled=true
                    tl_quiz_id.error="Please enter a valid quiz id"
                }
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean{
        onBackPressed()
        return true
    }
}