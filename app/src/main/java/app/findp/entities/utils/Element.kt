package app.findp.entities.utils

import java.io.Serializable

class Element : Serializable {
    //Getters and Setters
    var elementId: ElementId? = null

    // constructor
    constructor() {}
    constructor(elementId: ElementId?) {
        this.elementId = elementId
    }
}