package com.didichuxing.doraemonkit.widget

import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.didichuxing.doraemonkit.R
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-15-14:46
 * 描    述：多方的RadioGroup
 * 修订历史：
 * ================================================
 */
class MultiLineRadioGroup : RadioGroup {
    // the listener for callbacks to invoke when radio button checked state changes
    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null

    // the listener for callbacks to invoke when radio button is clicked
    private var mOnClickListener: OnClickListener? = null

    // holds the maximum number of radio buttons that should be in a row
    private var mMaxInRow = 0

    // all buttons are stored in table layout
    private var mTableLayout: TableLayout? = null

    // list to store all the buttons
    private var mRadioButtons: MutableList<RadioButton>? = null

    // the checked button
    private var mCheckedButton: RadioButton? = null

    /**
     * Creates a new MultiLineRadioGroup for the given context.
     *
     * @param context the application environment
     */
    constructor(context: Context?) : super(context) {
        init(null)
    }

    /**
     * Creates a new MultiLineRadioGroup for the given context
     * and with the specified set attributes.
     *
     * @param context the application environment
     * @param attrs   a collection of attributes
     */
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    // initializes the layout
    private fun init(attrs: AttributeSet?) {
        mRadioButtons = ArrayList()
        mTableLayout = tableLayout
        addView(mTableLayout!!)
        attrs?.let { initAttrs(it) }
    }

    // initializes the layout with the specified attributes
    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(
                attrs, R.styleable.dk_multi_line_radio_group,
                0, 0)
        try {
            // gets and sets the max in row.
            maxInRow = typedArray.getInt(R.styleable.dk_multi_line_radio_group_max_in_row,
                    DEF_VAL_MAX_IN_ROW)

            // gets and adds the starting buttons
            val radioButtonStrings = typedArray.getTextArray(
                    R.styleable.dk_multi_line_radio_group_radio_buttons)
            radioButtonStrings?.let {
                addButtons(*it)
            }


            // gets the default button and checks it if presents.
            val string = typedArray.getString(R.styleable.dk_multi_line_radio_group_default_button)
            string?.let { setDefaultButton(it) }
        } finally {
            typedArray.recycle()
        }
    }

    // checks the default button based on the passed string
    private fun setDefaultButton(string: String) {
        val START_OF_INDEX = XML_DEFAULT_BUTTON_PREFIX_INDEX.length
        val START_OF_TEXT = XML_DEFAULT_BUTTON_PREFIX_TEXT.length

        // the text of the button to check
        val buttonToCheck: String
        buttonToCheck = if (string.startsWith(XML_DEFAULT_BUTTON_PREFIX_INDEX)) {
            val indexString = string.substring(START_OF_INDEX)
            val index = indexString.toInt()
            require(!(index < 0 || index >= mRadioButtons!!.size)) {
                "index must be between 0 to getRadioButtonCount() - 1 [" +
                        (radioButtonCount - 1) + "]: " + index
            }
            mRadioButtons!![index].text.toString()
        } else if (string.startsWith(XML_DEFAULT_BUTTON_PREFIX_TEXT)) {
            string.substring(START_OF_TEXT)
        } else { // when there is no prefix assumes the string is the text of the button
            string
        }
        check(buttonToCheck)
    }

    /**
     * Returns the table layout to set for this layout.
     *
     * @return the table layout
     */
    protected val tableLayout: TableLayout
        protected get() = LayoutInflater.from(context)
                .inflate(R.layout.dk_radio_table_layout, this, false) as TableLayout

    /**
     * Returns the table row to set in this layout.
     *
     * @return the table row
     */
    protected val tableRow: TableRow
        protected get() = LayoutInflater.from(context)
                .inflate(R.layout.dk_radio_table_row, mTableLayout, false) as TableRow

    /**
     * Returns the default radio button to set in this layout.
     *
     *
     * This radio button will be used when radio buttons are added
     * with the methods addButtons() that accept string varargs.
     *
     * @return the radio button
     */
    protected val radioButton: RadioButton
        protected get() = LayoutInflater.from(context)
                .inflate(R.layout.dk_radio_button, null) as RadioButton

    /**
     * Register a callback to be invoked when a radio button checked state changes.
     * The only time the listener is passed a button where isChecked is false will be when
     * you call clearCheck.
     *
     * @param onCheckedChangeListener the listener to attach
     */
    fun setOnCheckedChangeListener(onCheckedChangeListener: OnCheckedChangeListener?) {
        mOnCheckedChangeListener = onCheckedChangeListener
    }

    /**
     * Register a callback to be invoked when a radio button is clicked.
     *
     * @param listener the listener to attach
     */
    fun setOnClickListener(listener: OnClickListener?) {
        mOnClickListener = listener
    }

    /**
     * Returns the maximum radio buttons in a row, 0 for all in one line.
     *
     * @return the maximum radio buttons in a row, 0 for all in one line
     */
    /**
     * Sets the maximum radio buttons in a row, 0 for all in one line
     * and arranges the layout accordingly.
     *
     * @param maxInRow the maximum radio buttons in a row
     * @throws IllegalArgumentException if maxInRow is negative
     */
    var maxInRow: Int
        get() = mMaxInRow
        set(maxInRow) {
            require(maxInRow >= 0) { "maxInRow must not be negative: $maxInRow" }
            mMaxInRow = maxInRow
            arrangeButtons()
        }

    /**
     * Adds a view to the layout
     *
     *
     * Consider using addButtons() instead
     *
     * @param child the view to add
     */
    override fun addView(child: View) {
        addView(child, -1, child.layoutParams)
    }

    /**
     * Adds a view to the layout in the specified index
     *
     *
     * Consider using addButtons() instead
     *
     * @param child the view to add
     * @param index the index in which to insert the view
     */
    override fun addView(child: View, index: Int) {
        addView(child, index, child.layoutParams)
    }

    /**
     * Adds a view to the layout with the specified width and height.
     * Note that for radio buttons the width and the height are ignored.
     *
     *
     * Consider using addButtons() instead
     *
     * @param child  the view to add
     * @param width  the width of the view
     * @param height the height of the view
     */
    override fun addView(child: View, width: Int, height: Int) {
        addView(child, -1, LinearLayout.LayoutParams(width, height))
    }

    /**
     * Adds a view to the layout with the specified layout params.
     * Note that for radio buttons the width and the height are ignored.
     *
     *
     * Consider using addButtons() instead
     *
     * @param child  the view to add
     * @param params the layout params of the view
     */
    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        addView(child, -1, params)
    }

    /**
     * Adds a view to the layout in the specified index
     * with the specified layout params.
     * Note that for radio buttons the width and the height are ignored.
     *
     *
     * * Consider using addButtons() instead
     *
     * @param child  the view to add
     * @param index  the index in which to insert the view
     * @param params the layout params of the view
     */
    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        var params: ViewGroup.LayoutParams? = params
        if (child is RadioButton) {
            addRadioButtons(index, child as RadioButton)
        } else {
            // if params are null sets them
            if (params == null) {
                params = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            super.addView(child, index, params)
        }
    }

    /**
     * Adds radio buttons to the layout based on the texts in the radioButtons array.
     * Adds them in the last index.
     * If radioButtons is null does nothing.
     *
     * @param radioButtons the texts of the buttons to add
     */
    fun addButtons(vararg radioButtons: CharSequence?) {
        addButtons(-1, *radioButtons)
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
    fun addButtons(index: Int, vararg radioButtons: CharSequence?) {
        require(!(index < -1 || index > mRadioButtons!!.size)) {
            "index must be between -1 to getRadioButtonCount() [" +
                    radioButtonCount + "]: " + index
        }
        if (radioButtons == null) return

        // creates radio button array
        val buttons = arrayOf<RadioButton>()
        for (i in buttons.indices) {
            val radioButton = radioButton
            radioButton.text = radioButtons[i]
            radioButton.id = generateId()
            buttons[i] = radioButton
        }
        addRadioButtons(index, *buttons)
    }


    // generates an id
    private fun generateId(): Int {
        // for API 17 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.generateViewId()

            // for API lower than 17
        } else {
            while (true) {
                val result = sNextGeneratedId.get()

                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                var newValue = result + 1
                if (newValue > 0x00FFFFFF) newValue = 1 // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) return result
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
    fun addButtons(vararg radioButtons: RadioButton) {
        addRadioButtons(-1, *radioButtons)
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
    fun addRadioButtons(index: Int, vararg radioButtons: RadioButton) {
        require(!(index < -1 || index > mRadioButtons!!.size)) {
            "index must be between -1 to getRadioButtonCount() [" +
                    radioButtonCount + "]: " + index
        }
        if (radioButtons == null) return
        var realIndex = if (index != -1) index else mRadioButtons!!.size

        // inits the buttons and adds them to the list
        for (radioButton in radioButtons) {
            initRadioButton(radioButton)
            mRadioButtons?.add(realIndex++, radioButton)
        }
        arrangeButtons()
    }

    // inits the radio button
    private fun initRadioButton(radioButton: RadioButton) {
        radioButton.setOnClickListener { v ->
            val didCheckStateChange = checkButton(v as RadioButton)
            if (didCheckStateChange && mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener!!.onCheckedChanged(this@MultiLineRadioGroup, mCheckedButton)
            }
            if (mOnClickListener != null) {
                mOnClickListener!!.onClick(this@MultiLineRadioGroup, mCheckedButton)
            }
        }
    }

    /**
     * Removes a view from the layout.
     *
     *
     * Consider using removeButton().
     *
     * @param view the view to remove
     */
    override fun removeView(view: View) {
        super.removeView(view)
    }

    /**
     * Removes a view from the layout in the specified index.
     *
     *
     * Consider using removeButton().
     *
     * @param index the index from which to remove the view
     */
    override fun removeViewAt(index: Int) {
        super.removeViewAt(index)
    }

    /**
     * Removes the specified range of views from the layout.
     *
     *
     * Consider using removeButtons().
     *
     * @param start the start index to remove
     * @param count the number of views to remove
     */
    override fun removeViews(start: Int, count: Int) {
        super.removeViews(start, count)
    }

    /**
     * Removes all the views from the layout.
     *
     *
     * Consider using removeAllButtons().
     */
    override fun removeAllViews() {
        super.removeAllViews()
    }

    /**
     * Removes a radio button from the layout.
     * If the radio button is null does nothing.
     *
     * @param radioButton the radio button to remove
     */
    fun removeButton(radioButton: RadioButton?) {
        if (radioButton == null) return
        removeButton(radioButton.text)
    }

    /**
     * Removes a radio button from the layout based on its text.
     * Removes the first occurrence.
     * If the text is null does nothing.
     *
     * @param text the text of the radio button to remove
     */
    fun removeButton(text: CharSequence?) {
        if (text == null) return
        var index = -1
        var i = 0
        val len = mRadioButtons!!.size
        while (i < len) {

            // checks if the texts are equal
            if (mRadioButtons!![i].text == text) {
                index = i
                break
            }
            i++
        }

        // removes just if the index was found
        if (index != -1) removeButton(index)
    }

    /**
     * Removes the radio button in the specified index from the layout.
     *
     * @param index the index from which to remove the radio button
     * @throws IllegalArgumentException if index is less than 0
     * or greater than the number of radio buttons - 1
     */
    fun removeButton(index: Int) {
        removeButtons(index, 1)
    }

    /**
     * Removes all the radio buttons in the specified range from the layout.
     * Count can be any non-negative number.
     *
     * @param start the start index to remove
     * @param count the number of radio buttons to remove
     * @throws IllegalArgumentException if index is less than 0
     * or greater than the number of radio buttons - 1
     * or count is negative
     */
    fun removeButtons(start: Int, count: Int) {
        if (count == 0) {
            return
        }
        require(!(start < 0 || start >= mRadioButtons!!.size)) {
            "start index must be between 0 to getRadioButtonCount() - 1 [" +
                    (radioButtonCount - 1) + "]: " + start
        }
        require(count >= 0) { "count must not be negative: $count" }
        var endIndex = start + count - 1
        // if endIndex is not in the range of the radio buttons sets it to the last index
        if (endIndex >= mRadioButtons!!.size) endIndex = mRadioButtons!!.size - 1

        // iterates over the buttons to remove
        for (i in endIndex downTo start) {
            val radiobutton = mRadioButtons!![i]
            // if the button to remove is the checked button set mCheckedButton to null
            if (radiobutton === mCheckedButton) mCheckedButton = null
            // removes the button from the list
            mRadioButtons?.removeAt(i)
        }
        arrangeButtons()
    }

    /**
     * Removes all the radio buttons from the layout.
     */
    fun removeAllButtons() {
        removeButtons(0, mRadioButtons!!.size)
    }

    // arrange the buttons in the layout
    private fun arrangeButtons() {
        // iterates over each button and puts it in the right place
        var i = 0
        val len = mRadioButtons!!.size
        while (i < len) {
            val radioButtonToPlace = mRadioButtons!![i]
            val rowToInsert = if (mMaxInRow != 0) i / mMaxInRow else 0
            val columnToInsert = if (mMaxInRow != 0) i % mMaxInRow else i
            // gets the row to insert. if there is no row create one
            val tableRowToInsert = (if (mTableLayout!!.childCount <= rowToInsert) addTableRow() else mTableLayout!!.getChildAt(rowToInsert) as TableRow)
            val tableRowChildCount = tableRowToInsert.childCount

            // if there is already a button in the position
            if (tableRowChildCount > columnToInsert) {
                val currentButton = tableRowToInsert.getChildAt(columnToInsert) as RadioButton

                // insert the button just if the current button is different
                if (currentButton !== radioButtonToPlace) {
                    // removes the current button
                    removeButtonFromParent(currentButton, tableRowToInsert)
                    // removes the button to place from its current position
                    removeButtonFromParent(radioButtonToPlace, radioButtonToPlace.parent as ViewGroup)
                    // adds the button to the right place
                    tableRowToInsert.addView(radioButtonToPlace, columnToInsert)
                }

                // if there isn't already a button in the position
            } else {
                // removes the button to place from its current position
                removeButtonFromParent(radioButtonToPlace, radioButtonToPlace.parent as ViewGroup)
                // adds the button to the right place
                tableRowToInsert.addView(radioButtonToPlace, columnToInsert)
            }
            i++
        }
        removeRedundancies()
    }

    // removes the redundant rows and radio buttons
    private fun removeRedundancies() {
        // the number of rows to fit the buttons
        val rows: Int
        rows = if (mRadioButtons!!.size == 0) 0 else if (mMaxInRow == 0) 1 else (mRadioButtons!!.size - 1) / mMaxInRow + 1
        var tableChildCount = mTableLayout!!.childCount
        // if there are redundant rows remove them
        if (tableChildCount > rows) mTableLayout!!.removeViews(rows, tableChildCount - rows)
        tableChildCount = mTableLayout!!.childCount
        val maxInRow = if (mMaxInRow != 0) mMaxInRow else mRadioButtons!!.size

        // iterates over the rows
        for (i in 0 until tableChildCount) {
            val tableRow = mTableLayout!!.getChildAt(i) as TableRow
            val tableRowChildCount = tableRow.childCount
            var startIndexToRemove: Int
            var count: Int

            // if it is the last row removes all redundancies after the last button in the list
            if (i == tableChildCount - 1) {
                startIndexToRemove = (mRadioButtons!!.size - 1) % maxInRow + 1
                count = tableRowChildCount - startIndexToRemove

                // if it is not the last row removes all the buttons after maxInRow position
            } else {
                startIndexToRemove = maxInRow
                count = tableRowChildCount - maxInRow
            }
            if (count > 0) tableRow.removeViews(startIndexToRemove, count)
        }
    }

    // adds and returns a table row
    private fun addTableRow(): TableRow {
        val tableRow = tableRow
        mTableLayout!!.addView(tableRow)
        return tableRow
    }

    // removes a radio button from a parent
    private fun removeButtonFromParent(radioButton: RadioButton?, parent: ViewGroup?) {
        if (radioButton == null || parent == null) return
        parent.removeView(radioButton)
    }

    /**
     * Returns the number of radio buttons.
     *
     * @return the number of radio buttons
     */
    val radioButtonCount: Int
        get() = mRadioButtons!!.size

    /**
     * Returns the radio button in the specified index.
     * If the index is out of range returns null.
     *
     * @param index the index of the radio button
     * @return the radio button
     */
    fun getRadioButtonAt(index: Int): RadioButton? {
        return if (index < 0 || index >= mRadioButtons!!.size) null else mRadioButtons!![index]
    }

    /**
     * Returns true if there is a button with the specified text, false otherwise.
     *
     * @param button the text to search
     * @return true if there is a button with the specified text, false otherwise
     */
    fun containsButton(button: String): Boolean {
        for (radioButton in mRadioButtons!!) if (radioButton.text == button) return true
        return false
    }

    /**
     * Checks the radio button with the specified id.
     * If the specified id is not found does nothing.
     *
     * @param id the radio button's id
     */
    override fun check(id: Int) {
        if (id <= 0) return
        for (radioButton in mRadioButtons!!) {
            if (radioButton.id == id) {
                if (checkButton(radioButton)) { // True if the button wasn't already checked.
                    if (mOnCheckedChangeListener != null) {
                        mOnCheckedChangeListener!!.onCheckedChanged(
                                this@MultiLineRadioGroup, radioButton)
                    }
                }
                return
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
    fun check(text: CharSequence?) {
        if (text == null) return
        for (radioButton in mRadioButtons!!) {
            if (radioButton.text == text) {
                if (checkButton(radioButton)) { // True if the button wasn't already checked.
                    if (mOnCheckedChangeListener != null) {
                        mOnCheckedChangeListener!!.onCheckedChanged(
                                this@MultiLineRadioGroup, radioButton)
                    }
                }
                return
            }
        }
    }

    /**
     * Checks the radio button at the specified index.
     * If the specified index is invalid does nothing.
     *
     * @param index the radio button's index
     */
    fun checkAt(index: Int) {
        if (index < 0 || index >= mRadioButtons!!.size) return
        if (checkButton(mRadioButtons!![index])) { // True if the button wasn't already checked.
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener!!.onCheckedChanged(
                        this@MultiLineRadioGroup, mRadioButtons!![index])
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
    private fun checkButton(button: RadioButton?): Boolean {
        if (button == null || button === mCheckedButton) {
            return false
        }

        // if the button to check is different from the current checked button
        // if exists un-checks mCheckedButton
        if (mCheckedButton != null) {
            mCheckedButton!!.isChecked = false
        }
        button.isChecked = true
        mCheckedButton = button
        return true
    }

    /**
     * Clears the checked radio button.
     */
    override fun clearCheck() {
        if (mCheckedButton != null) {
            mCheckedButton!!.isChecked = false
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener!!.onCheckedChanged(this, mCheckedButton)
            }
        }
        mCheckedButton = null
    }

    /**
     * Returns the checked radio button's id.
     * If no radio buttons are checked returns -1.
     *
     * @return the checked radio button's id
     */
    override fun getCheckedRadioButtonId(): Int {
        return if (mCheckedButton == null) -1 else mCheckedButton!!.id
    }

    /**
     * Returns the checked radio button's index.
     * If no radio buttons are checked returns -1.
     *
     * @return the checked radio button's index
     */
    val checkedRadioButtonIndex: Int
        get() = if (mCheckedButton == null) -1 else mRadioButtons!!.indexOf(mCheckedButton!!)

    /**
     * Returns the checked radio button's text.
     * If no radio buttons are checked returns null.
     *
     * @return the checked radio buttons's text
     */
    val checkedRadioButtonText: CharSequence?
        get() = if (mCheckedButton == null) null else mCheckedButton!!.text

    /**
     * Returns a parcelable representing the saved state of this layout.
     *
     * @return a parcelable representing the saved state of this layout
     */
    override fun onSaveInstanceState(): Parcelable? {
        val parcelable = super.onSaveInstanceState()
        val savedState = SavedState(parcelable)
        savedState.mMaxInRow = mMaxInRow
        savedState.mCheckedButtonIndex = checkedRadioButtonIndex
        return savedState
    }

    /**
     * Restores the state of this layout from a parcelable.
     *
     * @param state the parcelable
     */
    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        val savedState = state
        super.onRestoreInstanceState(savedState.superState)
        maxInRow = savedState.mMaxInRow
        checkAt(savedState.mCheckedButtonIndex)
    }

    /**
     * Interface definition for a callback to be invoked when a radio button is checked.
     */
    interface OnCheckedChangeListener {
        /**
         * Called when a radio button is checked.
         *
         * @param group  the group that stores the radio button
         * @param button the radio button that was checked
         */
        fun onCheckedChanged(group: ViewGroup?, button: RadioButton?)
    }

    /**
     * Interface definition for a callback to be invoked when a radio button is clicked.
     * Clicking a radio button multiple times will result in multiple callbacks.
     */
    interface OnClickListener {
        /**
         * Called when a radio button is clicked.
         *
         * @param group  the group that stores the radio button
         * @param button the radio button that was clicked
         */
        fun onClick(group: ViewGroup?, button: RadioButton?)
    }

    /**
     * A class definition to save and restore a state of this layout.
     */
    private class SavedState : BaseSavedState {
        var mMaxInRow = 0
        var mCheckedButtonIndex = 0

        /**
         * Constructor called by derived classes when creating their SavedState objects.
         *
         * @param superState the state of the superclass of this view
         */
        internal constructor(superState: Parcelable?) : super(superState) {}

        /**
         * Constructor to create a new instance with the specified parcel.
         *
         * @param in the parcel
         */
        internal constructor(`in`: Parcel) : super(`in`) {
            mMaxInRow = `in`.readInt()
            mCheckedButtonIndex = `in`.readInt()
        }

        /**
         * Saves this object to a parcel.
         *
         * @param out   the parcel to write to
         * @param flags additional flags about how the object should be written,
         * May be 0 or PARCELABLE_WRITE_RETURN_VALUE
         */
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(mMaxInRow)
            out.writeInt(mCheckedButtonIndex)
        }

        companion object {
            /**
             * The creator of this class.
             */
            val CREATOR: Parcelable.Creator<*> = object : Parcelable.Creator<SavedState?> {
                /**
                 * Creates SavedState instance from a specified parcel.
                 *
                 * @param in the parcel to create from
                 * @return an instance of SavedState
                 */
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                /**
                 * Creates a new array of the Parcelable class,
                 * with every entry initialized to null.
                 *
                 * @param size the size of the array.
                 * @return an array of the Parcelable class
                 */
                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        private const val XML_DEFAULT_BUTTON_PREFIX_INDEX = "index:"
        private const val XML_DEFAULT_BUTTON_PREFIX_TEXT = "text:"
        private const val DEF_VAL_MAX_IN_ROW = 0

        // for generating ids to APIs lower than 17
        private val sNextGeneratedId = AtomicInteger(1)
    }
}