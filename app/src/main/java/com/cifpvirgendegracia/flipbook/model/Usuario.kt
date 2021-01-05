package com.cifpvirgendegracia.flipbook.model

class Usuario(
    var id: String,
    var nombre: String,
    var apellido: String,
    var email: String,
    var pass: String,
    var foto: String
) {
    constructor() : this("", "", "", "", "", "")

    override fun toString(): String {
        return "Usuario(id='$id', nombre='$nombre', apellido='$apellido', email='$email'," +
                " pass='$pass', foto='$foto')"
    }
}
