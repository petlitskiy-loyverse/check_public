package com.loyverse.dashboard.mvp.calendar

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loyverse.dashboard.R

class VerticalCalendarView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var lastSelectedDate: CalendarDay? = null
    private val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spaceBottom)
    var listener: ((CalendarDay?, CalendarDay?) -> Unit)? = null


     val calendarAdapter: CalendarAdapter by lazy {
        CalendarAdapter(context) {
            val dateFrom = lastSelectedDate ?: it
            val dateTo = it.takeIf { lastSelectedDate != null }
            calendarAdapter.selectDate(dateFrom, dateTo ?: dateFrom)
            lastSelectedDate = dateFrom.takeIf { dateTo == null }
            when {
                dateTo == null -> listener?.invoke(dateFrom, dateFrom)
                dateFrom.getDate() < dateTo.getDate() -> listener?.invoke(dateFrom, dateTo)
                else -> listener?.invoke(dateTo, dateFrom)
            }
        }
    }

    private val recyclerView = RecyclerView(context).apply {
        layoutManager = GridLayoutManager(context, 7, RecyclerView.VERTICAL, false).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when(calendarAdapter.getItemViewType(position)) {
                        CalendarAdapter.EMPTY_DAY_VIEW_TYPE,
                        CalendarAdapter.DAY_VIEW_TYPE -> 1
                        CalendarAdapter.MONTH_TITLE_VIEW_TYPE -> 7
                        else -> -1
                    }
                }

            }
            adapter = calendarAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                ) {
                    outRect.bottom = spacingInPixels
                }
            })
            scrollToPosition(adapter!!.itemCount - 1)
            setHasFixedSize(true)
        }
    }

    private val headerView = LinearLayout(context).apply {
        layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.header_month_height)
        )
        gravity = Gravity.CENTER_VERTICAL
        val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val cellWidth = display.width / 7
        val count = if (calendarAdapter.isSundayFirst) { 0 } else { 1 }
        val listEnumWeekDay = WeekDay.values()
        val listDayOfWeek = ArrayList<Unit>()

        for (i in count until 7 + count) {
            listDayOfWeek.add(
                    addView(TextView(context).apply {
                        text = resources.getString(listEnumWeekDay[i].nameDay)
                        minWidth = cellWidth
                        gravity = Gravity.CENTER
                    })
            )
        }
    }

    private val divider = LinearLayout(context).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.divider_height))
        background = ColorDrawable(Color.LTGRAY)
    }

    init {
        orientation = VERTICAL
        background = resources.getDrawable(R.color.calanderBackground)
        addView(headerView)
        addView(divider)
        addView(recyclerView)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.AttrCalendarView, 0, 0)
            calendarAdapter.monthTitleViewHeight = typedArray.getDimensionPixelSize(R.styleable.AttrCalendarView_monthTitleViewHeight, resources.getDimensionPixelSize(R.dimen.month_title_view_height))
            calendarAdapter.monthTitleTextColor = typedArray.getColor(R.styleable.AttrCalendarView_monthTitleTextColor, resources.getColor(R.color.calanderTextColor))
        }
    }


    fun getSelectedDates(): List<CalendarDay> {
        return calendarAdapter.listSelectedDays
    }

    enum class WeekDay(val nameDay: Int) {
        SUN(R.string.sun),
        MON(R.string.mon),
        TUE(R.string.tue),
        WED(R.string.wed),
        THU(R.string.thu),
        FRI(R.string.fri),
        SAT(R.string.sat),
        SUN_RE(R.string.sun)
    }

}