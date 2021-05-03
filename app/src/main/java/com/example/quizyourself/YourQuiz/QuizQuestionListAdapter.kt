package com.example.quizyourself.YourQuiz

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizyourself.Constants.ConstantsQuestionInfo
import com.example.quizyourself.Data.QuizData
import com.example.quizyourself.R
import kotlinx.android.synthetic.main.item_recyclerview_question_list.view.*

class QuizQuestionListAdapter(val quesDataList : List<Pair<String,Any>>): RecyclerView.Adapter<QuizQuestionListAdapter.QuizQuestionListHolder>() {
    class QuizQuestionListHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val quesNo = itemView.findViewById<TextView>(R.id.tv_quesNoQuizQuestionList)
        val quesDesc = itemView.findViewById<TextView>(R.id.tv_quesDescQuizQuestionList)
        val corrOpt = itemView.findViewById<TextView>(R.id.tv_corrOptQuizQuestionList)
        val options = itemView.findViewById<TextView>(R.id.tv_OptQuizQuestionList)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizQuestionListHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_question_list,parent,false)
        return QuizQuestionListHolder(itemView)
    }

    override fun getItemCount(): Int {
        return quesDataList.size
    }

    override fun onBindViewHolder(holder: QuizQuestionListHolder, position: Int) {
        holder.quesNo.text="Question ${quesDataList[position].first}"
        var quesMapList : Map<String,Any> =quesDataList[position].second as Map<String, Any>
        holder.quesDesc.text=quesMapList.get(ConstantsQuestionInfo.QUES_DESC).toString()
        //Get corrOption String
        var corrOption =quesMapList.get(ConstantsQuestionInfo.QUES_CORR_OPT).toString()

        //Add options
        var optionMapList : Map<String,Any> = quesMapList.get(ConstantsQuestionInfo.OPTION) as Map<String, Any>
        var ctr=0
        for ((key,value) in optionMapList){
            ctr++
            //init correction option,corrOption=Option 1 or Option 2...
            if(corrOption.contains(key))
                holder.corrOpt.text="$key. $value"
            holder.options.append("$key. $value")
            if(ctr!=optionMapList.size)
                holder.options.append("\n\n")
        }
    }
}