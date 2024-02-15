package io.github.chinosk.arcst3upload.utils.models


data class UserMeValue (
    var user_id: Long = 0,
    var name: String? = null,
    var user_code: String? = null
    // var display_name: String? = null
)


data class UserMe (
    var success: Boolean = false,
    var value: UserMeValue? = null
)
