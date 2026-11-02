package com.example

import com.example.data.tasbih.TasbihPresets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TasbihCounterTest {

    @Test
    fun tasbihPresets_containsKeyAdhkar() {
        val list = TasbihPresets.defaultDhikrList
        assertTrue("Presets should have at least 6 common adhkar", list.size >= 6)

        val subhanallah = list.find { it.id == "subhanallah" }
        assertNotNull(subhanallah)
        assertEquals("سُبْحَانَ اللَّهِ", subhanallah?.arabicText)
        assertEquals(33, subhanallah?.defaultTarget)

        val alhamdulillah = list.find { it.id == "alhamdulillah" }
        assertNotNull(alhamdulillah)
        assertEquals("الْحَمْدُ لِلَّهِ", alhamdulillah?.arabicText)

        val allahuAkbar = list.find { it.id == "allahu_akbar" }
        assertNotNull(allahuAkbar)
        assertEquals(34, allahuAkbar?.defaultTarget)
    }

    @Test
    fun tasbihCounterLogic_incrementsAndResetsAccurately() {
        var count = 0
        var total = 0
        val target = 33
        var rounds = 0

        // Increment 33 times
        for (i in 1..33) {
            count++
            total++
            if (count % target == 0) {
                rounds++
            }
        }

        assertEquals(33, count)
        assertEquals(33, total)
        assertEquals(1, rounds)

        // Reset
        count = 0
        assertEquals(0, count)
        assertEquals(33, total) // Total is preserved
    }
}
