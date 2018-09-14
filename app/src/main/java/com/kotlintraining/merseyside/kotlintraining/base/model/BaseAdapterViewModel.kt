package com.kotlintraining.merseyside.kotlintraining.base.model

import com.kotlintraining.merseyside.kotlintraining.base.adapter.BaseAdapter

abstract class BaseAdapterViewModel : BaseObservable {

    private var listener: BaseAdapter.AdapterClickListener? = null

    fun setAdapterListener(listener: BaseAdapter.AdapterClickListener?) {
        this.listener = listener
    }
}
