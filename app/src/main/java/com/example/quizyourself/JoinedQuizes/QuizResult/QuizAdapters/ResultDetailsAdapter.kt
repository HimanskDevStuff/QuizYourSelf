package com.example.quizyourself.JoinedQuizes.QuizResult.QuizAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizyourself.Constants.ConstantAnsweredInfo
import com.example.quizyourself.Data.QuizResultData
import com.example.quizyourself.R

class ResultDetailsAdapter(val quizResultDetails : List<Pair<String,Any>>) : RecyclerView.Adapter<ResultDetailsAdapter.ResultDetailsHolder>() {
    class ResultDetailsHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val quesNo = itemView.findViewById<TextView>(R.id.tv_quesNoQuizResult)
        val quesDesc = itemView.findViewById<TextView>(R.id.tv_quesDescQuizResult)
        val corrOpt = itemView.findViewById<TextView>(R.id.tv_corrOptResult)
        val choosenOpt = itemView.findViewById<TextView>(R.id.tv_choosenOptResult)
        val totalOpt = itemView.findViewById<TextView>(R.id.tv_OptQuizResult)
        val score = itemView.findViewById<TextView>(R.id.tv_score_result)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultDetailsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_result,parent,false)
        return ResultDetailsHolder(v)
    }

    override fun onBindViewHolder(holder: ResultDetailsHolder, position: Int) {
        val currQues = quizResultDetails[position]
        val questInfo = currQues.second as HashMap<String,Any>

        holder.quesNo.append(currQues.first)
        holder.quesDesc.text = questInfo[ConstantAnsweredInfo.QUES_DESC].toString()
        holder.score.text = "Score : ${questInfo[ConstantAnsweredInfo.SCORE].toString()}"

        val opt = questInfo[ConstantAnsweredInfo.OPTION] as HashMap<String,String>

        var ctr = 1
        for(eachopt in opt.toList()){
            if(eachopt.second == questInfo[ConstantAnsweredInfo.CORRECT_OPT]){
                holder.corrOpt.text = "${eachopt.first}. ${eachopt.second}"
            }
            if(eachopt.second == questInfo[ConstantAnsweredInfo.CHOOSEN_OPTION]){
                holder.choosenOpt.text = "${eachopt.first}. ${eachopt.second}"
            }
            holder.totalOpt.append("${eachopt.first}. ${eachopt.second}")
            if(opt.size == ctr++)
                holder.totalOpt.append("\n\n")
        }

    }

    override fun getItemCount(): Int {
        return quizResultDetails.size
    }
}