package alexeyzhizhensky.watchberries.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
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
        @Query("key") key: UUID
    ): Call<List<Product>>
}
