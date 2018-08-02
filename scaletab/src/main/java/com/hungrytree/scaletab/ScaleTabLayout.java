package com.hungrytree.scaletab;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.HashMap;
import java.util.Map;

public class ScaleTabLayout extends HorizontalScrollView {

    //默认字体大小
    private int mNormalTextSize;
    private int indicatorColor = 0xFF666666;
    private int underlineColor = 0x1A000000;
    private int dividerColor = 0x1A000000;
    private int tabTextColor = 0xFF666666;
    private int selectedTabTextColor = 0xFF45c01a;
    private int indicatorHeight = 8;
    private int underlineHeight = 2;
    private int tabPadding = 20;
    //关联的viewpager
    private ViewPager mViewPager;
    //第一个子View
    private RelativeLayout mTabContainer;
    //Tab总数
    private int mTabCount;

    private RectF mLineRect = new RectF();
    private Paint rectPaint;
    private Paint dividerPaint;
    private int lastPosition = 0;
    private int currentPosition = 0;
    private int nextScrollPosition = -1;
    private float currentPositionOffset = 0f;
    private int currentPositionOffsetPixels;
    private float mRoundRadius = 4;
    private boolean isClick,isAnimEnd;



    private boolean isMeasuredSize = false;
    private boolean isNotifyDataSetChanged = false;

    private final float MAX_SCALE_VALUE = 2.0f;
    private Paint mTempPaint = null;
    private Map<String,Integer> mTextSizeMap = new HashMap<>();
    private Map<Integer,Float> mLastWidthRecord = new HashMap<>();


    public ScaleTabLayout(Context context) {
        this(context, null);
    }

    public ScaleTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyle(context, attrs);
        setFillViewport(true);
        setHorizontalScrollBarEnabled(false);
        mTabContainer = new RelativeLayout(context);
        addView(mTabContainer, 0, new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
    }

    private void initStyle(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ScaleTabLayout, 0, 0);
        mNormalTextSize = a.getDimensionPixelSize(R.styleable.ScaleTabLayout_tab_normal_textSize, sp2px(context,13));
        indicatorColor = a.getColor(R.styleable.ScaleTabLayout_tab_IndicatorColor, indicatorColor);//指示器的颜色
        underlineColor = a.getColor(R.styleable.ScaleTabLayout_tab_UnderlineColor, underlineColor);//底部线的颜色
        dividerColor = a.getColor(R.styleable.ScaleTabLayout_tab_DividerColor, dividerColor);//分割线的颜色
        indicatorHeight = a.getDimensionPixelSize(R.styleable.ScaleTabLayout_tab_IndicatorHeight, indicatorHeight);//指示器的高度
        underlineHeight = a.getDimensionPixelSize(R.styleable.ScaleTabLayout_tab_UnderlineHeight, underlineHeight);//底部线的高度
        selectedTabTextColor = a.getColor(R.styleable.ScaleTabLayout_tab_TextSelectedColor, selectedTabTextColor);//选中Tab文字的颜色
        tabTextColor  = a.getColor(R.styleable.ScaleTabLayout_tab_TextColor, tabTextColor);//选中Tab文字的颜色
        a.recycle();
        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(1);

    }

    /**
     * 关联viewpager
     */
    public void setupWithViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        if (viewPager == null) {
            throw new IllegalArgumentException("viewpager not is null");
        }

        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter == null) {
            throw new IllegalArgumentException("pagerAdapter not is null");
        }
        this.mViewPager.clearOnPageChangeListeners();
        this.mViewPager.addOnPageChangeListener(new TabPagerChanger());
        mTabCount = pagerAdapter.getCount();
        notifyDataSetChanged();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(!isMeasuredSize){
            isMeasuredSize = true;
            post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }

    }


    public int sp2px(Context context, float spVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }


    /**
     * 更新界面
     */
    public void notifyDataSetChanged() {
        if(mViewPager == null || !isMeasuredSize){
            return;
        }
        isNotifyDataSetChanged = true;

        mTabContainer.removeAllViews();
        int curX = 0;

        int curSelectedPosition = mViewPager.getCurrentItem();
        for (int i = 0; i < mTabCount; i++) {
            final int position = i;
            TextView tabTextView = createTextView();
            String title = mViewPager.getAdapter().getPageTitle(i).toString();
            tabTextView.setText(title);
            tabTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ValueAnimator rlHAnim = ValueAnimator.ofFloat(1,0).setDuration(200);
                    rlHAnim.setInterpolator(new LinearOutSlowInInterpolator());
                    rlHAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float animatedValue = (float) animation.getAnimatedValue();
                            updateView(position,animatedValue);
                        }
                    });
                    rlHAnim.addListener(new Animator.AnimatorListener() {
                        @Override
                            public void onAnimationStart(Animator animation) {
                                isAnimEnd = false;
                            }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isAnimEnd = true;
                            if(currentPositionOffsetPixels == 0){
                                isClick = false;
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    if(!isClick) {
                        lastPosition = currentPosition;
                        if(lastPosition != position) {
                            isClick = true;
                            rlHAnim.start();
                            mViewPager.setCurrentItem(position);
                        }
                    }
                }
            });

            //设置每个控件在relativeLayout中的位置
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            mTabContainer.addView(tabTextView, params);


            boolean isSelected = (i == curSelectedPosition);
            setSelectedTabView(i,isSelected);
            int width = getCharacterWidth(title,mNormalTextSize);

            if(isSelected){
                float actWidth = MAX_SCALE_VALUE * width;
                float actHeight = MAX_SCALE_VALUE * mNormalTextSize;
                tabTextView.setY(computeTabY(MAX_SCALE_VALUE,actHeight) );
                tabTextView.setX(actWidth / 4);
                tabTextView.setScaleX(MAX_SCALE_VALUE);
                tabTextView.setScaleY(MAX_SCALE_VALUE);
                curX += actWidth;
                mLastWidthRecord.put(position,actWidth);
            }else{

                tabTextView.setX(curX);
                curX += width;
                tabTextView.setY(computeTabY(1,mNormalTextSize));
                mLastWidthRecord.put(position,(float)width);
            }

            curX += tabPadding;


        }
    }


    private float computeTabY(float scale, float actHeight){
        float y = getTabLayoutHeight() - actHeight;
        float adjustY = mNormalTextSize * (1 - scale/MAX_SCALE_VALUE);
        return y - adjustY;
    }


    private float computeTabX(int position, float scale, float width){
        return getFrontWidth(position) + width/4 * (scale - 1);
    }


    public float getTabLayoutHeight(){
        float height = this.getHeight();
        if(height <= 0){
            height = getMeasuredHeight();
        }
        return height;

    }


    /**
     * textview的变化
     *
     * @param position
     */
    protected void setSelectedTabView(int position,boolean isSelected) {
        View view = mTabContainer.getChildAt(position);
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setSelected(isSelected);
            textView.setTypeface(isSelected ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            textView.setTextColor(isSelected ? selectedTabTextColor : tabTextColor);
        }
    }

    /**
     * pager监听
     */
    private class TabPagerChanger implements ViewPager.OnPageChangeListener {
        /***
         * @param position             当向右滑动时，此参数是点击的页面位置，
         *                             滑动完成时，及当前页面位置
         *                             当向左滑动时，此参数是向左滑动页面位置，
         *                             及当前页面位置-1，滑动完成时，及当前页面位置
         * @param positionOffset       页面滑动偏移量百分比
         * @param positionOffsetPixels 页面滑动偏移量

         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            DebugLog.d("position="+position+"   positionOffset="+positionOffset +"   positionOffsetPixels="+positionOffsetPixels);
            currentPositionOffsetPixels = positionOffsetPixels;
            if(isClick){
                if(positionOffsetPixels==0 && isAnimEnd){
                    isClick = false;
                }
            }else{
                updateView(position, positionOffset);
            }
        }

        @Override
        public void onPageSelected(int position) {
//            adjustScrollViewPosition(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


    private void updateView(int position,float positionOffset){

        currentPositionOffset = positionOffset;
        TextView selectedChild = (TextView) mTabContainer.getChildAt(position);;
        TextView nextChild = null;
        int nextPosition = -1;

        if(isClick){
            nextPosition = lastPosition;
            nextChild = (TextView) mTabContainer.getChildAt(nextPosition);
            selectedChild.setSelected(true);
            nextChild.setSelected(false);
        }else{
            if(positionOffset == 0 && currentPosition != position){
                //此处的判断是由于viewpager 的位置切换，有时到最后的滚动时才变化 position，导致nextPosition的计算错误
                nextPosition = currentPosition;
                nextChild = (TextView) mTabContainer.getChildAt(nextPosition);
            }else if(position + 1 < mTabContainer.getChildCount()){
                nextPosition = position + 1;
                nextChild = (TextView) mTabContainer.getChildAt(nextPosition);
            }
        }

        //执行顺序的不同会影响位置的计算
        if(position > nextPosition){
            adjustNextView(nextChild,positionOffset,nextPosition);
            adjustCenterView(nextPosition,position);
            adjustSelectedView(selectedChild,positionOffset,position);
            adjustAfterView(position + 1);
        }else{
            adjustSelectedView(selectedChild,positionOffset,position);
            adjustCenterView(position,nextPosition);
            adjustNextView(nextChild,positionOffset,nextPosition);
            adjustAfterView(nextPosition + 1);
        }



        //记录一下当前的位置
        currentPosition = position;
        this.nextScrollPosition = nextPosition;
        this.invalidate();
    }

//    /**
//     * 调整scrollView的位置
//     * @param position
//     */
//    public void adjustScrollViewPosition(int position){
//        TextView tab = (TextView) mTabContainer.getChildAt(position);
//        if(tab == null){
//            return;
//        }
//        float bestPosition = computeTabX(position,MAX_SCALE_VALUE,getCharacterWidth(tab.getText().toString(),mNormalTextSize));
//        if(bestPosition - this.getScrollX() < this.getWidth() / 2){
//            return;
//        }
//
//        this.smoothScrollTo(0,(int)bestPosition);
//    }


    /**
     * 当点击tab跨越多个时，中间的控件需要做位置的调整
     * @param startPosition
     * @param endPosition
     */
    public void adjustCenterView(int startPosition,int endPosition){
        if(endPosition - startPosition <= 1 || endPosition >= mViewPager.getAdapter().getCount()){
            return;
        }

        for(int i = startPosition + 1;i < endPosition; i++){
            TextView child = (TextView) mTabContainer.getChildAt(i);
            child.setX(computeTabX(i,1,getCharacterWidth(child.getText().toString(),mNormalTextSize)));
        }
    }


    public void adjustAfterView(int position){
        int count = mViewPager.getAdapter().getCount();
        if(position >= count){
            return;
        }

        for(int i = position;i< count;i++){
            TextView child = (TextView) mTabContainer.getChildAt(i);
            if(child != null){
                child.setX(computeTabX(i,1,getCharacterWidth(child.getText().toString(),mNormalTextSize)));
            }
        }
    }






    private void adjustSelectedView(TextView selectedChild,float positionOffset,int position){
        if (selectedChild != null) {
            //字体大小变化及位置的相应调整
            float actScale = MAX_SCALE_VALUE -  (MAX_SCALE_VALUE-1) * positionOffset;
            float selectedWidth = getCharacterWidth(selectedChild.getText().toString(),mNormalTextSize)  * actScale;
            float selectedHeight = mNormalTextSize * actScale;
            selectedChild.setX(computeTabX(position,actScale,selectedWidth));
            selectedChild.setY(computeTabY(actScale,selectedHeight));
            selectedChild.setScaleX(actScale);
            selectedChild.setScaleY(actScale);

            mLastWidthRecord.put(position,selectedWidth);

            //初始颜色值
            int bgColor;
            if (positionOffset == 0) {
                //显示初始透明颜色
                bgColor = selectedTabTextColor;
            } else if (positionOffset > 1) {
                //滚动到一个定值后,颜色最深,而且不再加深
                bgColor = tabTextColor;
            } else {
                //滚动过程中渐变的颜色
                bgColor = eval(positionOffset, selectedTabTextColor, tabTextColor);
            }
            selectedChild.setTextColor(bgColor);


            if (positionOffset > 0.5) {
                selectedChild.setTypeface(Typeface.DEFAULT);
            } else {
                selectedChild.setTypeface(Typeface.DEFAULT_BOLD);
            }
        }
    }



    private void adjustNextView(TextView nextChild,float positionOffset,int position){
        if (nextChild != null) {
            float actScale = (MAX_SCALE_VALUE-1) * positionOffset + 1;
            float nextWidth = getCharacterWidth(nextChild.getText().toString(),mNormalTextSize)  * actScale;
            float nextHeight = mNormalTextSize * actScale;
            nextChild.setX(computeTabX(position,actScale,nextWidth));
            nextChild.setY(computeTabY(actScale,nextHeight));
            nextChild.setScaleX(actScale);
            nextChild.setScaleY(actScale);
            mLastWidthRecord.put(position,nextWidth);


            //初始颜色值
            int bgColor;
            if (positionOffset == 0) {
                //显示初始透明颜色
                bgColor = tabTextColor;
            } else if (positionOffset > 1) {
                //滚动到一个定值后,颜色最深,而且不再加深
                bgColor = selectedTabTextColor;
            } else {
                //滚动过程中渐变的颜色
                bgColor = eval(positionOffset, tabTextColor, selectedTabTextColor);
            }
            nextChild.setTextColor(bgColor);

            if (Math.abs(positionOffset) > 0.5) {
                nextChild.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                nextChild.setTypeface(Typeface.DEFAULT);
            }
        }
    }


    public float getFrontWidth(int index){
        float totalX = 0;
        for(int i = 0;i<index;i++){
            totalX += mLastWidthRecord.get(i) + tabPadding;
        }

        return totalX;
    }


    //获取字符串的长度
    public int getCharacterWidth(String text, float size) {
        if (null == text || "".equals(text)){
            return 0;
        }

        Integer value = mTextSizeMap.get(text + "_" + size);
        if(value != null){
            return value;
        }

        if(mTempPaint == null){
            mTempPaint = new Paint();
        }
        mTempPaint.setTextSize(size);
        int width = (int) mTempPaint.measureText(text);// 得到总体长度;
        mTextSizeMap.put(text + "_" + size,width);
        return width;
    }

    /**
     * 创建textView
     *
     * @return
     */
    private TextView createTextView() {
        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize);
        textView.setGravity(Gravity.BOTTOM);
        textView.setTypeface(Typeface.DEFAULT);
        textView.setIncludeFontPadding(false);
        return textView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || mViewPager == null || mViewPager.getAdapter() == null ||
                mViewPager.getAdapter().getCount() == 0 || !isNotifyDataSetChanged) {
            return;
        }
        final int height = getHeight();

        // draw underline
        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, height - underlineHeight, getWidth(), height, rectPaint);
        // draw indicator line
        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = mTabContainer.getChildAt(currentPosition);

        float width = currentTab.getWidth() * currentTab.getScaleX();
        float lineLeft = currentTab.getX() - (currentTab.getScaleX() - 1) * (width / 4);
        float lineRight = lineLeft + width;

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && nextScrollPosition >= 0 && nextScrollPosition < mTabCount) {

            View nextTab = mTabContainer.getChildAt(nextScrollPosition);
            if(isClick){
                nextTab = mTabContainer.getChildAt(lastPosition);
            }
            float nextWidth = nextTab.getWidth() * nextTab.getScaleX();
            final float nextTabLeft = nextTab.getX() - (nextTab.getScaleX() - 1) * (nextWidth / 4);
            final float nextTabRight = nextTabLeft + nextWidth;

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }


        mLineRect.left = lineLeft + getPaddingLeft();
        mLineRect.right = lineRight + getPaddingLeft();
        mLineRect.top = height - indicatorHeight;
        mLineRect.bottom = height;

        canvas.drawRoundRect(mLineRect, mRoundRadius, mRoundRadius, rectPaint);

    }


    public int eval(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        int currentA = (startA + (int) (fraction * (endA - startA))) << 24;
        int currentR = (startR + (int) (fraction * (endR - startR))) << 16;
        int currentG = (startG + (int) (fraction * (endG - startG))) << 8;
        int currentB = startB + (int) (fraction * (endB - startB));

        return currentA | currentR | currentG | currentB;
    }
}
