package cn.uexpo.md_cloud.utils

import com.alibaba.fastjson2.JSON


object MDUtil {

    fun Map<*,*>.toJson() = JSON.toJSONString(this)


}