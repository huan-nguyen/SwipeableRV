package io.huannguyen.swipeablerv.adapter;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.Snackbar.Callback;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import io.huannguyen.swipeablerv.SWItemRemovalListener;
import io.huannguyen.swipeablerv.SWItemDelegate;
import io.huannguyen.swipeablerv.SWSnackBarDataProvider;
import io.huannguyen.swipeablerv.utils.ResourceUtils;

/**
 * Created by huannguyen
 */

/**
 * This class provide a default implementation of the {@link SWAdapter} interface. Unlike {@link
 * StandardSWAdapter}, this class does not hold a reference to or directly add/remove items
 * Instead, it relies on a {@link SWItemDelegate} for such operations. The purpose is to provide
 * your with more flexibility in manipulating the item list if required (i.e., depending on your
 * architecture, you may have a requirement that only a certain class can be allow to directly add
 * or remove items).
 * <p>
 * This means that, to use this class you have to provide your own implementation of {@link
 * SWItemDelegate}.
 * <p>
 * To have more flexibility, you can take this class and {@link StandardSWAdapter} as references
 * and build you own implementation of the {@link SWAdapter} interface.
 */
public abstract class DelegateSWAdapter<T> extends RecyclerView.Adapter<ViewHolder>
        implements SWAdapter<T> {

    protected SWItemDelegate<T> mItemDelegate;
    protected SWItemRemovalListener<T> mItemRemovalListener;
    protected SWSnackBarDataProvider mSnackBarDataProvider;

    protected DelegateSWAdapter(@NonNull SWItemDelegate<T> itemDelegate) {
        mItemDelegate = itemDelegate;
    }

    @Override
    public void onItemCleared(final ViewHolder viewHolder, int direction) {
        final int adapterPosition = viewHolder.getAdapterPosition();
        final T item = mItemDelegate.getItemAtAdapterPosition(adapterPosition);

        displaySnackBarIfNeeded(viewHolder, item, adapterPosition, direction);

        if (mItemRemovalListener != null) {
            mItemRemovalListener.onItemTemporarilyRemoved(item, adapterPosition);
        }
        mItemDelegate.removeItemAtAdapterPosition(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    private void displaySnackBarIfNeeded(ViewHolder viewHolder, final T item, final int adapterPosition,
                                         int direction) {
        if(mSnackBarDataProvider != null && mSnackBarDataProvider.isUndoEnabled()) {
            final Snackbar snackbar = Snackbar
                    .make(mSnackBarDataProvider
                            .getView(), getSnackBarMessage(viewHolder, direction), Snackbar
                            .LENGTH_LONG)
                    .setAction(getUndoActionText(viewHolder, direction), new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mItemDelegate.addItemWithAdapterPosition(item, adapterPosition);
                            notifyItemInserted(adapterPosition);
                            if (mItemRemovalListener != null) {
                                mItemRemovalListener.onItemAddedBack(item, adapterPosition);
                            }
                        }
                    });
            snackbar.setCallback(new Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event != DISMISS_EVENT_ACTION && mItemRemovalListener != null) {
                        mItemRemovalListener.onItemPermanentlyRemoved(item);
                    }
                }
            });

            // set colors
            View snackBarView = snackbar.getView();

            // background color
            int snackBarBackgroundColor =
                    mSnackBarDataProvider.getSnackBarBackgroundColor(direction);
            if (snackBarBackgroundColor != ResourceUtils.NO_COLOR) {
                snackBarView.setBackgroundColor(snackBarBackgroundColor);
            }

            // undo action text color
            int undoActionTextColor = mSnackBarDataProvider.getUndoActionTextColor(direction);
            if (undoActionTextColor != ResourceUtils.NO_COLOR) {
                snackbar.setActionTextColor(undoActionTextColor);
            }

            // info message color
            int infoMessageColor = mSnackBarDataProvider.getSnackBarBackgroundColor(direction);
            if (infoMessageColor != ResourceUtils.NO_COLOR) {
                TextView textView = (TextView) snackBarView
                        .findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(infoMessageColor);
            }

            snackbar.show();
            snackbar.getView().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
        }
    }

    @Override
    public String getSnackBarMessage(ViewHolder viewHolder, int direction) {
        if(mSnackBarDataProvider != null) {
            return mSnackBarDataProvider.getSnackBarMessage(direction);
        }

        return null;
    }

    @Override
    public String getUndoActionText(ViewHolder viewHolder, int direction) {
        if(mSnackBarDataProvider != null) {
            return mSnackBarDataProvider.getUndoActionText(direction);
        }

        return null;
    }

    @Override
    public int getSwipeDirs(ViewHolder viewHolder) {
        return -1;
    }

    public SWItemRemovalListener getItemRemovalListener() {
        return mItemRemovalListener;
    }

    public void setItemRemovalListener(SWItemRemovalListener<T> itemRemovalListener) {
        mItemRemovalListener = itemRemovalListener;
    }

    public SWItemDelegate<T> getItemDelegate() {
        return mItemDelegate;
    }

    public void setItemDelegate(SWItemDelegate<T> itemDelegate) {
        this.mItemDelegate = itemDelegate;
    }

    public SWSnackBarDataProvider getSnackBarDataProvider() {
        return mSnackBarDataProvider;
    }

    @Override
    public void setSnackBarDataProvider(SWSnackBarDataProvider snackBarDataProvider) {
        mSnackBarDataProvider = snackBarDataProvider;
    }
}
