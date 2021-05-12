package com.example.quizyourself.JoinedQuizes.QuizResult

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.quizyourself.Constants.ConstantsPutExtra
import com.example.quizyourself.Constants.ConstantsQuizInfo
import com.example.quizyourself.Data.QuizResultData
import com.example.quizyourself.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var quizResultResult: ArrayList<QuizResultData>
    lateinit var quizID: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navHostFrag = supportFragmentManager.findFragmentById(R.id.frag_host_result) as NavHostFragment
        navController = navHostFrag.findNavController()
        bottomNavResult.setupWithNavController(navController)

        quizID  = intent.getStringExtra(ConstantsPutExtra.QUIZ_ID)!!

        val bundle = Bundle()
        bundle.putString(ConstantsPutExtra.QUIZ_ID,quizID)
        navController.setGraph(navController.graph,bundle)
    }
}
