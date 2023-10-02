package com.digitalawesome.clearchoice.home.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.digitalawesome.clearchoice.databinding.FragmentFullscreenPopupBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BannerBottomSheet(
    private val imageUrl: String,
    private val bannerClickedCallback: () -> Unit
) : BottomSheetDialogFragment() {
    override fun getTheme() = com.digitalawesome.dispensary.components.R.style.da_components_NoBackgroundDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = com.digitalawesome.dispensary.components.R.style.da_components_NoBackgroundDialogTheme
        val offsetFromTop = 200
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isFitToContents = false
            expandedOffset = offsetFromTop
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentFullscreenPopupBinding.inflate(inflater, container, false)

        binding.ivPromoImage.load(imageUrl)

        binding.ivPromoImage.setOnClickListener {
            bannerClickedCallback.invoke()
            dismiss()
        }
        binding.btClose.setOnClickListener {
            dismiss()
        }
        return binding.root
    }
}