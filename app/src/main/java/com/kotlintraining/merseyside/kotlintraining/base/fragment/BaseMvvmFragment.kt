package com.kotlintraining.merseyside.kotlintraining.base.fragment

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kotlintraining.merseyside.kotlintraining.base.model.BaseViewModel

abstract class BaseMvvmFragment<B : ViewDataBinding, M : BaseViewModel> : BaseFragment() {

    private var viewDataBinding: B? = null
    private var viewModel: M? = null

    private val errorMessageObserver = Observer<BaseViewModel.TextMessage> { this.showErrorMsg(it!!) }
    private val errorObserver = Observer<Throwable> { this.handleError(it!!) }
    private val messageObserver = Observer<BaseViewModel.TextMessage> { this.showMsg(it!!) }
    private val loadingObserver = Observer<Boolean> { this.loadingObserver(it!!) }
    private val clearObserver = Observer<Boolean> { clear() }

    abstract fun setBindingVariable(): Int

    @LayoutRes
    abstract fun setLayoutId(): Int

    abstract fun onCreateViewModel(): M


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performInjection()
        viewModel = onCreateViewModel()
        setHasOptionsMenu(false)
    }

    protected abstract fun performInjection()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, setLayoutId(), container, false)
        viewDataBinding!!.setLifecycleOwner(this)
        return viewDataBinding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding!!.setVariable(setBindingVariable(), viewModel)
        viewDataBinding!!.executePendingBindings()

        viewModel!!.updateLanguage(context)
        viewModel!!.errorLiveData.observe(this, errorObserver)
        viewModel!!.errorMessageLiveData.observe(this, errorMessageObserver)
        viewModel!!.messageLiveData.observe(this, messageObserver)
        viewModel!!.isLoadingLiveData.observe(this, loadingObserver)
        viewModel!!.clearAll.observe(this, clearObserver)
    }

    protected abstract fun clear()

    @CallSuper
    fun updateLanguage(context: Context) {
        viewModel!!.updateLanguage(context)
    }

    override fun onNetworkStateChanged(state: Int) {
        if (state == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            viewModel!!.networkConnectionLost()
        } else {
            viewModel!!.networkConnectionEstablished()
        }
    }

    protected abstract fun loadingObserver(isLoading: Boolean)

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
