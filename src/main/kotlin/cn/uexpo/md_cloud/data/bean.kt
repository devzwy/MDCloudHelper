package cn.uexpo.md_cloud.data

open class RowBaseResult<T> {
    var rows: List<T> = arrayListOf()
    var total: Int = 0
}

open class Row {
    var rowid: String = ""
    var ctime: String = ""
    var caid: UserInfo? = null
    var ownerid: UserInfo? = null
    var utime: String = ""
    var autoid: Int = 0
    var allowdelete: Boolean = false
    var controlpermissions: String = ""
}

open class UserInfo {
    var accountId: String = ""
    var fullname: String = ""
    var avatar: String = ""
    var status: Int = 0
}