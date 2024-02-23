package cn.uexpo.md_cloud.data

import cn.uexpo.md_cloud.manager.ConfigManager

data class AppConfig(val appKey:String,val sign:String)

fun main() {
    ConfigManager.toString()
}
