package app.findp.entities.utils

import java.io.Serializable

class ElementId : Serializable {
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