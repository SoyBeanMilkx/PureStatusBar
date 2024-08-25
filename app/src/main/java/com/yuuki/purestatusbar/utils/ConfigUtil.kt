package com.yuuki.purestatusbar.utils

import android.content.Context

class ConfigUtil(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("config", Context.MODE_WORLD_READABLE)

    // 保存键值对到 SharedPreferences
    fun postValue(input: String) {
        val editor = sharedPreferences.edit()
        val lines = input.split("\n\n")
        for (line in lines) {
            val parts = line.split(" = ")
            if (parts.size == 2) {
                val key = parts[0].trim()
                val value = parts[1].trim()
                editor.putString(key, value)
            }
        }
        editor.apply()
    }

    fun getValue(key: String): Boolean {
        return sharedPreferences.getString(key, null).equals("\uD83D\uDE0B")
    }

    // 获取所有键值对并返回字符串
    fun getConfig(): String {
        val allEntries = sharedPreferences.all
        val configStringBuilder = StringBuilder()

        for ((key, value) in allEntries) {
            configStringBuilder.append("$key = $value\n\n")
        }

        return configStringBuilder.toString().trim()
    }

    // 设置初始值到 SharedPreferences
    fun setInitialValue() {
        val str = "is_hide_wifi = \uD83D\uDE0B\n\n" +
                "is_hide_mobile = \uD83D\uDE0B\n\n" +
                "is_hide_icon = ☹\uFE0F\n\n" +
                "is_hide_low_intspeed = \uD83D\uDE0B\n\n" +
                "is_change_time_style = \uD83D\uDE0B\n\n" +
                "is_hide_lock_paper = ☹\uFE0F\n\n" +
                "is_double_click_lock = \uD83D\uDE0B"

        if (sharedPreferences.all.isEmpty())
            postValue(str)
    }
}