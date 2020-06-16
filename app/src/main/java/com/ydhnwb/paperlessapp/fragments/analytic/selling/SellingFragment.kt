package com.ydhnwb.paperlessapp.fragments.analytic.selling

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.anychart.enums.*
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.ProfitByMonth
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.chart_bar.view.*
import kotlinx.android.synthetic.main.chart_pie.view.*
import kotlinx.android.synthetic.main.fragment_selling_analytic.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SellingFragment : Fragment(R.layout.fragment_selling_analytic){
    private val sellingViewModel: SellingAnalyticViewModel by viewModel()

    companion object {
        fun instance(store: Store?) : SellingFragment {
            if (store != null){
                val a = Bundle()
                a.putParcelable("store", store)
                return SellingFragment().apply { arguments = a }
            }
            return SellingFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val store = it.getParcelable<Store>("store")
            store?.let { s->
                sellingViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { d -> handleUIState(d) })
                sellingViewModel.listenToSellingByProfit().observe(viewLifecycleOwner, Observer { d -> handleProfitByMonth(d) })
                sellingViewModel.listenToSellingByMonth().observe(viewLifecycleOwner, Observer { d -> handleSellingByMonth(d) })
                sellingViewModel.listenToSellingByHour().observe(viewLifecycleOwner, Observer { d -> handleSellingByHour(d) })
                sellingViewModel.listenToSellingProductCluster().observe(viewLifecycleOwner, Observer { data -> handlePie(data) })
                sellingViewModel.fetchStoreInfo(PaperlessUtil.getToken(requireActivity()), s.id.toString())
            }
        }
    }

    private fun handleUIState(it: SellingAnalyticState){
        when(it){
            is SellingAnalyticState.IsLoading -> {
                if(it.state){
                    view!!.loading.visible()
                    view!!.analytic_hour.gone()
                    view!!.analytic_sebaran.gone()
                    view!!.analytic_month.gone()
                    view!!.analytic_month_profit.gone()
                }else{
                    view!!.loading.gone()
                    view!!.analytic_hour.visible()
                    view!!.analytic_sebaran.visible()
                    view!!.analytic_month.visible()
                    view!!.analytic_month_profit.visible()
                }
            }
        }
    }

    private fun handlePie(it: HashMap<String, Int>){
        if(sellingViewModel.listenToSellingProductCluster().value != null){
            val data: MutableList<DataEntry> = mutableListOf()
            it.forEach { (k, v) -> data.add(ValueDataEntry(k, v)) }
            view!!.pie_chart.setProgressBar(view!!.pie_progress_bar)
            APIlib.getInstance().setActiveAnyChartView(view!!.pie_chart)
            val pie = AnyChart.pie()
            val dataPie: MutableList<DataEntry> = mutableListOf()
            it.forEach { (t, u) -> dataPie.add(ValueDataEntry(t, u)) }
            pie.data(data)
            pie.labels().position("outside")
            pie.legend().position("center-bottom").itemsLayout(LegendLayout.HORIZONTAL).align(Align.CENTER)
            view!!.pie_chart.setChart(pie)
        }
    }

    private fun handleSellingByMonth(it: HashMap<String, Int>){
        if(sellingViewModel.listenToSellingByMonth().value != null){
            view!!.bar_chart_monthly.setProgressBar(view!!.bar_progress_bar_monthly)
            APIlib.getInstance().setActiveAnyChartView(view!!.bar_chart_monthly)


            val cartesian: Cartesian = AnyChart.column()

            val data: MutableList<DataEntry> = ArrayList()
            it.forEach { (k, v) -> data.add(ValueDataEntry(k, v)) }

            val column: Column = cartesian.column(data)

            column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0.0)
                .offsetY(5.0)
                .format("Jumlah trans.{%Value}{groupsSeparator: }")

            cartesian.animation(false)
            cartesian.yScale().minimum(0.0)
            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }")
            cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
            cartesian.interactivity().hoverMode(HoverMode.BY_X)

            cartesian.xAxis(0).title("Periode")
            cartesian.yAxis(0).title("Jumlah transaksi")
            view!!.bar_chart_monthly.setChart(cartesian)

        }
    }

    private fun handleProfitByMonth(it: HashMap<String, Int>){
        if(sellingViewModel.listenToSellingByMonth().value != null){
            view!!.bar_chart_monthly_profit.setProgressBar(view!!.bar_progress_bar_monthly_profit)
            APIlib.getInstance().setActiveAnyChartView(view!!.bar_chart_monthly_profit)


            val cartesian: Cartesian = AnyChart.column()

            val data: MutableList<DataEntry> = ArrayList()
            it.forEach { (key, v) -> data.add(ValueDataEntry(key, v)) }

            val column: Column = cartesian.column(data)

            column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0.0)
                .offsetY(5.0)
                .format("Penjualan .{%Value}{groupsSeparator: }")

            cartesian.animation(false)
            cartesian.yScale().minimum(0.0)
            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }")
            cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
            cartesian.interactivity().hoverMode(HoverMode.BY_X)

            cartesian.xAxis(0).title("Bulan")
            cartesian.yAxis(0).title("Penjualan")
            view!!.bar_chart_monthly_profit.setChart(cartesian)

        }
    }

    private fun handleSellingByHour(it: HashMap<Int, Int>){
        if(sellingViewModel.listenToSellingByHour().value != null){
            var generatedHour = hashMapOf<String, Int>()
            for (i in 0..23){
                val candidateKey = if (i < 10) "0$i" else i.toString()
                if(it.containsKey(i)) generatedHour[candidateKey] = it.getValue(i) else generatedHour[candidateKey] = 0
            }
            generatedHour = generatedHour.entries.sortedBy { it.key }.associate { it.toPair() } as HashMap<String, Int>
            view!!.bar_chart.setProgressBar(view!!.bar_progress_bar)
            APIlib.getInstance().setActiveAnyChartView(view!!.bar_chart)
            val cartesian = AnyChart.cartesian()
            val data: MutableList<DataEntry> = mutableListOf()
            generatedHour.forEach { (k, v) -> data.add(ValueDataEntry(k, v)) }
            val column: Column = cartesian.column(data)

            cartesian.animation(false)
            cartesian.yScale().minimum(0.0)
            cartesian.yAxis(0).labels().format("{%Value}{numDecimals:0}")
            cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
            cartesian.tooltip().format("Jumlah transaksi {%Value}")
            cartesian.interactivity().hoverMode(HoverMode.BY_X)


            cartesian.xAxis(0).title("Jam")
            cartesian.yAxis(0).title("Jumlah transaksi")
            view!!.bar_chart.setChart(cartesian)
        }
    }

}