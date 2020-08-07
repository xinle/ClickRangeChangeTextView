package com.ximalaya.ting.mytest;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by le.xin on 2020/8/7.
 *
 * @author le.xin
 * @email le.xin@ximalaya.com
 */
public class ClickRangeChangeTextView extends TextView {
    public ClickRangeChangeTextView(Context context) {
        super(context);
    }

    public ClickRangeChangeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickRangeChangeTextView(Context context, @Nullable AttributeSet attrs,
                                    int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Rect rect = new Rect();

    private int leftAndRightPadding, topAndBottomPadding;

    public void setClickRange(int leftAndRightPadding, int topAndBottomPadding) {
        this.leftAndRightPadding = leftAndRightPadding;
        this.topAndBottomPadding = topAndBottomPadding;

        expandTouchArea(Math.max(leftAndRightPadding, 0), Math.max(topAndBottomPadding, 0));
    }

    private void expandTouchArea(final int leftAndRightPadding, final int topAndBottomPadding) {
        final View parentView = (View) getParent();
        if(parentView == null) {
            return;
        }

        if(leftAndRightPadding == 0 && topAndBottomPadding == 0) {
            removeTouchDelegate();
            return;
        }

        parentView.post(new Runnable() {
            @Override
            public void run() {
                Rect rect = new Rect();
                getHitRect(rect);

                rect.top -= topAndBottomPadding;
                rect.bottom += topAndBottomPadding;
                rect.left -= leftAndRightPadding;
                rect.right += leftAndRightPadding;

                parentView.setTouchDelegate(new CustomizeTouchDelegate(rect, ClickRangeChangeTextView.this));
            }
        });
    }

    private void removeTouchDelegate() {
        final View parentView = (View) getParent();
        if(parentView == null) {
            return;
        }

        parentView.setTouchDelegate(null);
    }


    private static class CustomizeTouchDelegate extends TouchDelegate {
        /**
         * View that should receive forwarded touch events
         */
        private View mDelegateView;

        /**
         * Bounds in local coordinates of the containing view that should be mapped to the delegate
         * view. This rect is used for initial hit testing.
         */
        private Rect mBounds;

        /**
         * mBounds inflated to include some slop. This rect is to track whether the motion events
         * should be considered to be within the delegate view.
         */
        private Rect mSlopBounds;

        /**
         * True if the delegate had been targeted on a down event (intersected mBounds).
         */
        private boolean mDelegateTargeted;

        private int mSlop;
        /**
         * Constructor
         *
         * @param bounds       Bounds in local coordinates of the containing view that should be
         *                     mapped to
         *                     the delegate view
         * @param delegateView The view that should receive motion events
         */
        public CustomizeTouchDelegate(Rect bounds, View delegateView) {
            super(bounds, delegateView);

            mBounds = bounds;

            mSlop = ViewConfiguration.get(delegateView.getContext()).getScaledTouchSlop();
            mSlopBounds = new Rect(bounds);
            mSlopBounds.inset(-mSlop, -mSlop);
            mDelegateView = delegateView;
        }


        @Override
        public boolean onTouchEvent(@NonNull MotionEvent event) {
            int x = (int)event.getX();
            int y = (int)event.getY();
            boolean sendToDelegate = false;
            boolean hit = true;
            boolean handled = false;

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mDelegateTargeted = mBounds.contains(x, y);
                    sendToDelegate = mDelegateTargeted;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE:
                    sendToDelegate = mDelegateTargeted;
                    if (sendToDelegate) {
                        Rect slopBounds = mSlopBounds;
                        if (!slopBounds.contains(x, y)) {
                            hit = false;
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    sendToDelegate = mDelegateTargeted;
                    mDelegateTargeted = false;
                    break;
            }
            if (sendToDelegate) {
                if (hit) {
                    // Offset event coordinates to be inside the target view
                    // 这里系统会将点击事件设置到view的中心
//                    event.setLocation(mDelegateView.getWidth() / 2, mDelegateView.getHeight() / 2);
                    event.setLocation(event.getX() - mDelegateView.getX(), event.getY() - mDelegateView.getY());
                } else {
                    // Offset event coordinates to be outside the target view (in case it does
                    // something like tracking pressed state)
                    int slop = mSlop;
                    event.setLocation(-(slop * 2), -(slop * 2));
                }
                handled = mDelegateView.dispatchTouchEvent(event);
            }
            return handled;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(topAndBottomPadding == 0 && leftAndRightPadding == 0) {
            return super.onTouchEvent(event);
        }

        getLocalVisibleRect(rect);

        int x = (int) event.getX();
        int y = (int) event.getY();

        boolean clickIsInclude = rect.contains(x, y);

        rect.top -= topAndBottomPadding;
        rect.bottom += topAndBottomPadding;
        rect.left -= leftAndRightPadding;
        rect.right += leftAndRightPadding;

        boolean contains = rect.contains(x, y);

        if(contains && !clickIsInclude) {
            event.setLocation(getWidth() >> 1, getHeight() >> 1);
        }

        super.onTouchEvent(event);
        return contains;

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        expandTouchArea(Math.max(leftAndRightPadding, 0), Math.max(topAndBottomPadding, 0));
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if(visibility == View.VISIBLE) {
            expandTouchArea(Math.max(leftAndRightPadding, 0), Math.max(topAndBottomPadding, 0));
        } else {
            removeTouchDelegate();
        }
    }

}
