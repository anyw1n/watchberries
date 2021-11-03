package alexeyzhizhensky.watchberries.ui

import alexeyzhizhensky.watchberries.adapters.ProductAdapter
import alexeyzhizhensky.watchberries.databinding.FragmentProductListBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)

        with(binding) {

            val adapter = ProductAdapter()

            recyclerView.also {
                it.layoutManager = LinearLayoutManager(context)
                it.adapter = adapter
                it.setHasFixedSize(true)
            }

            fab.setOnClickListener { Log.d(TAG, "FAB clicked") }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {

        const val TAG = "ProductListFragment"
    }
}
