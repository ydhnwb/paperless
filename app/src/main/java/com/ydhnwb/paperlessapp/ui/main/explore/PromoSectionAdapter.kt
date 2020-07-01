package com.ydhnwb.paperlessapp.ui.main.explore

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Product
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters

internal class PromoSectionAdapter(private val headerTitle: String, itineraries: List<Product>, private val context: Context) :

    Section(SectionParameters.builder().itemResourceId(R.layout.list_item_catalog_alt).headerResourceId(R.layout.item_promo_header).build()) {

    private var products: List<Product> = itineraries

    override fun getContentItemsTotal() = products.size

    override fun getItemViewHolder(view: View) = PromoItemViewHolder(view)

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) = (holder as PromoItemViewHolder).bind(products[position])

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) = (holder as HeaderItemViewHolder).bind(headerTitle)

    override fun getHeaderViewHolder(view: View) = HeaderItemViewHolder(view)

}