package com.example.administrator.sexrecroding.Base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kuro
 * @date 2017/8/28
 */

public abstract class BaseBindAdapter<T> extends RecyclerView.Adapter<BaseBindViewHolder<T>> {

    private static final int TYPE_LOAD_MORE = 1;
    private static final int TYPE_EMPTY = 2;
    private static final int TYPE_LOADING = 3;

    protected List<T> items;
    protected Context mContext;

    private boolean openLoadMore = false;
    public boolean isLoading = false;
    private boolean canLoadMore = true;

    private View emptyView;
    private RelativeLayout footLayout;
    private View loadingView;
    private View loadingPageView;

    public BaseBindAdapter(Context context) {
        this.mContext = context;
        items = new ArrayList<>();
    }

    @Override
    public BaseBindViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_LOAD_MORE == viewType) {
            if (footLayout == null) {
                footLayout = new RelativeLayout(mContext);
            }
            return BaseBindViewHolder.create(footLayout);
        }
        if (TYPE_EMPTY == viewType) {
            return BaseBindViewHolder.create(emptyView);
        }
        if (TYPE_LOADING == viewType) {
            return BaseBindViewHolder.create(loadingPageView);
        }
        return createVH(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final BaseBindViewHolder<T> holder, int position) {
        if (holder.getItemViewType() == TYPE_LOAD_MORE
                || holder.getItemViewType() == TYPE_EMPTY || holder.getItemViewType() == TYPE_LOADING) {
            return;
        }
        holder.bindTo(holder, items.get(position));
        if (mItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position < 0) {
                        return;
                    }
                    mItemClickListener.onItemClick(items.get(position));
                }
            });
        }
        if (mItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position < 0) {
                        return false;
                    }
                    mItemLongClickListener.onItemLongClick(items.get(position));
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (items.isEmpty() && emptyView != null) {
            return 1;
        }
        if (items.isEmpty() && loadingPageView != null) {
            return 1;
        }
        if (openLoadMore) {
            return items.size() + (items.isEmpty() ? 0 : 1);
        }
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.isEmpty() && emptyView != null) {
            return TYPE_EMPTY;
        }
        if (items.isEmpty() && loadingPageView != null) {
            return TYPE_LOADING;
        }
        if (openLoadMore && position >= getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        }
        return getViewType(position);
    }

    protected abstract int getViewType(int position);

    public List<T> getData() {
        return new ArrayList<>(items);
    }

    public void add(T data) {
        items.add(data);
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        items.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(int index, List<T> list) {
        items.addAll(index, list);
        notifyDataSetChanged();
    }

    public void removeAll() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(int positon) {
        items.remove(positon);
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return items.get(position);
    }

    protected abstract BaseBindViewHolder<T> createVH(ViewGroup parent, int viewType);

    private OnLoadListener mLoadListener;

    public void setOnLoadListener(OnLoadListener listener) {
        this.mLoadListener = listener;
    }

    public interface OnLoadListener {
        void onLoadMore();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (canLoadMore && !isLoading && !recyclerView.canScrollVertically(1)) {
                    isLoading = true;
                    if (loadingView != null) {
                        addLoadingView(loadingView);
                        recyclerView.smoothScrollToPosition(getItemCount());
                    }
                    if (mLoadListener != null) {
                        mLoadListener.onLoadMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public void setOpenLoadMore(boolean openLoadMore) {
        this.openLoadMore = openLoadMore;
    }

    public void setLoadingFinished() {
        this.isLoading = false;
        removeLoadingView();
    }

    public void setEmptyView(View view) {
        emptyView = view;
        loadingPageView = null;
    }

    public void setLoadingPageView(View view) {
        loadingPageView = view;
    }

    public void setLoadingView(View view) {
        loadingView = view;
        addLoadingView(view);
    }

    public void removeLoadingView() {
        footLayout.removeAllViews();
    }

    public void addLoadingView(View view) {
        if (view == null) {
            return;
        }
        if (footLayout == null) {
            footLayout = new RelativeLayout(mContext);
        }
        removeLoadingView();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        footLayout.addView(view, params);
    }

    protected Context getContext() {
        return mContext;
    }

    private OnItemClickListener<T> mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.mItemClickListener = listener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T data);
    }

    private OnItemLongClickListener<T> mItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
        this.mItemLongClickListener = listener;
    }

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(T data);
    }

}
