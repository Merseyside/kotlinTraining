package com.kotlintraining.merseyside.kotlintraining.base.dialog

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kotlintraining.merseyside.kotlintraining.base.model.BaseViewModel

import com.merseyside.cripto.presentation.base.model.BaseViewModel

abstract class BaseMvvmDialog<B : ViewDataBinding, M : BaseViewModel> : BaseDialog() {

    var viewDataBinding: B? = null
        private set
    private var viewModel: M? = null

    private val errorObserver = Observer<Throwable> { this.showError(it!!) }
    private val messageObserver = Observer<BaseViewModel.TextMessage> { this.showMsg(it!!) }

    override fun onCreate(onSavedInstanceState: Bundle?) {
        performInjection()
        super.onCreate(onSavedInstanceState)
        viewModel = setViewModel()
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, setLayoutId(), container, false)
        return viewDataBinding!!.getRoot()
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding!!.setVariable(setBindingVariable(), viewModel)
        viewDataBinding!!.executePendingBindings()

        viewModel!!.updateLanguage(baseActivity!!.context)

        viewModel!!.errorLiveData.observe(this, errorObserver)
        viewModel!!.messageLiveData.observe(this, messageObserver)
    }

    protected abstract fun performInjection()

    abstract fun setBindingVariable(): Int

    @LayoutRes
    abstract fun setLayoutId(): Int

    abstract fun setViewModel(): M

    private fun showMsg(textMessage: BaseViewModel.TextMessage) {
        if (TextUtils.isEmpty(textMessage.actionMsg)) {
            showMsg(textMessage.msg)
        } else {
            showMsg(textMessage.msg, textMessage.actionMsg, textMessage.listener)
        }
    }
}
