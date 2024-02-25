package cn.uexpo.md_cloud.manager

import HttpClientUtil
import cn.uexpo.md_cloud.data.BaseResult
import cn.uexpo.md_cloud.utils.MDUtil.toJson
import cn.uexpo.md_cloud.utils.MdDataControl
import cn.uexpo.md_cloud.utils.MdFilterControl
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.TypeReference

/**
 * 数据管理器
 */
internal object ApiManager {

    //读取应用信息
    private const val URL_APP_INFO = "/api/v1/open/app/get"

    //增加行记录
    private const val URL_ADD_ROW = "/api/v2/open/worksheet/addRow"

    //批量增加行记录
    private const val URL_ADD_ROWS = "/api/v2/open/worksheet/addRows"

    //表结构信息
    private const val URL_TABLE_INFO = "/api/v2/open/worksheet/getWorksheetInfo"

    //编辑行记录
    private const val URL_EDIT_ROW = "/api/v2/open/worksheet/editRow"

    //获取行记录详情
    private const val URL_GET_ROW = "/api/v2/open/worksheet/getRowByIdPost"

    //删除行
    private const val URL_DEL_ROW = "/api/v2/open/worksheet/deleteRow"

    //过滤行
    private const val URL_FILTER_ROW = "/api/v2/open/worksheet/getFilterRows"


    //获取表结构：BASEURL

    //获取列表：BASEURL/api/v2/open/worksheet/getFilterRows
    //获取关联记录：BASEURL/api/v2/open/worksheet/getRowRelations
    //


    /**
     * 获取列表
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [filter] 过滤条件，使用[MdFilterControl.Builder]构造多个
     * [pageSize] 行数
     * [pageIndex] 页码
     * [viewId] 视图ID
     * [sortId] 排序字段ID
     * [isAsc] 是否升序
     * [notGetTotal] 是否不统计总行数以提高性能(默认: false)
     * [useControlId] 是否只返回controlId(默认: false)
     * @return 过滤后的数据列表
     */
    fun getRows(
        baseUrlKey: String? = null,
        appConfigKey: String? = null,
        tableId: String,
        filter: MdFilterControl,
        pageSize: Int? = null,
        pageIndex: Int? = null,
        viewId: String? = null,
        sortId: String? = null,
        isAsc: Boolean? = null,
        notGetTotal: Boolean? = null,
        useControlId: Boolean? = null,
    ): JSONObject {

        val url = getUrl(baseUrlKey, URL_FILTER_ROW)
        val appConfig = getAppConfig(appConfigKey)

        val requestData = hashMapOf(
            "appKey" to appConfig.appKey, "sign" to appConfig.sign,
            "worksheetId" to tableId, "filters" to filter.filters
        )

        viewId?.let { requestData.put("viewId", it) }
        pageSize?.let { requestData.put("pageSize", it) }
        pageIndex?.let { requestData.put("pageIndex", it) }
        sortId?.let { requestData.put("sortId", it) }
        isAsc?.let { requestData.put("isAsc", it) }
        notGetTotal?.let { requestData.put("notGetTotal", it) }
        useControlId?.let { requestData.put("useControlId", it) }


        val resultStr = HttpClientUtil.post(url, requestData.toJson())
        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<JSONObject>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("获取列表请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }


    /**
     * 删除行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 删除行的Id
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 删除成功返回true 否则返回false
     */
    fun deleteRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, rowId: String, triggerWorkflow: Boolean = true): Boolean {
        val url = getUrl(baseUrlKey, URL_DEL_ROW)
        val appConfig = getAppConfig(appConfigKey)

        val requestData = hashMapOf(
            "appKey" to appConfig.appKey, "sign" to appConfig.sign,
            "worksheetId" to tableId, "rowId" to rowId, "triggerWorkflow" to triggerWorkflow
        )
        val resultStr = HttpClientUtil.post(url, requestData.toJson())
        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<Boolean>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("删除行记录请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }

    /**
     * 插入多行记录，最大1000行
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [dataList] 写入的数据列，使用[MdDataControl.Builder]构造多个
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 写入成功后回传写入成功的总行数
     */
    fun insertRows(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, dataList: List<MdDataControl>, triggerWorkflow: Boolean = true): Int {

        val url = getUrl(baseUrlKey, URL_ADD_ROWS)
        val appConfig = getAppConfig(appConfigKey)

        val requestData = hashMapOf(
            "appKey" to appConfig.appKey, "sign" to appConfig.sign,
            "worksheetId" to tableId, "rows" to dataList.flatMap { arrayListOf(it.controls) }, "triggerWorkflow" to triggerWorkflow
        )
        val resultStr = HttpClientUtil.post(url, requestData.toJson())
        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<Int>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("获取应用数据请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }


    /**
     * 插入单行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [data] 写入的数据列，使用[MdDataControl.Builder]构造
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 写入成功后回传写入的行ID
     */
    fun insertRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, data: MdDataControl, triggerWorkflow: Boolean = true): String {

        val url = getUrl(baseUrlKey, URL_ADD_ROW)
        val appConfig = getAppConfig(appConfigKey)

        val requestData = hashMapOf("appKey" to appConfig.appKey, "sign" to appConfig.sign, "worksheetId" to tableId, "controls" to data.controls, "triggerWorkflow" to triggerWorkflow)
        val resultStr = HttpClientUtil.post(url, requestData.toJson())
        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<String>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("获取应用数据请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }

    /**
     * 编辑行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 行记录ID
     * [data] 更新的数据列，使用[MdDataControl.Builder]构造
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 编辑成功返回true，否则返回false
     */
    fun updateRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, rowId: String, data: MdDataControl, triggerWorkflow: Boolean = true): Boolean {

        val url = getUrl(baseUrlKey, URL_EDIT_ROW)
        val appConfig = getAppConfig(appConfigKey)

        val requestData = hashMapOf(
            "appKey" to appConfig.appKey, "sign" to appConfig.sign, "worksheetId" to tableId, "rowId" to rowId,
            "controls" to data.controls, "triggerWorkflow" to triggerWorkflow
        )
        val resultStr = HttpClientUtil.post(url, requestData.toJson())
        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<Boolean>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("编辑行记录请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }


    /**
     * 获取行记录详情
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 行记录ID
     * @return 行记录数据JSON
     */
    fun getRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, rowId: String): JSONObject {

        val url = getUrl(baseUrlKey, URL_GET_ROW)
        val appConfig = getAppConfig(appConfigKey)

        val requestData = hashMapOf("appKey" to appConfig.appKey, "sign" to appConfig.sign, "worksheetId" to tableId, "rowId" to rowId)
        val resultStr = HttpClientUtil.post(url, requestData.toJson())
        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<JSONObject>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("获取行记录详情请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }

    /**
     * 获取表结构信息
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * @return 表结构信息JSOn
     */
    fun getTableInfo(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String): JSONObject {
        val url = getUrl(baseUrlKey, URL_TABLE_INFO)
        val appConfig = getAppConfig(appConfigKey)
        val requestData = hashMapOf("appKey" to appConfig.appKey, "sign" to appConfig.sign, "worksheetId" to tableId)
        val resultStr = HttpClientUtil.post(url, requestData.toJson())
        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<JSONObject>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("获取表结构信息请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }

    /**
     * 获取应用数据
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * @return 返回应用信息
     */
    fun getAppInfo(baseUrlKey: String? = null, appConfigKey: String? = null): JSONObject? {
        val appInfo = ConfigManager.getAppConfig(appConfigKey)
        val resultStr = HttpClientUtil.get(
            "${ConfigManager.getBaseUrl(baseUrlKey)}${URL_APP_INFO}", hashMapOf("appKey" to appInfo.appKey, "sign" to appInfo.sign)
        )

        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<JSONObject>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data
        } else {
            throw RuntimeException("获取应用数据请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }

    private fun getUrl(baseUrlKey: String?, path: String?) = "${ConfigManager.getBaseUrl(baseUrlKey)}${path}"

    private fun getAppConfig(appConfigKey: String?) = ConfigManager.getAppConfig(appConfigKey)


}