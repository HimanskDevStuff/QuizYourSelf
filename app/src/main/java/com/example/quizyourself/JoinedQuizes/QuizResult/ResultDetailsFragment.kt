package com.example.quizyourself.JoinedQuizes.QuizResult

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quizyourself.Constants.ConstantsPutExtra
import com.example.quizyourself.Data.QuizResultData
import com.example.quizyourself.R

class ResultDetailsFragment : Fragment() {
    lateinit var resultDetails : ArrayList<QuizResultData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result_details, container, false)
    }

}