package com.ydhnwb.paperlessapp.ui.manage.home

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.manage.ManageStoreState
import com.ydhnwb.paperlessapp.ui.manage.ManageStoreViewModel
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.StoreMenu
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.showInfoAlert
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File


class HomeFragment : Fragment(R.layout.fragment_home), HomeClickInterface {
    private lateinit var storeMenus : List<StoreMenu>
    private val parentStoreViewModel : ManageStoreViewModel by sharedViewModel()
    private var downloadID : Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        observe()
        storeMenu()
    }

    private fun observe(){
        observeState()
        observeCurrentStore()
    }

    private fun observeCurrentStore() = parentStoreViewModel.listenToCurrentStore().observe(viewLifecycleOwner, Observer{ handleCurrentStore(it) })

    private fun observeState() = parentStoreViewModel.listenToUIState().observe(viewLifecycleOwner, Observer { handleState(it) })

    private fun handleCurrentStore(it: Store){
        with(view!!){
            store_name.text = it.name
            store_address.text = it.address
            store_image.load(it.store_logo)
            view!!.rv_store_menu.apply {
                adapter =
                    StoreMenuAdapter(
                        storeMenus,
                        context,
                        parentStoreViewModel.listenToCurrentStore().value!!,
                        this@HomeFragment
                    )
                layoutManager = GridLayoutManager(activity, 2)
            }
        }
    }

    private fun storeMenu(){
        storeMenus = listOf(
            StoreMenu(resources.getString(R.string.store_menu_analytic), R.drawable.ic_doodle_mail, ContextCompat.getColor(activity!!, R.color.colorFlueGreen)),
            StoreMenu(resources.getString(R.string.store_menu_invitation), R.drawable.ic_doodle_connection, ContextCompat.getColor(activity!!, R.color.colorRed)),
            StoreMenu(resources.getString(R.string.store_menu_report), R.drawable.ic_doodle_enthusiast, ContextCompat.getColor(activity!!, R.color.colorOrange)),
            StoreMenu(resources.getString(R.string.store_menu_more), R.drawable.ic_doodle_connection, ContextCompat.getColor(activity!!, R.color.colorGreen))
        )
    }

    private fun getReport(url: String){
        val file = File(requireActivity().getExternalFilesDir(null), "Download")
        if (!file.exists()) { file.mkdirs() }
        val request = DownloadManager.Request(Uri.parse(url))
                .setTitle("Report")
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationUri(Uri.fromFile(file))
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS.toString(), "REPORT-"+System.currentTimeMillis().toString()+".xlsx")

        val downloadManager = requireActivity().getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
        downloadID = downloadManager!!.enqueue(request)
    }

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                requireContext().showInfoAlert(resources.getString(R.string.info_success_download_report))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(onDownloadComplete)
    }

    private fun handleState(it: ManageStoreState){
        when(it){
            is ManageStoreState.DownloadedUrl -> {
                getReport(it.url)
            }
        }
    }

    override fun report() {
        PaperlessUtil.getToken(requireActivity())?.let {
            parentStoreViewModel.downloadReport(
                it, parentStoreViewModel.listenToCurrentStore().value!!.id.toString())
        }
    }

}

interface HomeClickInterface {
    fun report()
}