package com.example.quizyourself.JoinedQuizes

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizyourself.Data.QuizResultData
import com.example.quizyourself.R
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class JoinedQuizesAdapter(val quizResultDetailList : ArrayList<QuizResultData>) : RecyclerView.Adapter<JoinedQuizesAdapter.ViewHolderJoinedQuizes>() {
    class ViewHolderJoinedQuizes(itemView : View) : RecyclerView.ViewHolder(itemView){
        val quizName = itemView.findViewById<TextView>(R.id.tv_quizName_JoinedQuizes)
        val quizstartdate = itemView.findViewById<TextView>(R.id.tv_quizDate_JoinedQuizes)
        val quizStartTime = itemView.findViewById<TextView>(R.id.tv_startTime_JoinedQuizes)
        val quizEndTime = itemView.findViewById<TextView>(R.id.tv_endTime_JoinedQuizes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderJoinedQuizes {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_joined_quizes,parent,false)
        return ViewHolderJoinedQuizes(itemView)
    }

    override fun getItemCount(): Int {
       return quizResultDetailList.size
    }

    override fun onBindViewHolder(holder: ViewHolderJoinedQuizes, position: Int) {
        val currQuizResultData = quizResultDetailList[position]
        holder.quizName.text = currQuizResultData.QUIZ_TITLE
        //Format time to hh:mm
        var sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val cal = Calendar.getInstance()
        //StartTime
        cal.set(Calendar.HOUR_OF_DAY,currQuizResultData.START_HOUR.toInt())
        cal.set(Calendar.MINUTE,currQuizResultData.START_MIN.toInt())
        holder.quizStartTime.text=sdf.format(cal.time)
        //EndTime
        cal.set(Calendar.HOUR_OF_DAY,currQuizResultData.END_HOUR.toInt())
        cal.set(Calendar.MINUTE,currQuizResultData.END_MIN.toInt())
        holder.quizEndTime.text=sdf.format(cal.time)

        //Format Date to MMMM dd,yyyy
        sdf = SimpleDateFormat("MMMM dd,yyyy",Locale.ENGLISH)
        cal.set(Calendar.MONTH,currQuizResultData.START_MONTH.toInt())
        cal.set(Calendar.YEAR,currQuizResultData.START_YEAR.toInt())
        cal.set(Calendar.DAY_OF_MONTH,currQuizResultData.START_DATE.toInt())
        holder.quizstartdate.text=sdf.format(cal.time)
    }
}