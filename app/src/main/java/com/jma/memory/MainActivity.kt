package com.jma.memory

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore.Audio
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.jma.memory.databinding.ActivityMainBinding
import com.jma.memory.tablero.Tablero
import com.jma.memory.tablero.TableroAdapter
import nl.dionsegijn.konfetti.core.Party
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mp: MediaPlayer by lazy {
        MediaPlayer.create(this@MainActivity, R.raw.super_mario_fx)
    }
    //private lateinit var mpBack: MediaPlayer
    private val mpBack: MediaPlayer by lazy {
        MediaPlayer.create(this@MainActivity,R.raw.bg)
    }
    private lateinit var tablero: Tablero
    private val DIMENSION = 4

    // duracion de partida expresada en segundos
    private val TIEMPO_DEFECTO = 100
    private lateinit var tableroAdapter: TableroAdapter

    private var puntuacion by Delegates.observable(0) { property, oldValue, newValue ->
        binding.subtitle.text = resources.getString(R.string.etq_puntos,newValue)
    }

    private var tiempo: Int by Delegates.observable(TIEMPO_DEFECTO) { property, oldValue, newValue ->
        binding.etqTiempo.text = resources.getString(R.string.etq_tiempo, tiempo)
    }

    private var errores = 0
    private var jugando: Boolean = false

    private lateinit var jugada: MutableSet<Int>
    private lateinit var temporizador: CountDownTimer;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        puntuacion = 0
        tiempo = 0

        tablero = Tablero(this@MainActivity,DIMENSION);

        // tiempo
        temporizador = object : CountDownTimer(TIEMPO_DEFECTO * 1000L, 1000L) {
            override fun onTick(p0: Long) {
                binding.etqTiempo.text = resources.getString(R.string.etq_tiempo, p0 / 1000)

            }

            override fun onFinish() {
                Snackbar.make(binding.root,"Has perdido",Snackbar.LENGTH_LONG).show()
                reset();
                gameOver(false);
            }

        }

        binding.btnInicio.setOnClickListener {

            if(jugando) reset()
            else {

                jugando = true;

                binding.btnInicio.text = resources.getString(R.string.btn_reset)

                tablero.reboot()
                tablero.iniciar()
                jugada.clear()
                mpBack.start()
                temporizador.start()
            }


        }
        binding.btnGiveUp.setOnClickListener {
            tablero.giveUp()
            tableroAdapter.notifyItemRangeChanged(0, tablero.tamaño())
        }




        //marcador()
        errUpd()

        supportActionBar?.hide()

        // inicializamos jugada
        jugada = mutableSetOf()


        // iniciar el reproductor multimedia
        //mpBack = MediaPlayer.create(this@MainActivity,R.raw.bg)
       // mp = MediaPlayer.create(this@MainActivity, R.raw.super_mario_fx)
        mp.setVolume(0.25f,0.25f)
        // recycler view
        binding.rvTablero.apply {

            tableroAdapter = TableroAdapter(tablero)

            tableroAdapter.setOnItemClickListener { position ->

                    jugarCarta(position)


            }
            adapter = tableroAdapter
            setHasFixedSize(true);
            layoutManager = GridLayoutManager(this@MainActivity,DIMENSION)
        }
    }

    override fun onPause() {
        super.onPause()
        mpBack.stop()
    }

    override fun onRestart() {
        super.onRestart()
        mpBack.start()
    }
    private fun marcador() {
        binding.subtitle.text = resources.getString(R.string.etq_puntos,puntuacion)
    }

    private fun errUpd() {
        binding.etqErr.text = resources.getString(R.string.etq_err,errores)
    }

    /*private fun jugar(position: Int) {
        if (jugando) {
            if(tablero.celda(position)?.visible==false) {
                contador++;
                tablero.estado(position, true)
                tableroAdapter.notifyItemChanged(position)

                if (contador == 1) anterior = position

                if (contador == 2) {
                    //comprobamos las cartas

                    if (position == anterior) {
                        contador--
                    } else {
                        //val c1 = tablero.celda(anterior)
                        //val c2 = tablero.celda(position)

                        if (tablero.iguales(anterior, position) == true) {
                            mp.start()
                            mp.setOnCompletionListener {
                                puntuacion += 100

                                marcador()

                                if (tablero.endGame()) {
                                    gameOver()
                                }
                            }
                        } else {
                            thread {
                                Thread.sleep(1000)

                                runOnUiThread {
                                    tablero.estado(anterior, false)
                                    tableroAdapter.notifyItemChanged(anterior)

                                    tableroAdapter.notifyItemChanged(position)
                                    tablero.estado(position, false)
                                }
                            }
                        }
                        contador = 0
                    }
                }
            }
        }
    }*/

    private fun jugarCarta(position: Int) {
        if(tablero.visible(position)) return
        if(jugada.size < 2)
        {
            jugada.add(position)
            tablero.estado(position, true)
            tableroAdapter.notifyItemChanged(position)

        }
        if(jugada.size == 2) {
            if(cartasIguales()) {
                puntuacion += 100

                if(tablero.endGame()) binding.konfettiView.start(Presets.explode())
                //marcador()
                mp.start()
                mp.setOnCompletionListener {
                    if (tablero.endGame()) {

                        // mostramos confetti

                        gameOver()
                    }
                }
                jugada.clear()
            } else {
                // penalizo la jugada
                puntuacion -= 25

                if(puntuacion < 0) puntuacion = 0
                //marcador()
                errores ++
                errUpd()
                thread {
                    Thread.sleep(1000)

                    runOnUiThread {
                        jugada.forEach {
                            tablero.estado(it, false)
                            tableroAdapter.notifyItemChanged(it)
                        }
                        jugada.clear()
                    }
                }
            }

        }

    }

    private fun gameOver(estado: Boolean = true) {
        MaterialAlertDialogBuilder(this@MainActivity)
            .setTitle(if(estado) resources.getString(R.string.dialog_title,puntuacion,errores) else resources.getString(R.string.dialog_title_time,puntuacion,errores))
            .setMessage(resources.getString(R.string.dialog_msg))
            .setNegativeButton(resources.getString(R.string.cancell_btn)) { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton(resources.getString(R.string.accept_btn)) { dialog, which ->
                reset()
            }
            .show()

            mpBack.stop()
            mpBack.prepare()
            temporizador.cancel()

    }

    fun reset() {
        puntuacion = 0
        //marcador()
        errores = 0
        errUpd()
        jugando = false
        tablero.hideAll()
        binding.btnInicio.text = resources.getString(R.string.btn_inicio)
        /*for(i in 0..16) {
            tableroAdapter.notifyItemChanged(i)
        }*/
        tableroAdapter.notifyItemRangeChanged(0, tablero.tamaño())

        //tablero.iniciar()
        // paramos el temporizador

    }

    fun cartasIguales() : Boolean {
        return tablero.iguales(jugada.elementAt(0),jugada.elementAt(1))
    }

    override fun onDestroy() {
        super.onDestroy()
        mpBack.release()
        mp.release()
    }
}