package com.example.administrator.sexrecroding.Base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 *
 * @author nnv
 * @date 2017/9/13
 */

public abstract class BaseAdapter<T> extends BaseBindAdapter<T> {

    public BaseAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getViewType(int position) {
        return getItemLayout(position);
    }

    @Override
    protected BaseBindViewHolder<T> createVH(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                viewType, parent, false);
        return createHolder(binding);
    }

    public void setList(final List<T> list) {
        if (list == null) {
            return;
        }
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return items.size();
            }

            @Override
            public int getNewListSize() {
                return list.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return areItemsSame(items.get(oldItemPosition), list.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return areContentsSame(items.get(oldItemPosition), list.get(newItemPosition));
            }
        });
        result.dispatchUpdatesTo(this);
        items.clear();
        items.addAll(list);
    }

    protected abstract int getItemLayout(int position);

    protected abstract BaseBindViewHolder<T> createHolder(ViewDataBinding binding);

    protected abstract boolean areItemsSame(T oldItem, T newItem);

    protected abstract boolean areContentsSame(T oldItem, T newItem);

}
