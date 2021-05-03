package com.example.quizyourself.Data

import com.google.gson.annotations.Expose

data class QuizResultData(
    @Expose var CREATOR: String="",
    @Expose var QUIZ_TITLE: String="",
    @Expose var QUIZ_DESC: String="",
    @Expose var TOTAL_QUES: String="",
    @Expose var TOTAL_OPTIONS: String="",
    @Expose var START_HOUR: String="",
    @Expose var START_MIN: String="",
    @Expose var END_HOUR: String="",
    @Expose var END_MIN: String="",
    @Expose var START_DATE: String="",
    @Expose var START_MONTH: String="",
    @Expose var START_YEAR: String="",
    @Expose var QUIZ_ID: String="",
    @Expose var QUIZ_RESULT: Map<String, Any> = mapOf(),
    @Expose var TOTAL_SCORE : String = "0"
)