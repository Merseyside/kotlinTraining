package com.kotlintraining.merseyside.kotlintraining.base.view

import android.support.v7.widget.RecyclerView

import com.merseyside.cripto.BR

class BaseViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.getRoot()) {

    fun bind(obj: Any) {
        binding.setVariable(BR.obj, obj)
        binding.executePendingBindings()
    }
}
