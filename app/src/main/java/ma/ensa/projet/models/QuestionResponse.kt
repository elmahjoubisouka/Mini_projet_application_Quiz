package ma.ensa.projet.models


data class QuestionResponse(
    val response_code: Int,
    val results: List<Question>
)