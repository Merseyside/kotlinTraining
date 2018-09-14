package com.kotlintraining.merseyside.kotlintraining.base.model

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.View

import com.merseyside.cripto.CryptoApplication
import com.merseyside.cripto.domain.models.Rights

abstract class BaseViewModel protected constructor(lifecycle: Lifecycle) : ViewModel(), LifecycleObserver {

    private val TAG = "BaseViewModel"

    private lateinit var bundle: Bundle

    val inProgress = ObservableBoolean(false)
    val errorLiveData: MutableLiveData<Throwable>
    val errorMessageLiveData: MutableLiveData<TextMessage>
    val messageLiveData: MutableLiveData<TextMessage>
    val isLoadingLiveData: MutableLiveData<Boolean>
    val clearAll: MutableLiveData<Boolean>

//    val rights: Rights
//        get() = CryptoApplication.getInstance().getRights()

    inner class TextMessage {
        var msg: String? = null
        var actionMsg: String? = null
        var listener: View.OnClickListener? = null
    }

    init {
        lifecycle.addObserver(this)

        errorLiveData = MutableLiveData()
        messageLiveData = MutableLiveData()
        isLoadingLiveData = MutableLiveData()
        clearAll = MutableLiveData()
        errorMessageLiveData = MutableLiveData()
    }

    protected fun handleError(throwable: Throwable) {
        errorLiveData.setValue(throwable)
    }

    protected fun showMsg(msg: String) {
        val textMessage = TextMessage()
        textMessage.msg = msg
        messageLiveData.setValue(textMessage)
    }

    protected fun showErrorMsg(msg: String) {
        val textMessage = TextMessage()
        textMessage.msg = msg
        errorMessageLiveData.setValue(textMessage)
    }

    protected fun showMsg(msg: String, actionMsg: String, listener: View.OnClickListener) {
        val textMessage = TextMessage()
        textMessage.msg = msg
        textMessage.actionMsg = actionMsg
        textMessage.listener = listener
        messageLiveData.setValue(textMessage)
    }

    protected fun showErrorMsg(msg: String, actionMsg: String, listener: View.OnClickListener) {
        val textMessage = TextMessage()
        textMessage.msg = msg
        textMessage.actionMsg = actionMsg
        textMessage.listener = listener
        errorMessageLiveData.setValue(textMessage)
    }

    protected fun clearUi() {
        clearAll.setValue(true)
    }

    override fun onCleared() {
        super.onCleared()
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    protected fun onCreate() {
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected fun onStart() {
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected fun onResume() {
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected fun onPause() {
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected fun onStop() {
        clearDisposables()
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected fun onDestroy() {
        dispose()
    }

    fun onError(throwable: Throwable) {}

    @CallSuper
    protected fun showProgress() {
        inProgress.set(true)
        isLoadingLiveData.setValue(true)
    }

    @CallSuper
    protected fun hideProgress() {
        inProgress.set(false)
        isLoadingLiveData.setValue(false)
    }

    protected abstract fun dispose()

    protected abstract fun clearDisposables()

    fun setData(data: Bundle) {
        bundle = data
    }

    abstract fun updateLanguage(context: Context)

//    abstract fun networkConnectionLost()
//
//    abstract fun networkConnectionEstablished()
}
