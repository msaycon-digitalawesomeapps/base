package com.digitalawesome.recweed.home.stores

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalawesome.recweed.databinding.FragmentStoresBinding
import com.digitalawesome.recweed.store
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class StoresFragment : Fragment() {

    interface Listener {
        fun storeSelected()
        fun close()
    }

    companion object {
        fun newInstance() = StoresFragment()
    }

    private var listener: Listener? = null
    private val storesViewModel: StoresViewModel by sharedViewModel()
    private lateinit var binding: FragmentStoresBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupViewEvents()
        setupDataEvents()

        initialServiceCalls()

    }

    private fun setupViews() {
        binding.rvStores.withModels {
            storesViewModel.stores.value?.let {
                it.data.forEach { storeModel ->
                    store {
                        id(storeModel.id)
                        title(storeModel.attributes?.name ?: "")
                        address(storeModel.attributes?.address ?: "")
                        image(storeModel.attributes?.featured_image)
                        onClick(View.OnClickListener {
                            storesViewModel.selectStore(storeModel)
                            listener?.storeSelected() ?: kotlin.run {
                                findNavController().popBackStack()
                            }
                        })

                    }
                }
            }
        }
    }

    private fun setupViewEvents() {
        binding.ivBack.setOnClickListener {
            listener?.close() ?: kotlin.run {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupDataEvents() {
        storesViewModel.stores.observe(viewLifecycleOwner) {
            binding.rvStores.requestModelBuild()
        }
    }

    private fun initialServiceCalls() {
        storesViewModel.loadStores()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Listener
    }
}