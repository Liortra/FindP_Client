package app.findp.entities

import app.findp.entities.utils.ActionId
import app.findp.entities.utils.Element
import app.findp.entities.utils.InvokedBy
import java.util.*

class ActionEntity {
    //Getters and Setters
    var actionId: ActionId? = null
    var type: String? = null
    var element: Element? = null
    var createdTimestamp: Date? = null
    var invokedBy: InvokedBy? = null
    var actionAttributes: Map<String, Any>? = null

    // constructor
    constructor(
        actionId: ActionId?,
        type: String?,
        element: Element?,
        createdTimestamp: Date?,
        invokedBy: InvokedBy?,
        actionAttributes: Map<String, Any>?
    ) {
        this.actionId = actionId
        this.type = type
        this.element = element
        this.createdTimestamp = createdTimestamp
        this.invokedBy = invokedBy
        this.actionAttributes = actionAttributes
    }

    constructor() {}
}