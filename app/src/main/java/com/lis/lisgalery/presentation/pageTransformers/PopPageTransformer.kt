package com.lis.lisgalery.presentation.pageTransformers

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class PopPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        if (abs(position) < 0.5) {
            page.visibility = View.VISIBLE
            // Scale the page down (between MIN_SCALE and 1)
            val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position)))
            page.scaleX = scaleFactor
            page.scaleY = scaleFactor
        } else if (abs(position) > 0.5) {
            page.visibility = View.GONE
        }
    }
    companion object {
        private const val MIN_SCALE = 0.75f
    }
}