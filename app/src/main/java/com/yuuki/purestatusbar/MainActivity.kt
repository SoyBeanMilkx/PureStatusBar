package com.yuuki.purestatusbar

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yuuki.purestatusbar.utils.ConfigUtil
import com.yuuki.purestatusbar.view.RippleView
import com.yuuki.purestatusbar.view.blur.DialogUtil
import java.io.DataOutputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val card_background = findViewById<LinearLayout>(R.id.card_background)
        val module_statu = findViewById<TextView>(R.id.statu_show)
        val show_more = findViewById<Button>(R.id.show_more)
        val configure = findViewById<RippleView>(R.id.configure)
        val config = findViewById<EditText>(R.id.config)

        findViewById<TextView>(R.id.app_title).setTypeface(null, Typeface.BOLD)
        findViewById<TextView>(R.id.configure_text).setTypeface(null, Typeface.BOLD)

        val configUtil = ConfigUtil(this)
        configUtil.setInitialValue()

        config.setText(configUtil.getConfig())

        module_statu.setTypeface(null, Typeface.BOLD)
        if(isModuleActive()){
            module_statu.text = "This module is working\uD83D\uDE0B"
            card_background.setBackgroundResource(R.drawable.card_background_green)
            configure.setBackgroundColor(Color.parseColor("#5CC560"))

            configure.setOnLongClickListener{

                val inputText = config.text.toString()
                configUtil.postValue(inputText)

                try {
                    val root = Runtime.getRuntime().exec("su")
                    val outputStream = root.outputStream
                    val dataOutputStream = DataOutputStream(outputStream)
                    dataOutputStream.writeBytes("pkill -f com.android.systemui")
                    dataOutputStream.flush()
                    dataOutputStream.close()
                    outputStream.close()
                } catch (t: Throwable) {
                    t.printStackTrace()
                }

                true
            }
        }
        else{
            module_statu.text = "Please activate the module first☹\uFE0F"
            card_background.setBackgroundResource(R.drawable.card_background_red)
            configure.setBackgroundColor(Color.parseColor("#FB8E8E"))

            configure.setOnLongClickListener{
                Toast.makeText(this, "Please activate the module first☹\uFE0F", Toast.LENGTH_SHORT).show()

                true
            }
        }

        show_more.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.yuuki_dialog, null)
            val title_view = dialogView.findViewById<TextView>(R.id.dialog_title)

            title_view.setTypeface(null, Typeface.BOLD)
            val builder = AlertDialog.Builder(this)

            builder.setView(dialogView)

            builder.setPositiveButton("JoinQQ") { dialog, which ->
                if(joinQQGroup("ScaxpD6_dYtj3zdHtSQTR_n6i1SZACx1")){
                    Toast.makeText(this, "Welcome to my group!", Toast.LENGTH_SHORT).show()
                }
                else
                    Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
            }
            val dialog = builder.create()
            DialogUtil.setGravity(dialog, Gravity.CENTER)
            DialogUtil.setWith(dialog, this.resources.displayMetrics.widthPixels - 140)
            val blurView = DialogUtil.setBlur(dialog)
            blurView?.setBlurRadius(45f)
            blurView?.setDownscaleFactor(2f)
            blurView?.setOverlayColor(Color.parseColor("#94FFFFFF"))
            blurView?.setOutLineRound(40f)
            DialogUtil.setBackgroundOverlay(dialog, 0.3f)
            dialog.show()
        }


    }

    fun isModuleActive(): Boolean {
        return false
    }

    fun joinQQGroup(key: String): Boolean {
        val intent = Intent()
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key"))
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
            return true
        } catch (e: Exception) {
            // 未安装手Q或安装的版本不支持
            return false
        }
    }

    //是否开启桌面图标
    private fun onShowIconChange(showIcon: Boolean) {
        val state =
            if (showIcon) PackageManager.COMPONENT_ENABLED_STATE_DEFAULT else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        val aliasName = ComponentName(this, "com.surcumference.fingerprint.Main")
        packageManager.setComponentEnabledSetting(aliasName, state, PackageManager.DONT_KILL_APP)
    }

}