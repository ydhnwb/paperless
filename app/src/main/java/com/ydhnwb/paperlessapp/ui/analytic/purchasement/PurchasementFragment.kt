package com.ydhnwb.paperlessapp.ui.analytic.purchasement

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
import com.anychart.enums.Anchor
import com.anychart.enums.HoverMode
import com.anychart.enums.Position
import com.anychart.enums.TooltipPositionMode
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.showToast
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.fragment_purchasement_analytic.view.*
import kotlinx.android.synthetic.main.fragment_purchasement_analytic.view.loading
import org.koin.androidx.viewmodel.ext.android.viewModel

class PurchasementFragment : Fragment(R.layout.fragment_purchasement_analytic){
    private val purchasementViewModel : PurchasementFragmentViewModel by viewModel()

    companion object {
        fun instance(store: Store?) : PurchasementFragment {
            if (store != null){
                val a = Bundle()
                a.putParcelable("store", store)
                return PurchasementFragment().apply { arguments = a }
            }
            return PurchasementFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
    }

    private fun observe(){
        arguments?.let {
            val store = it.getParcelable<Store>("store")
            store?.let { s ->
                purchasementViewModel.listenToState().observer(viewLifecycleOwner, Observer { state -> handleState(state) })
                purchasementViewModel.listenToSpentByMonth().observe(viewLifecycleOwner, Observer { d -> handleSpentByMonth(d) })
                purchasementViewModel.fetchStoreInfo(PaperlessUtil.getToken(requireActivity()), s.id.toString())
            }
        }
    }

    private fun handleSpentByMonth(it: HashMap<String, Int>){
        if(purchasementViewModel.listenToSpentByMonth().value != null){
            view!!.bar_chart_spent_month.setProgressBar(view!!.bar_progress_bar_spent_month)
            APIlib.getInstance().setActiveAnyChartView(view!!.bar_chart_spent_month)

            val cartesian: Cartesian = AnyChart.column()

            val data: MutableList<DataEntry> = ArrayList()
            it.forEach { (key, v) -> data.add(ValueDataEntry(key, v)) }

            val column: Column = cartesian.column(data)

            column.tooltip().titleFormat("{%X}").position(Position.CENTER_BOTTOM).anchor(Anchor.CENTER_BOTTOM).offsetX(0.0)
                .offsetY(5.0)
                .format("Pengeluaran .{%Value}{groupsSeparator: }")

            cartesian.animation(false)
            cartesian.yScale().minimum(0.0)
            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }")
            cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
            cartesian.interactivity().hoverMode(HoverMode.BY_X)

            cartesian.xAxis(0).title("Bulan")
            cartesian.yAxis(0).title("Pengeluaran")
            view!!.bar_chart_spent_month.setChart(cartesian)
        }
    }

    private fun handleState(it: PurchasementFragmentState){
        when(it){
            is PurchasementFragmentState.IsLoading -> {
                if(it.state){
                    view!!.loading.visible()
                    view!!.analytic_spent_month.gone()
                }else{
                    view!!.loading.gone()
                    view!!.analytic_spent_month.visible()
                }
            }
            is PurchasementFragmentState.ShowToast -> requireContext().showToast(it.message)
        }
    }
}