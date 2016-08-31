package com.ankitgusai.IntervalSeekbar.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.ankitgusai.IntervalSeekbar.R;
import com.ankitgusai.IntervalSeekbar.databinding.SeekbarWithIntervalsLabelsBinding;

import java.util.ArrayList;

/**
 * This view renders a seekbar as its first child which is displayed from top.
 * below the seekbar are custom views drawn at equal distance. numbers of custom views are dynamically set using {@link #setItems(ArrayList)}.
 * <p/>
 * <p> The interval can be set using {@link #setInterval(int)}
 * <p> The interval can be retrived using {@link #getInterval()}
 * <p> If a listener is needed when interval changes, use {@link #setIntervalChangeListener(OnIntervalChangeListener)}
 * <p> you can also set SeekBar line color and thumb using {@link #setSeekBarLineColor(int)} and {@link #setSeekBarThumb(Drawable)} respectively</p>
 * <p/>
 * <p>Created by ankitgusai on 17-06-2016.
 */
//FIXME there is a major flaw in this view group. padding/margin given to parent is not handled, or rather i do not know how exactly to do that. this is still R&D.
public class IntervalSeekBar extends ViewGroup {
    private static final boolean PRINT_LOG = false;
    private static final String LOG_TAG = "Interval-SeekBar";

    private SeekBar seekBar;
    private ArrayList<Item> items = new ArrayList<>();
    private OnIntervalChangeListener intervalChangeListener;

    public IntervalSeekBar(Context context) {
        super(context);
        init();
    }

    public IntervalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IntervalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface OnIntervalChangeListener {
        void onIntervalChanged(int pos);
    }

    public void setIntervalChangeListener(OnIntervalChangeListener intervalChangeListener) {
        this.intervalChangeListener = intervalChangeListener;
    }

    /**
     * Init stuff. clear parent for any stale views.
     * add primary seekbar.
     */
    private void init() {
        removeAllViews();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setSeekBar();
        addView(seekBar, layoutParams);

        //the setItems is not behaving as expected due to the data binding. is isn't rendering anything with due to binding error. so i have set a semi transparent color just for indication.
        if (isInEditMode()) {
            //setBackgroundColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getContext(), android.R.color.black), 64));
            setItems(getDummyItems());
        }
        requestLayout();
    }

    /**
     * @return Current pos of seekbar. the number returned here corresponds to array of item inflated.
     */
    public int getInterval() {
        //printExperimental();
        double interval = ((seekBar.getProgress() * 1.0f) / (seekBar.getMax() / (items.size() - 1)));
        printLog("Interval ->" + items);
        return (int) interval;
    }

    /**
     * Values printed for testing
     */
    private void printExperimental() {
        printLog("progress " + seekBar.getProgress());
        /*printLog("Item size " + items.size());
        printLog("Each section should be " + ((1000.0f) / items.size()));*/

        int interval = (int) ((seekBar.getProgress() * (items.size() - 1) * 1.0f) / (seekBar.getMax()));
        int interval2 = (int) ((seekBar.getProgress() * (items.size()) * 1.0f) / (seekBar.getMax()));
        int interval3 = (int) Math.ceil(((seekBar.getProgress() * 1.0f) / seekBar.getMax()) * (items.size() - 1));
        int interval4 = (int) Math.round(((seekBar.getProgress() * 1.0f) / seekBar.getMax()) * (items.size() - 1));
        double interval5 = (seekBar.getProgress() * 1.0f) / (seekBar.getMax() / (items.size() - 1));

        printLog("interval with -1 " + interval);
        printLog("interval without  -1 " + interval2);
        printLog("interval with ceil " + interval3);
        printLog("interval with round " + interval4);
        printLog(" a " + interval5);


    }

    /**
     * Set position of SeekBar. this must within the range of the Item array size.
     */
    public void setInterval(int interval) {
        //int itemSize = (items.size() % 2 == 0) ? items.size() : items.size() - 1;
        int stepDistance = (int) ((seekBar.getMax() * 1.0f) / (items.size() - 1));
        int progress = interval * stepDistance;
        seekBar.setProgress(progress);
    }

    private void setSeekBar() {
        seekBar = new SeekBar(getContext());
        //we will always set this to 1000 for smooth transaction between point to point.( higher value will increase precision)
        seekBar.setMax(1000);
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(listener);
    }

    private SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.progress = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            /*
            * The below logic is for sticky seekbar. seekbar needs to automatically move to nearest valid interval.
              * This way is used for smooth SeekBar movements.
            * */

            int intervals = 1000 / (items.size() - 1);

            int delta = progress % intervals;

            if (delta > intervals / 2) {
                progress = progress + (intervals - delta);

            } else {
                progress = progress - delta;
            }
            printLog("zapping to ->" + progress);
            seekBar.setProgress(progress);

            if (intervalChangeListener != null) {
                intervalChangeListener.onIntervalChanged(getInterval());
            }
        }
    };


    /**
     * This draws a color layer on top of seekbar line.
     * Similar effect can be achieved by setting tint of seekbar which is available from API 21 onwards.
     */
    public void setSeekBarLineColor(int resolvedColor) {
        if (seekBar != null) {
            PorterDuffColorFilter filter = new PorterDuffColorFilter(resolvedColor, PorterDuff.Mode.SRC_IN);
            seekBar.getProgressDrawable().setColorFilter(filter);
            seekBar.getThumb().setColorFilter(filter);
        }
    }

    public void setSeekBarThumb(Drawable thumb) {
        seekBar.setThumb(thumb);
    }

    /**
     * sets Seekbar steps. creates custom view and adds them to this view as direct child.
     */
    public void setItems(ArrayList<Item> items) {
        this.items = new ArrayList<>(items);
        //addItemsCompat();
        clearIntervals();
        addItems();
        requestLayout();
    }

    private void addItems() {
        LayoutParams layoutParams;
        SeekbarWithIntervalsLabelsBinding mBinding;
        for (Item item : items) {
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(getContext()),
                    R.layout.seekbar_with_intervals_labels, null, false);

            if (item.getShowIndicatorLine() != 0) {
                mBinding.seekBarIntervalVerticalLine.setVisibility(VISIBLE);
            } else {
                mBinding.seekBarIntervalVerticalLine.setVisibility(GONE);
            }

            if (item.getIcn() != 0) {
                mBinding.seekBarIntervalThumb.setImageResource(item.getIcn());
            } else {
                mBinding.seekBarIntervalThumb.setVisibility(GONE);
            }

            if (!TextUtils.isEmpty(item.getText())) {
                mBinding.seekBarIntervalText.setText(item.getText());
            } else {
                mBinding.seekBarIntervalText.setVisibility(GONE);
            }

            addView(mBinding.getRoot(), layoutParams);
        }
    }

    private void clearIntervals() {
        int childCount = getChildCount();
        for (int i = (childCount - 1); i > 0; i--) {
            removeViewAt(i);
        }
    }

   /* private void addItemsCompat() {
        LayoutParams layoutParams;
        View view;
        int i = 0;
        for (Item item : items) {
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            view = LayoutInflater.from(getContext()).inflate(R.layout.seekbar_with_intervals_labels2, null, false);

            ((ImageView) view.findViewById(R.id.seek_bar_interval_thumb)).setImageResource(item.getIcn());
            ((TextView) view.findViewById(R.id.seek_bar_interval_text)).setText(item.getText());

            ((LinearLayout) view.findViewById(R.id.seek_bar_interval_thumb_parent)).setGravity(Gravity.CENTER);
            if (i == 0) {
                ((LinearLayout) view.findViewById(R.id.seek_bar_interval_thumb_parent)).setGravity(Gravity.START);
            }

            if (i == items.size() - 1) {
                ((LinearLayout) view.findViewById(R.id.seek_bar_interval_thumb_parent)).setGravity(Gravity.END);

            }

            i++;
            addView(view, layoutParams);
        }
    }*/


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (items == null || items.isEmpty()) return;
        View seekBar = getChildAt(0);

        int seekBarWidth = seekBar.getMeasuredWidth();
        int seekBarHeight = seekBar.getMeasuredHeight();

        //Sekkbar layout
        seekBar.layout(l + getPaddingLeft(), t + getPaddingTop(), seekBarWidth, seekBarHeight);

        int leftStart = l + seekBar.getPaddingLeft();

        seekBarWidth = seekBarWidth - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
        int sectionCount = getChildCount() - 2;
        int sectionWidth = seekBarWidth / (sectionCount);

        printLog("section Width" + sectionWidth);


        int stepStart = 0;
        int sectionStart = 0;
        int actuallStart;
        SeekbarWithIntervalsLabelsBinding mBinding;


        //For first and last child we need to make sure that vertical line
        // stays aligned to the seekbar line start and end.
        // with text below the vertical line should go up to width of the seekbar view.


        /*
        *
        * |-------Seekbar view width-----------------|
        *
        * |   |-- seekbar line width  ------------|  |
        * |   -------------------------------------  |
        * |                                          |
        *
        *
        *
        * */

        for (int i = 1; i < getChildCount(); i++) {
            View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            printLog("Layout params " + lp.leftMargin + "  " + lp.rightMargin + "  " + lp.topMargin + "  " + lp.bottomMargin);

            //for i = 0 we have already set - 15 padding
            stepStart = leftStart + sectionStart;

            if (i == 1) {
                if ((child.getMeasuredWidth() / 2) > seekBar.getPaddingLeft()) {
                    actuallStart = stepStart - seekBar.getPaddingLeft();

                    mBinding = DataBindingUtil.getBinding(child);


                    //FIXME apparently if we add icon inner view only them we are getting correct padding value.
                    boolean considerInnerVIewPadding = mBinding.seekBarIntervalThumb.getVisibility() == VISIBLE;

                    int leftPadding = seekBar.getPaddingLeft() - (considerInnerVIewPadding ? (mBinding.seekBarIntervalThumbInnerParent.getMeasuredWidth() / 2) : 0);
                    mBinding.seekBarIntervalThumbInnerParent.setPadding(leftPadding, mBinding.seekBarIntervalThumbInnerParent.getPaddingTop(), mBinding.seekBarIntervalThumbInnerParent.getPaddingRight(), mBinding.seekBarIntervalThumbInnerParent.getPaddingBottom());
                    mBinding.seekBarIntervalThumbParent.setGravity(Gravity.START);

                } else {
                    actuallStart = stepStart - (child.getMeasuredWidth() / 2);
                }

            } else if (i == (getChildCount() - 1)) {
                if ((child.getMeasuredWidth() / 2) > seekBar.getPaddingRight()) {
                    actuallStart = stepStart + seekBar.getPaddingLeft() - child.getMeasuredWidth();
                    mBinding = DataBindingUtil.getBinding(child);

                    //FIXME apparently if we add icon inner view only them we are getting correct padding value.
                    boolean considerInnerVIewPadding = mBinding.seekBarIntervalThumb.getVisibility() == VISIBLE;
                    int rightPadding = seekBar.getPaddingRight() - (considerInnerVIewPadding ? (mBinding.seekBarIntervalThumbInnerParent.getMeasuredWidth() / 2) : 0);
                    mBinding.seekBarIntervalThumbInnerParent.setPadding(mBinding.seekBarIntervalThumbInnerParent.getPaddingLeft(), mBinding.seekBarIntervalThumbInnerParent.getPaddingTop(), rightPadding, mBinding.seekBarIntervalThumbInnerParent.getPaddingBottom());
                    mBinding.seekBarIntervalThumbParent.setGravity(Gravity.END);
                } else {
                    actuallStart = stepStart - (child.getMeasuredWidth() / 2);
                }

            } else {
                actuallStart = stepStart - (child.getMeasuredWidth() / 2);
            }
            /*Log.i("Inter", "sectionStart --" + sectionStart);
            Log.i("Inter", "Child size " + child.getMeasuredWidth() / 2 + " so Starting from --" + stepStart);*/

            child.layout(actuallStart, seekBarHeight, actuallStart + child.getMeasuredWidth(), seekBarHeight + child.getMeasuredHeight());
            sectionStart += sectionWidth;
        }

    }


    /*
     *SeekBar and its interval views are measured here
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;
        int seekBarHeight = 0;
        int seekBarWidth = 0;

        final View seekBar = getChildAt(0);

        measureChild(seekBar, widthMeasureSpec, heightMeasureSpec);
        seekBarWidth = Math.max(maxWidth, seekBar.getMeasuredWidth());
        seekBarHeight = Math.max(maxHeight,
                seekBar.getMeasuredHeight());
        childState = combineMeasuredStates(childState, seekBar.getMeasuredState());


        for (int i = 1; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                maxWidth = Math.max(maxWidth,
                        child.getMeasuredWidth()/* + lp.leftMargin + lp.rightMargin*/);
                maxHeight = Math.max(maxHeight,
                        child.getMeasuredHeight()/* + lp.topMargin + lp.bottomMargin*/);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }
        maxHeight = Math.max(maxHeight + seekBarHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(seekBarWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public static class Item {
        private String text;
        private int icn;
        private int showIndicatorLine;


        public Item(String text, int icn) {
            this.text = text;
            this.icn = icn;
            this.showIndicatorLine = 1;
        }

        public Item(String text, int icn, int showIndicatorLine) {
            this.text = text;
            this.icn = icn;
            this.showIndicatorLine = showIndicatorLine;
        }

        public int getShowIndicatorLine() {
            return showIndicatorLine;
        }

        public void setShowIndicatorLine(int showIndicatorLine) {
            this.showIndicatorLine = showIndicatorLine;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getIcn() {
            return icn;
        }

        public void setIcn(int icn) {
            this.icn = icn;
        }
    }

    private ArrayList<Item> getDummyItems() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("such", R.drawable.dummy_thumb, 1));
        items.add(new Item("boy", 0));
        items.add(new Item("much happy", R.drawable.dummy_thumb, 1));
        items.add(new Item("wow", 0));

        return items;
    }

    //EXPERIMENTAL. No real usage
    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

    }

    private static void printLog(String msg) {
        if (PRINT_LOG) {
            Log.d(LOG_TAG, msg);
        }
    }
}
