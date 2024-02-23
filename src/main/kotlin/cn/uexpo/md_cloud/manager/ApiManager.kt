package cn.uexpo.md_cloud.manager

import cn.uexpo.md_cloud.MdLog
import cn.uexpo.md_cloud.data.BaseResult
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.TypeReference

/**
 * 数据管理器
 */
internal object ApiManager {

    //读取应用信息
    private const val URL_APP_INFO = "/api/v1/open/app/get"

    //新建表 BASEURL/api/v2/open/worksheet/addWorksheet
    //获取表结构：BASEURL/api/v2/open/worksheet/getWorksheetInfo

    //获取记录总行数：BASEURL/api/v2/open/worksheet/getFilterRowsTotalNum
    //获取列表：BASEURL/api/v2/open/worksheet/getFilterRows
    //插入行数据：BASEURL/api/v2/open/worksheet/addRow
    //批量插入行：BASEURL/api/v2/open/worksheet/addRows
    //获取记录详情：BASEURL/api/v2/open/worksheet/getRowByIdPost
    //编辑行记录：BASEURL/api/v2/open/worksheet/editRow
    //批量更新：BASEURL/api/v2/open/worksheet/editRows
    //删除记录：BASEURL/api/v2/open/worksheet/deleteRow
    //获取关联记录：BASEURL/api/v2/open/worksheet/getRowRelations
    //

    //

    /**
     * 获取应用数据
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     */
    fun getAppInfo(baseUrlKey: String? = null, appConfigKey: String? = null): JSONObject? {
        val appInfo = ConfigManager.getAppConfig(appConfigKey)
        val resultStr = HttpClientUtil.get(
            "${ConfigManager.getBaseUrl(baseUrlKey)}${URL_APP_INFO}",
            hashMapOf("appKey" to appInfo.appKey, "sign" to appInfo.sign)
        )

        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<JSONObject>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data
        } else {
            null
        }
    }


    enum class ErrorCodeEnum(val code: Int, val description: String) {
        FAILURE(0, "失败"),
        SUCCESS(1, "成功"),
        MISSING_PARAMETER(10001, "缺少参数"),
        INVALID_PARAMETER_VALUE(10002, "参数值错误"),
        NO_PERMISSION(10005, "数据操作无权限"),
        DATA_NOT_EXIST(10007, "数据不存在"),
        MISSING_TOKEN(10101, "请求令牌不存在"),
        INVALID_SIGNATURE(10102, "签名不合法"),
        DATA_OPERATION_EXCEPTION(99999, "数据操作异常");

        companion object {
            fun fromCode(code: Int): ErrorCodeEnum {
                return entries.find { it.code == code }!!
            }
        }
    }


}