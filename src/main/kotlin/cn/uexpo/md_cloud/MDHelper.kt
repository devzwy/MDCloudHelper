package cn.uexpo.md_cloud

import cn.uexpo.md_cloud.manager.ConfigManager

/**
 * 明道工具类
 * 支持多个BASEURL
 * 调试日志默认开启，关闭调用[MdLog.disable]，注意：无法关闭错误日志的输出，系统默认打印全部错误日志
 */
class MDHelper private constructor() {


    /**
     * 获取单例操作对象
     */
    companion object {

        @Volatile
        private var instance: MDHelper? = null

        /**
         * 获取单例操作对象
         */
        fun getInstance(): MDHelper {
            if (instance == null) {
                synchronized(MDHelper::class.java) {
                    if (instance == null) {
                        instance = MDHelper()
                        MdLog.debug("MDHelper实例初始化成功,hash值:${instance.hashCode()}")
                    }
                }
            }
            return instance!!
        }
    }

    //todo 數據操作

    /**
     * 添加应用配置
     * [configKey] 配置的key，后续操作改应用时必填,
     * [appKey] 明道云的appKey
     * [sign] 明道云应用的Sign
     */
    fun addAppConfig(configKey: String, appKey: String, sign: String) =
        ConfigManager.addAppConfig(configKey, appKey, sign)

    /**
     * 根据应用配置的key删除应用
     */
    fun removeAppByConfigKey(configKey: String) = ConfigManager.removeAppByConfigKey(configKey)

    /**
     * 获取应用配置,不填写key时会取第一个添加的应用配置,如果没有添加过应用时时可能会抛出异常
     * [appConfigKey] 调用[addApp]时的key
     */
    fun getAppConfig(appConfigKey: String? = null) = ConfigManager.getAppConfig(appConfigKey)

    /**
     * 添加BaseUrl,内部判断所添加的url值，存在时将跳过添加，跳过添加时会有错误日志输出。
     * [key] 后续操作需要携带，用于找到对应的url
     * [url] url值
     */
    fun addBaseUrl(key: String, url: String) = ConfigManager.addBaseUrl(key, url)

    /**
     * 根据key移除baseurl
     * [key] 调用[addBaseUrl]时的key
     */
    fun removeBaseUrlByKey(key: String) = ConfigManager.removeBaseUrlByKey(key)

    /**
     * 移除全部应用配置
     */
    fun removeAllAppConfigs() = ConfigManager.removeAllAppConfigs()

    /**
     * 移除全部baseurl配置
     */
    fun removeAllBaseUrls() = ConfigManager.removeAllBaseUrls()

    /**
     * 获取baseurl,不填写key时会取第一个添加的baseurl,如果没有添加过baseurl时可能会抛出异常
     * [key] 调用[addBaseUrl]时的key
     */
    fun getBaseUrl(key: String? = null) = ConfigManager.getBaseUrl(key)

    /**
     * 获取全部配置的baseurl
     */
    fun getAllBaseUrls() = ConfigManager.getAllBaseUrls()

    /**
     * 获取全部应用配置
     */
    fun getAllAppConfigs() = ConfigManager.getAllAppConfigs()

}