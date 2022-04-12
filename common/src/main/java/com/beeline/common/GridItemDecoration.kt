package com.beeline.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class GridItemDecoration(private val edge: Int, private val colCount: Int) :
    RecyclerView.ItemDecoration() {

    private val half = edge / 2

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        (view.layoutParams as RecyclerView.LayoutParams).run {
            outRect.top = if (viewAdapterPosition in 0 until colCount) {
                edge
            } else {
                half
            }
            outRect.bottom =
                if (viewAdapterPosition in state.itemCount - colCount..state.itemCount) {
                    edge
                } else {
                    half
                }
            when {
                viewAdapterPosition % colCount == 0 -> {
                    outRect.left = edge
                    outRect.right = half
                }
                viewAdapterPosition % colCount == colCount - 1 -> {
                    outRect.right = edge
                    outRect.left = half
                }
                else -> {
                    outRect.left = half
                    outRect.right = half
                }
            }
        }
    }
}