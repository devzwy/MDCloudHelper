package cn.uexpo.md_cloud.manager

import HttpClientUtil
import cn.uexpo.md_cloud.MdLog
import cn.uexpo.md_cloud.data.BaseResult
import cn.uexpo.md_cloud.utils.MDUtil.toJson
import cn.uexpo.md_cloud.utils.MdControl
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


    /**
     * 插入多行记录，最大1000行
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [dataList] 写入的数据列，使用[MdControl.Builder]构造多个
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 写入成功后回传写入成功的总行数
     */
    fun insertRows(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, dataList: List<MdControl>, triggerWorkflow: Boolean = true): Int {
        val url = "${ConfigManager.getBaseUrl(baseUrlKey)}${URL_ADD_ROWS}"
        val appConfig = ConfigManager.getAppConfig(appConfigKey)

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
            throw RuntimeException("获取应用数据请求失败，明道回传了失败的结果")
        }
    }


    /**
     * 插入单行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [data] 写入的数据列，使用[MdControl.Builder]构造
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 写入成功后回传写入的行ID
     */
    fun insertRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, data: MdControl, triggerWorkflow: Boolean = true): String {
        val url = "${ConfigManager.getBaseUrl(baseUrlKey)}${URL_ADD_ROW}"
        val appConfig = ConfigManager.getAppConfig(appConfigKey)

        val requestData = hashMapOf("appKey" to appConfig.appKey, "sign" to appConfig.sign, "worksheetId" to tableId, "controls" to data.controls, "triggerWorkflow" to triggerWorkflow)
        val resultStr = HttpClientUtil.post(url, requestData.toJson())
        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<String>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("获取应用数据请求失败，明道回传了失败的结果")
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
            throw RuntimeException("获取应用数据请求失败，明道回传了失败的结果")
        }
    }


    /**
     * 回传状态码
     */
    enum class ErrorCodeEnum(val code: Int, val description: String) {
        FAILURE(0, "失败"), SUCCESS(1, "成功"), MISSING_PARAMETER(10001, "缺少参数"), INVALID_PARAMETER_VALUE(
            10002, "参数值错误"
        ),
        NO_PERMISSION(10005, "数据操作无权限"), DATA_NOT_EXIST(10007, "数据不存在"), MISSING_TOKEN(
            10101, "请求令牌不存在"
        ),
        INVALID_SIGNATURE(10102, "签名不合法"), DATA_OPERATION_EXCEPTION(99999, "数据操作异常");

        companion object {
            fun fromCode(code: Int): ErrorCodeEnum {
                return entries.find { it.code == code }!!
            }
        }
    }

    /**
     * 数据类型
     */
    enum class DataTypeEnum(val value: Int, val description: String, val controlType: String) {
        TEXT_SINGLE_LINE(2, "文本", "单行、多行"), PHONE_MOBILE(3, "电话", "手机"), PHONE_LANDLINE(
            4, "电话", "座机"
        ),
        EMAIL(5, "邮箱", ""), NUMERIC(6, "数值", ""), ID_CARD(7, "证件", ""), AMOUNT(8, "金额", ""), RADIO_TILE(
            9, "单选", "平铺"
        ),
        MULTI_SELECT(10, "多选", ""), RADIO_DROPDOWN(11, "单选", "下拉"), ATTACHMENT(
            14, "附件", ""
        ),
        DATE_YEAR_MONTH_DAY(15, "日期", "年-月-日"), DATE_YEAR_MONTH_DAY_HOUR_MINUTE(
            16, "日期", "年-月-日 时:分"
        ),
        REGION_PROVINCE(19, "地区", "省"), FREE_CONNECTION(21, "自由连接", ""), SEGMENT(
            22, "分段", ""
        ),
        REGION_PROVINCE_CITY(23, "地区", "省/市"), REGION_PROVINCE_CITY_COUNTY(
            24, "地区", "省/市/县"
        ),
        AMOUNT_UPPERCASE(25, "大写金额", ""), MEMBER(26, "成员", ""), DEPARTMENT(27, "部门", ""), LEVEL(
            28, "等级", ""
        ),
        RELATED_RECORD(29, "关联记录", ""), OTHER_TABLE_FIELD(30, "他表字段", ""), FORMULA_NUMERIC(
            31, "公式", "数字"
        ),
        TEXT_COMBINATION(32, "文本组合", ""), AUTO_NUMBER(33, "自动编号", ""), SUB_TABLE(
            34, "子表", ""
        ),
        CASCADE_SELECT(35, "级联选择", ""), CHECKBOX(36, "检查框", ""), SUMMARY(37, "汇总", ""), FORMULA_DATE(
            38, "公式", "日期"
        ),
        LOCATION(40, "定位", ""), RICH_TEXT(41, "富文本", ""), SIGNATURE(42, "签名", ""), EMBEDDED(
            45, "嵌入", ""
        ),
        NOTE(10010, "备注", "");

        companion object {
            fun fromCode(value: Int) = entries.find { it.value == value }!!
        }
    }

    /**
     * 筛选方式
     */
    enum class FilterTypeEnum(val value: Int, val enumChar: String, val description: String) {
        DEFAULT(0, "Default", ""), LIKE(1, "Like", "包含"), EQ(2, "Eq", "是（等于）"), START(3, "Start", "开头为"), END(
            4, "End", "结尾为"
        ),
        NCONTAIN(5, "NContain", "不包含"), NE(6, "Ne", "不是（不等于）"), ISNULL(7, "IsNull", "为空"), HASVALUE(
            8, "HasValue", "不为空"
        ),
        BETWEEN(11, "Between", "在范围内"), NBETWEEN(12, "NBetween", "不在范围内"), GT(13, "Gt", ">"), GTE(
            14, "Gte", ">="
        ),
        LT(15, "Lt", "<"), LTE(16, "Lte", "<="), DATE_ENUM(17, "DateEnum", "日期是"), NDATE_ENUM(
            18, "NDateEnum", "日期不是"
        ),
        MYSELF(21, "MySelf", "我拥有的"), UNREAD(22, "UnRead", "未读"), SUB(23, "Sub", "下属"), RCEQ(
            24, "RCEq", "关联控件是"
        ),
        RCNE(25, "RCNe", "关联控件不是"), ARREQ(26, "ArrEq", "数组等于"), ARRNE(
            27, "ArrNe", "数组不等于"
        ),
        DATE_BETWEEN(31, "DateBetween", "在范围内"), DATE_NBETWEEN(32, "DateNBetween", "不在范围内"), DATE_GT(
            33, "DateGt", ">"
        ),
        DATE_GTE(34, "DateGte", ">="), DATE_LT(35, "DateLt", "<"), DATE_LTE(36, "DateLte", "<="), NORMAL_USER(
            41, "NormalUser", "常规用户"
        ),
        PORTAL_USER(42, "PortalUser", "外部门户用户封装");

        companion object {
            fun fromCode(value: Int) = entries.find { it.value == value }!!
        }
    }

    /**
     * 日期过滤类型
     */
    enum class DateRangeEnum(val value: Int, val enumChar: String, val description: String) {
        DEFAULT(0, "Default", ""), TODAY(1, "Today", "今天"), YESTERDAY(2, "Yesterday", "昨天"), TOMORROW(
            3, "Tomorrow", "明天"
        ),
        THIS_WEEK(4, "ThisWeek", "本周"), LAST_WEEK(5, "LastWeek", "上周"), NEXT_WEEK(
            6, "NextWeek", "下周"
        ),
        THIS_MONTH(7, "ThisMonth", "本月"), LAST_MONTH(8, "LastMonth", "上月"), NEXT_MONTH(
            9, "NextMonth", "下月"
        ),
        LAST_ENUM(10, "LastEnum", "上.."), NEXT_ENUM(11, "NextEnum", "下.."), THIS_QUARTER(
            12, "ThisQuarter", "本季度"
        ),
        LAST_QUARTER(13, "LastQuarter", "上季度"), NEXT_QUARTER(14, "NextQuarter", "下季度"), THIS_YEAR(
            15, "ThisYear", "本年"
        ),
        LAST_YEAR(16, "LastYear", "去年"), NEXT_YEAR(17, "NextYear", "明年"), CUSTOMIZE(
            18, "Customize", "自定义"
        ),
        LAST_7_DAY(21, "Last7Day", "过去7天"), LAST_14_DAY(22, "Last14Day", "过去14天"), LAST_30_DAY(
            23, "Last30Day", "过去30天"
        ),
        NEXT_7_DAY(31, "Next7Day", "未来7天"), NEXT_14_DAY(32, "Next14Day", "未来14天"), NEXT_33_DAY(
            33, "Next33Day", "未来33天"
        );

        companion object {
            fun fromCode(value: Int) = entries.find { it.value == value }!!
        }
    }

}