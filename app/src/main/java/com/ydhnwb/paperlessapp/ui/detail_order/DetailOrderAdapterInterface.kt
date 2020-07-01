package com.ydhnwb.paperlessapp.ui.detail_order

import com.ydhnwb.paperlessapp.models.OrderHistoryDetail

interface DetailOrderAdapterInterface {
    fun goToStockActivity(orderDetail: OrderHistoryDetail)
}