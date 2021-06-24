package app.findp.entities.utils

import java.io.Serializable

class CreatedBy : Serializable {
    //Getters and Setters
    var userId: UserId? = null

    // constructor
    constructor() {}
    constructor(userId: UserId?) {
        this.userId = userId
    }
}