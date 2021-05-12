package com.example.quizyourself.Data

import com.google.gson.annotations.Expose

data class QuizData(
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
    @Expose var QUIZ_QUESTIONS: Map<String, Any> = mapOf(),
    @Expose var ATTEMPTED_BY : Map<String,Any> = mapOf()
)
/*
class QuizData(
) {
    @PropertyName("CREATOR")
    private lateinit var CREATOR: String

    @PropertyName("END_HOUR")
    private lateinit var END_HOUR: String

    @PropertyName("END_MIN")
    private lateinit var END_MIN: String

    @PropertyName("QUIZ_DESC")
    private lateinit var QUIZ_DESC: String

    @PropertyName("QUIZ_QUESTIONS")
    private lateinit var QUIZ_QUESTIONS: LinkedHashMap<String, Any>

    @PropertyName("QUIZ_TITLE")
    private lateinit var QUIZ_TITLE: String

    @PropertyName("START_DATE")
    private lateinit var START_DATE: String

    @PropertyName("START_HOUR")
    private lateinit var START_HOUR: String

    @PropertyName("START_MIN")
    private lateinit var START_MIN: String

    @PropertyName("START_MONTH")
    private lateinit var START_MONTH: String

    @PropertyName("START_YEAR")
    private lateinit var START_YEAR: String

    @PropertyName("TOTAL_OPTIONS")
    private lateinit var TOTAL_OPTIONS: String

    @PropertyName("TOTAL_QUES")
    private lateinit var TOTAL_QUES: String


    constructor(
        CREATOR: String,
        QUIZ_TITLE: String,
        QUIZ_DESC: String,
        TOTAL_QUES: String,
        TOTAL_OPTIONS: String,
        START_HOUR: String,
        START_MIN: String,
        END_HOUR: String,
        END_MIN: String,
        START_DATE: String,
        START_MONTH: String,
        START_YEAR: String,
        QUIZ_QUESTIONS: LinkedHashMap<String, Any>
    ) : this() {
        this.CREATOR = CREATOR
        this.QUIZ_TITLE = QUIZ_TITLE
        this.QUIZ_DESC = QUIZ_DESC
        this.TOTAL_QUES = TOTAL_QUES
        this.TOTAL_OPTIONS = TOTAL_OPTIONS
        this.START_HOUR = START_HOUR
        this.START_MIN = START_MIN
        this.END_HOUR = END_HOUR
        this.END_MIN = END_MIN
        this.START_DATE = START_DATE
        this.START_MONTH = START_MONTH
        this.START_YEAR = START_YEAR
        this.QUIZ_QUESTIONS = QUIZ_QUESTIONS

    }


}
 */