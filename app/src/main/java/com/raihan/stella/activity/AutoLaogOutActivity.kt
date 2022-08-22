package com.raihan.stella.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.raihan.stella.model.DialogCustom
import java.util.*


open class AutoLaogOutActivity : AppCompatActivity(), View.OnTouchListener,
    LogOutTimerUtil.LogOutListener {
    private var isPause: Boolean = false
    private lateinit var pauseTime: Date
    private lateinit var currentTime: Date
    var pasusMillisecon: Long = 0

    //**** Begin session ***
    override fun doLogout() {
        val intent = Intent(this, MainActivity::class.java)
        var currentTime = System.currentTimeMillis()
        //CustomActivityClear.doClearActivity(intent, this)
        //Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
        this.runOnUiThread(Runnable {
            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
            //CustomActivityClear.doClearActivityWithSession(intent, this)
            val message = "Your Active Session is Expired! Please login Again."
            if (!(this).isFinishing) {
                try {
                    DialogCustom.doClearActivityWithSession(intent, this)
                } catch (e: WindowManager.BadTokenException) {
                    e.printStackTrace()
                }
            }
        })

    }

    override fun onStart() {
        super.onStart()
        LogOutTimerUtil.startLogoutTimer(this, this);

    }


    override fun onUserInteraction() {
        super.onUserInteraction()
        //globalVariable.pauseTime = 0;

        LogOutTimerUtil.startLogoutTimer(this, this)

    }


    override fun onPause() {
        super.onPause()
        isPause = true
        var time = System.currentTimeMillis()
        pauseTime = Calendar.getInstance().getTime()
        pasusMillisecon = System.currentTimeMillis();
        //   Log.e("idl time onPause",pauseTime.toString())
        //   Log.e("idl time onPause-->",time.toString())
        //  globalVariable.pauseTime  = time

    }

    override fun onResume() {
        super.onResume()
        //  Log.e("idl isPause***",isPause.toString())
        if (isPause) {
            isPause = false
            // var currentTime = System.currentTimeMillis()
            currentTime = Calendar.getInstance().time

            // val diffInMs: Long = currentTime.getTime() - pauseTime.getTime()
            val difm = System.currentTimeMillis() - pasusMillisecon
            //   resumeTime.
            //  val difi = resumeTime - pauseTime
            // Log.e("idl time onResume-->",diffInMs.toString())
            //  val diffInSec: Long = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
            // Log.e("idl diffInSec resume-->",diffInSec.toString())
            val intent = Intent(this, MainActivity::class.java)
            // CustomActivityClear.logoutExpireTime(intent, this, difm, LogOutTimerUtil.LOGOUT_TIME)

            if (!(this).isFinishing) {
                try {
                    DialogCustom.logoutExpireTime(
                        intent,
                        this,
                        difm,
                        LogOutTimerUtil.LOGOUT_TIME
                    )
                } catch (e: WindowManager.BadTokenException) {
                    e.printStackTrace()
                }
            }

        }
        //Log.e("idl isPause***",isPause.toString())


    }

    //begin keyboard hide
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val v = currentFocus
        if (v != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE)
            && v is EditText
            && !v.javaClass.name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x = ev.rawX + v.getLeft() - scrcoords[0]
            val y = ev.rawY + v.getTop() - scrcoords[1]
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()
            ) hideKeyboard(this)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null && activity.window.decorView != null
        ) {
            val imm = activity
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(
                activity.window.decorView
                    .windowToken, 0
            )
        }
    }


}