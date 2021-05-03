package com.example.quizyourself.QuestionsList

data class Questions(
    var quesId : Int,
    var quesDesc : String,
    var optionList : ArrayList<String>,
    var corrAns : Int
)