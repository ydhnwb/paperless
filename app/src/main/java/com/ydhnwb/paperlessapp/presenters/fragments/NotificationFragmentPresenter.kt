package com.ydhnwb.paperlessapp.presenters.fragments

import android.os.Handler
import com.ydhnwb.paperlessapp.contracts.fragments.NotificationFragmentContract
import com.ydhnwb.paperlessapp.models.Notification

class NotificationFragmentPresenter(private var view : NotificationFragmentContract.View?) : NotificationFragmentContract.Interactor{
    override fun load() {
        view?.isLoading(true)
        Handler().postDelayed(Runnable {
            val list = mutableListOf<Notification>().apply {
                add(Notification(1, "Prieyudha Akadita S", "Menambahkan anda sebagai karyawan di tokonya", "Prieyudha Akadita S menambahkan anda sebagai karyawan di tokonya", "19 mins", "https://avatars1.githubusercontent.com/u/26734262?s=460&v=4"))
                add(Notification(2, "Tirta Aries", "Laporan bulanan", "Laporan bulanan telah dibuat secara otomatis oleh sistem", "23 mins", "https://media-cdn.tripadvisor.com/media/photo-s/0f/6e/e3/0e/interior-del-cafe-the.jpg"))
                add(Notification(3, "Tirta Aries", "Laporan bulanan", "Laporan bulanan telah dibuat secara otomatis oleh sistem", "31 days", "https://media-cdn.tripadvisor.com/media/photo-s/0f/6e/e3/0e/interior-del-cafe-the.jpg"))
                add(Notification(4, "Bambang Gentolet", "Menambahkan anda sebagai kasir di tokonya", "Bambang Gentolet menambahkan anda sebagai kasir di tokonya", "40 days", "https://www.biography.com/.image/t_share/MTQxNDkyMjM0OTA1MjAwMzc5/edward_snowden_getty_images_170248179_photo_by_the_guardian_via_getty_images_croppedjpg.jpg"))
                add(Notification(5, "Seblak Gledeg", "Laporan bulanan", "Laporan bulanan telah dibuat secara otomatis oleh sistem", "48 days", "https://2.bp.blogspot.com/-weQyFcB7zHU/WckBJLcRvgI/AAAAAAAABck/UTceOYdW-HUGlhFMgcPDlQy-VcLLxYeNgCEwYBhgL/s1600/fullarea.jpg"))
            }
            view?.attachToRecycler(list)
            view?.isLoading(false)
        }, 2000)
    }

    override fun destroy() { view = null }

}