package com.codecamp.tripcplaner.model.remote

import com.codecamp.tripcplaner.OPENAI_API_KEY
import com.codecamp.tripcplaner.model.data.Message
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIChatApi{
    @Headers("Content-Type: application/json","Authorization: Bearer $OPENAI_API_KEY")
    @POST("v1/chat/completions")
    suspend fun generateResponse (@Body requestBody: OpenAIRequestBody): OpenAIResponse
}

data class OpenAIRequestBody(
    val model:String="gpt-3.5-turbo",
    val messages: List<Message>,
    val max_tokens: Int=2048,
    val n: Int=1,
    val temperature: Double=0.7,
    //val cities: List<String>,
    //val duration: Int
)

data class OpenAIResponse(
    val choices: List<MessageResponse>
)
data class MessageResponse(
    val message: Message
)