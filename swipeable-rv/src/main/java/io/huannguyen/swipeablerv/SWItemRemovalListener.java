package io.huannguyen.swipeablerv;

import io.huannguyen.swipeablerv.view.SWRecyclerView;

/**
 * Created by huannguyen
 */

public interface SWItemRemovalListener<TItem> {
    /**
     * Method invoked when an item is temporarily removed from a {@link SWRecyclerView}.
     * <p>
     * At this stage, user still has a chance to add the item back nto the list using the undo button.
     *
     * @param item
     *         The item being added back
     * @param position
     *         The position of the item being temporarily removed
     * @param direction
     *         The direction the item was swiped, either {@link #LEFT} or {@link #RIGHT}
     */
    void onItemTemporarilyRemoved(TItem item, int position, int direction);

    /**
     * Method invoked when it is no longer possible to add the previously removed item back into a {@link SWRecyclerView}.
     *
     * This is a good place for taking the next step in the removal such as database persistence
     *
     * @param item    The item being permanently removed
     * @param direction
     *         The direction the item was swiped, either {@link #LEFT} or {@link #RIGHT}
     */
    void onItemPermanentlyRemoved(TItem item, int direction);

    /**
     * Method invoked when an item associated to the given view holder is added back to a {@link SWRecyclerView}.
     *
     * @param item
     *         The item being added back
     * @param position
     *         The position of the item being added back
     * @param direction
     *         The direction the item was swiped, either {@link #LEFT} or {@link #RIGHT}
     */
    void onItemAddedBack(TItem item, int position, int direction);
}
