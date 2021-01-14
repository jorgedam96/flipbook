package com.cifpvirgendegracia.flipbook.model

/**
 * Localizacion
 *
 * @property id
 * @property latitud
 * @property longitud
 * @constructor Create empty Localizacion
 */
class Localizacion(
    var id: String,
    var latitud: String,
    var longitud: String
) {
    constructor() : this("", "", "")

    override fun toString(): String {
        return "Localizacion(id=$id, latitud='$latitud', longitud='$longitud')"
    }
}
