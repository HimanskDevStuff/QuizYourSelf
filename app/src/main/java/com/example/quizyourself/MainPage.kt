package com.example.quizyourself

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.SyncStateContract
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.text.parseAsHtml
import com.example.quizyourself.Constants.Constants
import com.example.quizyourself.CreateQuiz.CreateQuizActivity
import com.example.quizyourself.JoinQuiz.JoinQuizActivity
import com.example.quizyourself.JoinedQuizes.JoinedQuizesActivity
import com.example.quizyourself.YourQuiz.YourQuizActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_page.*
import java.util.*
import kotlin.collections.ArrayList

class MainPage : AppCompatActivity() {
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var userData : GoogleSignInAccount
    lateinit var firestoreDb: FirebaseFirestore
    lateinit var sharedPref : SharedPreferences
    lateinit var edit : SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        userData = GoogleSignIn.getLastSignedInAccount(this)!!
        firestoreDb = FirebaseFirestore.getInstance()

        saveUserDetails()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Set create button work
        btn_createQuiz.setOnClickListener{
            btn_createQuiz.setBackgroundResource(R.drawable.black_white_button_bg)
            btn_createQuiz.setTextColor(Color.parseColor("#FFFFFF"))
            startActivity(
                Intent(this,CreateQuizActivity::class.java)
            )
        }
        //set join button
        btn_joinQuiz.setOnClickListener{
            btn_joinQuiz.setBackgroundResource(R.drawable.white_black_button_bg)
            btn_joinQuiz.setTextColor(Color.parseColor("#000000"))
            startActivity(
                Intent(this,JoinQuizActivity::class.java)
            )
        }
        //set create button style back to normal when back is pressed in next activity
        btn_createQuiz.setBackgroundResource(R.drawable.white_black_button_bg)
        btn_createQuiz.setTextColor(Color.parseColor("#000000"))

        nav.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.Logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this,Home::class.java))
                    finish()
                }
                R.id.YourQuiz ->{
                    startActivity(Intent(this,YourQuizActivity::class.java))
                }
                R.id.JoinedQuiz ->{
                    startActivity(Intent(this,JoinedQuizesActivity::class.java))
                }
            }
            true
        }

    }
    private fun saveUserDetails() {
        var name: String
        var email: String
        var imageUrl: String
        var navHeaderView = nav.getHeaderView(0)

        var tv_username = navHeaderView.findViewById<TextView>(R.id.tv_name)
        var tv_useremail = navHeaderView.findViewById<TextView>(R.id.tv_email)
        var iv_displayPic = navHeaderView.findViewById<ImageView>(R.id.iv_dp)
        if (!userData.toString().isEmpty()) {
            name = userData.displayName.toString()
            email = userData.email.toString()
            imageUrl = userData.photoUrl.toString()
            //Displaying Details in Drawer
            tv_username.text = name
            tv_useremail.text = email
            Picasso.get()
                .load(imageUrl)
                .resize(50, 50)
                .centerCrop()
                .into(iv_displayPic)
            //Displaying name and greeting in front page
            displayGreetMessage(name)

            //Save userdetails to sharedpref
            sharedPref = getSharedPreferences(Constants.ROOT_SHAREDPREFERENCES, Context.MODE_PRIVATE)
            edit=sharedPref.edit()
            edit.putString(Constants.USERNAME_SHAREDPREF, name)
            edit.putString(Constants.EMAIL_SHAREDPREF, email)
            edit.apply()
            //Save to firestore UserInfo
            var data = hashMapOf<String, Any>(
                Constants.NAME to name,
                Constants.EMAIL to email,
                Constants.CREATED_QUESTION to ArrayList<String>(),
                Constants.JOINED_QUIZES to arrayListOf<String>()
            )
            firestoreDb.collection("UserData").document(email).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    var docSnapshot = it.getResult()
                    if (!docSnapshot!!.exists()) {
                        //User with this email id doesn't exist
                        //Save this user
                        firestoreDb.collection("UserData").document(email).set(data)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {


                                    //Make layout visible
                                    main_Relativelayout.visibility = View.VISIBLE
                                    //Make Loader Gone
                                    lottie_loader_main.visibility = View.GONE
                                    var snackbar = Snackbar.make(
                                        btn_joinQuiz,
                                        "Registration Successful !",
                                        Snackbar.LENGTH_INDEFINITE
                                    )
                                    snackbar.setAction("OKAY", View.OnClickListener {
                                    })
                                    snackbar.setBackgroundTint(Color.parseColor("#2CAA00"))
                                    snackbar.setTextColor(Color.parseColor("#FFFFFF"))
                                    snackbar.setActionTextColor(Color.parseColor("#004380"))
                                    snackbar.show()
                                }
                            }
                    }
                    else{
                        //Make layout visible
                        main_Relativelayout.visibility = View.VISIBLE
                        //Make Loader Gone
                        lottie_loader_main.visibility = View.GONE
                    }
                }
                else{
                    var snackbar = Snackbar.make(
                        coordinator,
                        "Registration Failed !",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.setBackgroundTint(Color.parseColor("#DC0000"))
                    snackbar.setTextColor(Color.parseColor("000000"))
                    snackbar.setActionTextColor(Color.parseColor("#FFFFFF"))
                    snackbar.setAction("OKAY", View.OnClickListener {
                    })
                    snackbar.show()

                }

            }
        }
    }

    private fun displayGreetMessage(name : String){
        var cal= Calendar.getInstance().time
        var currHour = cal.hours.toString().toInt()
        if(currHour>=12 && currHour<=16)
            tv_welcome_user.text="Good Afternoon, $name !"
        else if(currHour>=17 && currHour<=21)
            tv_welcome_user.text="Good Evening, $name !"
        else if(currHour>=22 && currHour<=4)
        {
            tv_welcome_user.text="Good Night, $name !"
            tv_greet.text="Sleep time, Go sleep !"
        }
        else{
            tv_welcome_user.text="Good Morning, $name !"
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        //set create button style back to normal when back is pressed in next activity
        btn_createQuiz.setBackgroundResource(R.drawable.white_black_button_bg)
        btn_createQuiz.setTextColor(Color.parseColor("#000000"))

        //set joinquiz
        btn_joinQuiz.setBackgroundResource(R.drawable.black_white_button_bg)
        btn_joinQuiz.setTextColor(Color.parseColor("#FFFFFF"))
    }

}