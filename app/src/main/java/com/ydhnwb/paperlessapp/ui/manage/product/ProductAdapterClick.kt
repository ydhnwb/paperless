package com.ydhnwb.paperlessapp.ui.manage.product

import com.ydhnwb.paperlessapp.models.Product

interface ProductAdapterClick {
    fun changeAvailibility(currentProduct: Product)
    fun click(product: Product)
}