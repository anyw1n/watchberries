package alexeyzhizhensky.watchberries.ui

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.LocaleUtils
import alexeyzhizhensky.watchberries.databinding.FragmentSettingsBinding
import alexeyzhizhensky.watchberries.utils.toast
import alexeyzhizhensky.watchberries.viewmodels.SettingsViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(CHANGE_LOCALE_REQUEST_KEY) { _, bundle ->
            val locale = LocaleUtils.SupportedLocale.values[bundle.getInt(INDEX_KEY)]
            viewModel.changeLocale(locale)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.setup()
        subscribeToFlows()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun FragmentSettingsBinding.setup() {
        settingsAppBar.toolbar.apply {
            title = getString(R.string.settings)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        languageSetting.apply {
            settingTitleTextView.text = getString(R.string.language)
            root.setOnClickListener {
                val action = SettingsFragmentDirections
                    .actionSettingsFragmentToSingleChoiceListDialogFragment(
                        R.string.language_pick_dialog_title,
                        R.array.language_items,
                        CHANGE_LOCALE_REQUEST_KEY
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun subscribeToFlows() = viewLifecycleOwner.lifecycleScope.apply {
        launch {
            viewModel.localeFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
                    binding.languageSetting.settingSubtitleTextView.text =
                        resources.getStringArray(R.array.language_items)[it.ordinal]
                }
        }

        launch {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is SettingsViewModel.Event.ShowToast -> context?.toast(event.textRes)
                }
            }
        }
    }

    companion object {

        const val CHANGE_LOCALE_REQUEST_KEY = "CHANGE_LOCALE_REQUEST"

        const val INDEX_KEY = "INDEX"
    }
}
