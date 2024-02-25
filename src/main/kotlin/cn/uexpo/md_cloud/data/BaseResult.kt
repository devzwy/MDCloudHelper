package cn.uexpo.md_cloud.data

data class BaseResult<T>(val success:Boolean, val error_code:Int, var data:T?=null)
