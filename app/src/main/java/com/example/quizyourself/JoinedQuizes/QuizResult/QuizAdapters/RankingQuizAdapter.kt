package com.example.quizyourself.JoinedQuizes.QuizResult.QuizAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizyourself.Constants.Constants
import com.example.quizyourself.Constants.ConstantsQuizInfo
import com.example.quizyourself.R
import com.squareup.picasso.Picasso

class RankingQuizAdapter(val participants : List<Pair<String,HashMap<String,Any>>>,val totalScore : String) : RecyclerView.Adapter<RankingQuizAdapter.RankingQuizHolder>(){
    class RankingQuizHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.name_rank)
        val email = itemView.findViewById<TextView>(R.id.email_rank)
        val score = itemView.findViewById<TextView>(R.id.score_rank)
        val dp = itemView.findViewById<ImageView>(R.id.iv_dp_rank)
        val rank = itemView.findViewById<TextView>(R.id.rank)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingQuizHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_ranklist,parent,false)
        return  RankingQuizHolder(v)
    }

    override fun onBindViewHolder(holder: RankingQuizHolder, position: Int) {
            val currParticipantMap = participants[position].second
            holder.name.text = currParticipantMap[Constants.NAME].toString()
            holder.email.text = currParticipantMap[Constants.EMAIL].toString()
            holder.score.text = "SCORE: ${currParticipantMap[ConstantsQuizInfo.SCORE].toString()}/$totalScore"
            Picasso.get()
                .load(currParticipantMap[Constants.DP_URL].toString())
                .resize(50,50)
                .centerCrop()
                .into(holder.dp)
            holder.rank.text = (position+2).toString()
    }

    override fun getItemCount(): Int {
      return participants.size
    }
}