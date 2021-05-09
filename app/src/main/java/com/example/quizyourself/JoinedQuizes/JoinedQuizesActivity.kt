package com.example.quizyourself.JoinedQuizes

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizyourself.Constants.ConstantAnsweredInfo
import com.example.quizyourself.Constants.Constants
import com.example.quizyourself.Constants.ConstantsFireStore
import com.example.quizyourself.Data.QuizResultData
import com.example.quizyourself.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_joined_quizes.*

class JoinedQuizesActivity : AppCompatActivity() {
    lateinit var firestore: FirebaseFirestore
    lateinit var userEmail : String
    var quizResultDetailsList : ArrayList<QuizResultData>?= null
    var quizResultData : QuizResultData?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joined_quizes)

        supportActionBar?.title="Joined Quizes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Init
        firestore = FirebaseFirestore.getInstance()
        quizResultDetailsList= arrayListOf()
        //get User Email
        getUserEmailFromSharedPref()
        //Get quizResultDetails and display
        DisplayJoinedQuizes()
    }

    private fun getUserEmailFromSharedPref(){
        val sharedPref = getSharedPreferences(Constants.ROOT_SHAREDPREFERENCES, Context.MODE_PRIVATE)
        userEmail=sharedPref.getString(Constants.EMAIL_SHAREDPREF,"")!!
    }

    private fun DisplayJoinedQuizes() {
        firestore.collection(ConstantsFireStore.USER_DATA_ROOT).document(userEmail).get().addOnCompleteListener{
            if(it.isSuccessful){
                val document=it.result
                val joinedQuizesIdsList = document?.get(Constants.JOINED_QUIZES) as List<String>
                firestore.collection(ConstantsFireStore.QUIZ_RESULT_ROOT).get().addOnSuccessListener {
                    if(!it.isEmpty) {
                        for (docs in it) {
                            if(docs.exists()) {
                                for (joinedQuizesId in joinedQuizesIdsList) {
                                    if (docs.id == joinedQuizesId) {
                                        quizResultData = docs.toObject(QuizResultData::class.java)
                                        quizResultDetailsList?.add(quizResultData!!)
                                    }
                                }
                            }
                        }

                        val adap = JoinedQuizesAdapter(quizResultDetailsList!!)
                        recyclerview_joinedQuizes.adapter = adap
                        recyclerview_joinedQuizes.layoutManager = LinearLayoutManager(this)
                    }
                }


            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}