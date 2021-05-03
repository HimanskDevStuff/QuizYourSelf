package com.example.quizyourself.JoinQuiz

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.quizyourself.Constants.ConstantAnsweredInfo
import com.example.quizyourself.Constants.ConstantsPutExtra
import com.example.quizyourself.Constants.ConstantsQuestionInfo
import com.example.quizyourself.R
import com.google.common.collect.ArrayListMultimap
import kotlinx.android.synthetic.main.activity_quiz_started.*
import kotlinx.android.synthetic.main.button_options.view.*

class QuizStartedActivity : AppCompatActivity() {
    lateinit var quizQuestion : ArrayList<Pair<String,Any>>
    lateinit var quizTotalOptions : String
    lateinit var quizTotalQuestion : String
    lateinit var choosenAnswer : String
    lateinit var choosenAnswerList : ArrayList<String>
    lateinit var optionViews : ArrayList<View>
    lateinit var quizId : String
    var eachQuestionMap : MutableMap<String,Any> ? = null
    var endTimeMillis : Long = 0
    var timeLeftMillis : Long = 0
    var position : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_started)
        var actionbar = supportActionBar
        actionbar?.title=""
        actionbar?.setDisplayHomeAsUpEnabled(true)
        //Init
        choosenAnswer="Not Answered"
        optionViews= arrayListOf()
        choosenAnswerList= arrayListOf()

        //Get extra values
        val i = intent
        quizQuestion = i.getSerializableExtra(ConstantsPutExtra.QUIZ_QUESTIONS) as ArrayList<Pair<String, Any>>
        quizTotalOptions = i.getStringExtra(ConstantsPutExtra.TOTAL_OPTIONS)!!
        quizTotalQuestion= i.getStringExtra(ConstantsPutExtra.TOTAL_QUESTION)!!
        endTimeMillis = i.getLongExtra(ConstantsPutExtra.END_TIME_MILLIS,0)
        quizId=i.getStringExtra(ConstantsPutExtra.QUIZ_ID)!!

        //Init AnsweredList with Not Answered
        for (i in 0 until quizTotalQuestion.toInt())
        {
            choosenAnswerList.add("Not Answered")
        }
        //Init Not answered tv with total questions
        tv_not_answered_quizStarted.text=quizTotalQuestion
        //Display Constant value throughout the quiz
        tv_totalQuestions_quizStated.append(quizTotalQuestion)
        //Create options according to the number of options available for this quiz
        for(count in 0 until quizTotalOptions.toInt()){
            val view = layoutInflater.inflate(R.layout.button_options,null)
            optionViews.add(view)
            ll_options.addView(view)
        }
        //Display First Question
        DisplayQuestion()

        //Set timer
        setTimer()
        //Listeners for optionClicks
        for (views in optionViews){
            views.btn_option_quizStarted.setOnClickListener{
                makeOptionsLikeDefault()
                if(views.btn_option_quizStarted.text.equals(choosenAnswer))
                {
                    makeOptionsLikeDefault()
                    choosenAnswer="Not Answered"
                    //make choosen ans in list as not answered
                    choosenAnswerList[position]=choosenAnswer
                    eachQuestionMap!![ConstantAnsweredInfo.CHOOSEN_OPTION]=choosenAnswer

                }else {
                    views.btn_option_quizStarted.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            android.R.color.holo_green_dark
                        )
                    )
                    //set Ans
                    choosenAnswer=views.btn_option_quizStarted.text.toString().substring(3)
                }
            }
        }
        //Listener for btns
        btn_next_quizStarted.setOnClickListener{
            //make prev button enabled
            btn_prev_quizStarted.visibility=View.VISIBLE
            //Save ans to list
            if ((choosenAnswerList[position]=="Not Answered")){
                choosenAnswerList[position]=choosenAnswer
                eachQuestionMap!![ConstantAnsweredInfo.CHOOSEN_OPTION]=choosenAnswer
                //add updated question detail to main question list
                quizQuestion[position]=quizQuestion[position].copy(second = eachQuestionMap!!)

            }else if(!choosenAnswerList[position].equals(choosenAnswer)){
                if(!(choosenAnswer=="Not Answered")){
                    choosenAnswerList[position]=choosenAnswer
                    eachQuestionMap!![ConstantAnsweredInfo.CHOOSEN_OPTION]=choosenAnswer

                    //add updated question detail to main question list
                    quizQuestion[position]=quizQuestion[position].copy(second = eachQuestionMap!!)
                }
            }
            Log.d("CHOOSEN",choosenAnswerList.toString())

            if(!((position+1)==(quizTotalQuestion.toInt()))){
                //incr pos for next ques
                position++
                choosenAnswer="Not Answered"
                makeOptionsLikeDefault()
                //count answered and not answered question
                countAnsweredNotAnswered()
                DisplayQuestion()
            }
            else{
                //make next btn gone and submit btn visible when question is last
                btn_next_quizStarted.visibility = View.GONE
                btn_submit_quizStarted.visibility=View.VISIBLE
                //count answered and not answered question
                countAnsweredNotAnswered()
            }
                //make next as done for last question
            if(position==(quizTotalQuestion.toInt()-1)) {
              btn_next_quizStarted.text="DONE"
            }


        }
        btn_prev_quizStarted.setOnClickListener{
            position--
            //make prev button disable when its first question
            if(position==0)
            btn_prev_quizStarted.visibility=View.GONE

            //make next btn visible and submit btn gone when position is less than totalQUestion
            if(position<(quizTotalQuestion.toInt()-1)) {
                btn_next_quizStarted.visibility = View.VISIBLE
                btn_next_quizStarted.text="NEXT"
                btn_submit_quizStarted.visibility=View.GONE
            }

            makeOptionsLikeDefault()
            DisplayQuestion()
        }

        //Listener for submit
        btn_submit_quizStarted.setOnClickListener{
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Do you want to submit the quiz?")
            dialog.setMessage("This can't be undone")
            dialog.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                val i = Intent(this,QuizSubmitted::class.java)
                i.putExtra(ConstantsPutExtra.QUIZ_ID,quizId)
                i.putExtra(ConstantsPutExtra.QUIZ_QUESTIONS,quizQuestion)
                startActivity(i)
                finish()
            }
            dialog.setNegativeButton("No",{ dialogInterface: DialogInterface, i: Int -> })
            dialog.show()
        }
    }

    private fun countAnsweredNotAnswered() {
        var answered = 0
        var notanswered = 0
        for(item in choosenAnswerList){
            if(item == "Not Answered"){
                notanswered++
            }
            else
            {
                answered++
            }
        }
        tv_answered_quizStarted.text=answered.toString()
        tv_not_answered_quizStarted.text=notanswered.toString()
    }


    private fun setTimer() {
        timeLeftMillis = endTimeMillis - System.currentTimeMillis()
        val countDownTimer = object : CountDownTimer(timeLeftMillis,1000){
            override fun onFinish() {

            }

            override fun onTick(millisUntilFinished: Long) {
                var totalSeconds = millisUntilFinished/1000
                var hour = totalSeconds/3600
                var min = (totalSeconds - hour*3600)/60
                var sec = totalSeconds - (hour*3600) - (min*60)
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
                tv_timer_quizStarted.text="$hourString:$minString:$secString"
            }

        }
        countDownTimer.start()
    }

    private fun makeOptionsLikeDefault() {
        for (views in optionViews){
            views.btn_option_quizStarted.background=ContextCompat.getDrawable(this,R.drawable.grey_white_button_bg)
        }
    }

    private fun DisplayQuestion() {
        //Get each Question from list which is saved as Map in firestore
       eachQuestionMap = quizQuestion[position].second as MutableMap<String, Any>

       //Display Questions
       tv_questDesc_quizStarted.text=eachQuestionMap!![ConstantsQuestionInfo.QUES_DESC] as String
       tv_currQuestionNumber_quizStarted.text=quizQuestion[position].first
       //DisplayOptions
        var ctr = -1
        val optionMap = eachQuestionMap!![ConstantsQuestionInfo.OPTION] as HashMap<String,String>
        var optionNo = 0
       for (views in optionViews){
           optionNo++
           views.btn_option_quizStarted.text="$optionNo. ${optionMap[optionNo.toString()]}"
       }

        //marking prev answered question in greem
            if (!choosenAnswerList[position].equals("Not Answered")){
                for(views in optionViews){
                    if(choosenAnswerList[position].equals(views.btn_option_quizStarted.text.toString().substring(3))){
                        views.btn_option_quizStarted.setBackgroundColor(ContextCompat.getColor(this,android.R.color.holo_green_dark))
                    }
                }
            }
    }

    override fun onBackPressed() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Do you want to close the app?")
        dialog.setMessage("Closing the app will autosubmit the quiz")
        dialog.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            finishAffinity()
        }
        dialog.setNegativeButton("No",{ dialogInterface: DialogInterface, i: Int -> })
        dialog.show()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}