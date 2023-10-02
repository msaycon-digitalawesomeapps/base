package com.digitalawesome.clearchoice.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.digitalawesome.dispensary.components.R
import com.digitalawesome.dispensary.components.databinding.DaComponentsFragmentFilterSidebarDialogBinding

class FilterSidebarDialog : DialogFragment() {
    override fun getTheme() = R.style.da_components_NoBackgroundDialogTheme

    lateinit var binding: DaComponentsFragmentFilterSidebarDialogBinding

    companion object {
        @JvmStatic
        fun newInstance() = FilterSidebarDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DaComponentsFragmentFilterSidebarDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainWrapper.setOnClickListener {
            dismiss()
        }

        binding.btClear.setOnClickListener {
            dismiss()
        }
    }
}