package com.kotlintraining.merseyside.kotlintraining.base.activity

import android.content.Context
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.github.javiersantos.bottomdialogs.BottomDialog
import com.kotlintraining.merseyside.kotlintraining.R
import com.kotlintraining.merseyside.kotlintraining.base.view.IActivityView
import com.merseyside.admin.merseylibrary.presentation.ActivityUtils
import com.merseyside.cripto.CryptoApplication
import com.merseyside.cripto.R
import com.merseyside.cripto.presentation.base.view.IActivityView
import com.merseyside.cripto.presentation.di.components.ActivityComponent
import com.merseyside.cripto.presentation.di.components.AppComponent
import com.merseyside.cripto.presentation.di.components.DaggerActivityComponent
import com.merseyside.cripto.presentation.di.modules.ActivityModule
import com.merseyside.cripto.utils.PrefsHelper
import com.merseyside.cripto.utils.networkUtils.NetworkStateReceiver

import javax.inject.Inject

import io.github.inflationx.viewpump.ViewPumpContextWrapper

/**
 * Created by merseyside on 13.09.18.
 */

abstract class BaseActivity : AppCompatActivity(), IActivityView {

    private val TAG = "BaseActivity"

    var context: Context? = null
        private set

    @Inject
    internal var networkReceiver: NetworkStateReceiver? = null

    @Inject
    protected var prefsHelper: PrefsHelper? = null

    protected val appComponent: AppComponent
        get() = CryptoApplication.getInstance().getApplicationComponent()

    protected val activityModule: ActivityModule
        get() = ActivityModule(this)

    val activityComponent: ActivityComponent
        get() = DaggerActivityComponent.builder()
                .appComponent(appComponent)
                .activityModule(activityModule)
                .build()

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)

        registerNetworkReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkReceiver()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(context = ViewPumpContextWrapper.wrap(CryptoApplication.getInstance().getLocaleHelper().onAttach(newBase)))
    }

    abstract fun updateLanguage(context: Context?)

    @CallSuper
    override fun updateLanguage() {
        context = CryptoApplication.getInstance().getLocaleHelper().onAttach(this)
        updateLanguage(context)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        //bindService();
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        //unbindService();
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        updateLanguage()
    }

    override fun showMsg(msg: String) {
        showSnackbar(msg, Snackbar.LENGTH_SHORT, R.color.message_color)
    }

    override fun showErrorMsg(msg: String) {
        showSnackbar(msg, Snackbar.LENGTH_LONG, R.color.error_message_color)
    }

    override fun showMsg(msg: String, actionMsg: String, clickListener: View.OnClickListener) {
        showSnackbar(msg, Snackbar.LENGTH_INDEFINITE, R.color.message_color, R.color.light_blue, actionMsg, clickListener)
    }

    override fun showErrorMsg(msg: String, actionMsg: String, clickListener: View.OnClickListener) {
        showSnackbar(msg, Snackbar.LENGTH_INDEFINITE, R.color.error_message_color, R.color.white, actionMsg, clickListener)
    }

    private fun showSnackbar(message: String, length: Int, color: Int) {
        createBaseSnackbar(message, length, color).show()
    }

    protected fun showSnackbar(message: String, length: Int, color: Int, actionColor: Int, actionMsg: String, clickListener: View.OnClickListener) {
        val snackbar = createBaseSnackbar(message, length, color)
        snackbar.setAction(actionMsg, clickListener)
        snackbar.setActionTextColor(ContextCompat.getColor(this, actionColor))
        snackbar.show()
    }

    protected fun createBaseSnackbar(message: String, length: Int, color: Int): Snackbar {
        val viewGroup = (this
                .findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        val snackbar = Snackbar.make(viewGroup, message, length)
        val snackbarView = snackbar.view

        snackbarView.setBackgroundColor(ContextCompat.getColor(this, color))

        val snackTextView = snackbarView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        snackTextView.setTextColor(ContextCompat.getColor(this, R.color.white))

        val font = Typeface.createFromAsset(context!!.assets, "fonts/Roboto-Regular.ttf")
        var tv = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        tv.typeface = font
        tv = snackbar.view.findViewById(android.support.design.R.id.snackbar_action)
        tv.typeface = font

        return snackbar
    }

    override fun hideKeyboard() {
        ActivityUtils.hideKeyboard(this)
    }

    private fun registerNetworkReceiver() {
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkReceiver, intentFilter)
        networkReceiver!!.setNetworkStateChangeListener{ state : Int -> this.onNetworkStateChanged(state) }
    }

    private fun unregisterNetworkReceiver() {
        unregisterReceiver(networkReceiver)
    }

    override fun showBottomDialog() {
        showBottomDialog(context!!.getString(R.string.log_in), context!!.getString(R.string.please_auth),
                context!!.getString(R.string.next_time), context!!.getString(R.string.log_in))
    }

    override fun showBottomDialog(title: String, content: String, negativeText: String, positiveText: String) {

        BottomDialog.Builder(this)
                .setTitle(title)
                .setContent(content)
                .setPositiveText(positiveText)
                .setNegativeText(negativeText)
                .setPositiveTextColorResource(R.color.white)
                .setNegativeTextColorResource(R.color.bid_color)
                .setPositiveBackgroundColorResource(R.color.ask_color)
                .onPositive({ bottomDialog : BottomDialog -> onBottomDialogPositiveClick() })
                .onNegative({ bottomDialog : BottomDialog -> onBottomDialogNegativeClick() })
                .show()
    }

    protected abstract fun onBottomDialogPositiveClick()

    protected abstract fun onBottomDialogNegativeClick()
}
