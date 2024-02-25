/**
 * 拼接方式
 */
enum class SpliceTypeEnum(val value: Int, val description: String) {
    AND(1, "AND"), OR(2, "OR");
    companion object {
        fun fromCode(value: Int) = entries.find { it.value == value }!!
    }
}
