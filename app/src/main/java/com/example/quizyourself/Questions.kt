package com.example.quizyourself

data class Questions(
    var id:Int,
    var ques : String,
    var image : Int,
    var optionOne : String,
    var optionTwo : String,
    var optionThree : String,
    var optionFour : String,
    var corrOption : Int
)