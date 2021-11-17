package alexeyzhizhensky.watchberries.network

import alexeyzhizhensky.watchberries.data.room.Product
import alexeyzhizhensky.watchberries.data.Sort
import alexeyzhizhensky.watchberries.data.room.User
import alexeyzhizhensky.watchberries.utils.registerDeserializer
import alexeyzhizhensky.watchberries.utils.registerSerializer
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime
import java.util.UUID

interface WbApiService {

    @POST("users")
    fun createUser(@Body tokenRequest: TokenRequest): Call<User>

    @PUT("users/{id}")
    fun updateUser(
        @Path("id") id: Int,
        @Query("key") key: UUID,
        @Body tokenRequest: TokenRequest
    ): Call<User>

    @POST("users/{id}/skus")
    fun addSku(
        @Path("id") id: Int,
        @Query("key") key: UUID,
        @Body skuRequest: SkuRequest
    ): Call<WbResponse>

    @HTTP(method = "DELETE", path = "users/{id}/skus", hasBody = true)
    fun deleteSku(
        @Path("id") id: Int,
        @Query("key") key: UUID,
        @Body skuRequest: SkuRequest
    ): Call<WbResponse>

    @GET("products")
    fun getProducts(
        @Query("user_id") userId: Int,
        @Query("key") key: UUID,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: Sort
    ): Call<List<Product>>

    companion object {

        const val INITIAL_PAGE = 1
        private const val BASE_URL = "http://132.226.208.67/api/"

        fun create() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(buildGson()))
            .build()
            .create<WbApiService>()

        private fun buildGson() = GsonBuilder().apply {
            registerDeserializer { LocalDateTime.parse(it.asJsonPrimitive.asString) }
            registerDeserializer { Product.Trend.valueOf(it.asJsonPrimitive.asString.uppercase()) }
            registerSerializer<Sort> { it.toString() }
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        }.create()
    }
}
