package com.example.shipseeker.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int topSpace, rightSpace, bottomSpace, leftSpace;

    public SpaceItemDecoration(Context context, int topSpace, int rightSpace, int bottomSpace, int leftSpace) {
        this.topSpace = topSpace;
        this.rightSpace = rightSpace;
        this.bottomSpace = bottomSpace;
        this.leftSpace = leftSpace;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = leftSpace;
        outRect.right = rightSpace;
        outRect.bottom = bottomSpace;

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = topSpace;
        } else {
            outRect.top = 0;
        }
    }
}
