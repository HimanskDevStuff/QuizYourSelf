package com.example.quizyourself.JoinedQuizes.QuizResult

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizyourself.Constants.Constants
import com.example.quizyourself.Constants.ConstantsFireStore
import com.example.quizyourself.Constants.ConstantsPutExtra
import com.example.quizyourself.Constants.ConstantsQuizInfo
import com.example.quizyourself.JoinedQuizes.QuizResult.QuizAdapters.RankingQuizAdapter
import com.example.quizyourself.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_ranking.*

class RankingFragment : Fragment() {
    lateinit var firestore: FirebaseFirestore
    lateinit var quizID: String
    var participants: List<Pair<String, HashMap<String, Any>>>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        firestore = FirebaseFirestore.getInstance()


        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Get quiz ID from sharePref
        val sharedPref = view.context.getSharedPreferences(
            Constants.ROOT_SHAREDPREFERENCES,
            Context.MODE_PRIVATE
        )
        quizID = sharedPref.getString(ConstantsPutExtra.QUIZ_ID, "")!!
        getResultAllParticipants()


    }

    private fun getResultAllParticipants() {
        firestore.collection(ConstantsFireStore.QUIZ_DATA_ROOT).document(quizID).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    lottoe_loading_ranking.visibility = View.GONE
                    cl_ranking.visibility = View.VISIBLE
                    val participantsMap =
                        it.get(ConstantsQuizInfo.ATTEMPTED_BY) as HashMap<String, HashMap<String, Any>>
                    val totalQues = it.getString(ConstantsQuizInfo.TOTAL_QUES)
                    participants = participantsMap!!.toList().sortedBy {
                        it.second["SCORE"].toString().toInt()
                    }
                    //Remove the last element who was first because he is already displayed
                    val restParticipants = arrayListOf<Pair<String, HashMap<String, Any>>>()
                    for (item in participants!!){
                        restParticipants.add(item)
                    }
                    restParticipants.removeAt(restParticipants.size-1)
                    lottie_winner.addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator?) {

                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            lottie_winner.visibility = View.INVISIBLE
                            ll_first_rank.visibility = View.VISIBLE
                            participants?.get(participants!!.size - 1)?.second?.apply {
                                name.text = get(Constants.NAME).toString()
                                email.text = get(Constants.EMAIL).toString()
                                score.text = "SCORE: ${get(ConstantsQuizInfo.SCORE)}/$totalQues"
                                Picasso.get()
                                    .load(get(Constants.DP_URL).toString())
                                    .resize(50, 50)
                                    .centerCrop()
                                    .into(iv_dp)
                            }
                        }

                        override fun onAnimationCancel(animation: Animator?) {

                        }

                        override fun onAnimationRepeat(animation: Animator?) {

                        }

                    })
                    val adap = RankingQuizAdapter(restParticipants.reversed(), totalQues.toString())
                    rv_rank_list.adapter = adap
                    rv_rank_list.layoutManager = LinearLayoutManager(context)
                }
            }
    }

}

