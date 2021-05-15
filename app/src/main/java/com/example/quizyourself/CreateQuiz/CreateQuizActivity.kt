package com.example.quizyourself.CreateQuiz

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quizyourself.Constants.Constants
import com.example.quizyourself.Constants.ConstantsQuizInfo
import com.example.quizyourself.R
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_create_quiz.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main_page.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap

class CreateQuizActivity : AppCompatActivity() {
    lateinit var userMail : String
    lateinit var title : String
    lateinit var desc :String
    lateinit var totalQues : String
    lateinit var totalOpt : String
    lateinit var quizInfoList: LinkedHashMap<String,Any>
    lateinit var startTime : String
    lateinit var endTime : String
    lateinit var cal : Calendar
    var currhour : Int = 0
    var currmin : Int = 0

    var startHour : Int = 0
    var startMin : Int = 0
    var endHour : Int = 0
    var endMin : Int = 0
    lateinit var quizDate : String
    lateinit var quizMonth : String
    lateinit var quizYear : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_quiz)

        var actionBar = supportActionBar
        actionBar?.title = "Create Quiz"
        actionBar?.setDisplayHomeAsUpEnabled(true)

        //get User email to add to firestore to mark who created the quiz
        val sharedpref = getSharedPreferences(Constants.ROOT_SHAREDPREFERENCES, Context.MODE_PRIVATE)
        userMail=sharedpref.getString(Constants.EMAIL_SHAREDPREF,"").toString()

        cal = Calendar.getInstance()
        currhour = cal.get(Calendar.HOUR_OF_DAY)
        currmin=cal.get(Calendar.MINUTE)

        //Init timePicker default hour and min
        startHour=currhour
        endHour=currmin
        startMin=currmin
        endMin=currmin
        setQuizDatewithCurrDate()
        btn_startAddQuest.setOnClickListener{
            initTextToString()
            if(validateData()){
                    quizInfoList  = linkedMapOf(
                    ConstantsQuizInfo.CREATED_BY to userMail,
                    ConstantsQuizInfo.TITLE to title,
                    ConstantsQuizInfo.DESC to desc,
                    ConstantsQuizInfo.TOTAL_QUES to totalQues,
                    ConstantsQuizInfo.TOTAL_OPTIONS to totalOpt,
                    ConstantsQuizInfo.QUIZ_START_HOUR to startHour.toString(),
                    ConstantsQuizInfo.QUIZ_START_HOUR to startHour.toString(),
                    ConstantsQuizInfo.QUIZ_START_MIN to startMin.toString(),
                    ConstantsQuizInfo.QUIZ_END_HOUR to endHour.toString(),
                    ConstantsQuizInfo.QUIZ_END_MIN to endMin.toString(),
                    ConstantsQuizInfo.QUIZ_START_DATE to quizDate,
                    ConstantsQuizInfo.QUIZ_START_MONTH to quizMonth,
                    ConstantsQuizInfo.QUIZ_START_YEAR to quizYear
                )
                saveQuizInfoToSharedPref()
                startActivity(Intent(this@CreateQuizActivity,AddQuestionsActivity::class.java))
            }
        }

        //Start time listerner
        tv_startTime.setOnClickListener{
            timePickerStart()
        }
        //End Time listener
        tv_endTime.setOnClickListener{
            timePickerEnd()
        }
        //Date picker listener
        tv_quizDate.setOnClickListener{
            datePick()
        }

    }

    private fun datePick() {
        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{
            view, selectedYear, selectedMonth, selectedDayOfMonth ->
            //override Quizdate,year,month with selectedDate,year,month
            quizMonth=selectedMonth.toString()
            quizYear=selectedYear.toString()
            quizDate=selectedDayOfMonth.toString()

            //Display selected date with MM dd,yyyy formating
            val sdf = SimpleDateFormat("MMMM dd,yyyy", Locale.ENGLISH)
            val cal=Calendar.getInstance()
            //override calender date,month,year
            cal.set(Calendar.YEAR,selectedYear)
            cal.set(Calendar.MONTH,selectedMonth)
            cal.set(Calendar.DAY_OF_MONTH,selectedDayOfMonth)
            tv_quizDate.text=sdf.format(cal.time)
        },quizYear.toInt(),quizMonth.toInt(),quizDate.toInt())
        //setMin date to currDate and disable past dates
        datePickerDialog.datePicker.minDate=System.currentTimeMillis()-1000
        datePickerDialog.show()
    }

    private fun timePickerEnd() {
        var timeDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener{
                view, selectedHour, selectedminute ->
            //Conver time to format
            var sdf = SimpleDateFormat("HH:mm",Locale.ENGLISH)
            var calIns= Calendar.getInstance()
            calIns.set(Calendar.HOUR_OF_DAY,selectedHour)
            calIns.set(Calendar.MINUTE,selectedminute)
            tv_endTime.text=sdf.format(calIns.time)

            //change time picker default hour and min to selectedHour and selectedMIn
            endHour=selectedHour
            endMin=selectedminute

        },endHour,endMin,true)
        timeDialog.show()
    }

    private fun timePickerStart() {
        var timeDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener{
            view, selectedHour, selectedminute ->
            //Conver time to format
            var sdf = SimpleDateFormat("HH:mm",Locale.ENGLISH)
            var calIns= Calendar.getInstance()
            calIns.set(Calendar.HOUR_OF_DAY,selectedHour)
            calIns.set(Calendar.MINUTE,selectedminute)
            tv_startTime.text=sdf.format(calIns.time)

            //change time picker default hour and min to selectedHour and selectedMIn
            startHour=selectedHour
            startMin=selectedminute
        },startHour,startMin,true)
        timeDialog.show()
    }

    private fun setQuizDatewithCurrDate() {
        var calendarInstance = Calendar.getInstance()
        var sdf = SimpleDateFormat("MMMM dd,yyyy",Locale.ENGLISH)
        tv_quizDate.text=sdf.format(calendarInstance.time)
        //Init quizDate with curr Date
        quizDate=calendarInstance.get(Calendar.DAY_OF_MONTH).toString()
        quizMonth=calendarInstance.get(Calendar.MONTH).toString()
        quizYear=calendarInstance.get(Calendar.YEAR).toString()
    }

    private fun saveQuizInfoToSharedPref() {
        var sharedPref = getSharedPreferences(Constants.ROOT_SHAREDPREFERENCES, Context.MODE_PRIVATE)
        var edit = sharedPref.edit()
        //Saving hashMap in sharedPref is not possible
        //So work around for this is converting hashMap to Json
        var gson = Gson()
        var json = gson.toJson(quizInfoList)
        edit.putString(ConstantsQuizInfo.QuizInfoList,json)
        edit.apply()
    }

    private fun initTextToString() {
        title=et_quizTitle.text.toString()
        desc=et_quizDesc.text.toString()
        totalQues=spinner_totalQues.selectedItem.toString()
        totalOpt=spinner_totalOption.selectedItem.toString()
        startTime=tv_startTime.text.toString()
        endTime=tv_endTime.text.toString()
    }

    private fun validateData() : Boolean{
        var check = true
        //title
        if(title.isEmpty() || title.length < 5)
        {
            tl_title.setError("Title length should be atleast 5 characters")
            tl_title.isErrorEnabled=true
            check = false
        }
        else{
            tl_title.isErrorEnabled=false
        }
        //desc
        if(desc.isEmpty() || desc.length < 20)
        {
            tl_desc.setError("Description length should be atleast 20 Characters")
            tl_desc.isErrorEnabled=true
            check = false
        }
        else{
            tl_desc.isErrorEnabled=false
        }
        if(startTime.equals("Select time") || endTime.equals("Select time"))
        {
            check=false
            snackBarMaker("Please enter a valid start and end time")
        }
        else if(quizDate.equals(Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString())){
            //Check if startTime and endTime is less than hour and min
            if(startHour<currhour){
                check=false
                snackBarMaker("Please enter a future time")
            }
            else if(startHour==currhour && startMin<currmin)
            {
                check=false
                snackBarMaker("Please enter a future time")
            }
        }
        return check

    }
    fun snackBarMaker(error : String){
        var snack=Snackbar.make(coordinator_startEndTime,error,Snackbar.LENGTH_LONG)
        snack.setAction("OKAY",{})
        snack.setTextColor(Color.parseColor("#FFFFFF"))
        snack.setActionTextColor(Color.parseColor("#FF0000"))
        snack.show()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}