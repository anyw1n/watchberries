package alexeyzhizhensky.watchberries.ui

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.adapters.ProductAdapter
import alexeyzhizhensky.watchberries.adapters.ProductLoadStateAdapter
import alexeyzhizhensky.watchberries.adapters.TopItemAdapter
import alexeyzhizhensky.watchberries.data.WbException
import alexeyzhizhensky.watchberries.databinding.FragmentProductListBinding
import alexeyzhizhensky.watchberries.utils.toast
import alexeyzhizhensky.watchberries.viewmodels.ProductListViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    // Variables

    private val viewModel: ProductListViewModel by viewModels()

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var productAdapter: ProductAdapter

    private var refreshSwiped = false

    // Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(ADD_SKU_REQUEST_KEY) { _, bundle ->
            viewModel.addProduct(bundle.getInt(SKU_KEY))
        }
        setFragmentResultListener(DELETE_SKU_REQUEST_KEY) { _, bundle ->
            viewModel.removeProduct(bundle.getInt(SKU_KEY))
        }
        setFragmentResultListener(CHANGE_SORT_REQUEST_KEY) { _, bundle ->
            viewModel.changeSort(bundle.getString(SORT_KEY))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)

        binding.setup()
        subscribeToFlows()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Functions

    private fun FragmentProductListBinding.setup() {
        include.toolbar.inflateMenu(R.menu.menu_product_list)

        recyclerView.apply {
            adapter = ConcatAdapter(
                TopItemAdapter(),
                productAdapter.withLoadStateHeaderAndFooter(
                    header = ProductLoadStateAdapter(context, productAdapter::retry),
                    footer = ProductLoadStateAdapter(context, productAdapter::retry)
                )
            )
            setHasFixedSize(true)
        }

        setListeners()
    }

    private fun FragmentProductListBinding.setListeners() {
        include.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_sort -> {
                    val action = ProductListFragmentDirections
                        .actionProductListFragmentToSortBottomSheetDialogFragment(
                            viewModel.getSort()
                        )
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }

        swipeRefresh.setOnRefreshListener {
            refreshSwiped = true
            productAdapter.refresh()
        }

        productAdapter.setOnItemClickListener {
            // open second screen
        }

        productAdapter.setOnItemLongClickListener {
            val action = ProductListFragmentDirections
                .actionProductListFragmentToDeleteProductDialogFragment(it)
            findNavController().navigate(action)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                when {
                    dy > 0 && fab.visibility == View.VISIBLE -> fab.hide()
                    dy < 0 && fab.visibility != View.VISIBLE -> fab.show()
                }
            }
        })

        fab.setOnClickListener {
            val action = ProductListFragmentDirections
                .actionProductListFragmentToAddProductDialogFragment()
            it.findNavController().navigate(action)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun subscribeToFlows() = viewLifecycleOwner.lifecycleScope.apply {
        launch {
            productAdapter.loadStateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .mapLatest { it.refresh }
                .distinctUntilChanged()
                .collectLatest { loadState ->
                    if (loadState is LoadState.Error) {
                        context?.let {
                            val wbException = loadState.error as? WbException ?: return@let
                            it.toast(wbException.getMessage(it))
                        }
                    }

                    if (refreshSwiped && loadState is LoadState.Loading) {
                        refreshSwiped = false
                    } else {
                        binding.swipeRefresh.isRefreshing = loadState is LoadState.Loading
                    }
                }
        }

        launch {
            viewModel.eventsFlow
                .collectLatest { event ->
                    when (event) {
                        ProductListViewModel.Event.RefreshProducts -> productAdapter.refresh()
                        is ProductListViewModel.Event.ShowToast -> context?.toast(event.textRes)
                        is ProductListViewModel.Event.ShowException -> context?.let {
                            it.toast(event.exception.getMessage(it))
                        }
                    }
                }
        }

        launch {
            viewModel.pagingDataFlow.collectLatest(productAdapter::submitData)
        }
    }

    // Constants

    companion object {

        const val CHANGE_SORT_REQUEST_KEY = "CHANGE_SORT_REQUEST"
        const val SORT_KEY = "SORT"

        const val ADD_SKU_REQUEST_KEY = "ADD_SKU_REQUEST"
        const val DELETE_SKU_REQUEST_KEY = "DELETE_SKU_REQUEST"
        const val SKU_KEY = "SKU"
    }
}
