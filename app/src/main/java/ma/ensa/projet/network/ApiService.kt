package ma.ensa.projet.network


import ma.ensa.projet.models.QuestionResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val BASE_URL = "https://opentdb.com/"

interface ApiService {
    @GET("api.php?amount=10&type=multiple")
    suspend fun getQuestions(): QuestionResponse
}

object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
