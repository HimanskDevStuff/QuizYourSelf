package com.example.quizyourself.YourQuiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.example.quizyourself.Constants.ConstantsFireStore
import com.example.quizyourself.Constants.ConstantsPutExtra
import com.example.quizyourself.Data.QuizData
import com.example.quizyourself.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_each_quiz_details.*
import java.text.SimpleDateFormat
import java.util.*

class eachQuizDetailsActivity : AppCompatActivity() {
    val firestore = FirebaseFirestore.getInstance()
    lateinit var quizId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_each_quiz_details)
        var actionBar = supportActionBar
        actionBar?.title=""
        actionBar?.setDisplayHomeAsUpEnabled(true)
        //Get quiz id from getExtra
        val i = intent
        quizId=i.getStringExtra(ConstantsPutExtra.QUIZ_ID)!!

        //listners
        btn_seeQuestions.setOnClickListener{
            val i = Intent(this,QuizQuestionListActivity::class.java)
            i.putExtra(ConstantsPutExtra.QUIZ_ID,quizId)
            startActivity(i)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            loadDataAndDisplay()
        },2000)

    }

    private fun loadDataAndDisplay() {
        //Extract data from document with id = quizId
        firestore.collection(ConstantsFireStore.QUIZ_DATA_ROOT).document(quizId).get().addOnSuccessListener {
            if(it.exists()){
                shimmer_yourQuiz.stopShimmer()
                shimmer_yourQuiz.visibility= View.GONE
                scrollview_eachQuizDetails.visibility=View.VISIBLE
                btn_seeQuestions.visibility=View.VISIBLE

                val quizDetails = it.toObject(QuizData::class.java)
                //set quizDetails in views
                tv_title_yourQuiz.text=quizDetails?.QUIZ_TITLE
                tv_eachQuizDesc.text=quizDetails?.QUIZ_DESC
                //Convert date to MMMM dd,yyyy
                var mycal = Calendar.getInstance()
                var sdf=SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
                //For date
                mycal.set(Calendar.MONTH,quizDetails!!.START_MONTH.toInt())
                mycal.set(Calendar.YEAR,quizDetails.START_YEAR.toInt())
                mycal.set(Calendar.DAY_OF_MONTH,quizDetails.START_DATE.toInt())
                tv_eachQuizDate.text=sdf.format(mycal.time)
                //For time
                sdf= SimpleDateFormat("HH:mm",Locale.ENGLISH)
                //start Time
                mycal.set(Calendar.HOUR_OF_DAY,quizDetails.START_HOUR!!.toInt())
                mycal.set(Calendar.MINUTE,quizDetails.START_MIN!!.toInt())
                tv_eachQuizStart.text=sdf.format(mycal.time)
                //end Time
                mycal.set(Calendar.HOUR_OF_DAY,quizDetails.END_HOUR.toInt())
                mycal.set(Calendar.MINUTE,quizDetails.END_MIN!!.toInt())
                tv_eachQuizEnd.text=sdf.format(mycal.time)

                //Total questions
                tv_eachQuizTotalQues.text=quizDetails!!.TOTAL_QUES

                //Started or not
                mycal= Calendar.getInstance()
                var currH = mycal.get(Calendar.HOUR_OF_DAY)
                var cuurM=mycal.get(Calendar.MINUTE)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shimmer_yourQuiz.startShimmer()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}