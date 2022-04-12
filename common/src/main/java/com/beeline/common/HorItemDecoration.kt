package com.beeline.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class HorItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    private val half = space / 2

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        (view.layoutParams as RecyclerView.LayoutParams).run {
            when (viewAdapterPosition) {
                0 -> {
                    outRect.left = space
                    outRect.right = half
                }
                state.itemCount - 1 -> {
                    outRect.right = space
                    outRect.left = half
                }
                else -> {
                    outRect.right = half
                    outRect.left = half
                }
            }

        }

    }
}