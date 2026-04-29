package com.poker.tournamentdirector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.poker.tournamentdirector.presentation.navigation.TournamentDirectorNavHost
import com.poker.tournamentdirector.ui.theme.PokerTournamentDirectorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokerTournamentDirectorTheme {
                TournamentDirectorNavHost()
            }
        }
    }
}
