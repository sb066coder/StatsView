package ru.netology.statsview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.statsview.ui.StatsView
import ru.netology.statsview.ui.StatsViewNotFilled

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<StatsView>(R.id.statsView).data = listOf(
            150F,
            200F,
            200F,
            400F
        )
        findViewById<StatsViewNotFilled>(R.id.statsView2).data = listOf(
            0.15F,
            0.25F,
            0.15F,
            0.2F
        )
    }
}