import cn.uexpo.md_cloud.MdLog
import cn.uexpo.md_cloud.manager.ConfigManager
import java.lang.Exception
import kotlin.test.Test

/**
 * SDK内部日志打印
 */
class MdLogTest {
    /**
     * 单例实例化
     */
    @Test
    fun testLog() {
        MdLog.debug("测试调试")
        MdLog.error("测试异常")
        MdLog.error(Exception("测试的Exception"))
        MdLog.disable()
        MdLog.debug("这条信息应该看不到")
    }
}