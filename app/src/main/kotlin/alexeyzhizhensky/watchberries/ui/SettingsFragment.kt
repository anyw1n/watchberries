package alexeyzhizhensky.watchberries.ui

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.LocaleUtils
import alexeyzhizhensky.watchberries.data.Price
import alexeyzhizhensky.watchberries.data.ThemeUtils
import alexeyzhizhensky.watchberries.databinding.FragmentSettingsBinding
import alexeyzhizhensky.watchberries.viewmodels.SettingsViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

        setFragmentResultListener(CHANGE_CURRENCY_REQUEST_KEY) { _, bundle ->
            val currency = Price.Currency.values[bundle.getInt(INDEX_KEY)]
            viewModel.changeCurrency(currency)
        }

        setFragmentResultListener(CHANGE_THEME_REQUEST_KEY) { _, bundle ->
            val theme = ThemeUtils.Theme.values[bundle.getInt(INDEX_KEY)]
            viewModel.changeTheme(theme)
        }

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

        currencySetting.apply {
            settingTitleTextView.text = getString(R.string.currency)
            root.setOnClickListener {
                val action = SettingsFragmentDirections
                    .actionSettingsFragmentToSingleChoiceListDialogFragment(
                        R.string.currency_pick_dialog_title,
                        R.array.currency_items,
                        CHANGE_CURRENCY_REQUEST_KEY
                    )
                findNavController().navigate(action)
            }
        }

        themeSetting.apply {
            settingTitleTextView.text = getString(R.string.theme)
            root.setOnClickListener {
                val action = SettingsFragmentDirections
                    .actionSettingsFragmentToSingleChoiceListDialogFragment(
                        R.string.theme_pick_dialog_title,
                        R.array.theme_items,
                        CHANGE_THEME_REQUEST_KEY
                    )
                findNavController().navigate(action)
            }
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.currencyFlow.collectLatest {
                        binding.currencySetting.settingSubtitleTextView.text =
                            resources.getStringArray(R.array.currency_items)[it.ordinal]
                    }
                }

                launch {
                    viewModel.themeFlow.collectLatest {
                        binding.themeSetting.settingSubtitleTextView.text =
                            resources.getStringArray(R.array.theme_items)[it.ordinal]
                    }
                }

                launch {
                    viewModel.localeFlow.collectLatest {
                        binding.languageSetting.settingSubtitleTextView.text =
                            resources.getStringArray(R.array.language_items)[it.ordinal]
                    }
                }
            }
        }

        launch {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is SettingsViewModel.Event.RecreateActivity -> activity?.recreate()
                }
            }
        }
    }

    companion object {

        const val CHANGE_CURRENCY_REQUEST_KEY = "CHANGE_CURRENCY_REQUEST"
        const val CHANGE_THEME_REQUEST_KEY = "CHANGE_THEME_REQUEST"
        const val CHANGE_LOCALE_REQUEST_KEY = "CHANGE_LOCALE_REQUEST"

        const val INDEX_KEY = "INDEX"
    }
}
