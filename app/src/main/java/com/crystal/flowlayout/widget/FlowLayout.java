package com.crystal.flowlayout.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xpchi on 2016/7/2.
 */
public class FlowLayout extends ViewGroup{

    //垂直间距
    private static final int verticalSpecing = 20;

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int widthUsed = paddingLeft + paddingRight; //已经被使用了的空间
        int childCount = getChildCount();
        //每行最大的行高
        int childMaxHeightForCurrentRow = 0;
        //叠加每行的行高
        int allRowsHeight = 0;
        for(int i = 0; i < childCount; i++){
            View child = getChildAt(i);
            if(child.getVisibility() != View.GONE){
                int childWidthUsed = 0;
                int childHeightUsed = 0;
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                //子view宽、高
                childWidthUsed = child.getMeasuredWidth();
                childHeightUsed = child.getMeasuredHeight();
                //子view的MaringLayoutParams
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) child.getLayoutParams();
                childWidthUsed += marginLayoutParams.leftMargin + childWidthUsed + marginLayoutParams.rightMargin;
                childHeightUsed += marginLayoutParams.topMargin + childHeightUsed + marginLayoutParams.bottomMargin;
                if(widthUsed + childWidthUsed < widthSpecSize){
                    widthUsed += childWidthUsed;
                    if(childHeightUsed > childMaxHeightForCurrentRow){
                        childMaxHeightForCurrentRow = childHeightUsed;
                    }
                }else{
                    allRowsHeight += childMaxHeightForCurrentRow + verticalSpecing; //叠加行高
                    widthUsed = paddingLeft + paddingRight + childWidthUsed; //换行后刚开始已使用的空间
                    childMaxHeightForCurrentRow = childHeightUsed; //初始化当前行高
                }
            }
        }
        allRowsHeight += childMaxHeightForCurrentRow; //最后叠加行高
        setMeasuredDimension(widthMeasureSpec, allRowsHeight); //设置宽高
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int widthUsed = paddingLeft + paddingRight;
        int childCount = getChildCount();
        int recordLeft = paddingLeft;
        int recordTop = paddingTop;
        int maxHeight = 0;
        for (int i = 0; i < childCount; i++){
            View child = getChildAt(i);
            if(child.getVisibility() != View.GONE){
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) child.getLayoutParams();
                int left = 0;
                int top = 0;
                int right = 0;
                int bottom = 0;
                int childWidth, childHeight;
                int childMeasureWidth = child.getMeasuredWidth();
                int childMeasureHeight = child.getMeasuredHeight();
                childWidth = marginLayoutParams.leftMargin + childMeasureWidth + marginLayoutParams.rightMargin;
                childHeight = marginLayoutParams.topMargin + childMeasureHeight + marginLayoutParams.bottomMargin;
                if(widthUsed + childWidth < r - l){
                    left = recordLeft + marginLayoutParams.leftMargin;
                    top = recordTop + marginLayoutParams.topMargin;
                    right = left + childMeasureWidth;
                    bottom = top + childMeasureHeight;
                    widthUsed += childWidth;
                    recordLeft += childWidth;
                    if(maxHeight < childHeight){
                        maxHeight = childHeight;
                    }
                }else{
                    recordLeft = paddingLeft; //初始化
                    recordTop += maxHeight + verticalSpecing; //复制上一行的高的间距
                    left = recordLeft + marginLayoutParams.leftMargin;
                    top = recordTop + marginLayoutParams.topMargin;
                    right = left + childMeasureWidth;
                    bottom = top + childMeasureHeight;
                    widthUsed = paddingLeft + paddingRight + childWidth;
                    recordLeft += childWidth;
                    maxHeight = childHeight;
                }
                child.layout(left, top, right, bottom);
            }
        }
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FlowLayout.MarginLayoutParams(getContext(), attrs);
    }
}
