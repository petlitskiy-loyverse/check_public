package com.loyverse.dashboard.mvp.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.loyverse.dashboard.R
import com.loyverse.dashboard.base.BaseApplication
import com.loyverse.dashboard.base.Utils
import com.loyverse.dashboard.core.Navigator
import kotlinx.android.synthetic.main.activity_new_loyverse.*
import javax.inject.Inject

class PosInformationActivity : AppCompatActivity() {

    @Inject
    lateinit var navigator: Navigator
    private val SEPARATE = "\n"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_loyverse)
        (application as BaseApplication).activityComponent.inject(this)
        setLightStatusBarColor()

        iv_close.setOnClickListener { finish() }

        iv_google_play.setOnClickListener {
            navigator.showPosInPlayStore(this)
        }

        val textLine1 = resources.getString(R.string.description_kds_l1) + " "
        val textLine2 = resources.getString(R.string.description_kds_l2)
        if (Utils.isPhoneLayout(this)) {
            tv_information?.text = StringBuilder().append(textLine1).append(textLine2)
        } else {
            tv_information?.text = StringBuilder().append(textLine1).append(SEPARATE).append(textLine2)
        }
    }

    @SuppressLint("NewApi")
    private fun setLightStatusBarColor() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = resources.getColor(R.color.white)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}