package com.kotlintraining.merseyside.kotlintraining.base.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kotlintraining.merseyside.kotlintraining.base.model.BaseAdapterViewModel
import com.kotlintraining.merseyside.kotlintraining.base.view.BaseViewHolder

abstract class BaseAdapter<T : BaseAdapterViewModel> : RecyclerView.Adapter<BaseViewHolder>() {

    private var listener: AdapterClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false)

        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val obj = getObjForPosition(position)
        obj.setAdapterListener(listener)
        holder.bind(obj)
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition(position)
    }

    protected abstract fun getObjForPosition(position: Int): T

    protected abstract fun getLayoutIdForPosition(position: Int): Int

    fun setOnAdapterClickListener(listener: AdapterClickListener) {
        this.listener = listener
    }

    interface AdapterClickListener {
        fun onItemClicked(obj: Any)
    }
}
