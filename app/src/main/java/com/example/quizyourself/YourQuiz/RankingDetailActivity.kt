package com.example.quizyourself.YourQuiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import com.example.quizyourself.Constants.Constants
import com.example.quizyourself.Constants.ConstantsPutExtra
import com.example.quizyourself.R

class RankingDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking_detail)
        supportActionBar?.title=""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val quizID = intent.getStringExtra(ConstantsPutExtra.QUIZ_ID)
        val sharedPref = getSharedPreferences(Constants.ROOT_SHAREDPREFERENCES, MODE_PRIVATE)
        val edit = sharedPref.edit()
        edit.putString(ConstantsPutExtra.QUIZ_ID,quizID)
        edit.apply()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}