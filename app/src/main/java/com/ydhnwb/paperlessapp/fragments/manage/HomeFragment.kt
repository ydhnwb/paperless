package com.ydhnwb.paperlessapp.fragments.manage

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.api.load
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.core.cartesian.series.Column
import com.anychart.enums.*
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.adapters.StoreMenuAdapter
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.StoreMenu
import kotlinx.android.synthetic.main.chart_bar.view.*
import kotlinx.android.synthetic.main.chart_pie.*
import kotlinx.android.synthetic.main.chart_pie.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var storeMenus : List<StoreMenu>

    companion object {
        fun instance(store: Store): HomeFragment{
            val args = Bundle()
            args.putParcelable("store", store)
            return HomeFragment().apply {
                arguments = args
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fill()
        storeMenu()
        dummyChart()
        dummyPie()
    }

    private fun fill(){
        arguments?.let {
            val store : Store = it.getParcelable("store")!!
            with(view!!){
                store_name.text = store.name
                store_address.text = store.address
                store_image.load(store.store_logo)
            }
        }
    }

    private fun storeMenu(){
        storeMenus = listOf(
            StoreMenu(resources.getString(R.string.store_menu_report), R.drawable.ic_doodle_mail, ContextCompat.getColor(activity!!, R.color.colorFlueGreen)),
            StoreMenu(resources.getString(R.string.store_menu_invitation), R.drawable.ic_doodle_connection, ContextCompat.getColor(activity!!, R.color.colorRed)),
            StoreMenu(resources.getString(R.string.store_menu_invitation), R.drawable.ic_doodle_connection, ContextCompat.getColor(activity!!, R.color.colorOrange)),
            StoreMenu(resources.getString(R.string.store_menu_invitation), R.drawable.ic_doodle_connection, ContextCompat.getColor(activity!!, R.color.colorGreen))
        )
        view!!.rv_store_menu.apply {
            adapter = StoreMenuAdapter(storeMenus, context)
            layoutManager = GridLayoutManager(activity, 2)
        }
    }

    private fun dummyChart(){
        view!!.bar_chart.setProgressBar(view!!.bar_progress_bar)
        APIlib.getInstance().setActiveAnyChartView(view!!.bar_chart)
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

        view!!.bar_chart.setChart(cartesian)

    }

    private fun dummyPie(){
        val data: MutableList<DataEntry> = mutableListOf()
        data.add(ValueDataEntry("Latte", 910))
        data.add(ValueDataEntry("Americano", 902))
        data.add(ValueDataEntry("Delhi Ice", 102))
        data.add(ValueDataEntry("Mocca", 89))

        view!!.pie_chart.setProgressBar(pie_progress_bar)
        APIlib.getInstance().setActiveAnyChartView(view!!.pie_chart)
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
        view!!.pie_chart.setChart(pie)
    }
}