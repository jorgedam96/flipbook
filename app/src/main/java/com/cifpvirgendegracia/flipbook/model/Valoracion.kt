package com.cifpvirgendegracia.flipbook.model

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