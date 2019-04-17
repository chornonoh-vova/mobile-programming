package com.hbvhuwe.osu.views

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlin.random.Random

class GameSurfaceView(ctx: Context, attrs: AttributeSet):
    SurfaceView(ctx, attrs),
    Runnable,
    View.OnTouchListener,
    LifecycleObserver {

    private lateinit var thread: Thread
    private var runFlag = false

    private var circleX = 100f
    private var circleY = 100f

    private var radius = 75f

    private var updateSpeed = 1000

    private var lastTime = System.currentTimeMillis()

    private var paint = Paint().also {
        it.flags = Paint.ANTI_ALIAS_FLAG
        it.color = Color.BLUE
    }

    var listener: OnCircleClickListener? = null

    private val rand = Random(System.currentTimeMillis())

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        thread = Thread(this)

        runFlag = true

        thread.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        runFlag = false
    }

    override fun run() {
        while (runFlag) {
            if (!holder.surface.isValid)
                continue

            if ((System.currentTimeMillis() - lastTime) >= updateSpeed) {
                lastTime = System.currentTimeMillis()
                updateCirclesPosition()
            }

            val canvas = holder.lockCanvas()

            canvas.drawColor(Color.WHITE)

            canvas.drawCircle(circleX, circleY, radius, paint)

            holder.unlockCanvasAndPost(canvas)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {

                val dx = Math.abs(event.x - circleX).toDouble()
                val dy = Math.abs(event.y - circleY).toDouble()

                val two = 2.toDouble()

                if ((Math.pow(dx, two) + Math.pow(dy, two)) < Math.pow(radius.toDouble(), two)) {
                    listener?.onCircleClick()

                    lastTime = System.currentTimeMillis()
                    updateCirclesPosition()
                }
            }
        }
        return true
    }

    private fun updateCirclesPosition() {
        circleX = randomX()
        circleY = randomY()
    }

    private fun randomX() = rand.nextInt(radius.toInt(), width - radius.toInt()).toFloat()

    private fun randomY() = rand.nextInt(radius.toInt(), height - radius.toInt()).toFloat()

    interface OnCircleClickListener {
        fun onCircleClick()
    }
}