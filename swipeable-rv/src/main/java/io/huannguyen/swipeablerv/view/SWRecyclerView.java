package io.huannguyen.swipeablerv.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import io.huannguyen.swipeablerv.R;
import io.huannguyen.swipeablerv.SWSnackBarDataProvider;
import io.huannguyen.swipeablerv.adapter.SWAdapter;
import io.huannguyen.swipeablerv.utils.ResourceUtils;

/**
 * Created by huannguyen
 */

public class SWRecyclerView
        extends RecyclerView
        implements SWSnackBarDataProvider {
    @ColorInt
    private int mBorderColor;
    private float mBorderWidth;
    private boolean mHasBorder;
    private boolean mAllowUndo;

    // if swrv_swipe_background is set, its value would override
    // swrv_ltr_swipe_background and
    // swrv_rtl_swipe_background. This also applied to all other attributes
    @ColorInt
    private int mLTRSwipeBackground;
    @ColorInt
    private int mRTLSwipeBackground;

    private int mSwipeIconHeight;
    private int mSwipeIconWidth;

    private float mLTRSwipeIconMargin;
    private float mRTLSwipeIconMargin;

    private String mLTRSwipeMessage;
    private String mRTLSwipeMessage;

    private float mLTRSwipeIconAndMessageGap;
    private float mRTLSwipeIconAndMessageGap;

    private Typeface mSwipeMessageFont;
    private float mSwipeMessageTextSize;

    // bitmaps hosting swipe message text are needed to show message in case item's view doesn't
    // have a background. In such case, the text would be fully visible as soon as the item's
    // edge reaches the text's starting point. Creating bitmaps is overkilled for cases item view
    // has background.
    // However, at the time the RV is loaded, we have no way to know if the item view has
    // background or not.
    // We only know if a background is set when the onChildDraw callback is invoked.
    // However, that would be a performance concern to create the bitmap at that point when
    // user's swiping the item.
    // Hence, it's best to create bitmaps right from the beginning.
    private Bitmap mLTRSwipeMessageBitmap;
    private Bitmap mRTLSwipeMessageBitmap;

    @ColorInt
    private int mLTRSwipeMessageColor;
    @ColorInt
    private int mRTLSwipeMessageColor;

    @DrawableRes
    private int mLTRSwipeIconRes;

    @DrawableRes
    private int mRTLSwipeIconRes;

    @ColorInt
    private int mLTRSwipeIconColor;

    @ColorInt
    private int mRTLSwipeIconColor;

    private Bitmap mLTRSwipeIcon;
    private Bitmap mRTLSwipeIcon;

    private String mLTRSnackBarMessage;
    private String mRTLSnackBarMessage;

    @ColorInt
    private int mLTRSnackBarMessageColor;
    @ColorInt
    private int mRTLSnackBarMessageColor;

    @ColorInt
    private int mLTRSnackBarBackground;
    @ColorInt
    private int mRTLSnackBarBackground;

    private String mLTRUndoActionText;
    private String mRTLUndoActionText;

    @ColorInt
    private int mLTRUndoActionTextColor;
    @ColorInt
    private int mRTLUndoActionTextColor;

    private Paint mPaint = new Paint();

    public SWRecyclerView(Context context) {
        super(context);
    }

    public SWRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        processAttributes(context, attrs, 0);
    }

    public SWRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        processAttributes(context, attrs, defStyle);
    }

    private void processAttributes(Context context, AttributeSet attrs, int defStyle) {
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.swrv, defStyle, 0);

        mBorderColor = typedArray.getColor(R.styleable.swrv_border_color, ResourceUtils
                .getColor(context, R.color.swrv_default_border_color));

        mBorderWidth = typedArray.getDimension(R.styleable.swrv_border_width, ResourceUtils
                .getDimension(context, R.dimen.swrv_default_border_width));

        // swipe view background color
        int swipeBackground = typedArray
                .getColor(R.styleable.swrv_swipe_background, ResourceUtils.NO_COLOR);

        if (swipeBackground != ResourceUtils.NO_COLOR) {
            mLTRSwipeBackground = swipeBackground;
            mRTLSwipeBackground = swipeBackground;
        } else {
            mLTRSwipeBackground =
                    typedArray.getColor(R.styleable.swrv_ltr_swipe_background, ResourceUtils
                            .getColor(context, R.color.swrv_default_swipe_view_background));

            mRTLSwipeBackground =
                    typedArray.getColor(R.styleable.swrv_rtl_swipe_background, ResourceUtils
                            .getColor(context, R.color.swrv_default_swipe_view_background));
        }

        processSwipeIconAndMessageAttributes(context, typedArray);

        // allow undo and displaying snack bar
        mAllowUndo = typedArray.getBoolean(R.styleable.swrv_allow_undo, true);

        if (mAllowUndo) {
            processSnackBarAttributes(typedArray);
        }

        mHasBorder = typedArray.getBoolean(R.styleable.swrv_has_border, true);

        typedArray.recycle();

        initResources();
    }

    private void processSwipeIconAndMessageAttributes(Context context, TypedArray typedArray) {
        // swipe icon resource
        int swipeIconRes =
                typedArray.getResourceId(R.styleable.swrv_swipe_icon, 0);

        if (swipeIconRes != 0) {
            mLTRSwipeIconRes = swipeIconRes;
            mRTLSwipeIconRes = swipeIconRes;
        } else {
            mLTRSwipeIconRes = typedArray
                    .getResourceId(R.styleable.swrv_ltr_swipe_icon, R.drawable
                            .swrv_default_ic_delete);
            mRTLSwipeIconRes = typedArray
                    .getResourceId(R.styleable.swrv_rtl_swipe_icon, R.drawable
                            .swrv_default_ic_delete);
        }

        // icon height and width
        mSwipeIconHeight = (int) typedArray
                .getDimension(R.styleable.swrv_swipe_icon_height, ResourceUtils
                        .getDimension(context, R.dimen.swrv_default_icon_size));
        mSwipeIconWidth = (int) typedArray
                .getDimension(R.styleable.swrv_swipe_icon_width, ResourceUtils
                        .getDimension(context, R.dimen.swrv_default_icon_size));

        // swipe icon's margin to the nearest edge
        float swipeIconMargin = typedArray
                .getDimension(R.styleable.swrv_swipe_icon_margin, -1);

        if (swipeIconMargin != -1) {
            mLTRSwipeIconMargin = swipeIconMargin;
            mRTLSwipeIconMargin = swipeIconMargin;
        } else {
            mLTRSwipeIconMargin = typedArray
                    .getDimension(R.styleable.swrv_ltr_swipe_icon_margin, ResourceUtils
                            .getDimension(context, R.dimen.swrv_default_icon_margin));
            mRTLSwipeIconMargin = typedArray
                    .getDimension(R.styleable.swrv_rtl_swipe_icon_margin, ResourceUtils
                            .getDimension(context, R.dimen.swrv_default_icon_margin));
        }

        // swipe icon color
        int swipeIconColor =
                typedArray.getColor(R.styleable.swrv_swipe_icon_color, ResourceUtils.NO_COLOR);

        if (swipeIconColor != ResourceUtils.NO_COLOR) {
            mLTRSwipeIconColor = swipeIconColor;
            mRTLSwipeIconColor = swipeIconColor;
        } else {
            mLTRSwipeIconColor = typedArray
                    .getColor(R.styleable.swrv_ltr_swipe_icon_color, ResourceUtils.NO_COLOR);
            mRTLSwipeIconColor = typedArray
                    .getColor(R.styleable.swrv_rtl_swipe_icon_color, ResourceUtils.NO_COLOR);
        }

        // swipe messages
        String swipeMessage = typedArray.getString(R.styleable.swrv_swipe_message);

        if (swipeMessage != null) {
            mLTRSwipeMessage = swipeMessage;
            mRTLSwipeMessage = swipeMessage;
        } else {
            mLTRSwipeMessage = typedArray.getString(R.styleable.swrv_ltr_swipe_message);
            mRTLSwipeMessage = typedArray.getString(R.styleable.swrv_rtl_swipe_message);
        }

        // swipe message color
        int swipeMessageColor =
                typedArray.getColor(R.styleable.swrv_swipe_message_color, ResourceUtils.NO_COLOR);

        if (swipeMessageColor != ResourceUtils.NO_COLOR) {
            mLTRSwipeMessageColor = swipeMessageColor;
            mRTLSwipeMessageColor = swipeMessageColor;
        } else {
            mLTRSwipeMessageColor =
                    typedArray.getColor(R.styleable.swrv_ltr_swipe_message_color, ResourceUtils
                            .getColor(context, R.color.swrv_default_swipe_message_color));
            mRTLSwipeMessageColor =
                    typedArray.getColor(R.styleable.swrv_rtl_swipe_message_color, ResourceUtils
                            .getColor(context, R.color.swrv_default_swipe_message_color));
        }

        // gap between swipe icon and message on each side
        float swipeIconAndMessageGap = typedArray
                .getDimension(R.styleable.swrv_swipe_icon_and_message_gap, -1);

        if (swipeIconAndMessageGap != -1) {
            mLTRSwipeIconAndMessageGap = swipeIconAndMessageGap;
            mRTLSwipeIconAndMessageGap = swipeIconAndMessageGap;
        } else {
            mLTRSwipeIconAndMessageGap = typedArray
                    .getDimension(R.styleable.swrv_ltr_swipe_icon_and_message_gap, ResourceUtils
                            .getDimension(context, R.dimen
                                    .swrv_default_swipe_icon_and_message_gap));
            mRTLSwipeIconAndMessageGap = typedArray
                    .getDimension(R.styleable.swrv_rtl_swipe_icon_and_message_gap, ResourceUtils
                            .getDimension(context, R.dimen
                                    .swrv_default_swipe_icon_and_message_gap));
        }

        // message size and font
        mSwipeMessageTextSize = typedArray
                .getDimension(R.styleable.swrv_swipe_message_text_size, ResourceUtils
                        .getDimension(context, R.dimen.swrv_default_swipe_message_text_size));

        String swipeMessageFontPath =
                typedArray.getString(R.styleable.swrv_swipe_message_font_path);

        boolean isTextBold = typedArray.getBoolean(R.styleable.swrv_swipe_message_bold, false);

        mSwipeMessageFont = swipeMessageFontPath != null ?
                            Typeface.createFromAsset(getContext().getAssets(), swipeMessageFontPath) :
                            Typeface.create(Typeface.DEFAULT, isTextBold ? Typeface.BOLD : Typeface.NORMAL);
    }

    private void processSnackBarAttributes(TypedArray typedArray) {
        // snack bar message
        String snackBarMessage = typedArray.getString(R.styleable.swrv_snackbar_message);

        if (snackBarMessage != null) {
            mLTRSnackBarMessage = snackBarMessage;
            mRTLSnackBarMessage = snackBarMessage;
        } else {
            mLTRSnackBarMessage = typedArray.getString(R.styleable.swrv_ltr_snackbar_message);
            mRTLSnackBarMessage = typedArray.getString(R.styleable.swrv_rtl_snackbar_message);
        }

        // snack bar message color
        int snackBarMessageColor = typedArray
                .getColor(R.styleable.swrv_snackbar_message_color, ResourceUtils.NO_COLOR);

        if (snackBarMessageColor != ResourceUtils.NO_COLOR) {
            mLTRSnackBarMessageColor = snackBarMessageColor;
            mRTLSnackBarMessageColor = snackBarMessageColor;
        } else {
            mLTRSnackBarMessageColor = typedArray
                    .getColor(R.styleable.swrv_ltr_snackbar_message_color, ResourceUtils
                            .NO_COLOR);
            mRTLSnackBarMessageColor = typedArray
                    .getColor(R.styleable.swrv_rtl_snackbar_message_color, ResourceUtils
                            .NO_COLOR);
        }

        // snack bar background color
        int snackBarBackground = typedArray
                .getColor(R.styleable.swrv_snackbar_background, ResourceUtils.NO_COLOR);

        if (snackBarBackground != ResourceUtils.NO_COLOR) {
            mLTRSnackBarBackground = snackBarBackground;
            mRTLSnackBarBackground = snackBarBackground;
        } else {
            // if nothing is set, use the system's default color
            mLTRSnackBarBackground = typedArray
                    .getColor(R.styleable.swrv_ltr_snackbar_background, ResourceUtils
                            .NO_COLOR);
            mRTLSnackBarBackground = typedArray
                    .getColor(R.styleable.swrv_rtl_snackbar_background, ResourceUtils
                            .NO_COLOR);
        }

        // snack bar undo message
        String undoText = typedArray.getString(R.styleable.swrv_undo_action_text);

        if (undoText != null) {
            mLTRUndoActionText = undoText;
            mRTLUndoActionText = undoText;
        } else {
            mLTRUndoActionText = ResourceUtils
                    .getString(getContext(), typedArray, R.styleable.swrv_ltr_undo_action_text,
                               R.string.swrv_action_undo);
            mRTLUndoActionText = ResourceUtils
                    .getString(getContext(), typedArray, R.styleable.swrv_rtl_undo_action_text,
                               R.string.swrv_action_undo);
        }

        // snack bar undo message color
        int undoActionColor = typedArray.getColor(R.styleable.swrv_undo_action_text_color, ResourceUtils.NO_COLOR);

        if (undoActionColor != ResourceUtils.NO_COLOR) {
            mLTRUndoActionTextColor = undoActionColor;
            mRTLUndoActionTextColor = undoActionColor;
        } else {
            // if nothing is set, use the system's default color
            mLTRUndoActionTextColor = typedArray.getColor(R.styleable.swrv_ltr_undo_action_text_color, ResourceUtils
                    .NO_COLOR);
            mRTLUndoActionTextColor =
                    typedArray.getColor(R.styleable.swrv_rtl_undo_action_text_color, ResourceUtils.NO_COLOR);
        }
    }

    private void initResources() {
        if (mLTRSwipeIconRes != 0) {
            mLTRSwipeIcon = ResourceUtils
                    .createBitmap(getResources(), mLTRSwipeIconRes, mLTRSwipeIconColor, mSwipeIconHeight,
                                  mSwipeIconWidth);
        }

        if (mRTLSwipeIconRes != 0) {
            if (mRTLSwipeIconRes == mLTRSwipeIconRes && mLTRSwipeIconColor == mRTLSwipeIconColor) {
                mRTLSwipeIcon = mLTRSwipeIcon;
            } else {
                mRTLSwipeIcon = ResourceUtils.createBitmap(getResources(), mRTLSwipeIconRes,
                                                           mRTLSwipeIconColor, mSwipeIconHeight, mSwipeIconWidth);
            }
        }

        createLTRSwipeMessageBitmap();
        createRTLSwipeMessageBitmap();
    }

    /**
     * This method is called to enable swipe-to-dismiss feature on the {@link SWRecyclerView}
     *
     * @param adapter
     *         The adapter for interacting with the items in the {@link SWRecyclerView}
     * @param swipeDirection
     *         The direction that items in the recycler view can be swiped
     */
    public void setupSwipeToDismiss(final SWAdapter adapter, final int swipeDirection) {
        setupSwipeToDismiss(adapter, this, swipeDirection);
    }

    /**
     * This method is called to enable swipe-to-dismiss feature on the {@link SWRecyclerView}
     * with a custom {@link SWSnackBarDataProvider}
     *
     * @param adapter
     *         The adapter for interacting with the items in the {@link SWRecyclerView}
     * @param snackBarDataProvider
     *         a SnackBarProvider instance
     * @param swipeDirection
     *         The direction that items in the recycler view can be swiped
     */
    public void setupSwipeToDismiss(final SWAdapter adapter, SWSnackBarDataProvider snackBarDataProvider, final int swipeDirection) {
        adapter.setSnackBarDataProvider(snackBarDataProvider);
        ItemTouchHelper.Callback callback = new SimpleCallback(0, swipeDirection) {
            @Override
            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder
                    target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dirs = adapter.getSwipeDirs(viewHolder);
                if (dirs == -1) {
                    return super.getSwipeDirs(recyclerView, viewHolder);
                }
                return dirs;
            }

            @Override
            public void onSwiped(ViewHolder viewHolder, int direction) {
                adapter.onItemCleared(viewHolder, direction);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                                  isCurrentlyActive);

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) (itemView.getBottom() - itemView.getTop());

                    if (dX != 0) {
                        boolean hasBackground = itemView.getBackground() != null;

                        // swipe from left to right
                        if (dX > 0) {
                            handleLTRSwipeDraw(c, dX, itemView, hasBackground, height);
                        } else { // swipe from left to right
                            handleRTLSwipeDraw(c, dX, itemView, hasBackground, height);
                        }
                    }
                }
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, ViewHolder
                    viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                // borders must be drawn within onChildDrawOver instead of onChildDraw to make
                // sure it is on top of the item view
                // in case the item view has a background
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX != 0) {
                    if (mHasBorder) {
                        View itemView = viewHolder.itemView;
                        mPaint.setColor(mBorderColor);
                        c.drawRect(itemView.getLeft(), itemView.getTop(), itemView
                                .getRight(), itemView.getTop() + mBorderWidth, mPaint);
                        c.drawRect(itemView.getLeft(), itemView.getBottom() - mBorderWidth, itemView
                                .getRight(), itemView.getBottom(), mPaint);
                    }
                }
                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState,
                                      isCurrentlyActive);
            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(SWRecyclerView.this);
    }

    private void handleLTRSwipeDraw(Canvas c, float dX, View itemView, boolean hasBackground, float height) {
        // set paint color as the respective swipe view background color
        mPaint.setColor(mLTRSwipeBackground);

        // draw background (area which is visible when the item view is swiped)
        c.drawRect(itemView.getLeft(), itemView.getTop(), dX, itemView
                .getBottom(), mPaint);

        // draw swipe icon
        // in case the item view has a background (defined with i.e.,
        // android:background),
        // then we can draw the full icon since it can be hidden by the item
        // view.
        // otherwise, we have to draw just a portion of the icon in case the
        // item view hasn't been moved out of
        // the area in which the icon should be displayed.
        if (dX > mLTRSwipeIconMargin) {
            if (!hasBackground && dX < mLTRSwipeIconMargin + mSwipeIconWidth) {
                int iconWidth = (int) (dX - mLTRSwipeIconMargin);
                // specify the portion of the icon being drawn
                Rect iconDrawnPortion = new Rect(0, 0, iconWidth, mSwipeIconHeight);
                RectF swipeIconPos = new RectF(itemView.getLeft() + (int) mLTRSwipeIconMargin,
                                               (int) (itemView.getTop() +
                                                       0.5 * (height - mSwipeIconHeight)), (int)
                                                       (itemView.getLeft() + mLTRSwipeIconMargin +
                                                               iconWidth), (int)
                                                       (itemView.getBottom() -
                                                               0.5 * (height - mSwipeIconHeight)));
                c.drawBitmap(mLTRSwipeIcon, iconDrawnPortion, swipeIconPos,
                             mPaint);
            } else {
                c.drawBitmap(mLTRSwipeIcon, itemView
                        .getLeft() + (int) mLTRSwipeIconMargin, (int) (itemView
                        .getTop() + 0.5 * (height - mSwipeIconHeight)), mPaint);

                int messageMargin = (int) mLTRSwipeIconMargin + mSwipeIconWidth +
                        (int) mLTRSwipeIconAndMessageGap;

                if (dX > messageMargin && mLTRSwipeMessageBitmap != null) {
                    int messageWidth = mLTRSwipeMessageBitmap.getWidth();
                    int messageHeight = mLTRSwipeMessageBitmap.getHeight();

                    if (!hasBackground && dX < messageMargin + messageWidth) {
                        messageWidth = (int) (dX - messageMargin);

                        // specify the portion of the message being drawn
                        Rect messageDrawnPortion = new Rect(0, 0, messageWidth, messageHeight);
                        RectF messagePos = new RectF(itemView.getLeft() + messageMargin,
                                                     (int) (itemView.getTop() +
                                                             0.5 * (height - messageHeight)),
                                                     itemView.getLeft() + messageMargin +
                                                             messageWidth, (int)
                                                             (itemView.getBottom() - 0.5 *
                                                                     (height - messageHeight)));
                        c.drawBitmap(mLTRSwipeMessageBitmap, messageDrawnPortion, messagePos,
                                     mPaint);
                    } else {
                        c.drawBitmap(mLTRSwipeMessageBitmap, itemView.getLeft() + messageMargin,
                                     (int) (itemView.getTop() + 0.5 * (height - messageHeight)),
                                     mPaint);
                    }
                }
            }
        }
    }

    private void handleRTLSwipeDraw(Canvas c, float dX, View itemView, boolean hasBackground, float height) {
        // set paint color as the respective swipe view background color
        mPaint.setColor(mRTLSwipeBackground);

        // draw background (area which is visible when the item view is swiped)
        c.drawRect(itemView.getRight() + dX, itemView.getTop(), itemView
                .getRight(), itemView.getBottom(), mPaint);

        // draw swipe icon
        if (dX < 0 - mRTLSwipeIconMargin) {
            if (!hasBackground && dX > 0 - mRTLSwipeIconMargin - mSwipeIconWidth) {
                int iconWidth = (int) (0 - dX - mRTLSwipeIconMargin);

                // specify the portion of the icon being drawn
                Rect iconDrawnPortion =
                        new Rect(mSwipeIconWidth - iconWidth, 0, mSwipeIconWidth, mSwipeIconHeight);
                RectF swipeIconPos =
                        new RectF(itemView.getRight() - mRTLSwipeIconMargin - iconWidth,
                                  (float) (itemView.getTop() + 0.5 * (height - mSwipeIconHeight)),
                                  (float) itemView.getRight() - mRTLSwipeIconMargin, (float)
                                          (itemView.getBottom() -
                                                  0.5 * (height - mSwipeIconHeight)));
                c.drawBitmap(mRTLSwipeIcon, iconDrawnPortion, swipeIconPos, mPaint);
            } else {
                c.drawBitmap(mRTLSwipeIcon,
                             itemView.getRight() - mRTLSwipeIconMargin - mSwipeIconWidth,
                             (float) (itemView.getTop() + 0.5 * (height - mSwipeIconHeight)),
                             mPaint);

                int messageMargin = (int) mRTLSwipeIconMargin + mSwipeIconWidth +
                        (int) mRTLSwipeIconAndMessageGap;

                if (dX < 0 - messageMargin && mRTLSwipeMessageBitmap != null) {
                    int messageWidth = mRTLSwipeMessageBitmap.getWidth();
                    int messageHeight = mRTLSwipeMessageBitmap.getHeight();

                    if(!hasBackground && dX > 0 - messageMargin - messageWidth) {
                        int visibleMessageWidth = (int) (0 - messageMargin - dX);

                        // specify the portion of the message being drawn
                        Rect messageDrawnPortion =
                                new Rect(messageWidth - visibleMessageWidth, 0,
                                         messageWidth, messageHeight);
                        RectF messagePos =
                                new RectF(itemView.getRight() - messageMargin - visibleMessageWidth,
                                          (float) (itemView.getTop() +
                                                  0.5 * (height - messageHeight)),
                                          (float) itemView.getRight() - messageMargin,
                                          (float)
                                                  (itemView.getBottom() -
                                                          0.5 * (height - messageHeight)));
                        c.drawBitmap(mRTLSwipeMessageBitmap, messageDrawnPortion, messagePos, mPaint);
                    } else {
                        c.drawBitmap(mRTLSwipeMessageBitmap,
                                     itemView.getRight() - messageMargin - messageWidth,
                                     (float) (itemView.getTop() + 0.5 * (height - messageHeight)),
                                     mPaint);
                    }
                }
            }
        }
    }

    @ColorInt
    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(@ColorInt int borderColor) {
        mBorderColor = borderColor;
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        mBorderWidth = borderWidth;
    }

    public boolean isHasBorder() {
        return mHasBorder;
    }

    public void setHasBorder(boolean hasBorder) {
        mHasBorder = hasBorder;
    }

    public boolean isAllowUndo() {
        return mAllowUndo;
    }

    public void setAllowUndo(boolean allowUndo) {
        mAllowUndo = allowUndo;
    }

    @ColorInt
    public int getLTRSwipeBackground() {
        return mLTRSwipeBackground;
    }

    public void setLTRSwipeBackground(@ColorInt int LTRSwipeBackground) {
        mLTRSwipeBackground = LTRSwipeBackground;
    }

    @ColorInt
    public int getRTLSwipeBackground() {
        return mRTLSwipeBackground;
    }

    public void setRTLSwipeBackground(@ColorInt int RTLSwipeBackground) {
        mRTLSwipeBackground = RTLSwipeBackground;
    }

    public float getSwipeIconHeight() {
        return mSwipeIconHeight;
    }

    public void setSwipeIconHeight(int swipeIconHeight) {
        mSwipeIconHeight = swipeIconHeight;
    }

    public float getSwipeIconWidth() {
        return mSwipeIconWidth;
    }

    public void setSwipeIconWidth(int swipeIconWidth) {
        mSwipeIconWidth = swipeIconWidth;
    }

    public float getLTRSwipeIconMargin() {
        return mLTRSwipeIconMargin;
    }

    public void setLTRSwipeIconMargin(float LTRSwipeIconMargin) {
        mLTRSwipeIconMargin = LTRSwipeIconMargin;
    }

    public float getRTLSwipeIconMargin() {
        return mRTLSwipeIconMargin;
    }

    public void setRTLSwipeIconMargin(float RTLSwipeIconMargin) {
        mRTLSwipeIconMargin = RTLSwipeIconMargin;
    }

    public String getLTRSnackBarMessage() {
        return mLTRSnackBarMessage;
    }

    public void setLTRSnackBarMessage(String LTRSnackBarMessage) {
        mLTRSnackBarMessage = LTRSnackBarMessage;
    }

    public String getRTLSnackBarMessage() {
        return mRTLSnackBarMessage;
    }

    public void setRTLSnackBarMessage(String RTLSnackBarMessage) {
        mRTLSnackBarMessage = RTLSnackBarMessage;
    }

    @ColorInt
    public int getLTRSnackBarMessageColor() {
        return mLTRSnackBarMessageColor;
    }

    public void setLTRSnackBarMessageColor(@ColorInt int LTRSnackBarMessageColor) {
        mLTRSnackBarMessageColor = LTRSnackBarMessageColor;
    }

    @ColorInt
    public int getRTLSnackBarMessageColor() {
        return mRTLSnackBarMessageColor;
    }

    public void setRTLSnackBarMessageColor(@ColorInt int RTLSnackBarMessageColor) {
        mRTLSnackBarMessageColor = RTLSnackBarMessageColor;
    }

    @DrawableRes
    public int getLTRSwipeIconRes() {
        return mLTRSwipeIconRes;
    }

    public void setLTRSwipeIconRes(@DrawableRes int LTRSwipeIconRes) {
        mLTRSwipeIconRes = LTRSwipeIconRes;
    }

    @DrawableRes
    public int getRTLSwipeIconRes() {
        return mRTLSwipeIconRes;
    }

    public void setRTLSwipeIconRes(@DrawableRes int RTLSwipeIconRes) {
        mRTLSwipeIconRes = RTLSwipeIconRes;
    }

    @ColorInt
    public int getLTRSwipeIconColor() {
        return mLTRSwipeIconColor;
    }

    public void setLTRSwipeIconColor(@ColorInt int LTRSwipeIconColor) {
        mLTRSwipeIconColor = LTRSwipeIconColor;
    }

    @ColorInt
    public int getRTLSwipeIconColor() {
        return mRTLSwipeIconColor;
    }

    public void setRTLSwipeIconColor(@ColorInt int RTLSwipeIconColor) {
        mRTLSwipeIconColor = RTLSwipeIconColor;
    }

    public Bitmap getLTRSwipeIcon() {
        return mLTRSwipeIcon;
    }

    public void setLTRSwipeIcon(Bitmap LTRSwipeIcon) {
        mLTRSwipeIcon = LTRSwipeIcon;
    }

    public Bitmap getRTLSwipeIcon() {
        return mRTLSwipeIcon;
    }

    public void setRTLSwipeIcon(Bitmap RTLSwipeIcon) {
        mRTLSwipeIcon = RTLSwipeIcon;
    }

    public float getLTRSwipeIconAndMessageGap() {
        return mLTRSwipeIconAndMessageGap;
    }

    public void setLTRSwipeIconAndMessageGap(float LTRSwipeIconAndMessageGap) {
        mLTRSwipeIconAndMessageGap = LTRSwipeIconAndMessageGap;
    }

    public float getRTLSwipeIconAndMessageGap() {
        return mRTLSwipeIconAndMessageGap;
    }

    public void setRTLSwipeIconAndMessageGap(float RTLSwipeIconAndMessageGap) {
        mRTLSwipeIconAndMessageGap = RTLSwipeIconAndMessageGap;
    }

    public int getLTRSnackBarBackground() {
        return mLTRSnackBarBackground;
    }

    public void setLTRSnackBarBackground(int LTRSnackBarBackground) {
        mLTRSnackBarBackground = LTRSnackBarBackground;
    }

    public int getRTLSnackBarBackground() {
        return mRTLSnackBarBackground;
    }

    public void setRTLSnackBarBackground(int RTLSnackBarBackground) {
        mRTLSnackBarBackground = RTLSnackBarBackground;
    }

    public String getLTRUndoActionText() {
        return mLTRUndoActionText;
    }

    public void setLTRUndoActionText(String LTRUndoActionText) {
        mLTRUndoActionText = LTRUndoActionText;
    }

    public String getRTLUndoActionText() {
        return mRTLUndoActionText;
    }

    public void setRTLUndoActionText(String RTLUndoActionText) {
        mRTLUndoActionText = RTLUndoActionText;
    }

    public int getLTRUndoActionTextColor() {
        return mLTRUndoActionTextColor;
    }

    public void setLTRUndoActionTextColor(int LTRUndoActionTextColor) {
        mLTRUndoActionTextColor = LTRUndoActionTextColor;
    }

    public int getRTLUndoActionTextColor() {
        return mRTLUndoActionTextColor;
    }

    public void setRTLUndoActionTextColor(int RTLUndoActionTextColor) {
        mRTLUndoActionTextColor = RTLUndoActionTextColor;
    }

    @Override
    public String getSnackBarMessage(int direction) {
        if (direction == ItemTouchHelper.RIGHT) {
            return mLTRSnackBarMessage;
        } else {
            return mRTLSnackBarMessage;
        }
    }

    @Override
    public String getUndoActionText(int direction) {
        if (direction == ItemTouchHelper.RIGHT) {
            return mLTRUndoActionText;
        } else {
            return mRTLUndoActionText;
        }
    }

    @Override
    public boolean isUndoEnabled() {
        return isAllowUndo();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    @ColorInt
    public int getSnackBarBackgroundColor(int direction) {
        if (direction == ItemTouchHelper.RIGHT) {
            return mLTRSnackBarBackground;
        } else {
            return mRTLSnackBarBackground;
        }
    }

    @Override
    public int getSnackBarMessageColor(int direction) {
        if (direction == ItemTouchHelper.RIGHT) {
            return mLTRSnackBarMessageColor;
        } else {
            return mRTLSnackBarMessageColor;
        }
    }

    @Override
    @ColorInt
    public int getUndoActionTextColor(int direction) {
        if (direction == ItemTouchHelper.RIGHT) {
            return mLTRUndoActionTextColor;
        } else {
            return mRTLUndoActionTextColor;
        }
    }

    /**
     * Get a {@link SwipeMessageBuilder} to customise swipe messages
     *
     * @return  an SwipeMessageBuilder instance
     *
     */
    public SwipeMessageBuilder getSwipeMessageBuilder() {
        return new SwipeMessageBuilder();
    }

    public class SwipeMessageBuilder {
        private static final String TAG = "SwipeMessageBuilder";

        public static final int LTR = 1;
        public static final int RTL = 2;
        public static final int BOTH = 3;
        public static final String DEFAULT_FONT = "default_font";

        private String mMessage;
        private String mFontPath;
        private Typeface mTypeface;
        private boolean mBold;
        private int mSwipeDirection;
        private float mTextSize;
        @ColorInt
        private int mTextColor;

        /**
         * Set message content
         *
         * @param message
         *         message content
         *
         * @return SwipeMessageBuilder instance
         */
        public SwipeMessageBuilder withMessage(String message) {
            mMessage = message;
            return this;
        }

        /**
         * Set path of the font used for displaying the message
         *
         * @param fontPath
         *         font path
         *
         * @return SwipeMessageBuilder instance
         */
        public SwipeMessageBuilder withFontPath(String fontPath) {
            mFontPath = fontPath;
            return this;
        }

        /**
         * Only use this method if {@link SwipeMessageBuilder#withFontPath(String)} is not used
         *
         * @param typeface
         *         Type face of the message
         *
         * @return SwipeMessageBuilder instance
         */
        public SwipeMessageBuilder withTypeface(Typeface typeface) {
            mTypeface = typeface;
            return this;
        }

        /**
         * Only use this method if {@link SwipeMessageBuilder#withFontPath(String)} and {@link
         * SwipeMessageBuilder#withTypeface(Typeface)} are not used
         *
         * @param bold
         *         whether the text should be bold
         *
         * @return SwipeMessageBuilder instance
         */
        public SwipeMessageBuilder withBoldTextAndDefaultFont(boolean bold) {
            mBold = bold;
            return this;
        }

        public SwipeMessageBuilder withSwipeDirection(int swipeDirection) {
            mSwipeDirection = swipeDirection;
            return this;
        }

        public SwipeMessageBuilder withTextSize(float textSize) {
            mTextSize = textSize;
            return this;
        }

        public SwipeMessageBuilder withTextColor(int textColor) {
            mTextColor = textColor;
            return this;
        }

        public void build() {
            switch (mSwipeDirection) {
            case LTR:
                setupLTRMessage();
                break;
            case RTL:
                setupRTLMessage();
                break;
            case BOTH:
                setupLTRMessage();
                setupRTLMessage();
                break;
            default:
                Log.e(TAG, "Swipe direction is invalid. Should be LTR or RTL.");
                break;
            }
        }

        private void setupLTRMessage() {
            setupTextSizeAndFont();
            if(mMessage != null) {
                mLTRSwipeMessage = mMessage;
            }

            if(mLTRSwipeMessage == null) {
                Log.e(TAG, "LTR Swipe message is not set.");
                return;
            }

            if(mTextColor != 0) {
                mLTRSwipeMessageColor = mTextColor;
            }
            createLTRSwipeMessageBitmap();
        }

        private void setupRTLMessage() {
            setupTextSizeAndFont();
            if(mMessage != null) {
                mRTLSwipeMessage = mMessage;
            }

            if(mRTLSwipeMessage == null) {
                Log.e(TAG, "RTL Swipe message is not set.");
                return;
            }

            if(mTextColor != 0) {
                mRTLSwipeMessageColor = mTextColor;
            }
            createRTLSwipeMessageBitmap();
        }

        private void setupTextSizeAndFont() {
            if(mTextSize > 0) {
                mSwipeMessageTextSize = mTextSize;
            }

            if(mTypeface != null) {
                mSwipeMessageFont = mTypeface;
            } else if(mFontPath != null) {
                if(mFontPath.equals(DEFAULT_FONT)) {
                    mSwipeMessageFont = Typeface.create(Typeface.DEFAULT, mBold ? Typeface.BOLD : Typeface.NORMAL);
                } else {
                    mSwipeMessageFont = Typeface.createFromAsset(getContext().getAssets(), mFontPath);
                }
            }
        }
    }

    private void createLTRSwipeMessageBitmap() {
        if(mLTRSwipeMessage != null) {
            mLTRSwipeMessageBitmap = ResourceUtils
                    .createBitmapFromText(mLTRSwipeMessage, mSwipeMessageTextSize,
                                          mLTRSwipeMessageColor, mSwipeMessageFont);
        }
    }

    private void createRTLSwipeMessageBitmap() {
        if(mRTLSwipeMessage != null) {
            mRTLSwipeMessageBitmap = ResourceUtils
                    .createBitmapFromText(mRTLSwipeMessage, mSwipeMessageTextSize,
                                          mRTLSwipeMessageColor, mSwipeMessageFont);
        }
    }
}
