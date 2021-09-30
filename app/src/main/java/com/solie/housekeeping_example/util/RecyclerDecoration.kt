package com.solie.housekeeping_example.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerDecoration (var divTop : Int, var divBot : Int, var divRight : Int, var divLeft : Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if(parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount) {
            outRect.top = divTop
            outRect.bottom = divBot
            outRect.right = divRight
            outRect.left = divLeft
        }
    }
}