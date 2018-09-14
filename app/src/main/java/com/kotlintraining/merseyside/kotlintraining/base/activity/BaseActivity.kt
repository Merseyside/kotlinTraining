package com.kotlintraining.merseyside.kotlintraining.base.activity

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.support.annotation.CallSuper
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.kotlintraining.merseyside.kotlintraining.R
import com.kotlintraining.merseyside.kotlintraining.base.view.IActivityView

/**
 * Created by merseyside on 13.09.18.
 */

abstract class BaseActivity : AppCompatActivity(), IActivityView {

    private val TAG = "BaseActivity"

    var context: Context? = null
        private set


//    @Inject
//    internal var networkReceiver: NetworkStateReceiver? = null
//
//    @Inject
//    protected var prefsHelper: PrefsHelper? = null

//    protected val appComponent: AppComponent
//        get() = CryptoApplication.getInstance().getApplicationComponent()
//
//    protected val activityModule: ActivityModule
//        get() = ActivityModule(this)
//
//    val activityComponent: ActivityComponent
//        get() = DaggerActivityComponent.builder()
//                .appComponent(appComponent)
//                .activityModule(activityModule)
//                .build()

//    override fun attachBaseContext(newBase: Context) {
//        context = ViewPumpContextWrapper.wrap(CryptoApplication.getInstance().getLocaleHelper().onAttach(newBase))
//        super.attachBaseContext(context)
//    }

    abstract fun updateLanguage(context: Context?)

//    @CallSuper
//    override fun updateLanguage() {
//        context = CryptoApplication.getInstance().getLocaleHelper().onAttach(this)
//        updateLanguage(context)
//    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        updateLanguage()
    }

    /*Should override in child*/
    override fun showMsg(msg: String) {
        showSnackbar(msg, Snackbar.LENGTH_SHORT, android.R.color.white)
    }

    override fun showErrorMsg(msg: String) {
        showSnackbar(msg, Snackbar.LENGTH_LONG, android.R.color.white)
    }

    override fun showMsg(msg: String, actionMsg: String, clickListener: View.OnClickListener) {
        showSnackbar(msg, Snackbar.LENGTH_INDEFINITE, android.R.color.white, R.attr.colorAccent, actionMsg, clickListener)
    }

    override fun showErrorMsg(msg: String, actionMsg: String, clickListener: View.OnClickListener) {
        showSnackbar(msg, Snackbar.LENGTH_INDEFINITE, android.R.color.white, android.R.color.holo_red_dark, actionMsg, clickListener)
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

    private fun createBaseSnackbar(message: String, length: Int, color: Int): Snackbar {
        val viewGroup = (this
                .findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        val snackbar = Snackbar.make(viewGroup, message, length)
        val snackbarView = snackbar.view

        snackbarView.setBackgroundColor(ContextCompat.getColor(this, color))

        val snackTextView = snackbarView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        snackTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white))

        val font = Typeface.createFromAsset(context!!.assets, "fonts/Roboto-Regular.ttf")
        var tv = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        tv.typeface = font
        tv = snackbar.view.findViewById(android.support.design.R.id.snackbar_action)
        tv.typeface = font

        return snackbar
    }

    override fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

//    private fun registerNetworkReceiver() {
//        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
//        registerReceiver(networkReceiver, intentFilter)
//        networkReceiver!!.setNetworkStateChangeListener{ state : Int -> this.onNetworkStateChanged(state) }
//    }
//
//    private fun unregisterNetworkReceiver() {
//        unregisterReceiver(networkReceiver)
//    }


//    override fun showBottomDialog() {
//        showBottomDialog(context!!.getString(R.string.log_in), context!!.getString(R.string.please_auth),
//                context!!.getString(R.string.next_time), context!!.getString(R.string.log_in))
//    }
//
//    override fun showBottomDialog(title: String, content: String, negativeText: String, positiveText: String) {
//
//        BottomDialog.Builder(this)
//                .setTitle(title)
//                .setContent(content)
//                .setPositiveText(positiveText)
//                .setNegativeText(negativeText)
//                .setPositiveTextColorResource(R.color.white)
//                .setNegativeTextColorResource(R.color.bid_color)
//                .setPositiveBackgroundColorResource(R.color.ask_color)
//                .onPositive({ bottomDialog : BottomDialog -> onBottomDialogPositiveClick() })
//                .onNegative({ bottomDialog : BottomDialog -> onBottomDialogNegativeClick() })
//                .show()
//    }
//
//    protected abstract fun onBottomDialogPositiveClick()
//
//    protected abstract fun onBottomDialogNegativeClick()
}
