package app.findp.entities.utils

import java.io.Serializable

class Location : Serializable {
    //Getters and Setters
    var lat: Double? = null
    var lng: Double? = null

    // constructor
    constructor() {}
    constructor(lat: Double?, lng: Double?) {
        this.lat = lat
        this.lng = lng
    }
}