package com.example.administrator.sexrecroding;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;

import com.example.administrator.sexrecroding.Base.BaseBindingFragment;
import com.example.administrator.sexrecroding.databinding.HomeFragmentBinding;

/**
 * @author Administrator
 * @date 2018/5/23 0023
 */

public class HomeFragment extends BaseBindingFragment<HomeFragmentBinding> {

    private static HomeFragment homeFragment;
    private int tran = 0xFF000000;
    private int end = 0x00000000;

    public static Fragment getInstance() {
        if (homeFragment == null) {
            synchronized (HomeFragment.class) {
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
            }
        }
        return homeFragment;
    }

    @Override
    protected void bindData(HomeFragmentBinding dataBinding) {
        final FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        final AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar_layout);
        binding.imgReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.setVisibility(View.GONE);
                ValueAnimator colorAnim = ObjectAnimator.ofInt(binding.imgReady, "backgroundColor", tran, end);
                colorAnim.setDuration(300);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        binding.imgReady.setVisibility(View.GONE);
                        hideAppBarLayout(appBarLayout);
                        //TODO:，播放背景音乐，场景变换

                    }
                });
                colorAnim.start();
            }

        });
    }

    private void hideAppBarLayout(AppBarLayout appBarLayout) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(appBarLayout, "translationY", 0, -appBarLayout.getHeight());
        animator.setDuration(300);
        animator.start();
    }

    private void showAppBarLayout(AppBarLayout appBarLayout) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(appBarLayout, "translationY", -appBarLayout.getHeight(), 0);
        animator.setDuration(300);
        animator.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment;
    }
}
