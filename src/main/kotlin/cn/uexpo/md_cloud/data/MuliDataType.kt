package cn.uexpo.md_cloud.data

/**
 * 附件的类型
 */
enum class MuliDataType private constructor(val type:Int) {
    URL(1),
    BASE64(2)
}