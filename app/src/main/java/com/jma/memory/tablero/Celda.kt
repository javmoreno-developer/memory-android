package com.jma.memory.tablero

data class Celda(var img: Int,var visible: Boolean = false) {

    fun igual(celda: Celda?): Boolean {
        return this.img == celda?.img
    }
}