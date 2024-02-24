package cn.uexpo.md_cloud.data

/**
 * 附件的类型
 */
enum class MuliEditType private constructor(val type:Int) {
    REPLACE(0),
    ADD(1)
}