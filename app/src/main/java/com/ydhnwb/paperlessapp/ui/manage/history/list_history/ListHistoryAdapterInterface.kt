package com.ydhnwb.paperlessapp.ui.manage.history.list_history

import com.ydhnwb.paperlessapp.models.OrderHistory

interface ListHistoryAdapterInterface {
    fun click(orderHistory: OrderHistory)
}