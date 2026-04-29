package com.poker.tournamentdirector

import com.poker.tournamentdirector.domain.model.PremiumStatus
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PremiumStatusTest {
    @Test
    fun `free status shows ads`() {
        val status = PremiumStatus(isPremium = false)

        assertTrue(status.shouldShowAds)
    }

    @Test
    fun `premium status hides ads`() {
        val status = PremiumStatus(isPremium = true)

        assertFalse(status.shouldShowAds)
    }
}
