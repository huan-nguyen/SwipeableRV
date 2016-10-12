package io.huannguyen.swipeablerv;

import android.support.annotation.ColorInt;
import android.view.View;

/**
 * Created by huannguyen
 */

public interface SWSnackBarDataProvider {

    /**
     * Return a message to be displayed on the snack bar when an item is swiped out, depending on the swipe direction
     *
     * @param direction
     *         Swipe direction (either LTR or RTL)
     *
     * @return Message on snack bar
     */
    String getSnackBarMessage(int direction);

    /**
     * Return a the snack bar background color, depending on the swipe direction
     *
     * @param direction
     *         Swipe direction (either LTR or RTL)
     *
     * @return snack bar's background color
     */
    @ColorInt
    int getSnackBarBackgroundColor(int direction);


    /**
     * Return a the snack bar message color, depending on the swipe direction
     *
     * @param direction
     *         Swipe direction (either LTR or RTL)
     *
     * @return snack bar message color
     */
    @ColorInt
    int getSnackBarMessageColor(int direction);

    /**
     * Return the undo action text to be displayed on the snack bar, depending of the swipe direction
     *
     * @param direction
     *         Swipe direction (either LTR or RTL)
     *
     * @return undo action text
     */
    String getUndoActionText(int direction);

    /**
     * Return the undo action text color, depending of the swipe direction
     *
     * @param direction
     *         Swipe direction (either LTR or RTL)
     *
     * @return undo action text color
     */
    @ColorInt
    int getUndoActionTextColor(int direction);

    /**
     * Return if snack bar and undo message should be displayed when an item is swiped out
     *
     * @return true if snack bar and undo message should be displayed, false otherwise
     */
    boolean isUndoEnabled();

    /**
     * Return a view for displaying a snack bar
     *
     * @return view
     */
    View getView();
}
