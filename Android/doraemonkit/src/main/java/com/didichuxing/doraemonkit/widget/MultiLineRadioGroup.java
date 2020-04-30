package com.didichuxing.doraemonkit.widget;

import android.widget.RadioGroup;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.didichuxing.doraemonkit.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-15-14:46
 * 描    述：多方的RadioGroup
 * 修订历史：
 * ================================================
 */
public class MultiLineRadioGroup extends RadioGroup {

    private static final String XML_DEFAULT_BUTTON_PREFIX_INDEX = "index:";

    private static final String XML_DEFAULT_BUTTON_PREFIX_TEXT = "text:";

    private static final int DEF_VAL_MAX_IN_ROW = 0;

    // for generating ids to APIs lower than 17
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    // the listener for callbacks to invoke when radio button checked state changes
    private OnCheckedChangeListener mOnCheckedChangeListener;

    // the listener for callbacks to invoke when radio button is clicked
    private OnClickListener mOnClickListener;

    // holds the maximum number of radio buttons that should be in a row
    private int mMaxInRow;

    // all buttons are stored in table layout
    private TableLayout mTableLayout;

    // list to store all the buttons
    private List<RadioButton> mRadioButtons;

    // the checked button
    private RadioButton mCheckedButton;

    /**
     * Creates a new MultiLineRadioGroup for the given context.
     *
     * @param context the application environment
     */
    public MultiLineRadioGroup(Context context) {
        super(context);
        init(null);
    }

    /**
     * Creates a new MultiLineRadioGroup for the given context
     * and with the specified set attributes.
     *
     * @param context the application environment
     * @param attrs   a collection of attributes
     */
    public MultiLineRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    // initializes the layout
    private void init(AttributeSet attrs) {
        mRadioButtons = new ArrayList<>();

        mTableLayout = getTableLayout();
        addView(mTableLayout);

        if (attrs != null)
            initAttrs(attrs);
    }

    // initializes the layout with the specified attributes
    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.dk_multi_line_radio_group,
                0, 0);
        try {
            // gets and sets the max in row.
            setMaxInRow(typedArray.getInt(R.styleable.dk_multi_line_radio_group_max_in_row,
                    DEF_VAL_MAX_IN_ROW));

            // gets and adds the starting buttons
            CharSequence[] radioButtonStrings = typedArray.getTextArray(
                    R.styleable.dk_multi_line_radio_group_radio_buttons);
            addButtons(radioButtonStrings);

            // gets the default button and checks it if presents.
            String string = typedArray.getString(R.styleable.dk_multi_line_radio_group_default_button);
            if (string != null)
                setDefaultButton(string);

        } finally {
            typedArray.recycle();
        }
    }

    // checks the default button based on the passed string
    private void setDefaultButton(String string) {
        final int START_OF_INDEX = XML_DEFAULT_BUTTON_PREFIX_INDEX.length();
        final int START_OF_TEXT = XML_DEFAULT_BUTTON_PREFIX_TEXT.length();

        // the text of the button to check
        String buttonToCheck;

        if (string.startsWith(XML_DEFAULT_BUTTON_PREFIX_INDEX)) {
            String indexString = string.substring(START_OF_INDEX);
            int index = Integer.parseInt(indexString);
            if (index < 0 || index >= mRadioButtons.size())
                throw new IllegalArgumentException("index must be between 0 to getRadioButtonCount() - 1 [" +
                        (getRadioButtonCount() - 1) + "]: " + index);
            buttonToCheck = mRadioButtons.get(index).getText().toString();

        } else if (string.startsWith(XML_DEFAULT_BUTTON_PREFIX_TEXT)) {
            buttonToCheck = string.substring(START_OF_TEXT);

        } else { // when there is no prefix assumes the string is the text of the button
            buttonToCheck = string;
        }

        check(buttonToCheck);
    }

    /**
     * Returns the table layout to set for this layout.
     *
     * @return the table layout
     */
    protected TableLayout getTableLayout() {
        return (TableLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.dk_radio_table_layout, this, false);
    }

    /**
     * Returns the table row to set in this layout.
     *
     * @return the table row
     */
    protected TableRow getTableRow() {
        return (TableRow) LayoutInflater.from(getContext())
                .inflate(R.layout.dk_radio_table_row, mTableLayout, false);
    }

    /**
     * Returns the default radio button to set in this layout.
     * <p>
     * This radio button will be used when radio buttons are added
     * with the methods addButtons() that accept string varargs.
     *
     * @return the radio button
     */
    protected RadioButton getRadioButton() {
        return (RadioButton) LayoutInflater.from(getContext())
                .inflate(R.layout.dk_radio_button, null);
    }

    /**
     * Register a callback to be invoked when a radio button checked state changes.
     * The only time the listener is passed a button where isChecked is false will be when
     * you call clearCheck.
     *
     * @param onCheckedChangeListener the listener to attach
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeListener = onCheckedChangeListener;
    }

    /**
     * Register a callback to be invoked when a radio button is clicked.
     *
     * @param listener the listener to attach
     */
    public void setOnClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    /**
     * Returns the maximum radio buttons in a row, 0 for all in one line.
     *
     * @return the maximum radio buttons in a row, 0 for all in one line
     */
    public int getMaxInRow() {
        return mMaxInRow;
    }

    /**
     * Sets the maximum radio buttons in a row, 0 for all in one line
     * and arranges the layout accordingly.
     *
     * @param maxInRow the maximum radio buttons in a row
     * @throws IllegalArgumentException if maxInRow is negative
     */
    public void setMaxInRow(int maxInRow) {
        if (maxInRow < 0)
            throw new IllegalArgumentException("maxInRow must not be negative: " + maxInRow);
        this.mMaxInRow = maxInRow;
        arrangeButtons();
    }

    /**
     * Adds a view to the layout
     * <p>
     * Consider using addButtons() instead
     *
     * @param child the view to add
     */
    @Override
    public void addView(View child) {
        addView(child, -1, child.getLayoutParams());
    }

    /**
     * Adds a view to the layout in the specified index
     * <p>
     * Consider using addButtons() instead
     *
     * @param child the view to add
     * @param index the index in which to insert the view
     */
    @Override
    public void addView(View child, int index) {
        addView(child, index, child.getLayoutParams());
    }

    /**
     * Adds a view to the layout with the specified width and height.
     * Note that for radio buttons the width and the height are ignored.
     * <p>
     * Consider using addButtons() instead
     *
     * @param child  the view to add
     * @param width  the width of the view
     * @param height the height of the view
     */
    @Override
    public void addView(View child, int width, int height) {
        addView(child, -1, new LinearLayout.LayoutParams(width, height));
    }

    /**
     * Adds a view to the layout with the specified layout params.
     * Note that for radio buttons the width and the height are ignored.
     * <p>
     * Consider using addButtons() instead
     *
     * @param child  the view to add
     * @param params the layout params of the view
     */
    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        addView(child, -1, params);
    }

    /**
     * Adds a view to the layout in the specified index
     * with the specified layout params.
     * Note that for radio buttons the width and the height are ignored.
     * <p>
     * * Consider using addButtons() instead
     *
     * @param child  the view to add
     * @param index  the index in which to insert the view
     * @param params the layout params of the view
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof RadioButton) {
            addButtons(index, ((RadioButton) child));

        } else {
            // if params are null sets them
            if (params == null) {
                params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }

            super.addView(child, index, params);
        }
    }

    /**
     * Adds radio buttons to the layout based on the texts in the radioButtons array.
     * Adds them in the last index.
     * If radioButtons is null does nothing.
     *
     * @param radioButtons the texts of the buttons to add
     */
    public void addButtons(CharSequence... radioButtons) {
        addButtons(-1, radioButtons);
    }

    /**
     * Adds radio buttons to the layout based on the texts in the radioButtons array.
     * Adds them in the specified index, -1 for the last index.
     * If radioButtons is null does nothing.
     *
     * @param index        the index in which to insert the radio buttons
     * @param radioButtons the texts of the buttons to add
     * @throws IllegalArgumentException if index is less than -1 or greater than the number of radio buttons
     */
    public void addButtons(int index, CharSequence... radioButtons) {
        if (index < -1 || index > mRadioButtons.size())
            throw new IllegalArgumentException("index must be between -1 to getRadioButtonCount() [" +
                    getRadioButtonCount() + "]: " + index);

        if (radioButtons == null)
            return;

        // creates radio button array
        RadioButton[] buttons = new RadioButton[radioButtons.length];
        for (int i = 0; i < buttons.length; i++) {
            RadioButton radioButton = getRadioButton();
            radioButton.setText(radioButtons[i]);
            radioButton.setId(generateId());
            buttons[i] = radioButton;
        }

        addButtons(index, buttons);
    }

    // generates an id
    private int generateId() {
        // for API 17 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.generateViewId();

            // for API lower than 17
        } else {

            while (true) {
                final int result = sNextGeneratedId.get();

                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.

                if (sNextGeneratedId.compareAndSet(result, newValue))
                    return result;
            }

        }
    }

    /**
     * Adds radio buttons to the layout.
     * Adds them in the last index.
     * If radioButtons is null does nothing.
     *
     * @param radioButtons the texts of the buttons to add
     */
    public void addButtons(RadioButton... radioButtons) {
        addButtons(-1, radioButtons);
    }

    /**
     * Adds radio buttons to the layout.
     * Adds them in the specified index, -1 for the last index.
     * If radioButtons is null does nothing.
     *
     * @param index        the index in which to insert the radio buttons
     * @param radioButtons the texts of the buttons to add
     * @throws IllegalArgumentException if index is less than -1 or greater than the number of radio buttons
     */
    public void addButtons(int index, RadioButton... radioButtons) {
        if (index < -1 || index > mRadioButtons.size())
            throw new IllegalArgumentException("index must be between -1 to getRadioButtonCount() [" +
                    getRadioButtonCount() + "]: " + index);

        if (radioButtons == null)
            return;

        int realIndex = (index != -1) ? index : mRadioButtons.size();

        // inits the buttons and adds them to the list
        for (RadioButton radioButton : radioButtons) {
            initRadioButton(radioButton);
            mRadioButtons.add(realIndex++, radioButton);
        }

        arrangeButtons();
    }

    // inits the radio button
    private void initRadioButton(RadioButton radioButton) {
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean didCheckStateChange = checkButton((RadioButton) v);
                if (didCheckStateChange && mOnCheckedChangeListener != null) {
                    mOnCheckedChangeListener.onCheckedChanged(MultiLineRadioGroup.this, mCheckedButton);
                }
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(MultiLineRadioGroup.this, mCheckedButton);
                }
            }
        });
    }

    /**
     * Removes a view from the layout.
     * <p>
     * Consider using removeButton().
     *
     * @param view the view to remove
     */
    @Override
    public void removeView(View view) {
        super.removeView(view);
    }

    /**
     * Removes a view from the layout in the specified index.
     * <p>
     * Consider using removeButton().
     *
     * @param index the index from which to remove the view
     */
    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
    }

    /**
     * Removes the specified range of views from the layout.
     * <p>
     * Consider using removeButtons().
     *
     * @param start the start index to remove
     * @param count the number of views to remove
     */
    @Override
    public void removeViews(int start, int count) {
        super.removeViews(start, count);
    }

    /**
     * Removes all the views from the layout.
     * <p>
     * Consider using removeAllButtons().
     */
    @Override
    public void removeAllViews() {
        super.removeAllViews();
    }

    /**
     * Removes a radio button from the layout.
     * If the radio button is null does nothing.
     *
     * @param radioButton the radio button to remove
     */
    public void removeButton(RadioButton radioButton) {
        if (radioButton == null)
            return;

        removeButton(radioButton.getText());
    }

    /**
     * Removes a radio button from the layout based on its text.
     * Removes the first occurrence.
     * If the text is null does nothing.
     *
     * @param text the text of the radio button to remove
     */
    public void removeButton(CharSequence text) {
        if (text == null)
            return;

        int index = -1;

        for (int i = 0, len = mRadioButtons.size(); i < len; i++) {
            // checks if the texts are equal
            if (mRadioButtons.get(i).getText().equals(text)) {
                index = i;
                break;
            }
        }

        // removes just if the index was found
        if (index != -1)
            removeButton(index);
    }

    /**
     * Removes the radio button in the specified index from the layout.
     *
     * @param index the index from which to remove the radio button
     * @throws IllegalArgumentException if index is less than 0
     *                                  or greater than the number of radio buttons - 1
     */
    public void removeButton(int index) {
        removeButtons(index, 1);
    }

    /**
     * Removes all the radio buttons in the specified range from the layout.
     * Count can be any non-negative number.
     *
     * @param start the start index to remove
     * @param count the number of radio buttons to remove
     * @throws IllegalArgumentException if index is less than 0
     *                                  or greater than the number of radio buttons - 1
     *                                  or count is negative
     */
    public void removeButtons(int start, int count) {
        if (count == 0) {
            return;
        }
        if (start < 0 || start >= mRadioButtons.size())
            throw new IllegalArgumentException("start index must be between 0 to getRadioButtonCount() - 1 [" +
                    (getRadioButtonCount() - 1) + "]: " + start);

        if (count < 0)
            throw new IllegalArgumentException("count must not be negative: " + count);

        int endIndex = start + count - 1;
        // if endIndex is not in the range of the radio buttons sets it to the last index
        if (endIndex >= mRadioButtons.size())
            endIndex = mRadioButtons.size() - 1;

        // iterates over the buttons to remove
        for (int i = endIndex; i >= start; i--) {
            RadioButton radiobutton = mRadioButtons.get(i);
            // if the button to remove is the checked button set mCheckedButton to null
            if (radiobutton == mCheckedButton)
                mCheckedButton = null;
            // removes the button from the list
            mRadioButtons.remove(i);
        }

        arrangeButtons();
    }

    /**
     * Removes all the radio buttons from the layout.
     */
    public void removeAllButtons() {
        removeButtons(0, mRadioButtons.size());
    }

    // arrange the buttons in the layout
    private void arrangeButtons() {
        // iterates over each button and puts it in the right place
        for (int i = 0, len = mRadioButtons.size(); i < len; i++) {
            RadioButton radioButtonToPlace = mRadioButtons.get(i);
            int rowToInsert = (mMaxInRow != 0) ? i / mMaxInRow : 0;
            int columnToInsert = (mMaxInRow != 0) ? i % mMaxInRow : i;
            // gets the row to insert. if there is no row create one
            TableRow tableRowToInsert = (mTableLayout.getChildCount() <= rowToInsert)
                    ? addTableRow() : (TableRow) mTableLayout.getChildAt(rowToInsert);
            int tableRowChildCount = tableRowToInsert.getChildCount();

            // if there is already a button in the position
            if (tableRowChildCount > columnToInsert) {
                RadioButton currentButton = (RadioButton) tableRowToInsert.getChildAt(columnToInsert);

                // insert the button just if the current button is different
                if (currentButton != radioButtonToPlace) {
                    // removes the current button
                    removeButtonFromParent(currentButton, tableRowToInsert);
                    // removes the button to place from its current position
                    removeButtonFromParent(radioButtonToPlace, (ViewGroup) radioButtonToPlace.getParent());
                    // adds the button to the right place
                    tableRowToInsert.addView(radioButtonToPlace, columnToInsert);
                }

                // if there isn't already a button in the position
            } else {
                // removes the button to place from its current position
                removeButtonFromParent(radioButtonToPlace, (ViewGroup) radioButtonToPlace.getParent());
                // adds the button to the right place
                tableRowToInsert.addView(radioButtonToPlace, columnToInsert);
            }
        }

        removeRedundancies();
    }

    // removes the redundant rows and radio buttons
    private void removeRedundancies() {
        // the number of rows to fit the buttons
        int rows;
        if (mRadioButtons.size() == 0)
            rows = 0;
        else if (mMaxInRow == 0)
            rows = 1;
        else
            rows = (mRadioButtons.size() - 1) / mMaxInRow + 1;

        int tableChildCount = mTableLayout.getChildCount();
        // if there are redundant rows remove them
        if (tableChildCount > rows)
            mTableLayout.removeViews(rows, tableChildCount - rows);

        tableChildCount = mTableLayout.getChildCount();
        int maxInRow = (mMaxInRow != 0) ? mMaxInRow : mRadioButtons.size();

        // iterates over the rows
        for (int i = 0; i < tableChildCount; i++) {
            TableRow tableRow = (TableRow) mTableLayout.getChildAt(i);
            int tableRowChildCount = tableRow.getChildCount();

            int startIndexToRemove;
            int count;

            // if it is the last row removes all redundancies after the last button in the list
            if (i == tableChildCount - 1) {
                startIndexToRemove = (mRadioButtons.size() - 1) % maxInRow + 1;
                count = tableRowChildCount - startIndexToRemove;

                // if it is not the last row removes all the buttons after maxInRow position
            } else {
                startIndexToRemove = maxInRow;
                count = tableRowChildCount - maxInRow;
            }

            if (count > 0)
                tableRow.removeViews(startIndexToRemove, count);
        }
    }

    // adds and returns a table row
    private TableRow addTableRow() {
        TableRow tableRow = getTableRow();
        mTableLayout.addView(tableRow);
        return tableRow;
    }

    // removes a radio button from a parent
    private void removeButtonFromParent(RadioButton radioButton, ViewGroup parent) {
        if (radioButton == null || parent == null)
            return;

        parent.removeView(radioButton);
    }

    /**
     * Returns the number of radio buttons.
     *
     * @return the number of radio buttons
     */
    public int getRadioButtonCount() {
        return mRadioButtons.size();
    }

    /**
     * Returns the radio button in the specified index.
     * If the index is out of range returns null.
     *
     * @param index the index of the radio button
     * @return the radio button
     */
    public RadioButton getRadioButtonAt(int index) {
        if (index < 0 || index >= mRadioButtons.size())
            return null;

        return mRadioButtons.get(index);
    }

    /**
     * Returns true if there is a button with the specified text, false otherwise.
     *
     * @param button the text to search
     * @return true if there is a button with the specified text, false otherwise
     */
    public boolean containsButton(String button) {
        for (RadioButton radioButton : mRadioButtons)
            if (radioButton.getText().equals(button))
                return true;

        return false;
    }

    /**
     * Checks the radio button with the specified id.
     * If the specified id is not found does nothing.
     *
     * @param id the radio button's id
     */
    @Override
    public void check(int id) {
        if (id <= 0)
            return;

        for (RadioButton radioButton : mRadioButtons) {
            if (radioButton.getId() == id) {
                if (checkButton(radioButton)) { // True if the button wasn't already checked.
                    if (mOnCheckedChangeListener != null) {
                        mOnCheckedChangeListener.onCheckedChanged(
                                MultiLineRadioGroup.this, radioButton);
                    }
                }
                return;
            }
        }
    }

    /**
     * Checks the radio button with the specified text.
     * If there is more than one radio button associated with this text
     * checks the first radio button.
     * If the specified text is not found does nothing.
     *
     * @param text the radio button's text
     */
    public void check(CharSequence text) {
        if (text == null)
            return;

        for (RadioButton radioButton : mRadioButtons) {
            if (radioButton.getText().equals(text)) {
                if (checkButton(radioButton)) { // True if the button wasn't already checked.
                    if (mOnCheckedChangeListener != null) {
                        mOnCheckedChangeListener.onCheckedChanged(
                                MultiLineRadioGroup.this, radioButton);
                    }
                }
                return;
            }
        }
    }

    /**
     * Checks the radio button at the specified index.
     * If the specified index is invalid does nothing.
     *
     * @param index the radio button's index
     */
    public void checkAt(int index) {
        if (index < 0 || index >= mRadioButtons.size())
            return;

        if (checkButton(mRadioButtons.get(index))) { // True if the button wasn't already checked.
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(
                        MultiLineRadioGroup.this, mRadioButtons.get(index));
            }
        }
    }

    /**
     * Checks and switches the button with mCheckedButton.
     * Returns true if check state changed, false otherwise.
     *
     * @param button the button to check.
     * @return true if check state changed, false otherwise.
     */
    private boolean checkButton(RadioButton button) {
        if (button == null || button == mCheckedButton) {
            return false;
        }

        // if the button to check is different from the current checked button
        // if exists un-checks mCheckedButton
        if (mCheckedButton != null) {
            mCheckedButton.setChecked(false);
        }

        button.setChecked(true);
        mCheckedButton = button;
        return true;
    }

    /**
     * Clears the checked radio button.
     */
    @Override
    public void clearCheck() {
        if (mCheckedButton != null) {
            mCheckedButton.setChecked(false);
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, mCheckedButton);
            }
        }
        mCheckedButton = null;
    }

    /**
     * Returns the checked radio button's id.
     * If no radio buttons are checked returns -1.
     *
     * @return the checked radio button's id
     */
    @Override
    public int getCheckedRadioButtonId() {
        if (mCheckedButton == null)
            return -1;

        return mCheckedButton.getId();
    }

    /**
     * Returns the checked radio button's index.
     * If no radio buttons are checked returns -1.
     *
     * @return the checked radio button's index
     */
    public int getCheckedRadioButtonIndex() {
        if (mCheckedButton == null)
            return -1;

        return mRadioButtons.indexOf(mCheckedButton);
    }

    /**
     * Returns the checked radio button's text.
     * If no radio buttons are checked returns null.
     *
     * @return the checked radio buttons's text
     */
    public CharSequence getCheckedRadioButtonText() {
        if (mCheckedButton == null)
            return null;

        return mCheckedButton.getText();
    }

    /**
     * Returns a parcelable representing the saved state of this layout.
     *
     * @return a parcelable representing the saved state of this layout
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();

        SavedState savedState = new SavedState(parcelable);

        savedState.mMaxInRow = this.mMaxInRow;
        savedState.mCheckedButtonIndex = getCheckedRadioButtonIndex();

        return savedState;
    }

    /**
     * Restores the state of this layout from a parcelable.
     *
     * @param state the parcelable
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setMaxInRow(savedState.mMaxInRow);
        checkAt(savedState.mCheckedButtonIndex);
    }

    /**
     * Interface definition for a callback to be invoked when a radio button is checked.
     */
    public interface OnCheckedChangeListener {

        /**
         * Called when a radio button is checked.
         *
         * @param group  the group that stores the radio button
         * @param button the radio button that was checked
         */
        void onCheckedChanged(ViewGroup group, RadioButton button);
    }

    /**
     * Interface definition for a callback to be invoked when a radio button is clicked.
     * Clicking a radio button multiple times will result in multiple callbacks.
     */
    public interface OnClickListener {

        /**
         * Called when a radio button is clicked.
         *
         * @param group  the group that stores the radio button
         * @param button the radio button that was clicked
         */
        void onClick(ViewGroup group, RadioButton button);
    }

    /**
     * A class definition to save and restore a state of this layout.
     */
    private static class SavedState extends BaseSavedState {

        /**
         * The creator of this class.
         */
        public static final Parcelable.Creator CREATOR =
                new Creator<SavedState>() {

                    /**
                     * Creates SavedState instance from a specified parcel.
                     *
                     * @param in the parcel to create from
                     * @return an instance of SavedState
                     */
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    /**
                     * Creates a new array of the Parcelable class,
                     * with every entry initialized to null.
                     *
                     * @param size the size of the array.
                     * @return an array of the Parcelable class
                     */
                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }

                };

        int mMaxInRow;
        int mCheckedButtonIndex;

        /**
         * Constructor called by derived classes when creating their SavedState objects.
         *
         * @param superState the state of the superclass of this view
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor to create a new instance with the specified parcel.
         *
         * @param in the parcel
         */
        SavedState(Parcel in) {
            super(in);

            mMaxInRow = in.readInt();
            mCheckedButtonIndex = in.readInt();
        }

        /**
         * Saves this object to a parcel.
         *
         * @param out   the parcel to write to
         * @param flags additional flags about how the object should be written,
         *              May be 0 or PARCELABLE_WRITE_RETURN_VALUE
         */
        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeInt(mMaxInRow);
            out.writeInt(mCheckedButtonIndex);
        }
    }
}