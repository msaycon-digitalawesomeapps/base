package com.digitalawesome.clearchoice.utils

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.CarouselModelBuilder
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModel.SpanSizeOverrideCallback
import com.airbnb.epoxy.ModelView
import kotlin.math.roundToInt


/** Add models to a CarouselModel_ by transforming a list of items into EpoxyModels.
 *
 * @param items The items to transform to models
 * @param modelBuilder A function that take an item and returns a new EpoxyModel for that item.
 */
inline fun <T> CarouselModelBuilder.withModelsFrom(
    items: List<T>,
    modelBuilder: (T) -> EpoxyModel<*>
) {
    models(items.map { modelBuilder(it) })
}


class NumItemsInGridRow(context: Context?, forPhone: Int, forTablet: Int, forWideTablet: Int) :
    SpanSizeOverrideCallback {
    val numItemsForCurrentScreen: Int

    init {
        numItemsForCurrentScreen = 1
            // override in values-w600 etc
            // if (isWideTabletScreen(context)) forWideTablet else if (isTabletScreen(context)) forTablet else forPhone
    }

    override fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int {
        check(totalSpanCount % numItemsForCurrentScreen == 0) { "Total Span Count of : $totalSpanCount can not evenly fit: $numItemsForCurrentScreen cards per row" }
        return totalSpanCount / numItemsForCurrentScreen
    }

    companion object {
        /** Shows one item per row on phone and two for all tablet sizes.  */
        fun oneColumnPhoneTwoColumnTablet(context: Context?): NumItemsInGridRow {
            return NumItemsInGridRow(context, 1, 2, 2)
        }

        /** Specify how many items to show per grid row on phone. Tablet will show more items per row according to a default ratio.  */
        fun forPhoneWithDefaultScaling(
            context: Context?,
            numItemsPerRowOnPhone: Int
        ): NumItemsInGridRow {
            return NumItemsInGridRow(
                context,
                numItemsPerRowOnPhone,
                (numItemsPerRowOnPhone * 1.5f).roundToInt(),
                numItemsPerRowOnPhone * 2
            )
        }
    }
}


@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
internal open class VerticalGridCarousel(context: Context) : Carousel(context) {
    protected override fun createLayoutManager(): LayoutManager {
        return GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
    }
}

@ModelView(autoLayout = ModelView.Size.WRAP_WIDTH_WRAP_HEIGHT)
internal open class HorizontalGridCarousel(context: Context) : Carousel(context) {
    protected override fun createLayoutManager(): LayoutManager {
        return GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
    }
}