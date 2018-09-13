package com.kotlintraining.merseyside.kotlintraining.base.activity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.text.TextUtils
import android.util.Log

import com.merseyside.cripto.presentation.base.model.BaseViewModel
import com.merseyside.cripto.utils.networkUtils.NetworkUtil

abstract class BaseMvvmActivity<B : ViewDataBinding, M : BaseViewModel> : BaseActivity() {

    private var viewDataBinding: B? = null
    private var viewModel: M? = null

    private val errorObserver = Observer<BaseViewModel.TextMessage> { this.showErrorMsg(it) }
    private val messageObserver = Observer<BaseViewModel.TextMessage> { this.showMsg(it) }
    private val loadingObserver = Observer<Boolean> { this.loadingObserver(it!!) }
    private val clearObserver = { clear() }

    abstract fun setBindingVariable(): Int

    @LayoutRes
    abstract fun setLayoutId(): Int

    abstract fun onCreateViewModel(): M

    protected abstract fun performInjection()

    override fun onCreate(savedInstance: Bundle?) {
        performInjection()
        performDataBinding()

        super.onCreate(savedInstance)

        init()
    }

    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, setLayoutId())
        viewDataBinding!!.setLifecycleOwner(this)
        viewModel = if (viewModel == null) onCreateViewModel() else viewModel
        viewDataBinding!!.setVariable(setBindingVariable(), viewModel)
        viewDataBinding!!.executePendingBindings()
    }

    private fun init() {
        viewModel!!.updateLanguage(context)
        viewModel!!.getErrorMessageLiveData().observe(this, errorObserver)
        viewModel!!.getMessageLiveData().observe(this, messageObserver)
        viewModel!!.getIsLoadingLiveData().observe(this, loadingObserver)
        viewModel!!.getClearAll().observe(this, clearObserver)
    }

    protected abstract fun clear()

    override fun handleError(throwable: Throwable) {
        viewModel!!.onError(throwable)
    }

    override fun updateLanguage(context: Context?) {
        Log.d("BaseMvvmActivity", "update")
        viewModel!!.updateLanguage(context)
    }

    protected abstract fun loadingObserver(isLoading: Boolean)

    @CallSuper
    override fun onNetworkStateChanged(state: Int) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            if (state == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED)
                viewModel!!.networkConnectionLost()
            else
                viewModel!!.networkConnectionEstablished()
        }
    }


    protected fun showErrorMsg(textMessage: BaseViewModel.TextMessage) {
        if (TextUtils.isEmpty(textMessage.actionMsg)) {
            showErrorMsg(textMessage.msg)
        } else {
            showErrorMsg(textMessage.msg, textMessage.actionMsg, textMessage.listener)
        }
    }

    protected fun showMsg(textMessage: BaseViewModel.TextMessage) {
        if (TextUtils.isEmpty(textMessage.actionMsg)) {
            showMsg(textMessage.msg)
        } else {
            showMsg(textMessage.msg, textMessage.actionMsg, textMessage.listener)
        }
    }
}
