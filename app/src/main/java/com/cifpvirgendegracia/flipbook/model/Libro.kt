package com.cifpvirgendegracia.flipbook.model

class Libro(
    var id: String,
    var titulo: String,
    var isbn: String,
    var autor: String,
    var genero: String,
    var foto: String,
    var estado: String,
    var usuario: String,
    var localizacion: Localizacion
) {
    constructor() : this("", "", "", "", "", "", "", "", Localizacion())

    override fun toString(): String {
        return "Libro(id='$id', titulo='$titulo', isbn='$isbn', autor='$autor', genero='$genero', foto='$foto', estado='$estado', usuario='$usuario', localizacion=$localizacion)"
    }


}