package com.loyverse.dashboard.mvp.calendar

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.loyverse.dashboard.R
import com.loyverse.dashboard.base.Utils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarAdapter(val context: Context, private val onDateClickListener: (CalendarDay) -> Unit) : RecyclerView.Adapter<CalendarAdapter.VH>() {

    private var items = emptyList<Item>()
    private val listSelectedCells = ArrayList<Int>()
    val listSelectedDays = ArrayList<CalendarDay>()
    var isSundayFirst = false
    var monthTitleViewHeight = 0
    var monthTitleTextColor = 0


    init {
        val dateManager = DateModel()
        val calendar = Calendar.getInstance(dateManager.deviceLocale)
        isSundayFirst = dateManager.firstDayOfWeek == Calendar.SUNDAY

        items = (0 until FIVE_YEARS_IN_MONTHS)
                .map {
                    val newItems = mutableListOf<Item>()
                    val monthTitleFormat = SimpleDateFormat("LLLL yyyy", dateManager.deviceLocale)
                    newItems.add(Item.MonthTitle(monthTitleFormat.format(dateManager.calendar.time).capitalize()))

                    val monthDays = dateManager.days().map {
                        val dateFormat = SimpleDateFormat("dd", Utils.EN_LOCALE)
                        if (dateManager.isCurrentMonth(it)) {
                            calendar.time = it
                            Item.MonthDay(
                                    name = dateFormat.format(it),
                                    isEnabled = !dateManager.isFutureDays(it),
                                    selectionType = SelectionType.NONE,
                                    date = CalendarDay(
                                            day = calendar.get(Calendar.DAY_OF_MONTH),
                                            month = calendar.get(Calendar.MONTH) + 1,
                                            year = calendar.get(Calendar.YEAR)
                                    )
                            )
                        } else Item.EmptyDay
                    }
                    newItems.addAll(monthDays)
                    dateManager.prevMonth()
                    newItems
                }.run { reverse(this) }
                .flatten()
    }

    private fun <T> reverse(list: List<T>) : List<T> {
        val result = ArrayList<T>(list.size)
        for (i in list.size - 1 downTo 0) {
            result.add(list[i])
        }
        return result
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = when(viewType) {
        MONTH_TITLE_VIEW_TYPE -> VH.MonthTitleViewHolder(TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  monthTitleViewHeight)
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.text_size_month))
            setTextColor(context.resources.getColor(R.color.calanderTextColor))
        })
        DAY_VIEW_TYPE -> VH.DayViewHolder(
                TextView(context).apply {
                    val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                    val cellWidth = (display.width) / 7
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cellWidth)
                    gravity = Gravity.CENTER
                },
                onDateClickListener
        )
        EMPTY_DAY_VIEW_TYPE -> VH.EmptyDayViewHolder(context)
        else -> throw IllegalStateException("wrong view holder type")
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = when(items[position]) {
        is Item.MonthTitle -> MONTH_TITLE_VIEW_TYPE
        is Item.MonthDay -> DAY_VIEW_TYPE
        Item.EmptyDay -> EMPTY_DAY_VIEW_TYPE
    }

    override fun onBindViewHolder(holder: VH, position: Int) = when(holder) {
        is VH.MonthTitleViewHolder -> holder.bind(items[position] as Item.MonthTitle)
        is VH.DayViewHolder -> holder.bind(items[position] as Item.MonthDay)
        is VH.EmptyDayViewHolder -> {}
    }

    fun selectDate(startDate: CalendarDay, endDate: CalendarDay) {
        resetOldSelectedItems()
        val (positionFirst, positionLast) = initializedSelectedPositions(startDate, endDate)
        if (positionFirst == -1 || positionLast == -1) return

        items = when {
            positionFirst == positionLast -> {
                items.toMutableList().apply {
                    set(positionFirst, (get(positionFirst) as Item.MonthDay).copy(selectionType = SelectionType.SINGLE))
                    listSelectedCells.add(positionFirst)
                    listSelectedDays.add((get(positionFirst) as Item.MonthDay).date)
                }
            }
            positionFirst > positionLast -> items.selectRange(positionLast, positionFirst)
            else -> items.selectRange(positionFirst, positionLast)
        }
        notifyDataSetChanged()
    }

    private fun resetOldSelectedItems() {
        if (listSelectedCells.size == 0) return

        items = items.toMutableList().apply {
            listSelectedCells.forEach {
                (this[it] as? Item.MonthDay)
                        ?.copy(selectionType = SelectionType.NONE)
                        ?.let { item -> this.set(it, item) }
            }
        }

        (listSelectedCells.first() to listSelectedCells.last()).let { (first, last) ->
            listSelectedCells.clear()
            listSelectedDays.clear()
            notifyItemRangeChanged(first, last - first + 1)
        }
    }

    private fun initializedSelectedPositions(startDate: CalendarDay, endDate: CalendarDay): Pair<Int, Int> {
        val positionFirst = items.indexOfFirst {
            it is Item.MonthDay && it.date == startDate
        }

        val positionLast = if (startDate != endDate) {
            items.indexOfFirst { it is Item.MonthDay && it.date == endDate }
        } else positionFirst

        return Pair(positionFirst, positionLast)
    }

    private fun List<Item>.selectRange(positionFrom: Int, positionTo: Int) : List<Item> {
        val newList = this.toMutableList()

        (positionFrom until positionTo+1).forEach{ index ->
            val item = newList[index] as? Item.MonthDay
            if(item != null){
                newList[index] = item.copy(
                        selectionType = if (item.isEnabled) {
                            when (index) {
                                positionFrom -> SelectionType.FIRST
                                positionTo -> SelectionType.LAST
                                else -> SelectionType.MIDDLE
                            }
                        } else {
                            SelectionType.NONE
                        }
                )
                listSelectedCells.add(index) //FIXME: Side effect
                listSelectedDays.add(item.date)
            }
        }
        return newList
    }


    /** data classes with holders *****************************************************************/
    sealed class Item {
        data class MonthTitle(val name: String) : Item()
        data class MonthDay(
                val name: String,
                val isEnabled: Boolean,
                val selectionType: SelectionType,
                val date: CalendarDay
        ) : Item() {
            val isSelected: Boolean
                get() = selectionType != SelectionType.NONE
        }
        object EmptyDay : Item()
    }


    sealed class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class MonthTitleViewHolder(itemView: View) : VH(itemView) {
            fun bind(month: Item.MonthTitle) {
                (itemView as TextView).apply {
                    text = month.name
                }
            }
        }

        class DayViewHolder(itemView: View, onDateClickListener: (CalendarDay) -> Unit) : VH(itemView) {
            private var date: CalendarDay? = null

            init {
                itemView.setOnClickListener {
                    date?.let(onDateClickListener)
                }
            }

            fun bind(day: Item.MonthDay) {
                date = day.date

                (itemView as TextView).apply {
                    isEnabled = day.isEnabled
                    text = day.name

                    if (day.isEnabled) {
                        when(day.selectionType) {
                            SelectionType.SINGLE,
                                SelectionType.FIRST,
                                SelectionType.LAST -> Color.WHITE
                            else -> context.resources.getColor(R.color.calanderTextColor)
                        }.let (::setTextColor)
                    } else setTextColor(context.resources.getColor(R.color.calanderTextColorDisabled))

                    background = when (day.selectionType) {
                        SelectionType.SINGLE -> {
                            context.resources.getDrawable(R.drawable.bg_circle_selected)
                        }
                        SelectionType.FIRST -> {
                            context.resources.getDrawable(R.drawable.bg_selected_start)
                        }
                        SelectionType.MIDDLE -> {
                            context.resources.getDrawable(R.drawable.bg_line_selected)
                        }
                        SelectionType.LAST -> {
                            context.resources.getDrawable(R.drawable.bg_selected_end)
                        }
                        SelectionType.NONE -> null

                    }
                }
            }
        }

        class EmptyDayViewHolder(context: Context) : VH(View(context))
    }

    companion object {
        const val MONTH_TITLE_VIEW_TYPE = 0
        const val DAY_VIEW_TYPE = 1
        const val EMPTY_DAY_VIEW_TYPE = 2
        private const val FIVE_YEARS_IN_MONTHS = 60
    }

    enum class SelectionType { NONE, SINGLE, FIRST, MIDDLE, LAST }
}