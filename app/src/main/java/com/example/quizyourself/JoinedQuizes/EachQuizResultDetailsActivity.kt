package com.example.quizyourself.JoinedQuizes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.example.quizyourself.Constants.ConstantsFireStore
import com.example.quizyourself.Constants.ConstantsPutExtra
import com.example.quizyourself.Data.QuizResultData
import com.example.quizyourself.JoinedQuizes.QuizResult.ResultActivity
import com.example.quizyourself.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_each_quiz_details.*
import kotlinx.android.synthetic.main.activity_each_quiz_result_details.*
import kotlinx.android.synthetic.main.activity_each_quiz_result_details.tv_eachQuizDate
import kotlinx.android.synthetic.main.activity_each_quiz_result_details.tv_eachQuizDesc
import kotlinx.android.synthetic.main.activity_each_quiz_result_details.tv_eachQuizEnd
import kotlinx.android.synthetic.main.activity_each_quiz_result_details.tv_eachQuizStart
import kotlinx.android.synthetic.main.activity_each_quiz_result_details.tv_eachQuizTotalQues
import java.text.SimpleDateFormat
import java.util.*

class EachQuizResultDetailsActivity : AppCompatActivity() {
    var quizResultList : QuizResultData? = null
    lateinit var firestore: FirebaseFirestore
    lateinit var quizId : String

    var endMin = 0
    var endHour = 0
    var Day = 0
    var Month = 0
    var Year = 0

    var currTimeMillis : Long = 0
    var EndTimeMillis : Long = 0
    var TimeLeftForResult : Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_each_quiz_result_details)
        supportActionBar?.title=""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        quizId = intent.getStringExtra(ConstantsPutExtra.QUIZ_ID)!!
        firestore = FirebaseFirestore.getInstance()
        //currTimeMillis
        currTimeMillis = System.currentTimeMillis()
        Log.d("MY",currTimeMillis.toString())
        loadQuizDetailsFromFirestore()

        btn_seeResult.setOnClickListener{
            startActivity(Intent(this,ResultActivity::class.java))
        }
    }

    private fun loadQuizDetailsFromFirestore() {
        firestore.collection(ConstantsFireStore.QUIZ_RESULT_ROOT).document(quizId).get().addOnSuccessListener {
            if(it.exists()){

                //Stop shimmering
                shimmer_quizResult.stopShimmer()
                shimmer_quizResult.visibility = View.GONE
                scrollview_eachQuizResult.visibility =View.VISIBLE
                quizResultList = it.toObject(QuizResultData::class.java)

                tv_eachQuizDesc.text = quizResultList?.QUIZ_DESC
                tv_title_quizResult.text = quizResultList?.QUIZ_TITLE

                //Int endMin,endHour....
                endHour = quizResultList!!.END_HOUR.toInt()
                endMin = quizResultList!!.END_MIN.toInt()
                Day = quizResultList!!.START_DATE.toInt()
                Month = quizResultList!!.START_MONTH.toInt()
                Year = quizResultList!!.START_YEAR.toInt()

                //Convert date to MMMM dd,yyyy
                var mycal = Calendar.getInstance()
                var sdf= SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
                //For date
                mycal.set(Calendar.MONTH,quizResultList!!.START_MONTH.toInt())
                mycal.set(Calendar.YEAR,quizResultList!!.START_YEAR.toInt())
                mycal.set(Calendar.DAY_OF_MONTH,quizResultList!!.START_DATE.toInt())
                tv_eachQuizDate.text=sdf.format(mycal.time)
                //For time
                sdf= SimpleDateFormat("HH:mm", Locale.ENGLISH)
                //start Time
                mycal.set(Calendar.HOUR_OF_DAY,quizResultList!!.START_HOUR!!.toInt())
                mycal.set(Calendar.MINUTE,quizResultList!!.START_MIN!!.toInt())
                tv_eachQuizStart.text=sdf.format(mycal.time)
                //end Time
                mycal.set(Calendar.HOUR_OF_DAY,quizResultList!!.END_HOUR.toInt())
                mycal.set(Calendar.MINUTE,quizResultList!!.END_MIN!!.toInt())
                tv_eachQuizEnd.text=sdf.format(mycal.time)
                //Total questions
                tv_eachQuizTotalQues.text=quizResultList!!.TOTAL_QUES

                displayTimeLeftForResult()


            }
        }
    }

    private fun displayTimeLeftForResult() {
        //EndTimeMillis
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY,endHour)
        cal.set(Calendar.MINUTE,endMin)
        cal.set(Calendar.DAY_OF_MONTH,Day)
        cal.set(Calendar.MONTH,Month)
        cal.set(Calendar.YEAR,Year)
        cal.set(Calendar.SECOND,0)
        EndTimeMillis = cal.timeInMillis

        TimeLeftForResult = EndTimeMillis - currTimeMillis

        if(TimeLeftForResult <=0){
            btn_seeResult.visibility = View.VISIBLE
            cv_TimeLeftResult.visibility = View.GONE
        }
        else
        {
            Log.d("HERE",(currTimeMillis/1000).toString())
            var countDownTimer = object : CountDownTimer(TimeLeftForResult,1000){
                override fun onTick(millisUntilFinished: Long) {
                    var totalsec = millisUntilFinished / 1000
                    var hour= totalsec/3600  //hour if 8.04545 => 8 Hour
                    var min =(totalsec-(3600*hour))/60 // total second mai se jitna hour ka sec use hata k divide by 60 (to convert to min)
                    var sec=(totalsec-(3600*hour)-(60*min))
                    var hourString=hour.toString()
                    var minString=min.toString()
                    var secString = sec.toString()
                    if(hourString.length<=1){
                        hourString="0"+hourString
                    }
                    if(minString.length<=1){
                        minString="0"+minString
                    }
                    if(secString.length<=1){
                        secString="0"+secString
                    }

                    tv_timeLeftResult.text ="$hourString:$minString:$secString"
                }

                override fun onFinish() {
                    btn_seeResult.visibility = View.VISIBLE
                    cv_TimeLeftResult.visibility = View.GONE
                }

            }
            countDownTimer.start()
        }

    }

    override fun onResume() {
        super.onResume()
        shimmer_quizResult.startShimmer()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}