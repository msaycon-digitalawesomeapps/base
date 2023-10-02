package com.digitalawesome.recweed.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import com.digitalawesome.recweed.R

@BindingAdapter(
    "srcUrl",
    "circleCrop",
    "placeholder",
    requireAll = false // make the attributes optional
)
fun ImageView.bindSrcUrl(
    url: String?,
    circleCrop: Boolean = false,
    placeholder: Int? = R.drawable.ic_app_icon,
) = load(url ?: R.drawable.ic_app_icon) {
    if (circleCrop) {
        transformations(CircleCropTransformation())
    }

    if (placeholder != null) {
        placeholder(placeholder)
    }
}
