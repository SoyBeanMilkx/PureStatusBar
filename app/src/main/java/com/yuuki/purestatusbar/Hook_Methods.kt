package com.yuuki.purestatusbar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.yuuki.purestatusbar.utils.HardwareInfo
import com.yuuki.purestatusbar.utils.TimeConvert
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.util.Calendar

class Hook_Methods {
    fun hide_wifi(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            "com.android.systemui.statusbar.StatusBarWifiView",
            lpparam.classLoader,
            "isIconVisible",
            object : XC_MethodReplacement() {
                @Throws(Throwable::class)
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    return false
                }
            })
    }

    fun hide_mobile(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            "com.android.systemui.statusbar.StatusBarMobileView",
            lpparam.classLoader,
            "isIconVisible",
            object : XC_MethodReplacement() {
                @Throws(Throwable::class)
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    return false
                }
            })
    }

    fun hide_icon(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            "com.android.systemui.statusbar.StatusBarIconView",
            lpparam.classLoader,
            "isIconVisible",
            object : XC_MethodReplacement() {
                @Throws(Throwable::class)
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    return false
                }
            })
    }

    fun hide_low_intspeed(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            "com.flyme.statusbar.connectionRateView.ConnectionRateView",
            lpparam.classLoader,
            "updateConnectionRate",
            Double::class.javaPrimitiveType, // 确保方法参数类型匹配
            object : XC_MethodHook() {
                @Throws(Throwable::class)

                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    val rate = param.args[0] as Double
                    XposedBridge.log("After updateConnectionRate: rate = $rate")

                    val view = XposedHelpers.getObjectField(param.thisObject, "mUnitView") as View
                    view.visibility = if (rate < 200) View.GONE else View.VISIBLE
                }
            }
        )
    }

    fun time_style_null_bold(lpparam: XC_LoadPackage.LoadPackageParam){

        val clockClass = XposedHelpers.findClass("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader)

        XposedBridge.hookAllConstructors(clockClass, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val clockView = param.thisObject as TextView
                clockView.setTypeface(null, Typeface.BOLD)
            }
        })

    }

    fun time_style_null(lpparam: XC_LoadPackage.LoadPackageParam){

        val clockClass = XposedHelpers.findClass("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader)

        XposedHelpers.findAndHookMethod(
            clockClass,
            "getSmallTime", // 目标方法名
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val calendar = Calendar.getInstance()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)

                    val timeConvert = TimeConvert()
                    val CN_time = timeConvert.timeToShiChen(hour, minute)

                    param.result = CN_time
                }
            }
        )
    }

    fun time_style_cpu(lpparam: XC_LoadPackage.LoadPackageParam){

        val clockClass = XposedHelpers.findClass("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader)

        XposedHelpers.findAndHookMethod(
            clockClass,
            "getSmallTime", // 目标方法名
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val calendar = Calendar.getInstance()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)

                    val timeConvert = TimeConvert()
                    val CN_time = timeConvert.timeToShiChen(hour, minute)

                    val info = HardwareInfo()
                    val CPU_temp = info.getCpuAverageTemperature()

                    param.result = "$CN_time $CPU_temp"
                }
            }
        )

    }

    fun write_cpu_temp(lpparam: XC_LoadPackage.LoadPackageParam) {
        val phoneStatusBarViewClass = XposedHelpers.findClass(
            "com.flyme.statusbar.battery.FlymeBatteryMeterView",
            lpparam.classLoader
        )

        XposedHelpers.findAndHookMethod(
            phoneStatusBarViewClass,
            "onFinishInflate",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val statusBarView = param.thisObject as ViewGroup
                    val context = statusBarView.context

                    // 创建并配置新的文本视图
                    val textView = TextView(context).apply {
                        text = "fuck fuck fuck"
                        textSize = 160f
                        setBackgroundColor(Color.RED)
                        //gravity = Gravity.CENTER
                        setOnClickListener {
                            // 输出当前上下文信息
                            val contextInfo = "Context info: ${context.toString()}"
                            Toast.makeText(context, contextInfo, Toast.LENGTH_SHORT).show()
                        }
                    }

                    // 设置布局参数，使文本视图与其他视图在同一高度
                   /* val layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        marginEnd = 800 // 可根据需要调整边距
                    }
                    textView.layoutParams = layoutParams*/

                    // 将文本视图添加到状态栏中
                    statusBarView.addView(textView)
                }
            }
        )
    }


    //修改通知背景模糊度和锁屏界面控件模糊度
    fun edit_blur(lpparam: XC_LoadPackage.LoadPackageParam){
        XposedHelpers.findAndHookMethod(
            "com.flyme.systemui.wallpaper.WallpaperBlurDrawableManager",  // 替换为实际的类名
            lpparam.classLoader,
            "createBlurBitmap",
            Context::class.java,
            Bitmap::class.java,
            Float::class.javaPrimitiveType,
            Float::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    // 修改最后一个参数 (radius) 为 0
                    param.args[3] = 25f
                }
            }
        )
    }

    //修改锁屏时下拉通知栏背景模糊度
    fun edit_lockwall_blur(lpparam: XC_LoadPackage.LoadPackageParam){
        XposedHelpers.findAndHookMethod(
            "com.android.systemui.statusbar.NotificationMediaManager",  // 替换为实际的类名
            lpparam.classLoader,
            "updateWallpaperBlurByRadius",
            Float::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    param.args[0] = 0
                }
            }
        )
    }

    //修改锁屏状态下控制中心遮罩颜色
    fun edit_overl_color(lpparam: XC_LoadPackage.LoadPackageParam){
        XposedHelpers.findAndHookMethod(
            "com.flyme.systemui.controlcenter.phone.CenterController", // 替换为实际的类名
            lpparam.classLoader,
            "setBehindViewColor",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val thisObject = param.thisObject
                    val mBehindView = XposedHelpers.getObjectField(thisObject, "mBehindView")
                    if (mBehindView != null) {
                        // 设置背景颜色为纯黑
                        val setBehindViewColorMethod = XposedHelpers.findMethodExact(
                            "com.android.systemui.scrim.ScrimView", // 替换为实际的 ScrimView 类名
                            lpparam.classLoader,
                            "setDrawableMz",
                            Drawable::class.java
                        )
                        val blackDrawable = ColorDrawable(Color.BLACK)
                        setBehindViewColorMethod.invoke(mBehindView, blackDrawable)
                    }
                }
            }
        )
    }

    //锁屏也会变暗
    fun hide_lock_paper(lpparam: XC_LoadPackage.LoadPackageParam){
        XposedHelpers.findAndHookMethod(
            "com.android.systemui.statusbar.NotificationMediaManager",  // 替换为实际的类名
            lpparam.classLoader,
            "setBackdropAlpha",
            Float::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    param.args[0] = 0f
                }
            }
        )

        XposedHelpers.findAndHookMethod(
            "com.android.systemui.statusbar.notification.row.ActivatableNotificationView",
            lpparam.classLoader,
            "setBlurBgForStatic",
            object : XC_MethodReplacement() {
                @Throws(Throwable::class)
                override fun replaceHookedMethod(param: MethodHookParam): Any?{

                    val thisObject = param.thisObject
                    val mBackgroundFlyme = XposedHelpers.getObjectField(thisObject, "mBackgroundFlyme") as View
                    mBackgroundFlyme.setBackgroundColor(Color.parseColor("#B7FFFFFF"))

                    return null
                }
            })

    }

    fun statusbar_text(lpparam: XC_LoadPackage.LoadPackageParam){
        val phoneStatusBarViewClass = XposedHelpers.findClass(
            "com.flyme.systemui.controlcenter.qs.QSStatusBar",
            lpparam.classLoader
        )
        XposedHelpers.findAndHookMethod(
            phoneStatusBarViewClass,
            "onFinishInflate",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val statusBarView = param.thisObject as ViewGroup
                    val context = statusBarView.context

                    // 创建并配置新的文本视图
                    val textView = TextView(context).apply {
                        text = "不以物喜 不以己悲"
                        textSize = 14f
                        setBackgroundColor(Color.TRANSPARENT)
                        setTextColor(Color.parseColor("#9CDBA6"))
                        setTypeface(null, Typeface.BOLD)
                        setShadowLayer(1f, 0.5f, 0.5f, Color.parseColor("#50B498"))
                        //gravity = Gravity.CENTER
                        setOnLongClickListener {
                            Toast.makeText(context, "别点人家，嘤嘤嘤~~", Toast.LENGTH_LONG).show()
                            true
                        }
                    }

                    // 设置布局参数，使文本视图与其他视图在同一高度
                    val layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        marginStart = 60 // 可根据需要调整边距
                        topMargin = 38
                    }
                    textView.layoutParams = layoutParams

                    // 将文本视图添加到状态栏中
                    statusBarView.addView(textView)
                }
            }
        )
    }


    fun statu_lock(lpparam: XC_LoadPackage.LoadPackageParam){
        XposedHelpers.findAndHookMethod(
            "com.android.systemui.statusbar.phone.PhoneStatusBarView",
            lpparam.classLoader,
            "onFinishInflate",
            object : XC_MethodHook() {
                var preTime = 0L
                override fun afterHookedMethod(param: MethodHookParam) {
                    val statusVarView = param.thisObject as ViewGroup
                    statusVarView.setOnTouchListener { view, event ->
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            val currTime = System.currentTimeMillis()
                            if (currTime - preTime <= 200) {
                                XposedHelpers.callMethod(
                                    view.context.getSystemService(Context.POWER_SERVICE),
                                    "goToSleep",
                                    SystemClock.uptimeMillis()
                                )
                            }
                            preTime = currTime
                        }
                        view.performClick()
                        return@setOnTouchListener false
                    }
                }
            })
    }
}

/*
com.flyme.systemui.controlcenter.phone.ControlCenterPanelView 锁屏状态下的下拉控制中心
com.flyme.systemui.statusbar.notification.NotificationFilterPanel 控制中心右上角通知设置
com.flyme.systemui.controlcenter.phone.MzQSPanel 控制中心下拉功能列表(V2RAY之类的那种logo)
com.flyme.systemui.statusbar.phone.StatusBarHeaderView 控制中心头部(时间日期之类的)
com.flyme.systemui.controlcenter.phone.MzQSPanel 锁屏和解锁之后的控制中心中间位置
com.flyme.systemui.controlcenter.phone.MzQSContainerImpl 在MzQSPanel下一层

com.flyme.systemui.wallpaper.WallpaperBlurDrawableManager$updateWallpaperBitmap 控制中心通知的模糊度
com.flyme.systemui.wallpaper.WallpaperBlurDrawableManager$createBlurBitmap 同上

com.flyme.systemui.qs.MzQSFragment 解锁后的控制中心头的 长按以编辑界面

com.flyme.systemui.qs.ClassicsModeController$isUseClassicsStyle 切换控制中心样式（经典/新的）

com.flyme.systemui.controlcenter.qs.QSStatusBar 控制中心的状态栏，可以用来放文字
com.android.systemui.statusbar.notification.row.ActivatableNotificationView 下拉通知栏的通知item

com.flyme.keyguard.wallpaper 锁屏界面绘制内容
com.flyme.keyguard.wallpaper.WallpaperView.plugin 锁屏界面插件

com.flyme.statusbar.bouncer.KeyguardBouncerStatusBarView 锁屏密码解锁页面的状态栏

com.flyme.aod 息屏显示

*/