package com.digitalawesome.recweed.home.product

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitalawesome.dispensary.components.views.molecules.bottomsheet.StandardHeaderBottomSheet
import com.digitalawesome.recweed.databinding.FragmentItemAddedToCartBinding
import com.digitalawesome.recweed.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Use the [ItemAddedToCartBottomSheetDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class ItemAddedToCartBottomSheetDialog : StandardHeaderBottomSheet() {

    private var listener: Listener? = null
    lateinit var binding: FragmentItemAddedToCartBinding
    private val homeViewModel: HomeViewModel by sharedViewModel()

    interface Listener {
        fun checkout()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ItemAddedToCartBottomSheetDialog()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getContentView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemAddedToCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDataEvents()
    }

    private fun setupDataEvents() {

    }

    override fun setupViews() {
        super.setupViews()

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                dismiss()
            } catch (_: Exception) {
            }
        }, 2000)


    }

    override fun setupViewEvents() {
        super.setupViewEvents()
        binding.btCheckout.setOnClickListener {
            dismiss()
            listener?.checkout()
        }

    }

    override fun getTitle(): String = ""

    override fun getDescription() = null

    override fun hasCloseButton(): Boolean = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Listener
    }
}