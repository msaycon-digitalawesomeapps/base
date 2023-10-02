package com.digitalawesome.clearchoice.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import com.digitalawesome.clearchoice.databinding.DialogAgeAlertBinding
import com.digitalawesome.dispensary.components.R

/**
 * Created by msaycon on 11,Sep,2023
 */
fun Context.displayAgeAlert(
    iconID: Int? = null,
    title: String,
    message: String? = null,
    cancelButtonText: String = "",
    cancelButtonAction: (() -> Unit)? = null,
    confirmButtonText: String = "",
    confirmButtonAction: () -> Unit = { },
    cancelable: Boolean = true
) {
    val alertDialog = AlertDialog.Builder(this).create()

    // Fill in the values of the custom alert
    val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val binding = DialogAgeAlertBinding.inflate(inflater, null, false)

    iconID?.let {
        // Uncomment these lines if you want to set the icon
        // binding.alertIconImageView.setImageResource(it)
        // binding.alertIconImageView.show()
    }

    binding.tvTitle.text = title
    if (message == null) {
        binding.tvDescription.isVisible = false
    } else {
        binding.tvDescription.text = HtmlCompat.fromHtml(
            message,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        ) // Assumes fromHtml is an available function
    }

    binding.btCancel.text = cancelButtonText
    binding.btCancel.setOnClickListener {
        alertDialog.dismiss()
        cancelButtonAction?.invoke()
    }

    binding.btConfirm.label = confirmButtonText
    binding.btConfirm.setOnClickListener {
        alertDialog.dismiss()
        confirmButtonAction()
    }

    alertDialog.setView(binding.root)
    alertDialog.setCancelable(cancelable)
    alertDialog.show()

    // Set window background
    val window = alertDialog.window
    window?.setBackgroundDrawableResource(R.drawable.da_components_bg_custom_alert)

}