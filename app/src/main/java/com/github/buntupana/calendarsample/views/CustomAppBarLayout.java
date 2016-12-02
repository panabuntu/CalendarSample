package com.github.buntupana.calendarsample.views;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.animation.ValueAnimatorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

@CoordinatorLayout.DefaultBehavior(CustomAppBarLayout.ScrollBehavior.class)
public class CustomAppBarLayout extends AppBarLayout {

    private final String TAG = CustomAppBarLayout.class.getSimpleName();

    public CustomAppBarLayout(Context context) {
        super(context);
    }

    public CustomAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public static class ScrollBehavior extends AppBarLayout.Behavior {

        private int mPreviousoffset;
        ValueAnimatorCompat mValueAnimatorCompat;


        @Override
        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target) {
//            super.onStopNestedScroll(coordinatorLayout, abl, target);
            if (abl.getTotalScrollRange() != Math.abs(abl.getY()) && 0 != Math.abs(abl.getY())) {

//                mValueAnimatorCompat.setDuration(300);

//                abl.setY(200);

//                abl.setExpanded();
            }
        }

        public void animateHeight(final View v, final int height) {

            final int initialHeight = v.getMeasuredHeight();
            int duration = 500;
            Interpolator interpolator = new AccelerateInterpolator(2);

            // I have to set the same height before the animation because there is a glitch
            // in the beginning of the animation
            v.getLayoutParams().height = initialHeight;
            v.requestLayout();

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
//                    Log.d(TAG, "InterpolatedTime: " + interpolatedTime);
//                    Log.d(TAG, "Collapsing height: " + (initialHeight - (int) (height * interpolatedTime)));
                    v.getLayoutParams().height = initialHeight - (int) (height * interpolatedTime);
                    v.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration(duration);
            a.setInterpolator(interpolator);
            v.startAnimation(a);
        }

        @Override
        public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {
            return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
//            return false;
        }

        private void snapToChildIfNeeded(CoordinatorLayout coordinatorLayout, AppBarLayout abl) {

//            final int offset = getTopBottomOffsetForScrollingSibling();
//            final int offsetChildIndex = getChildIndexOnOffset(abl, offset);
//            if (offsetChildIndex >= 0) {
//                final View offsetChild = abl.getChildAt(offsetChildIndex);
//                final LayoutParams lp = (LayoutParams) offsetChild.getLayoutParams();
//                final int flags = lp.getScrollFlags();
//
//                if ((flags & LayoutParams.FLAG_SNAP) == LayoutParams.FLAG_SNAP) {
//                    // We're set the snap, so animate the offset to the nearest edge
//                    int snapTop = -offsetChild.getTop();
//                    int snapBottom = -offsetChild.getBottom();
//
//                    if (offsetChildIndex == abl.getChildCount() - 1) {
//                        // If this is the last child, we need to take the top inset into account
//                        snapBottom += abl.getTopInset();
//                    }
//
//                    if (checkFlag(flags, LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)) {
//                        // If the view is set only exit until it is collapsed, we'll abide by that
//                        snapBottom += ViewCompat.getMinimumHeight(offsetChild);
//                    } else if (checkFlag(flags, LayoutParams.FLAG_QUICK_RETURN
//                            | LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)) {
//                        // If it's set to always enter collapsed, it actually has two states. We
//                        // select the state and then snap within the state
//                        final int seam = snapBottom + ViewCompat.getMinimumHeight(offsetChild);
//                        if (offset < seam) {
//                            snapTop = seam;
//                        } else {
//                            snapBottom = seam;
//                        }
//                    }
//
//                    final int newOffset = offset < (snapBottom + snapTop) / 2
//                            ? snapBottom
//                            : snapTop;
//                    animateOffsetTo(coordinatorLayout, abl,
//                            MathUtils.constrain(newOffset, -abl.getTotalScrollRange(), 0), 0);
//                }
//            }
        }

    }
}
