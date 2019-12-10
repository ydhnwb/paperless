package com.ydhnwb.paperlessapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.adapters.NotificationAdapter
import com.ydhnwb.paperlessapp.contracts.fragments.NotificationFragmentContract
import com.ydhnwb.paperlessapp.models.Notification
import com.ydhnwb.paperlessapp.presenters.activities.MainActivityPresenter
import com.ydhnwb.paperlessapp.presenters.fragments.NotificationFragmentPresenter
import kotlinx.android.synthetic.main.fragment_notifications.view.*

class NotificationFragment(private var mainPresenter : MainActivityPresenter) : Fragment(), NotificationFragmentContract.View {
    private var presenter = NotificationFragmentPresenter(this)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.load()
    }

    override fun attachToRecycler(notifications : List<Notification>) {
        view!!.rv_notification.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = NotificationAdapter(activity!!, notifications)
        }
    }

    override fun isLoading(state: Boolean) {
        if(state){
            view!!.loading_bar.visibility = View.VISIBLE
        }else{
            view!!.loading_bar.visibility = View.GONE
        }
    }

    override fun toast(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}