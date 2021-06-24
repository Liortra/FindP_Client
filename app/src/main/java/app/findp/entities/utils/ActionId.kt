package app.findp.entities.utils

class ActionId {
    //Getters and Setters
    var domain: String? = null
    var id: String? = null

    // constructor
    constructor() {}
    constructor(domain: String?, id: String?) {
        this.domain = domain
        this.id = id
    }
}