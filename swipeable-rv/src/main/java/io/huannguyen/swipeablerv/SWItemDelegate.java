package io.huannguyen.swipeablerv;

/**
 * Created by huannguyen
 */

public interface SWItemDelegate<TItem> {

    /**
     * Look for an item that matches an adapter position
     *
     * @param adapterPosition
     *         adapter position to look for the item
     *
     * @return Item being looked for
     */
    TItem getItemAtAdapterPosition(int adapterPosition);

    /**
     * Remove an item given a adapter position
     *
     * @param adapterPosition
     *         adapter position as a reference to remove an item
     */
    void removeItemAtAdapterPosition(int adapterPosition);

    /**
     * Add an item given an adapter position
     *
     * @param item
     *         Item being added
     * @param adapterPosition
     *         adapter position
     */
    void addItemWithAdapterPosition(TItem item, int adapterPosition);
}
