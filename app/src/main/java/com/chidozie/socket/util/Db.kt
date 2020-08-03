package com.chidozie.socket.util

class Db {

    private val userAnswers: MutableList<String> = mutableListOf()

    private val questions = listOf(
        "1 + 1",
        "log1",
        "Sqrt(-4)",
        "Android or Web?"
    )

    val answers = listOf(
        "2",
        "0",
        "2i",
        "Android"
    )

    fun getQuestion(index: Int): String? {
        return questions.getOrNull(index)
    }

    fun addAnswer(index: Int, answer: String) {
        if (userAnswers.size - 1 > index) {
            userAnswers.add(index, answer)
        } else {
            userAnswers.add(answer)
        }
    }

    fun getUserAnswers(): List<String> {
        return userAnswers
    }

    fun getNoOfCorrectAnswers(): Int {
        var count = 0

        userAnswers.forEachIndexed { index, answer ->
            if (answer == answers.getOrNull(index)) {
                count += 1
            }
        }

        return count
    }

}
