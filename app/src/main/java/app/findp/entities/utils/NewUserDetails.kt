package app.findp.entities.utils

class NewUserDetails(
    var email: String,
    var role: UserRole,
    var username: String,
    var avatar: String
) {
    fun setUsename(username: String) {
        this.username = username
    }
}