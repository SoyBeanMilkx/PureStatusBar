package com.yuuki.purestatusbar

import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage


class MainHook : IXposedHookLoadPackage, IXposedHookInitPackageResources {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val hook_list = Hook_Methods()
        val xsp = Xosed_Config()
        if (lpparam.packageName.equals("com.android.systemui")) {

            if(xsp.getValue("is_hide_wifi"))
                hook_list.hide_wifi(lpparam)

            if(xsp.getValue("is_hide_mobile"))
                hook_list.hide_mobile(lpparam)

            if(xsp.getValue("is_hide_icon"))
                hook_list.hide_icon(lpparam)

            if(xsp.getValue("is_double_click_lock"))
                hook_list.statu_lock(lpparam)

            if(xsp.getValue("is_hide_low_intspeed"))
                hook_list.hide_low_intspeed(lpparam)

            if(xsp.getValue("is_hide_lock_paper"))
                hook_list.hide_lock_paper(lpparam)

            if(xsp.getValue("is_change_time_style")){
                if(xsp.getValueStr("is_change_time_style").contains("bold"))
                    hook_list.time_style_null_bold(lpparam)
                if(xsp.getValueStr("is_change_time_style").contains("null"))
                    hook_list.time_style_null(lpparam)
                if(xsp.getValueStr("is_change_time_style").contains("cpu"))
                    hook_list.time_style_cpu(lpparam)
            }


            //hook_list.edit_overl_color(lpparam)
            //hook_list.write_cpu_temp(lpparam)
            //hook_list.hook_test(lpparam)
            //hook_list.statusbar_text(lpparam)
            //hook_list.edit_blur(lpparam)
        }

        //check is active
        if(lpparam.packageName.equals("com.yuuki.purestatusbar")){
            XposedHelpers.findAndHookMethod("com.yuuki.purestatusbar.MainActivity",lpparam.classLoader,
                "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }

    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {

    }

}