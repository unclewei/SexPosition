package com.example.administrator.sexrecroding.Base;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;


/**
 * @author nnv
 * @date 2017/7/19
 */

public abstract class BaseBindingActivity<T extends ViewDataBinding> extends Activity {

    protected T binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        bindData(binding);
    }

    public Bundle getBundle() {
        if (getIntent() != null && getIntent().hasExtra(getPackageName())) {
            return getIntent().getBundleExtra(getPackageName());
        }
        return null;
    }


    protected abstract void bindData(T dataBinding);

    protected abstract int getLayoutId();
}
