package com.example.quizyourself.YourQuiz

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizyourself.Constants.ConstantsPutExtra
import com.example.quizyourself.Data.QuizData
import com.example.quizyourself.R
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class YourQuizAdapter(val quizData : ArrayList<QuizData>) : RecyclerView.Adapter<YourQuizAdapter.YourQuizHolder>() {
    var mParent : ViewGroup? =null
    //Holder dor view which will hold or create a cache for items (item reference) inside each row of recylerview
    class YourQuizHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val quizName = itemView.findViewById<TextView>(R.id.tv_quizName)
        val startTime = itemView.findViewById<TextView>(R.id.tv_startTime)
        val endTime = itemView.findViewById<TextView>(R.id.tv_endTime)
        val quizDate = itemView.findViewById<TextView>(R.id.tv_quizDate)
        val quizId = itemView.findViewById<TextView>(R.id.tv_copyquizIDyourQuiz)
        val eachRowCardView = itemView

    }

    //createViews and returns instance of ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourQuizHolder {
        //inflate each Row for recyclerview layout  ( convert the static xml item file into view instance
        val itemView =LayoutInflater.from(parent.context).inflate(R.layout.item_for_recyclerview_yourquiz,parent,false)
        mParent=parent
        return YourQuizHolder(itemView)
    }

    override fun getItemCount(): Int {
        return quizData.size
    }

    override fun onBindViewHolder(holder: YourQuizHolder, position: Int) {
        var currQuizData = quizData[position]
        holder.quizName.text=currQuizData.QUIZ_TITLE
        //Format time to hh:mm
        var sdf = SimpleDateFormat("HH:mm",Locale.ENGLISH)
        val cal = Calendar.getInstance()
        //StartTime
        cal.set(Calendar.HOUR_OF_DAY,currQuizData.START_HOUR.toInt())
        cal.set(Calendar.MINUTE,currQuizData.START_MIN.toInt())
        holder.startTime.text=sdf.format(cal.time)
        //EndTime
        cal.set(Calendar.HOUR_OF_DAY,currQuizData.END_HOUR.toInt())
        cal.set(Calendar.MINUTE,currQuizData.END_MIN.toInt())
        holder.endTime.text=sdf.format(cal.time)

        //Format Date to MMMM dd,yyyy
        sdf = SimpleDateFormat("MMMM dd,yyyy",Locale.ENGLISH)
        cal.set(Calendar.MONTH,currQuizData.START_MONTH.toInt())
        cal.set(Calendar.YEAR,currQuizData.START_YEAR.toInt())
        cal.set(Calendar.DAY_OF_MONTH,currQuizData.START_DATE.toInt())
        holder.quizDate.text=sdf.format(cal.time)
        //click listerns
        //copy quiz id
        holder.quizId.setOnClickListener{
            val clipboard=holder.quizId.context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("copied",currQuizData.QUIZ_ID)
            clipboard.setPrimaryClip(clip)
            //Snackbar when copied
            var snackbar= Snackbar.make(mParent!!.rootView,"Copied to clipboard",
                Snackbar.LENGTH_LONG)
            snackbar.setTextColor(Color.parseColor("#FFFFFF"))
            snackbar.setActionTextColor(Color.parseColor("#00BEE1"))
            snackbar.setAction("OKAY",{})
            snackbar.show()


        }
        holder.eachRowCardView.setOnClickListener{
            val intent = Intent(holder.eachRowCardView.context,eachQuizDetailsActivity::class.java)
            intent.putExtra(ConstantsPutExtra.QUIZ_ID,currQuizData.QUIZ_ID)
            holder.eachRowCardView.context.startActivity(intent)
        }


    }
}