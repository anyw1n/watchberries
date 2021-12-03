package alexeyzhizhensky.watchberries.viewmodels

import alexeyzhizhensky.watchberries.data.ProductRepository
import alexeyzhizhensky.watchberries.data.SortSettings
import alexeyzhizhensky.watchberries.data.WbException
import alexeyzhizhensky.watchberries.network.WbConnectivityManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

@ExperimentalCoroutinesApi
object ProductListViewModelTest : Spek({

    val connectivityManager by memoized { mockk<WbConnectivityManager>(relaxed = true) }
    val productRepository by memoized {
        mockk<ProductRepository>(relaxed = true) {
            coEvery { addSku(0) } throws WbException.InvalidSku
        }
    }
    val sortSettings by memoized { mockk<SortSettings>(relaxed = true) }
    val viewModel by memoized {
        ProductListViewModel(connectivityManager, productRepository, sortSettings)
    }

    val testDispatcher = StandardTestDispatcher()

    beforeEachTest {
        Dispatchers.setMain(testDispatcher)
    }

    afterEachTest {
        Dispatchers.resetMain()
    }

    describe("Product list view model tests") {
        describe("Add product") {
            it("Should refresh products") {
                viewModel.addProduct(123).invokeOnCompletion {
                    coVerify { productRepository.addSku(any()) }
                    runTest {
                        expectThat(viewModel.eventFlow.last())
                            .isEqualTo(ProductListViewModel.Event.RefreshProducts)
                    }
                }
            }

            it("Should show exception") {
                viewModel.addProduct(0).invokeOnCompletion {
                    coVerify { productRepository.addSku(any()) }
                    runTest {
                        expectThat(viewModel.eventFlow.last())
                            .isA<ProductListViewModel.Event.ShowException>()
                    }
                }
            }
        }
    }
})
