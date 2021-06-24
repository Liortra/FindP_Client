package app.findp.entities

import app.findp.entities.utils.UserId
import app.findp.entities.utils.UserRole
import java.io.Serializable

class UserEntity : Serializable {
    //Getters and Setters
    var userId: UserId? = null
    var role: UserRole? = null
    var username: String? = null
    var avatar: String? = null

    // constructor
    constructor(userId: UserId?, role: UserRole?, username: String?, avatar: String?) {
        this.userId = userId
        this.role = role
        this.username = username
        this.avatar = avatar
    }

    constructor() {}
}