package com.example.quizyourself.JoinedQuizes.QuizResult

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizyourself.Constants.Constants
import com.example.quizyourself.Constants.ConstantsFireStore
import com.example.quizyourself.Constants.ConstantsPutExtra
import com.example.quizyourself.Constants.ConstantsSaveInstance
import com.example.quizyourself.Data.QuizResultData
import com.example.quizyourself.JoinedQuizes.QuizResult.QuizAdapters.ResultDetailsAdapter
import com.example.quizyourself.R
import com.google.api.Monitoring
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_result_details.*

class ResultDetailsFragment : Fragment() {
    lateinit var resultDetails : QuizResultData
     var quizID: String?=null
    lateinit var firestore: FirebaseFirestore
     var userEmail : String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result_details, container, false)
    }

    private fun initializeResultDetails() {
        firestore.collection(ConstantsFireStore.QUIZ_RESULT_ROOT)
            .document(userEmail!!)
            .collection(ConstantsFireStore.RESULTS)
            .document(quizID!!)
            .get().addOnSuccessListener {
                resultDetails = it.toObject(QuizResultData::class.java)!!
                val adap = ResultDetailsAdapter(resultDetails.QUIZ_RESULT.toList())
                rv_result_details.adapter = adap
                rv_result_details.layoutManager = LinearLayoutManager(context)
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //get UserEmail and quizID

        val sharedPref = view.context.getSharedPreferences(Constants.ROOT_SHAREDPREFERENCES,MODE_PRIVATE)
        userEmail = sharedPref.getString(Constants.EMAIL_SHAREDPREF,"")
        quizID = sharedPref.getString(ConstantsPutExtra.QUIZ_ID,"")

        Log.d("CHECK",userEmail+",$quizID")

          firestore = FirebaseFirestore.getInstance()
          initializeResultDetails()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ConstantsSaveInstance.EMAIL,userEmail)
        outState.putString(ConstantsSaveInstance.QUIZID,quizID)
    }
}