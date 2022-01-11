package com.libo.greedysnake

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    var sceneView: SceneView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById(R.id.sceneView)
        sceneView?.startGame()
    }

    override fun onStop() {
        super.onStop()

        sceneView?.stopGame()
    }
}