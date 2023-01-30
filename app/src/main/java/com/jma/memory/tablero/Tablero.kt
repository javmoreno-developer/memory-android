package com.jma.memory.tablero

import android.content.Context
import com.jma.memory.R
import com.jma.memory.tablero.Celda
import java.util.*
import kotlin.random.Random


class Tablero(val contexto: Context, val dimension: Int = 4) {
    private lateinit var celdas: MutableList<Celda?>
    private var dim2: Int = dimension * dimension

    init {
        celdas = MutableList<Celda?>(dim2) { null }
    }

    fun tamaño(): Int = celdas.size

    fun celda(pos: Int): Celda? {
        return celdas[pos];
    }

    /**
     * Reboot
     */
    fun reboot() {
        celdas = MutableList<Celda?>(dim2) { null }
    }

    /**
     * Inicializa el tablero con imagenes aleatorias
     */
    fun iniciar() {
        // solicitamos un paquete de imagenes
        var imagenes = elegirImagenes(dim2/2)

        imagenes.forEach {
            val p1 = elegirPosicion()
            celdas[p1] = Celda(it)

            val p2 = elegirPosicion()
            celdas[p2] = Celda(it)
        }

    }

    /**
     * Elige NUM imagenes aleatorias sin repeticion,entre las que hemos añadido a
     * los recursos de la app.
     */
    private fun elegirImagenes(num: Int): Set<Int> {
        val imagenes: MutableSet<Int> = mutableSetOf()
        while(imagenes.size != num) {
            imagenes.add(recuperarRecurso(Random.nextInt(1,15)))

        }
        return imagenes;
    }

    private fun recuperarRecurso(num: Int): Int {
        val nombre = "img_${num.toString().padStart(3, '0')}"

        with(contexto) {
           return resources.getIdentifier(nombre,"drawable", packageName)
        }
    }

    /**
     * Elige una pos al azar en el tablero
     */
    private fun elegirPosicion(): Int {
        var p: Int // posicion real en el tablero
        val c: Int = Random.nextInt(dimension)
        val f: Int = Random.nextInt(dimension)

        p = (f*dimension) + c


            while(celdas[p]!=null) {
                p = (p+1)%dim2 // 15 % 16 = 15
            }


        return p
    }

    fun visible(pos: Int) :Boolean {
        //celdas[pos]?.visible = visible
        return celdas[pos]?.visible ?: false
    }

    fun estado(pos: Int,visible: Boolean) {
        celdas[pos]?.visible = visible
        //celdas[pos]?.visible ?: false
    }

    fun iguales(x: Int,y: Int) : Boolean {
        return celdas[x]?.igual(celdas[y]) ?: false
    }

    fun endGame() : Boolean {
        // 1 forma
        //  var ended = true
        /* celdas.forEach {
             if(it?.visible == false) {
                 ended= false
             }
         }*/

        // 2 forma

       /*var resultado = celdas.filter { it?.visible == true }

        return (resultado.size == 0)*/

        // 3 forma
        //return !celdas.any{it?.visible == false}

        // 4 forma
        return celdas.none { it?.visible == false}

        // 5 forma
        //return celdas.all {it?.visible == true}


    }

    fun hideAll() {
        celdas.clear()
        celdas = MutableList<Celda?>(dim2) { null }

    }

    fun giveUp() {
        celdas.forEach {
            it?.visible = true
        }
    }
}