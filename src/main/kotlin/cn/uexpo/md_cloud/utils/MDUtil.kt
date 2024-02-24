package cn.uexpo.md_cloud.utils

import com.alibaba.fastjson2.JSON


object MDUtil {

    fun Any.toJson() = JSON.toJSONString(this)


}