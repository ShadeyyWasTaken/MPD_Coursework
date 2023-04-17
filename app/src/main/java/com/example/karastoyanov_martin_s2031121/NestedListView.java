package com.example.karastoyanov_martin_s2031121;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class NestedListView extends ListView {

    public NestedListView(Context context) {
        super(context);
    }

    public NestedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Measure the ListView with a custom height that's not
        // dependent on the ScrollView's height
        int heightMeasureSpecCustom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpecCustom);
    }
}
