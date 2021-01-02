package com.cifpvirgendegracia.flipbook.model

class Localizacion(
    var id: String,
    var latitud: String,
    var longitud: String
) {
    override fun toString(): String {
        return "Localizacion(id=$id, latitud='$latitud', longitud='$longitud')"
    }
}
