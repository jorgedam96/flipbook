package com.cifpvirgendegracia.flipbook.model

class Valoracion(
    var id: Long,
    var idUsuario: Long,
    var idLibro: Long,
    var valoracion: String,

    ) {

    override fun toString(): String {
        return "Valoracion(id=$id, idUsuario=$idUsuario, idLibro=$idLibro, valoracion='$valoracion')"
    }
}