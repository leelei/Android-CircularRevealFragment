package com.fernandofgallego.ciruclarrevealfragment;

import android.animation.Animator;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;


public class MainActivity extends Activity implements OnFragmentTouched {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addFragment(View v) {
        int randomColor = Color.argb(255, (int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
        Fragment fragment = CircularRevealingFragment.newInstance(20,20, randomColor);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }

    @Override
    public void onFragmentTouched(Fragment fragment, float x, float y) {
        if(fragment instanceof CircularRevealingFragment) {
            final CircularRevealingFragment theFragment = (CircularRevealingFragment) fragment;

            Animator unreveal = theFragment.prepareUnrevealAnimator(x, y);

            unreveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) { }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //remove the fragment only when the animation finishes
                    getFragmentManager().beginTransaction().remove(theFragment).commit();
                }

                @Override
                public void onAnimationCancel(Animator animation) { }

                @Override
                public void onAnimationRepeat(Animator animation) { }
            });
            unreveal.start();
        }
    }

    /**
     * Our demo fragment
     */
    public static class CircularRevealingFragment extends Fragment {

        OnFragmentTouched listener;

        public CircularRevealingFragment() {
        }

        public static CircularRevealingFragment newInstance(int centerX, int centerY, int color) {
            Bundle args = new Bundle();
            args.putInt("cx", centerX);
            args.putInt("cy", centerY);
            args.putInt("color",color);
            CircularRevealingFragment fragment = new CircularRevealingFragment();
            fragment.setArguments(args);
            return fragment;

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            rootView.setBackgroundColor(getArguments().getInt("color"));

            // To run the animation as soon as the view is layout in the view hierarchy we add this listener and remove it
            // as soon as it runs to prevent multiple animations if the view changes bounds
            rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                        int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    int cx = getArguments().getInt("cx");
                    int cy = getArguments().getInt("cy");

                    //get the hypothenuse so the radius is from one corner to the other
                    int radius = (int) Math.hypot(right, bottom);

                    Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                    reveal.setInterpolator(new DecelerateInterpolator(2f));
                    reveal.setDuration(1000);
                    reveal.start();
                }
            });

            //attach a touch listener
            rootView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(listener != null)
                        listener.onFragmentTouched(CircularRevealingFragment.this, event.getX(), event.getY());
                    return true;
                }
            });
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            if(activity instanceof OnFragmentTouched)
                listener = (OnFragmentTouched)activity;
        }

        /**
         * Get the animator to unreveal the circle
         * @param cx center x of the circle (or where the view was touched)
         * @param cy center y of the circle (or where the view was touched)
         * @return
         */
        public Animator prepareUnrevealAnimator(float cx, float cy) {

            int radius  = getEnclosingCircleRadius(getView(), (int)cx, (int)cy);
            Animator anim = ViewAnimationUtils .createCircularReveal(getView(), (int) cx, (int) cy, radius, 0);
            anim.setInterpolator(new AccelerateInterpolator(2f));
            anim.setDuration(1000);
            return anim;
        }

        /**
         * To be really accurate we have to start the circle on the furthest corner of the view
         * @param v the view to unreveal
         * @param cx center x of the circle
         * @param cy center y of the circle
         * @return the maximum radius
         */
        private int getEnclosingCircleRadius(View v, int cx, int cy) {
            int realCenterX = cx+v.getLeft();
            int realCenterY = cy+v.getTop();
            int distanceTopLeft = (int) Math.hypot(realCenterX-v.getLeft(), realCenterY-v.getTop());
            int distanceTopRight = (int) Math.hypot(v.getRight()-realCenterX, realCenterY-v.getTop());
            int distanceBottomLeft = (int) Math.hypot(realCenterX-v.getLeft(),v.getBottom()-realCenterY);
            int distanceBotomRight = (int) Math.hypot(v.getRight()-realCenterX,v.getBottom()-realCenterY);

            return Math.max(Math.max(Math.max(distanceTopLeft,distanceTopRight),distanceBottomLeft),distanceBotomRight);
        }
    }
}
