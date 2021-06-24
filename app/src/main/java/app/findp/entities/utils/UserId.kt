package app.findp.entities.utils

import java.io.Serializable

class UserId : Serializable {
    //Getters and Setters
    var domain: String? = null
    var email: String? = null

    // constructor
    constructor(domain: String?, email: String?) {
        this.domain = domain
        this.email = email
    }

    constructor() {}
}