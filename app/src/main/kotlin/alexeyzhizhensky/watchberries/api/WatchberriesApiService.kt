package alexeyzhizhensky.watchberries.api

import alexeyzhizhensky.watchberries.data.Product
import alexeyzhizhensky.watchberries.data.SkuRequestBody
import alexeyzhizhensky.watchberries.data.TokenRequestBody
import alexeyzhizhensky.watchberries.data.User
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime
import java.util.UUID

interface WatchberriesApiService {

    @POST("users")
    fun createUser(@Body tokenRequestBody: TokenRequestBody): Call<User>

    @PUT("users/{id}")
    fun updateUser(
        @Path("id") id: Int,
        @Query("key") key: UUID,
        @Body tokenRequestBody: TokenRequestBody
    ): Call<User>

    @POST("users/{id}/skus")
    fun addSku(
        @Path("id") id: Int,
        @Query("key") key: UUID,
        @Body skuRequestBody: SkuRequestBody
    ): Call<User>

    @DELETE("users/{id}/skus")
    fun deleteSku(
        @Path("id") id: Int,
        @Query("key") key: UUID,
        @Body skuRequestBody: SkuRequestBody
    ): Call<User>

    @GET("products")
    fun getProducts(
        @Query("user_id") userId: Int,
        @Query("key") key: UUID,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<List<Product>>

    companion object {

        private const val BASE_URL = "http://132.226.208.67/api/"

        private val localDateTimeSerializer = JsonSerializer<LocalDateTime> { localDateTime, _, _ ->
            JsonPrimitive(localDateTime.toString())
        }

        private val localDateTimeDeserializer = JsonDeserializer { json, _, _ ->
            LocalDateTime.parse(json.asJsonPrimitive.asString)
        }

        private val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, localDateTimeSerializer)
            .registerTypeAdapter(LocalDateTime::class.java, localDateTimeDeserializer)
            .create()

        fun create() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create<WatchberriesApiService>()
    }
}
