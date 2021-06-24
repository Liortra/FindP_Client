package app.findp.entities

import app.findp.entities.utils.CreatedBy
import app.findp.entities.utils.ElementId
import app.findp.entities.utils.Location
import java.io.Serializable
import java.util.*

class ElementEntity : Serializable {
    //Getters and Setters
    var elementId: ElementId? = null
    var type: String? = null
    var name: String? = null
    var active: Boolean? = null
    var createdTimestamp: Date? = null
    var createdBy: CreatedBy? = null
    var location: Location? = null
    var elementAttributes: Map<String, Any>? = null

    // constructor
    constructor(
        elementId: ElementId?,
        type: String?,
        name: String?,
        active: Boolean?,
        createdTimestamp: Date?,
        createdBy: CreatedBy?,
        location: Location?,
        elementAttributes: Map<String, Any>?
    ) {
        this.elementId = elementId
        this.type = type
        this.name = name
        this.active = active
        this.createdTimestamp = createdTimestamp
        this.createdBy = createdBy
        this.location = location
        this.elementAttributes = elementAttributes
    }

    constructor(name: String?, location: Location?) {
        this.name = name
        this.location = location
    }

    constructor() {}

    override fun toString(): String {
        return "ElementEntity{" +
                "elementId=" + elementId +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", createdTimestamp=" + createdTimestamp +
                ", createdBy=" + createdBy +
                ", location=" + location +
                ", elementAttributes=" + elementAttributes +
                '}'
    }
}