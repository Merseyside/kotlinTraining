package com.kotlintraining.merseyside.kotlintraining.base.fragment

import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import com.kotlintraining.merseyside.kotlintraining.base.activity.BaseActivity
import com.kotlintraining.merseyside.kotlintraining.base.view.IView

abstract class BaseFragment : Fragment(), IView {

    private val TAG = "BaseFragment"
    private lateinit var context: Context

    lateinit var baseActivityView: BaseActivity
        private set
    protected var bundle: Bundle? = null


//    protected val appComponent: AppComponent
//        get() = CryptoApplication.getInstance().getApplicationComponent()
//
//    protected val rights: Rights
//        get() = CryptoApplication.getInstance().getRights()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
        if (context is BaseActivity) {
            baseActivityView = context
        }
    }

    override fun getContext(): Context {
        return context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        setTitle()
    }

    fun hideKeyboard() {
        baseActivityView.hideKeyboard()
    }

    override fun showMsg(msg: String) {
        baseActivityView.showMsg(msg)
    }

    override fun handleError(throwable: Throwable) {
        baseActivityView.handleError(throwable)
    }

    override fun showErrorMsg(msg: String) {
        baseActivityView.showErrorMsg(msg)
    }

    override fun showMsg(msg: String, actionMsg: String, clickListener: View.OnClickListener) {
        baseActivityView.showMsg(msg, actionMsg, clickListener)
    }

    override fun showErrorMsg(msg: String, actionMsg: String, clickListener: View.OnClickListener) {
        baseActivityView.showMsg(msg, actionMsg, clickListener)
    }

    private fun updateLanguage(context: Context) {
        setTitle()
    }

    protected abstract fun getTitle(context: Context): String

    private fun setTitle() {
        val title = getTitle(context)
        if (!TextUtils.isEmpty(title)) {
            baseActivityView.supportActionBar!!.setTitle(title)
        }
    }

    @CallSuper
    override fun onResume() {
        super.onResume()

        updateLanguage(getApplicationContext())
    }

    abstract fun getApplicationContext() : Context
}
