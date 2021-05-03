package com.example.quizyourself.JoinQuiz

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.quizyourself.Constants.ConstantsFireStore
import com.example.quizyourself.Constants.ConstantsPutExtra
import com.example.quizyourself.Data.QuizData
import com.example.quizyourself.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_each_quiz_details.*
import kotlinx.android.synthetic.main.activity_starting_the_quiz.*
import java.io.Serializable
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*

class StartingTheQuizActivity : AppCompatActivity() {
    var currSystemTimeMillis : Long= 0
    var quizStartTimeInMills : Long = 0
    var quizEndTimeInMills : Long = 0

    var QuizTimeMillis : Long = 0
    var timeLeftMillis : Long = 0

    var quizQuestions : List<Pair<String,Any>>?=null
    var quizDetails : QuizData ?=null
    lateinit var quizId : String
    lateinit var firestore: FirebaseFirestore
    var quizStartDay = 0
    var quizStartMonth = 0
    var quizStartYear = 0

    var quizStartHour = 0
    var quizStartMin=0

    var quizEndHour = 0
    var quizEndMin=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting_the_quiz)
        var actionbar = supportActionBar
        actionbar?.title = ""
        actionbar?.setDisplayHomeAsUpEnabled(true)
        firestore= FirebaseFirestore.getInstance()
        val i = intent
        quizId=i.getStringExtra(ConstantsPutExtra.QUIZ_ID)!!
        //get system time in millis
        currSystemTimeMillis=System.currentTimeMillis()

        btn_startQuiz_start.setOnClickListener{
            val i = Intent(this,QuizStartedActivity::class.java)
            i.putExtra(ConstantsPutExtra.QUIZ_QUESTIONS,quizQuestions as Serializable)
            i.putExtra(ConstantsPutExtra.TOTAL_OPTIONS,quizDetails?.TOTAL_OPTIONS)
            i.putExtra(ConstantsPutExtra.TOTAL_QUESTION,quizDetails?.TOTAL_QUES)
            i.putExtra(ConstantsPutExtra.END_TIME_MILLIS,quizEndTimeInMills)
            i.putExtra(ConstantsPutExtra.QUIZ_ID,quizId)
            startActivity(i)
        }
        DisplayQuizInfo()
    }

    private fun DisplayQuizInfo() {
        firestore.collection(ConstantsFireStore.QUIZ_DATA_ROOT).document(quizId).get().addOnSuccessListener {
            if(it.exists()){
                lottie_startQuiz.visibility= View.GONE
                ll_quizInfo_startQuiz.visibility=View.VISIBLE
                quizDetails = it.toObject(QuizData::class.java)
                quizQuestions=quizDetails?.QUIZ_QUESTIONS?.toList()

                Log.e("MAI IDHAR",quizQuestions.toString())
                //Display all Info
                tv_quizName_start.text=quizDetails?.QUIZ_TITLE
                tv_desc_start.text=quizDetails?.QUIZ_DESC
                tv_QuizTotalQues_start.text=quizDetails?.TOTAL_QUES
                //Convert date to MMMM dd,yyyy
                var mycal = Calendar.getInstance()
                var sdf= SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
                //For date
                mycal.set(Calendar.MONTH,quizDetails!!.START_MONTH.toInt())
                mycal.set(Calendar.YEAR,quizDetails!!.START_YEAR.toInt())
                mycal.set(Calendar.DAY_OF_MONTH,quizDetails!!.START_DATE.toInt())
                tv_quizDate_start.text=sdf.format(mycal.time)
                //save start day,month,year
                quizStartDay=quizDetails!!.START_DATE.toInt()
                quizStartMonth=quizDetails!!.START_MONTH.toInt()
                quizStartYear=quizDetails!!.START_YEAR.toInt()
                //For time
                sdf= SimpleDateFormat("HH:mm",Locale.ENGLISH)
                //start Time
                mycal.set(Calendar.HOUR_OF_DAY,quizDetails!!.START_HOUR!!.toInt())
                mycal.set(Calendar.MINUTE,quizDetails!!.START_MIN!!.toInt())
                tv_QuizStartTime_start.text=sdf.format(mycal.time)
                //save start hour,min
                quizStartHour=quizDetails!!.START_HOUR.toInt()
                quizStartMin=quizDetails!!.START_MIN.toInt()
                //end Time
                mycal.set(Calendar.HOUR_OF_DAY,quizDetails!!.END_HOUR.toInt())
                mycal.set(Calendar.MINUTE,quizDetails!!.END_MIN!!.toInt())
                tv_QuizEndTime_start.text=sdf.format(mycal.time)
                //save end hour,min
                quizEndHour=quizDetails!!.END_HOUR.toInt()
                quizEndMin=quizDetails!!.END_MIN.toInt()

                tv_enable_disable_startQuiz.text="Start button will be enabled sharp at ${tv_QuizStartTime_start.text} on ${tv_quizDate_start.text}"

                displayTimeLeftForQuiz()
            }
            else{
                Toast.makeText(this,"Doesn't exist",Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this,it.toString(),Toast.LENGTH_LONG).show()
        }
    }

    private fun displayTimeLeftForQuiz(){
        //Get difference between quizCurrtimeMillis - systemCurrentTimeMills
        timeLeftMillis = getDifferenceBetweenQuizStartTimeSystemTime()
        QuizTimeMillis = getDifferenceBetweenStartTimeEndTime()

        if(timeLeftMillis<0){
            //dont start timer
            if(abs(timeLeftMillis)<QuizTimeMillis){
                //Test is going on
                btn_startQuiz_start.visibility=View.VISIBLE
                tv_timer_startQuiz.visibility=View.GONE
                tv_startsIn_startQuiz.text="You can now start the quiz"
                tv_startsIn_startQuiz.setTextColor(resources.getColor(R.color.text_green_color))
                cv_startEndDate.visibility=View.GONE
                lottie_timer_startQuiz.visibility=View.GONE
            }
            else if(abs(timeLeftMillis)>=QuizTimeMillis){
                //Test Ended
                tv_timer_startQuiz.visibility=View.GONE
                tv_startsIn_startQuiz.text="Quiz Ended"
                tv_startsIn_startQuiz.setTextColor(resources.getColor(android.R.color.holo_red_light))
                lottie_startQuiz.visibility=View.GONE
                tv_enable_disable_startQuiz.visibility=View.GONE
                btn_startQuiz_start.visibility=View.GONE
                lottie_timer_startQuiz.visibility=View.GONE

            }
        }else{
            startCountDownTimer(timeLeftMillis)
        }
    }

    private fun getDifferenceBetweenStartTimeEndTime() : Long{
        //Get quiz time in mills
        var cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH,quizStartDay)
        cal.set(Calendar.MONTH,quizStartMonth)
        cal.set(Calendar.YEAR,quizStartYear)
        cal.set(Calendar.HOUR_OF_DAY,quizEndHour)
        cal.set(Calendar.MINUTE,quizEndMin)
        cal.set(Calendar.SECOND,0)

        quizEndTimeInMills=cal.timeInMillis

        return quizEndTimeInMills-quizStartTimeInMills
    }

    private fun startCountDownTimer(timeLeftMillis: Long) {
        //countdowntimer is interface
        var countDownTimer=object : CountDownTimer(timeLeftMillis,1000){
            override fun onFinish() {
                btn_startQuiz_start.visibility=View.VISIBLE
                tv_timer_startQuiz.visibility=View.GONE
                tv_startsIn_startQuiz.text="You can now start the quiz"
                tv_startsIn_startQuiz.setTextColor(resources.getColor(R.color.text_green_color))
                cv_startEndDate.visibility=View.GONE
                lottie_timer_startQuiz.visibility=View.GONE
            }

            override fun onTick(millisUntilFinished: Long) {
                //on tick is called after every timeIntervlMillis
                //millisUtilFinish contains totalTimeInterval - timeIntervalMillis
                //convert millis to sec
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

                tv_timer_startQuiz.text="$hourString:$minString:$secString"

            }

        }
        countDownTimer.start()
    }

    private fun getDifferenceBetweenQuizStartTimeSystemTime() : Long {

        //Get quiz time in mills
        var cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH,quizStartDay)
        cal.set(Calendar.MONTH,quizStartMonth) //starts with 0
        cal.set(Calendar.YEAR,quizStartYear)
        cal.set(Calendar.HOUR_OF_DAY,quizStartHour)
        cal.set(Calendar.MINUTE,quizStartMin)
        cal.set(Calendar.SECOND,0)

        quizStartTimeInMills=cal.timeInMillis
     
        return quizStartTimeInMills-currSystemTimeMillis
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}