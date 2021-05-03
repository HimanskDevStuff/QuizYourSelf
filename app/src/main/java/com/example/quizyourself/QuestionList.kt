package com.example.quizyourself

object QuestionList {
    var questionsList = ArrayList<Questions>()
    val USER_NAME = "user_name"
    val CORRECT_ANS = "correct_ans"
    val TOTAL_QUES = "total_ques"
    fun getQuestionList(): ArrayList<Questions> {
        var que1 = Questions(
            1,
            "Which country flag is this ?",
            R.drawable.ic_flag_of_argentina,
            "India",
            "Austria",
            "Argentina",
            "Denmark",
            2
        )
        var que2 = Questions(
            2,
            "Which country flag is this ?",
            R.drawable.ic_flag_of_belgium,
            "Belgium",
            "Austria",
            "Argentina",
            "Denmark",
            1
        )
        var que3 = Questions(
            3,
            "Which country flag is this ?",
            R.drawable.ic_flag_of_germany,
            "India",
            "Austria",
            "Germany",
            "Denmark",
            3
        )
        var que4 = Questions(
            4,
            "Which country flag is this ?",
            R.drawable.ic_flag_of_brazil,
            "India",
            "Brazil",
            "Argentina",
            "Denmark",
            2
        )
        var que5 = Questions(
            5,
            "Which country flag is this ?",
            R.drawable.ic_flag_of_india,
            "India",
            "Austria",
            "Argentina",
            "Denmark",
            1
        )
        questionsList.add(que1)
        questionsList.add(que2)
        questionsList.add(que3)
        questionsList.add(que4)
        questionsList.add(que5)
        return questionsList
    }
}