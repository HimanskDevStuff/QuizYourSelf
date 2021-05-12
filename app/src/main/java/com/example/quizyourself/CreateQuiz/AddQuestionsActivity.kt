package com.example.quizyourself.CreateQuiz

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.ClipboardManager
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import com.example.quizyourself.Constants.*
import com.example.quizyourself.MainPage
import com.example.quizyourself.R
import com.example.quizyourself.YourQuiz.YourQuizActivity
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firestore.v1.FirestoreGrpc
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_questions.*
import kotlinx.android.synthetic.main.activity_home.view.*
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap
import kotlin.concurrent.schedule

class AddQuestionsActivity : AppCompatActivity() {
    var totalOpt : Int = 0
    var totalQues : Int = 0
    var currQuesNo : Int = 0
    var quizUploaded : Boolean = false
    lateinit var optionLayout : ArrayList<LinearLayout>
    lateinit var quizDetail : HashMap<String,Any>
    lateinit var questDetailNumberWise : HashMap<String,Any>
    lateinit var eachQuesDetail : HashMap<String,Any>
    lateinit var eachQuestionOpt: HashMap<String,String>
    var btnAddQues : Button? = null

    lateinit var firestoreDB : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_questions)
        var actionBar = supportActionBar
        actionBar?.title = "Add Questions"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        quizDetail = hashMapOf()
        questDetailNumberWise= hashMapOf()
        eachQuesDetail= hashMapOf()
        eachQuestionOpt= hashMapOf()
        firestoreDB= FirebaseFirestore.getInstance()

        InitTotalOptionAndQuestion()
        DisplayQuesDetailsToFill(currQuesNo)
        //Set Button
        btnAddQues = Button(this)
        btnAddQues?.background =
            ContextCompat.getDrawable(this, R.drawable.black_white_button_bg)
        btnAddQues?.setTextColor(Color.parseColor("#FFFFFF"))
        btnAddQues?.typeface = Typeface.DEFAULT_BOLD
        btnAddQues?.textSize = 15f
        btnAddQues?.setPadding(8, 8, 8, 8)
        btnAddQues?.text="ADD QUESTION (${currQuesNo+1}/$totalQues)"
        btnAddQues?.backgroundTintMode = null
        var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(0,40,0,0)
        root_linearlayout.addView(btnAddQues,params)

        btnAddQues?.setOnClickListener {
            loadQuizInfo()
            var optionCtr = 0
            var check = 0
            var rl: RelativeLayout
            //Save Question Desc only if valid
            if (!validateInputString(et_quesDesc.text.toString())) {
                et_quesDesc.setError("Description can't be empty !")
            }
            else
            {
                et_quesDesc.error=null
                //Init it again so it doesnt point to same location
                 eachQuestionOpt= hashMapOf()
                 eachQuesDetail= hashMapOf()
                eachQuesDetail.put(ConstantsQuestionInfo.QUES_DESC,et_quesDesc.text.toString())
                for (eachOption in optionLayout) {
                    var v = eachOption.getChildAt(1)
                    if (v is EditText) {
                        if(!v.text.toString().isEmpty())
                        {
                            optionCtr++
                            eachQuestionOpt.put("${optionCtr}","${v.text}")
                            check++
                        }
                    } else if (v is Spinner) {
                        for (i in 1 until (totalOpt+1)){
                            if (v.selectedItem.toString().contains(i.toString())){
                                val et_corr_opt=optionLayout[i-1].getChildAt(1) as EditText
                                eachQuesDetail.put(ConstantsQuestionInfo.QUES_CORR_OPT,et_corr_opt.text.toString())
                            }
                        }
                    }
                }
                //put questionOptLinkedMap to eachQuestionDetail HashMap
                eachQuesDetail.put(ConstantsQuestionInfo.OPTION,eachQuestionOpt)

                if (check==totalOpt) {
                    Toast.makeText(this, "Question ${currQuesNo + 1} Added !", Toast.LENGTH_SHORT)
                        .show()
                    questDetailNumberWise.put("${currQuesNo + 1}", eachQuesDetail)

                    //Go to next Question Steps
                    //Make All field blanks and remove
                    for (eachOption in optionLayout) {
                        root_linearlayout.removeView(eachOption)
                        et_quesDesc.text?.clear()
                    }
                    var saveBtnInstance = btnAddQues
                    root_linearlayout.removeView(btnAddQues)
                    currQuesNo++
                    if (currQuesNo < totalQues) {
                        DisplayQuesDetailsToFill(currQuesNo)
                        //Add Button Again
                        btnAddQues = saveBtnInstance
                        btnAddQues?.background =
                            ContextCompat.getDrawable(this, R.drawable.black_white_button_bg)
                        btnAddQues?.setTextColor(Color.parseColor("#FFFFFF"))
                        btnAddQues?.typeface = Typeface.DEFAULT_BOLD
                        btnAddQues?.textSize = 15f
                        btnAddQues?.setPadding(8, 8, 8, 8)
                        btnAddQues?.backgroundTintMode = null
                        btnAddQues?.text = "ADD QUESTION (${currQuesNo + 1}/$totalQues)"
                        if (currQuesNo == totalQues - 1)
                            btnAddQues?.append(" AND SAVE QUIZ")
                        var params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 40, 0, 0)
                        root_linearlayout.addView(btnAddQues, params)
                    } else {
                        //Display uploading
                        tv_questionNo.visibility=View.GONE
                        et_quesDesc.visibility=View.GONE
                        tv_optionsAddQues.visibility=View.GONE
                        lottie_uploading.visibility=View.VISIBLE

                        //Adding Data to Final linkedMap Structure
                        quizDetail.put(ConstantsQuizInfo.QUESTIONS,questDetailNumberWise)
                        for ( i in quizDetail)
                            Log.d("DETAIILS",i.toString())
                        var uniqueQuizID = firestoreDB.collection(ConstantsFireStore.QUIZ_DATA_ROOT).document().id
                        quizDetail.put(ConstantsQuizInfo.QUIZ_ID,uniqueQuizID)
                        //Add empty parameter of number of people attempted
                        quizDetail.put(ConstantsQuizInfo.ATTEMPTED_BY, hashMapOf<String,String>())

                        //Adding Data to firestore !! Finally !
                        firestoreDB.collection(ConstantsFireStore.QUIZ_DATA_ROOT).document(uniqueQuizID).set(quizDetail).addOnCompleteListener {
                            if(it.isSuccessful)
                            {
                                quizUploaded=true
                                lottie_uploading.visibility=View.GONE
                                lottie_uploaded.visibility=View.VISIBLE
                                lottie_uploaded.loop(false)

                                //Finish

                                //Show unique quiz id for users to copy
                                tv_QuizUploaded.visibility=View.VISIBLE
                                tv_copyQuizID.visibility=View.VISIBLE
                                tv_quizId.text=uniqueQuizID
                                LinearLayout_CopyID.visibility=View.VISIBLE
                                btn_Go_TO_YOUR_QUIZ.visibility=View.VISIBLE

                                //Add Quiz ID to Users Info in FiresStore
                                var sharedPref = getSharedPreferences(Constants.ROOT_SHAREDPREFERENCES, Context.MODE_PRIVATE)
                                var email = sharedPref.getString(Constants.EMAIL_SHAREDPREF,"")
                                firestoreDB.collection(ConstantsFireStore.USER_DATA_ROOT).document(email!!).update(
                                    Constants.CREATED_QUESTION,FieldValue.arrayUnion(uniqueQuizID)
                                )
                            }
                        }
                        //firestoreDB.collection(ConstantsFireStore.QUIZ_DATA_ROOT).orderBy("number")


                    }


                }
                else{
                    var snacky =Snackbar.make(coordinateForSnackBar,"Please fill all the options !",Snackbar.LENGTH_SHORT)
                    snacky.setAction("OKAY ! Lemme Add",{})
                    snacky.setActionTextColor(Color.parseColor("#FF0000"))
                    snacky.show()


                }


            }
        }

        //Copy Quiz ID
        iv_copyBtn.setOnClickListener{
            var clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            var clipData = ClipData.newPlainText("Done",tv_quizId.text)
            clipboard.setPrimaryClip(clipData)
            var snackbar=Snackbar.make(coordinator_For_quizId_copied_Snackbar,"Copied to clipboard",Snackbar.LENGTH_LONG)
            snackbar.setTextColor(Color.parseColor("#FFFFFF"))
            snackbar.setActionTextColor(Color.parseColor("#00BEE1"))
            snackbar.setAction("OKAY",{})
            snackbar.show()
            coordinator_For_quizId_copied_Snackbar.visibility=View.VISIBLE

        }

        //Go to your quizes button
        btn_Go_TO_YOUR_QUIZ.setOnClickListener{
            btn_Go_TO_YOUR_QUIZ.background=ContextCompat.getDrawable(this,R.drawable.white_black_button_bg)
            btn_Go_TO_YOUR_QUIZ.setTextColor(Color.parseColor("#000000"))
            startActivity(Intent(this,YourQuizActivity::class.java))
            finish()
        }

    }

    private fun validateInputString(value : String): Boolean {
        if (value.isEmpty())
            return false
       return true
    }

    private fun loadQuizInfo(){
        var sharedPref = getSharedPreferences(Constants.ROOT_SHAREDPREFERENCES, Context.MODE_PRIVATE)
        var gson = Gson()
        var json = sharedPref.getString(ConstantsQuizInfo.QuizInfoList,null)
        var type = object : TypeToken<LinkedHashMap<String,Any>>(){}.type
        quizDetail=gson.fromJson(json,type)
    }
    private fun DisplayQuesDetailsToFill(quesNo : Int) {
        //Set Question Number
        tv_questionNo.text="Question ${currQuesNo+1}"
        optionLayout = arrayListOf()
        var linearLayoutParent = root_linearlayout
        for(optionNo in 0..(totalOpt-1)){
            optionLayout.add(LinearLayout(this))
            optionLayout[optionNo].layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            var layoutParam=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParam.setMargins(0,40,0,0)
            optionLayout[optionNo].orientation=LinearLayout.HORIZONTAL

            var tv_optionNo=TextView(this)
            tv_optionNo.text=(optionNo+1).toString()+"."
            tv_optionNo.textSize=27f
            tv_optionNo.setTextColor(Color.parseColor("#FFFFFF"))
            tv_optionNo.layoutParams = LinearLayout.LayoutParams(120,LinearLayout.LayoutParams.WRAP_CONTENT)

            var et_Option = EditText(this)
            et_Option.hint="Option ${optionNo+1}"
            et_Option.setPadding(20,30,30,30)
            et_Option.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            et_Option.background=ContextCompat.getDrawable(this,R.drawable.textbox_shape)


            optionLayout[optionNo].addView(tv_optionNo)
            optionLayout[optionNo].addView(et_Option)

            linearLayoutParent.addView(optionLayout[optionNo],layoutParam)



        }
        optionLayout.add(totalOpt, LinearLayout(this))
        optionLayout[totalOpt].layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        var layoutParam=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParam.setMargins(0,40,0,0)
        optionLayout[totalOpt].orientation=LinearLayout.VERTICAL

        var tv_corrOption=TextView(this)
        tv_corrOption.text="Correct Option"
        tv_corrOption.textSize=22f
        tv_corrOption.setTextColor(Color.parseColor("#019F07"))
        tv_corrOption.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        var typeFace = ResourcesCompat.getFont(this,R.font.caveat)
        tv_corrOption.setTypeface(typeFace)

        var spinner_CorrOpt = Spinner(this)
        spinner_CorrOpt.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,150)
        var layout_corrOptParam=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,150)
        spinner_CorrOpt.setPadding(15,0
            ,15,15)
        layout_corrOptParam.setMargins(0,30,0,0)

        spinner_CorrOpt.background=ContextCompat.getDrawable(this,R.drawable.spinner_bg_correctans)
        //fill spinner from arraylist
        var spinnerData = ArrayList<String>()
        for(i in 0..(totalOpt-1))
            spinnerData.add("OPTION ${i+1}")
        var arrayAdap=ArrayAdapter(this,R.layout.spinner_holder,R.id.spinner_text_holder,spinnerData)
        spinner_CorrOpt.adapter=arrayAdap


        optionLayout[totalOpt].addView(tv_corrOption)
        optionLayout[totalOpt].addView(spinner_CorrOpt,layout_corrOptParam)

        linearLayoutParent.addView(optionLayout[totalOpt],layoutParam)

    }



    private fun InitTotalOptionAndQuestion() {
        var hashMap : HashMap<String,Any>
        var sharedPref = getSharedPreferences(Constants.ROOT_SHAREDPREFERENCES, Context.MODE_PRIVATE)
        var gson = Gson()
        var json = sharedPref.getString(ConstantsQuizInfo.QuizInfoList,"")
        var type = object : TypeToken<HashMap<String,Any>>(){
        }.type
        if(json == null){
            //doNothing
        }
        else
        {
         hashMap = gson.fromJson(json,type)
            totalQues=hashMap[ConstantsQuizInfo.TOTAL_QUES].toString().toInt()
            totalOpt=hashMap[ConstantsQuizInfo.TOTAL_OPTIONS].toString().toInt()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
      return true
    }

}