package com.example.quizyourself.YourQuiz

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizyourself.Constants.Constants
import com.example.quizyourself.Constants.ConstantsFireStore
import com.example.quizyourself.Constants.ConstantsQuizInfo
import com.example.quizyourself.Data.QuizData
import com.example.quizyourself.MainPage
import com.example.quizyourself.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_your_quiz.*

class YourQuizActivity : AppCompatActivity() {
    lateinit var userEmail : String
    lateinit var fireStoreDB : FirebaseFirestore
    lateinit var quizDetails : ArrayList<QuizData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_quiz)
        var actionBar = supportActionBar
        actionBar?.title = "Your Quiz"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        //init
        fireStoreDB= FirebaseFirestore.getInstance()
        quizDetails= arrayListOf()
        //get UserEmail to show only those quizes that he created
        val sharedPref = getSharedPreferences(Constants.ROOT_SHAREDPREFERENCES, Context.MODE_PRIVATE)
        userEmail=sharedPref.getString(Constants.EMAIL_SHAREDPREF,"").toString()
        //load Data to recyclerView from fireStore
        loadQuizDataFromFirestore()
    }

    private fun loadQuizDataFromFirestore() {
        fireStoreDB.collection(ConstantsFireStore.QUIZ_DATA_ROOT).get().addOnSuccessListener {
           if(it.isEmpty){
               recyclerview_YourQuiz.visibility=View.GONE
               ll_yourQuiz_lottie.visibility=View.VISIBLE
           }else {
               for (docSnap in it) {
                   if (docSnap.exists()) {
                       if (docSnap.getString(ConstantsQuizInfo.CREATED_BY).equals(userEmail)) {
                           var quizData = docSnap.toObject(QuizData::class.java)
                           quizDetails.add(quizData)
                           Log.d("IDHAR HU", quizDetails.toString())
                       }

                   }
               }
               val adap = YourQuizAdapter(quizDetails)
               recyclerview_YourQuiz.adapter = adap
               recyclerview_YourQuiz.layoutManager = LinearLayoutManager(this)

               //Item
               ItemTouchHelper(
                   object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
                       override fun onMove(
                           recyclerView: RecyclerView,
                           viewHolder: RecyclerView.ViewHolder,
                           target: RecyclerView.ViewHolder
                       ): Boolean {
                           return false
                       }

                       override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            //Delete from firestore
                           var quizId=quizDetails.get(viewHolder.adapterPosition).QUIZ_ID
                           var tempQuizDetails = quizDetails.get(viewHolder.adapterPosition)
                           var check = false

                           //delete from arraylist
                           //first remove from arraylist
                           quizDetails.removeAt(viewHolder.adapterPosition)
                           //then notify recyclerview that this item is deleted so that it can refersh that item only
                           adap.notifyDataSetChanged()
                           val snack = Snackbar.make(coordinator_copyquizId_yourQuiz,"Item Deleted ",Snackbar.LENGTH_LONG)
                           snack.setTextColor(Color.parseColor("#FFFFFF"))
                           snack.setActionTextColor(Color.parseColor("#00BEE1"))
                           snack.setAction("UNDO"){
                               //add data again
                               quizDetails.add(tempQuizDetails)
                               adap.notifyDataSetChanged()
                               check=true
                           }
                           snack.show()
                           //wait for Snackbar.LENTH_LONG time then check if undo is pressed then dont delete from firestore
                           //else delete
                           Handler(Looper.getMainLooper()).postDelayed({
                               if(check==false){
                                   fireStoreDB.collection(ConstantsFireStore.QUIZ_DATA_ROOT).document(quizId).delete().addOnSuccessListener {
                                       if(it==null) {

                                       }
                                   }
                               }
                           },3000)


                           //show empty animation
                           if (quizDetails.isEmpty())
                               ll_yourQuiz_lottie.visibility = View.VISIBLE



                       }

                   }
               ).attachToRecyclerView(recyclerview_YourQuiz)

           }
        }.addOnFailureListener{

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this,MainPage::class.java))
        finish()
        return false
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.yourquiz_menu,menu)
        return true
    }
}