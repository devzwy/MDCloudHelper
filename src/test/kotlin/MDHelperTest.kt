import cn.uexpo.md_cloud.MDHelper
import cn.uexpo.md_cloud.data.MuliDataType
import cn.uexpo.md_cloud.data.OptionDataType
import cn.uexpo.md_cloud.utils.MdControl
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 单元测试 针对全部Api测试
 * 全部通过表示正常
 */
class MDHelperTest {

    @Test
    fun dataTest() {

        //todo 修改为自己的
//        val baseUrl = ""
//        val appKey = ""
//        val sign = ""

        val instance = MDHelper.getInstance()

//        instance.disableLog()

        instance.addBaseUrl("my", baseUrl)

        instance.addAppConfig("我的应用", appKey, sign)

        assertNotNull(instance.getAppInfo())

        //写入记录
        val rowId = instance.insertRow(
            tableId = "658e7f60dd2e9988fc03dc25",
            data = MdControl.Builder()
                .addControl("658e7f60dd2e9988fc03dc26", "1111111")
                .addControl("658e7f75dd2e9988fc03dc31", "你好")
                .addMulti("658e8870dd2e9988fc03dc57", arrayListOf("SGVsbG8sIEJhc2U2NA=="), arrayListOf("111.list"), MuliDataType.BASE64)
                .addOption("65b0b8ef384db183c9a18342", "已打印2", OptionDataType.ADD)
                .build()
        )
        assertNotNull(rowId)

        val rowCount = instance.insertRows(
            tableId = "658e7f60dd2e9988fc03dc25",
            dataList = arrayListOf(
                MdControl.Builder()
                    .addControl("658e7f60dd2e9988fc03dc26", "1111111")
                    .addControl("658e7f75dd2e9988fc03dc31", "你好")
                    .addMulti("658e8870dd2e9988fc03dc57", arrayListOf("SGVsbG8sIEJhc2U2NA=="), arrayListOf("222.list"), MuliDataType.BASE64)
                    .addOption("65b0b8ef384db183c9a18342", "已打印2", OptionDataType.ADD)
                    .build(),
                MdControl.Builder()
                    .addControl("658e7f60dd2e9988fc03dc26", "222")
                    .addControl("658e7f75dd2e9988fc03dc31", "你好啊啊啊啊啊")
                    .addMulti("658e8870dd2e9988fc03dc57", arrayListOf("SGVsbG8sIEJhc2U2NA=="), arrayListOf("333.list"), MuliDataType.BASE64)
                    .addOption("65b0b8ef384db183c9a18342", "已打印")
                    .build()
            )
        )

        assertEquals(rowCount, 2)

    }

    @Test
    fun addOrRemoveAppConfig() {
        val instance = MDHelper.getInstance()

        assertThrows(Exception::class.java) {
            instance.getAppConfig()
        }

        assertThrows(Exception::class.java) {
            instance.getAppConfig(null)
        }

        assertThrows(Exception::class.java) {
            instance.getAppConfig("")
        }

        assertThrows(Exception::class.java) {
            instance.getAppConfig("not")
        }

        val key1 = "我的应用"
        val appKey1 = "123456"
        val sign1 = "666666"

        val key2 = "我的应用2"
        val appKey2 = "654321"
        val sign2 = "111111"

        instance.addAppConfig(key1, appKey1, sign1)

        assertEquals(instance.getAppConfig().appKey, appKey1)

        instance.addAppConfig(key2, appKey2, sign2)

        assertEquals(instance.getAppConfig().appKey, appKey1)

        assertEquals(instance.getAppConfig(key2).appKey, appKey2)

        assertEquals(instance.getAllAppConfigs().size, 2)

        instance.removeAppByConfigKey(key1)

        assertEquals(instance.getAllAppConfigs().size, 1)

        instance.removeAppByConfigKey(key2)
        instance.removeAppByConfigKey("unkonwKey")

        assertThrows(Exception::class.java) {
            instance.getAppConfig()
        }

        instance.removeAllAppConfigs()

        assertEquals(instance.getAllAppConfigs().size, 0)
    }

    /**
     * baseurl的增删查
     */
    @Test
    fun addOrRemoveBaseUrl() {
        val instance = MDHelper.getInstance()

        assertThrows(Exception::class.java) {
            instance.getBaseUrl()
        }

        assertThrows(Exception::class.java) {
            instance.getBaseUrl(null)
        }

        assertThrows(Exception::class.java) {
            instance.getBaseUrl("")
        }

        assertThrows(Exception::class.java) {
            instance.getBaseUrl("not")
        }

        val key1 = "configKey"
        val url1 = "https://www.baidu.com"

        val key2 = "configKey2"
        val url2 = "https://www.baidu.com2"

        instance.addBaseUrl(key1, url1)

        assertEquals(instance.getBaseUrl(), url1)

        instance.addBaseUrl(key2, url2)

        assertEquals(instance.getBaseUrl(), url1)

        assertEquals(instance.getBaseUrl(key2), url2)

        assertEquals(instance.getAllBaseUrls().size, 2)

        instance.removeBaseUrlByKey(key1)

        assertEquals(instance.getAllBaseUrls().size, 1)

        instance.removeBaseUrlByKey(key2)
        instance.removeBaseUrlByKey("unkonwKey")

        assertThrows(Exception::class.java) {
            instance.getBaseUrl()
        }

        instance.removeAllBaseUrls()

        assertEquals(instance.getAllBaseUrls().size, 0)
    }

    /**
     * 单例实例化
     */
    @Test
    fun testGetInstance() {
        val instance1 = MDHelper.getInstance()
        val instance2 = MDHelper.getInstance()

        // 检查获取的实例是否非空
        assertNotNull(instance1)
        assertNotNull(instance2)

        // 检查获取的实例是否是同一个实例
        assert(instance1 === instance2)
    }


}
