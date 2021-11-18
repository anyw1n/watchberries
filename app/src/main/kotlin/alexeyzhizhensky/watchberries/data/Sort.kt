package alexeyzhizhensky.watchberries.data

data class Sort(
    val by: Field,
    val order: Order
) {

    override fun toString() = "${by.name.lowercase()}_${order.name.lowercase()}"

    enum class Field {

        ID, SKU, BRAND, TITLE, UPDATE;

        companion object {

            val values = values().toList()
        }
    }

    enum class Order {

        ASC, DESC;

        companion object {

            val values = values().toList()
        }
    }

    companion object {

        val DEFAULT = Sort(Field.ID, Order.ASC)
    }
}
