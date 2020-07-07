package com.ydhnwb.paperlessapp.ui.detail_order

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.OrderHistory
import com.ydhnwb.paperlessapp.models.OrderHistoryDetail
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.ui.quickupdate.QuickUpdateActivity
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.showInfoAlert
import kotlinx.android.synthetic.main.activity_detail_order.*
import kotlinx.android.synthetic.main.content_detail_order.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailOrderActivity : AppCompatActivity(), DetailOrderAdapterInterface {
    private val detailOrderViewModel: DetailOrderViewModel by viewModel()
    private var downloadID : Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_order)
        setSupportActionBar(findViewById(R.id.toolbar))
        registerDownloadReceiver()
        setupToolbar()
        setupRecyclerView()
        downloadInvoice()
        observeState()
        fill()
    }

    private fun fill(){
        getOrder()?.let {
            detail_orderId.text = "#${it.id}"
            detail_order_date.text = it.datetime.toString()
        }
    }

    private fun registerDownloadReceiver() = registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    private fun observeState() = detailOrderViewModel.listenToState().observer(this, Observer { handleState(it) })

    private fun setupToolbar(){
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun downloadInvoice(){
        detail_order_invoice.setOnClickListener {
            PaperlessUtil.getToken(this)?.let { it1 -> detailOrderViewModel.downloadInvoice(it1, getOrder()?.id.toString()) }
        }
    }

    private fun setupRecyclerView(){
        detail_order_recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DetailOrderActivity)
            adapter = DetailOrderAdapter(getOrder()?.orderDetails!!, this@DetailOrderActivity, getIsPurchasement())
        }
    }

    private fun getOrder() = intent.getParcelableExtra<OrderHistory>("ORDER")

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                showInfoAlert(resources.getString(R.string.info_success_download))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadComplete)
    }

    private fun handleState(it: DetailOrderActivityState){
        when(it){
            is DetailOrderActivityState.Downloaded -> getReport(it.url)
        }
    }

    private fun getReport(url: String){
        val file = File(getExternalFilesDir(null), "Download")
        if (!file.exists()) { file.mkdirs() }
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Invoice")
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationUri(Uri.fromFile(file))
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS.toString(), "INVOICE-"+System.currentTimeMillis().toString()+".pdf")

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        downloadID = downloadManager!!.enqueue(request)
//        downloadManager.enqueue(request)
    }

    override fun goToStockActivity(orderDetail: OrderHistoryDetail) {
        startActivity(Intent(this@DetailOrderActivity, QuickUpdateActivity::class.java).apply {
            putExtra("order_detail", orderDetail)
            putExtra("store", getPassedProduct())
        })
    }

    private fun getPassedProduct() = intent.getParcelableExtra<Store>("store")
    private fun getIsPurchasement() = !intent.getBooleanExtra("is_in", false)
}