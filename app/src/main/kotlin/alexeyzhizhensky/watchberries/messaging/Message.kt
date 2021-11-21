package alexeyzhizhensky.watchberries.messaging

import alexeyzhizhensky.watchberries.R
import androidx.annotation.StringRes

data class Message(
    val type: Type,
    val sku: Int,
    val title: String,
    val price: String,
    val priceDiff: String?,
    val imageUrl: String
) {

    enum class Type(
        @StringRes val titleResId: Int,
        @StringRes val textResId: Int
    ) {

        BECOME_AVAILABLE(R.string.become_available_title, R.string.become_available_message),
        PRICE_DROP(R.string.price_drop_title, R.string.price_drop_message)
    }

    companion object {

        private const val TYPE_KEY = "TYPE"
        private const val SKU_KEY = "SKU"
        private const val TITLE_KEY = "TITLE"
        private const val PRICE_KEY = "PRICE"
        private const val DIFF_KEY = "DIFF"
        private const val IMAGE_URL_KEY = "IMAGE_URL"

        fun fromData(data: Map<String, String>): Message? = runCatching {
            Message(
                type = Type.valueOf(data[TYPE_KEY]!!),
                sku = data[SKU_KEY]!!.toInt(),
                title = data[TITLE_KEY]!!,
                price = data[PRICE_KEY]!!,
                priceDiff = data[DIFF_KEY],
                imageUrl = data[IMAGE_URL_KEY]!!
            )
        }.getOrNull()
    }
}
