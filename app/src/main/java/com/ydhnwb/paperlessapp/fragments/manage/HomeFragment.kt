package com.ydhnwb.paperlessapp.fragments.manage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.core.cartesian.series.Column
import com.anychart.enums.*
import com.ydhnwb.paperlessapp.R
import kotlinx.android.synthetic.main.chart_bar.view.*
import kotlinx.android.synthetic.main.chart_pie.*
import kotlinx.android.synthetic.main.chart_pie.view.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.bar_chart.setProgressBar(view.bar_progress_bar)
        APIlib.getInstance().setActiveAnyChartView(view.bar_chart)
        val cartesian = AnyChart.cartesian()
        val data: MutableList<DataEntry> = mutableListOf()
        data.add(ValueDataEntry("Latte", 910))
        data.add(ValueDataEntry("Americano", 902))
        data.add(ValueDataEntry("Delhi Ice", 102))
        data.add(ValueDataEntry("Mocca", 89))
        val column: Column = cartesian.column(data)

        cartesian.animation(false)
        cartesian.title("4 Produk terlaris (this month)")
        cartesian.yScale().minimum(0.0)
        cartesian.yAxis(0).labels().format("Rp.{%Value}{groupsSeparator: }")
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
        cartesian.interactivity().hoverMode(HoverMode.BY_X)

        cartesian.xAxis(0).title("Product")
        cartesian.yAxis(0).title("Revenue")

        view.bar_chart.setChart(cartesian)

        view.pie_chart.setProgressBar(pie_progress_bar)
        APIlib.getInstance().setActiveAnyChartView(view.pie_chart)
        val pie = AnyChart.pie()
        val dataPie: MutableList<DataEntry> = mutableListOf()
        dataPie.add(ValueDataEntry("Latte", 910))
        dataPie.add(ValueDataEntry("Americano", 902))
        dataPie.add(ValueDataEntry("Delhi Ice", 102))
        dataPie.add(ValueDataEntry("Mocca", 89))
        pie.data(data)

        pie.title("Lorem ipsum")
        pie.labels().position("outside")
        pie.legend().title().enabled(true)
        pie.legend().title()
            .text("Retail channels")
            .padding(0.0, 0.0, 10.0, 0.0)

        pie.legend()
            .position("center-bottom")
            .itemsLayout(LegendLayout.HORIZONTAL)
            .align(Align.CENTER)
        view.pie_chart.setChart(pie)
    }
}