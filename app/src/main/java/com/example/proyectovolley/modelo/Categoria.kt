package com.example.proyectovolley.modelo

class Categoria constructor(id: Int, nombre: String) {
    var nombre = nombre
    var id = id
    override fun toString(): String {
        return nombre
    }
}