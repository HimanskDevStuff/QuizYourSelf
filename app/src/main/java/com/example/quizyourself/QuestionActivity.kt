package com.example.quizyourself

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_question.*

class QuestionActivity : AppCompatActivity(), View.OnClickListener{
    var mCurrentPos = 1
    var questionsList = QuestionList.getQuestionList()
    var mSelectedAns = 0
    var correctAnsCount = 0
    var username : String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        setQuestions()
        tv_optionOne.setOnClickListener(this)
        tv_optionTwo.setOnClickListener(this)
        tv_optionThree.setOnClickListener(this)
        tv_optionFour.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
        username = intent.getStringExtra(QuestionList.USER_NAME).toString()

    }

    private fun defaultOptionsView() {
        var options = ArrayList<TextView>()
        options.add(tv_optionOne)
        options.add(tv_optionTwo)
        options.add(tv_optionThree)
        options.add(tv_optionFour)

        for (op in options) {
            op.setTextColor(Color.parseColor("#889188"))
            op.typeface = Typeface.DEFAULT
            op.background = ContextCompat.getDrawable(this,R.drawable.textbox_shape)
        }

    }

    private fun setQuestions() {
        var quesIs = questionsList[mCurrentPos-1]
        tv_quesid.text = "${quesIs.id}. ${quesIs.ques}"
        iv_flag.setImageResource(quesIs.image)
        progressbar.progress = mCurrentPos
        tv_progresscount.text = "$mCurrentPos / ${progressbar.max}"
        tv_optionOne.text=quesIs.optionOne
        tv_optionTwo.text=quesIs.optionTwo
        tv_optionThree.text=quesIs.optionThree
        tv_optionFour.text=quesIs.optionFour
    }

    override fun onClick(v: View?) {
        defaultOptionsView()
        when(v)
        {
            tv_optionOne -> selectedOptionView(tv_optionOne,1)
            tv_optionTwo -> selectedOptionView(tv_optionTwo,2)
            tv_optionThree -> selectedOptionView(tv_optionThree,3)
            tv_optionFour -> selectedOptionView(tv_optionFour,4)
            btn_submit -> {
                if(mSelectedAns == 0){
                    mCurrentPos++
                    if(mCurrentPos<=questionsList.size)
                    {
                        setQuestions()
                        btn_submit.text="SUBMIT"
                    }
                    else
                    {
                        var intent = Intent(this,Result::class.java)
                        intent.putExtra(QuestionList.USER_NAME,username)
                        intent.putExtra(QuestionList.CORRECT_ANS,correctAnsCount)
                        intent.putExtra(QuestionList.TOTAL_QUES,questionsList.size)
                        startActivity(intent)
                        finish()
                    }
                }
                else{
                    var quesIs=questionsList[mCurrentPos-1]
                    if(mSelectedAns != quesIs.corrOption)
                    {
                        answerView(mSelectedAns,R.drawable.wrong_ans_box)
                    }
                    else {
                        correctAnsCount++
                    }
                    answerView(quesIs.corrOption,R.drawable.curr_ans_box)
                    if(mCurrentPos == questionsList.size){
                        btn_submit.text = "FINISH"
                    }
                    else
                        btn_submit.text = "GO TO NEXT QUESTION"
                    mSelectedAns = 0
                }
            }
        }
    }
    private fun answerView(ans : Int,drawable : Int){
        when(ans){
            1->{
                tv_optionOne.background=ContextCompat.getDrawable(this,drawable)
                tv_optionOne.setTextColor(Color.parseColor("#000000"))
                tv_optionOne.typeface= Typeface.DEFAULT_BOLD

            }
            2->{
                tv_optionTwo.background=ContextCompat.getDrawable(this,drawable)
                tv_optionTwo.setTextColor(Color.parseColor("#000000"))
                tv_optionTwo.typeface= Typeface.DEFAULT_BOLD
            }
            3->{
                tv_optionThree.background=ContextCompat.getDrawable(this,drawable)
                tv_optionThree.setTextColor(Color.parseColor("#000000"))
                tv_optionThree.typeface= Typeface.DEFAULT_BOLD
            }
            4->{
                tv_optionFour.background=ContextCompat.getDrawable(this,drawable)
                tv_optionFour.setTextColor(Color.parseColor("#000000"))
                tv_optionFour.typeface= Typeface.DEFAULT_BOLD
            }

        }
    }
    private fun selectedOptionView(tv : TextView, optionNo : Int){
        mSelectedAns = optionNo
        tv.setTextColor(Color.parseColor("#000000"))
        tv.typeface = Typeface.DEFAULT_BOLD
        tv.background = ContextCompat.getDrawable(this,R.drawable.selected_textbox_shape)
    }
}