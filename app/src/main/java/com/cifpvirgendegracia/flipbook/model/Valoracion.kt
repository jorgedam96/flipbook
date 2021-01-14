package com.cifpvirgendegracia.flipbook.model

/**
 * Valoracion
 *
 * @property id
 * @property idUsuario
 * @property valoracion
 * @constructor Create empty Valoracion
 */
class Valoracion(
    var id: String,
    var idUsuario: String,
    var valoracion: String,

    ) {

    constructor() : this("", "", "",)

    override fun toString(): String {
        return "Valoracion(id='$id', idUsuario='$idUsuario', valoracion='$valoracion')"
    }

}