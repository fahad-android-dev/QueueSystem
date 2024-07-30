package com.orbits.queuesystem.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.orbits.queuesystem.R

class WheelView : ScrollView {
    private var contexts: Context? = null

    private var views: LinearLayout? = null

    private var items: MutableList<Any>? = null
    private var offset = OFF_SET_DEFAULT

    private var displayItemCount: Int = 0 // 每页显示的数量

    private var selectedIndex = 1

    private var initialY: Int = 0
    private var scrollerTask: Runnable? = null
    private val newCheck = 50

    private var itemHeight = 0

    private var selectedAreaBorder: IntArray? = null

    private var paint: Paint? = null
    private var viewWidth: Int = 0

    val seletedItem: String
        get() = items?.get(seletedIndex) as String

    val seletedIndex: Int
        get() = selectedIndex - offset

    var onWheelViewListener: OnWheelViewListener? = null

    class OnWheelViewListener {
        internal fun onSelected(selectedIndex: Int, item: String) {}
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    private fun getItems(): List<Any>? {
        return items
    }

    fun setItems(list: List<Any>) {

        if (null == items) {
            items = ArrayList()
        }
        items?.clear()
        items?.addAll(list)

        for (i in 0 until offset) {
            items?.add(0, "")
            items?.add("")
        }

        initData()

    }


    private fun init(context: Context) {
        this.contexts = context
        this.isVerticalScrollBarEnabled = false
        views = LinearLayout(context)
        views?.overScrollMode = View.OVER_SCROLL_NEVER
        views?.orientation = LinearLayout.VERTICAL
        this.addView(views)

        scrollerTask = Runnable {
            val newY = scrollY
            if (initialY - newY == 0) { // stopped
                val remainder = initialY % itemHeight
                val divided = initialY / itemHeight
                if (remainder == 0) {
                    selectedIndex = divided + offset
                    onSeletedCallBack()
                } else {
                    if (remainder > itemHeight / 2) {
                        this@WheelView.post {
                            this@WheelView.smoothScrollTo(0, initialY - remainder + itemHeight)
                            selectedIndex = divided + offset + 1
                            onSeletedCallBack()
                        }
                    } else {
                        this@WheelView.post {
                            this@WheelView.smoothScrollTo(0, initialY - remainder)
                            selectedIndex = divided + offset
                            onSeletedCallBack()
                        }
                    }
                }
            } else {
                initialY = scrollY
                this@WheelView.postDelayed(scrollerTask, newCheck.toLong())
            }
        }


    }

    private fun startScrollerTask() {
        initialY = scrollY
        this.postDelayed(scrollerTask, newCheck.toLong())
    }

    private fun initData() {
        displayItemCount = offset * 2 + 1
        for (item in items!!) {
            views?.addView(createView(item))

            //views?.addView(createDivLine(item))

        }
        refreshItemView(0)
    }

    private fun createView(item: Any): TextView {
        val tv = TextView(context)
        tv.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tv.isSingleLine = true

        tv.textSize = Global.setFontSize(
            contexts as Activity,
            resources.getDimension(R.dimen.subtitle_text_size)
        )
        tv.typeface = Global.getTypeFace(context,Constants.fontRegular)

        if (item is String) {
            tv.text = item
        }

        tv.text = item.toString()

        tv.gravity = Gravity.CENTER
        val padding = dip2px(15f)
        tv.setPadding(padding, padding, padding, padding)

        if (0 == itemHeight) {
            itemHeight = getViewMeasuredHeight(tv)
            views?.layoutParams =
                LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount)
            val lp = this.layoutParams as LinearLayout.LayoutParams
            this.layoutParams = LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount)
        }
        return tv
    }


    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        refreshItemView(t)
    }

    private fun refreshItemView(y: Int) {
        var position = y / itemHeight + offset
        val remainder = y % itemHeight
        val divided = y / itemHeight
        if (remainder == 0) {
            position = divided + offset
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1
            }
        }

        val childSize = views?.childCount ?: 0
        for (i in 0 until childSize) {
            val itemView = views?.getChildAt(i) as TextView
            if (position == i) {
                itemView.setTextColor(
                    ContextCompat.getColor(
                        context as Activity,
                        R.color.color_black
                    )
                )
            } else {
                itemView.setTextColor(
                    ContextCompat.getColor(
                        context as Activity,
                        R.color.secondary_color
                    )
                )
            }
        }
    }

    private fun obtainSelectedAreaBorder(): IntArray {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = IntArray(2)
            selectedAreaBorder!![0] = itemHeight * offset
            selectedAreaBorder!![1] = itemHeight * (offset + 1)
        }
        return selectedAreaBorder!!
    }

    override fun setBackground(background: Drawable?) {
        val background: Drawable?

        if (viewWidth == 0) {
            viewWidth = (context as Activity).windowManager.defaultDisplay.width
        }

        if (null == paint) {
            paint = Paint()
            paint?.color = ContextCompat.getColor(context as Activity, R.color.divider_line_color)
            paint?.strokeWidth = dip2px(1f).toFloat()
        }

        background = object : Drawable() {
            override fun draw(canvas: Canvas) {
                canvas.drawLine(
                    0f,
                    obtainSelectedAreaBorder()[0].toFloat(),
                    (viewWidth).toFloat(),
                    obtainSelectedAreaBorder()[0].toFloat(),
                    paint!!
                )
                canvas.drawLine(
                    0f,
                    obtainSelectedAreaBorder()[1].toFloat(),
                    (viewWidth).toFloat(),
                    obtainSelectedAreaBorder()[1].toFloat(),
                    paint!!
                )
            }

            override fun setAlpha(alpha: Int) {

            }

            override fun setColorFilter(cf: ColorFilter?) {

            }

            @SuppressLint("WrongConstant")
            override fun getOpacity(): Int {
                return 0
            }
        }


        super.setBackground(background)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        background = null
    }

    private fun onSeletedCallBack() {
        if (null != onWheelViewListener) {
            onWheelViewListener?.onSelected(selectedIndex, items!![selectedIndex] as String)
        }
    }

    fun setSeletion(position: Int) {
        selectedIndex = position + offset
        this.post { this@WheelView.smoothScrollTo(0, position * itemHeight) }
    }


    override fun fling(velocityY: Int) {
        super.fling(velocityY / 3)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {

            startScrollerTask()
        }
        return super.onTouchEvent(ev)
    }

    private fun dip2px(dpValue: Float): Int {
        val scale = context?.resources?.displayMetrics?.density ?: 0f
        return (dpValue * scale + 0.5f).toInt()
    }

    private fun getViewMeasuredHeight(view: View): Int {
        val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val expandSpec =
            View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
        view.measure(width, expandSpec)
        return view.measuredHeight
    }

    companion object {
        //val TAG = WheelView::class.java.simpleName


        private val OFF_SET_DEFAULT = 1

        private val SCROLL_DIRECTION_UP = 0
        private val SCROLL_DIRECTION_DOWN = 1
    }

}
