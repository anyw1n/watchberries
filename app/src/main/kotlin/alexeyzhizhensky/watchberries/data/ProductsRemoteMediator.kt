package alexeyzhizhensky.watchberries.data

import alexeyzhizhensky.watchberries.data.room.Product
import alexeyzhizhensky.watchberries.data.room.ProductDao
import alexeyzhizhensky.watchberries.data.room.ProductRemoteKey
import alexeyzhizhensky.watchberries.data.room.ProductRemoteKeyDao
import alexeyzhizhensky.watchberries.data.room.User
import alexeyzhizhensky.watchberries.data.room.WbDatabase
import alexeyzhizhensky.watchberries.network.WbApiService
import alexeyzhizhensky.watchberries.utils.anchorItemOrNull
import alexeyzhizhensky.watchberries.utils.suspendResponse
import alexeyzhizhensky.watchberries.utils.toWbException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ProductsRemoteMediator @Inject constructor(
    private val userRepository: UserRepository,
    private val service: WbApiService,
    private val db: WbDatabase,
    private val productRemoteKeyDao: ProductRemoteKeyDao,
    private val productDao: ProductDao,
    sharedPrefsRepository: SharedPrefsRepository
) : RemoteMediator<Int, Product>() {

    private val initialPage = WbApiService.INITIAL_PAGE
    private val sort = sharedPrefsRepository.sort

    override suspend fun initialize() = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Product>) = runCatching {
        val user = userRepository.getUser()

        val remoteKey = getRemoteKey(loadType, state)
        val page = when (loadType) {
            LoadType.PREPEND -> remoteKey?.prevPage
            LoadType.REFRESH -> remoteKey?.prevPage?.inc() ?: initialPage
            LoadType.APPEND -> remoteKey?.nextPage
        } ?: return@runCatching remoteKey != null
        val limit = state.config.pageSize

        val (products, remoteKeys, endOfPaginationReached) = if (loadType == LoadType.REFRESH) {
            refresh(user, page, limit)
        } else {
            fetchPage(user, loadType, page, limit)
        }

        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                productRemoteKeyDao.deleteAll()
                productDao.deleteAll()
            }

            productRemoteKeyDao.insertAll(remoteKeys)
            productDao.insertAll(products)
        }

        endOfPaginationReached
    }.fold(
        onSuccess = { MediatorResult.Success(it) },
        onFailure = { MediatorResult.Error(it) }
    )

    private suspend fun getRemoteKey(
        loadType: LoadType,
        state: PagingState<Int, Product>
    ): ProductRemoteKey? {
        val sku = when (loadType) {
            LoadType.PREPEND -> state.firstItemOrNull()
            LoadType.REFRESH -> state.anchorItemOrNull()
            LoadType.APPEND -> state.lastItemOrNull()
        }?.sku ?: return null

        return productRemoteKeyDao.getBySku(sku)
    }

    private suspend fun refresh(user: User, page: Int, limit: Int): Page {
        val pages = mutableListOf<Page>().apply {
            if (page != initialPage) {
                add(fetchPage(user, LoadType.REFRESH, page.dec(), limit))
            }
            val currentPage = fetchPage(user, LoadType.APPEND, page, limit).also(::add)
            if (!currentPage.endOfPaginationReached) {
                add(fetchPage(user, LoadType.REFRESH, page.inc(), limit))
            }
        }

        return Page(pages.flatMap { it.products }, pages.flatMap { it.remoteKeys }, false)
    }

    private suspend fun fetchPage(user: User, loadType: LoadType, page: Int, limit: Int): Page {
        val response =
            service.getProducts(user.id, user.key, page, limit, sort.value).suspendResponse()

        if (!response.isSuccessful) throw HttpException(response).toWbException()

        val pages = response.headers()[PAGES_COUNT_HEADER]?.toIntOrNull()
        var products = response.body() ?: throw WbException.Server.Response

        val firstIndex = (page - initialPage) * limit
        products = products.mapIndexed { index, product ->
            product.copy(id = firstIndex + index)
        }

        val isFirstPage = page == initialPage
        val isLastPage = if (pages != null) page == pages else products.size < limit

        val prevPage = if (isFirstPage) null else page.dec()
        val nextPage = if (isLastPage) null else page.inc()
        val remoteKeys = products.map { ProductRemoteKey(it.sku, prevPage, nextPage) }

        val endOfPaginationReached = when (loadType) {
            LoadType.PREPEND -> isFirstPage
            LoadType.REFRESH -> false
            LoadType.APPEND -> isLastPage
        }

        return Page(products, remoteKeys, endOfPaginationReached)
    }

    private data class Page(
        val products: List<Product>,
        val remoteKeys: List<ProductRemoteKey>,
        val endOfPaginationReached: Boolean
    )

    private companion object {

        const val PAGES_COUNT_HEADER = "Pagination-Pages"
    }
}
