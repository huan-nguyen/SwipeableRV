package io.huannguyen.swipeablerv.adapter;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView.ViewHolder;

import io.huannguyen.swipeablerv.SWItemRemovalListener;
import io.huannguyen.swipeablerv.SWSnackBarDataProvider;
import io.huannguyen.swipeablerv.view.SWRecyclerView;

/**
 * Created by huannguyen
 */

public interface SWAdapter<TItem, TViewHolder extends ViewHolder> {

    /**
     * Register an adapter with a {@link SWSnackBarDataProvider}
     *
     * @param snackBarDataProvider a SWSnackBarDataProvider instance
     */
    void setSnackBarDataProvider(SWSnackBarDataProvider snackBarDataProvider);

    /**
     * This method is invoked when an item is swiped to remove from a {@link SWRecyclerView}. A
     * {@link Snackbar} with a removal message and an undo button would be displayed. Tapping on the
     * undo button would bring the item back to the {@link SWRecyclerView}.
     * <p>
     * <p>
     * In order to add additional actions on removing or adding the item back to the {@link
     * SWRecyclerView}, you can set a {@link SWItemRemovalListener} for the {@link SWAdapter}.
     *
     * @param viewHolder
     *         The view holder associated with the item being removed
     * @param direction
     *         The swipe direction
     *         <p>
     *         See {@link #setItemRemovalListener(SWItemRemovalListener)}
     */
    void onItemCleared(final TViewHolder viewHolder, int direction);

    /**
     * Get a message being displayed on a {@link Snackbar} when an item is swiped away.
     * <p>
     * By default, this return the snack bar message set in the {@link SWRecyclerView} instance (via
     * attributes or programmatically).
     * <p>
     * Override this method if you want to programmatically assign different deletion message to
     * different items
     *
     * @param viewHolder
     *         The {@link ViewHolder} associated with the item being swiped out
     * @param direction
     *         Direction of the swipe
     *
     * @return a snack bar message
     */
    String getSnackBarMessage(TViewHolder viewHolder, int direction);

    /**
     * Get the snack bar action text being displayed when an item is swiped away.
     * <p>
     * By default, this return the undo action text set in the {@link SWRecyclerView} instance (via
     * attributes or programmatically).
     * <p>
     * Override this method if you want to programmatically assign different undo action text to
     * different items
     *
     * @param viewHolder
     *         The {@link ViewHolder} associated with the item being swiped out
     * @param direction
     *         Direction of the swipe
     *
     * @return an undo action text
     */
    String getUndoActionText(TViewHolder viewHolder, int direction);

    /**
     * Get the supported swipe directions for a particular item in a {@link SWRecyclerView}.
     * <p>
     * By default, this function returns -1, which means the swipe directions for this item is
     * identical to the swipe directions that was set via {@link
     * SWRecyclerView#setupSwipeToDismiss(SWAdapter, SWSnackBarDataProvider,
     * int)}.
     * <p>
     * You can override this function to specify the swipe directions for particular types of
     * items.
     *
     * @param viewHolder
     *         The view holder associated with the item whose swipe directions are being set
     *
     * @return Swipe directions
     */
    int getSwipeDirs(TViewHolder viewHolder);

    SWItemRemovalListener getItemRemovalListener();

    void setItemRemovalListener(SWItemRemovalListener<TItem> SWItemRemovalListener);
}
