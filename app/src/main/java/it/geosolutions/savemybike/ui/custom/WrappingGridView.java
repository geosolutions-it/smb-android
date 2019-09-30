package it.geosolutions.savemybike.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class WrappingGridView extends GridView
{

	public WrappingGridView(Context context) {
		super(context);
	}

	public WrappingGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WrappingGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heightSpec = heightMeasureSpec;
		if (getLayoutParams().height == LayoutParams.WRAP_CONTENT)
			heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightSpec);
	}

}