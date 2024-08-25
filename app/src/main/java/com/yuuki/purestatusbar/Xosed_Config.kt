package com.yuuki.purestatusbar

import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge

class Xosed_Config {
    private var prefs: XSharedPreferences

    init {
        prefs = XSharedPreferences("com.yuuki.purestatusbar", "config")
        prefs.makeWorldReadable()
    }

    fun getValue(key: String): Boolean {
        prefs.reload()
        val value = prefs.getString(key, null)
        XposedBridge.log("Value for key $key: $value")
        if (value != null) {
            return value.contains("\uD83D\uDE0B")
        }
        return false
    }

    fun getValueStr(key: String): String {
        prefs.reload()
        val value = prefs.getString(key, null)
        return value.toString()
    }
}
