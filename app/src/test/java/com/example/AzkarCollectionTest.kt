package com.example

import com.example.data.azkar.AzkarCategory
import com.example.data.azkar.AzkarCollection
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AzkarCollectionTest {

    @Test
    fun azkarCollection_containsKeyCategories() {
        val azkar = AzkarCollection.allAzkar
        assertTrue("Azkar list should have items", azkar.isNotEmpty())

        val morning = azkar.filter { it.category == AzkarCategory.MORNING }
        assertTrue("Morning azkar should be populated", morning.isNotEmpty())

        val evening = azkar.filter { it.category == AzkarCategory.EVENING }
        assertTrue("Evening azkar should be populated", evening.isNotEmpty())

        val sleep = azkar.filter { it.category == AzkarCategory.BEFORE_SLEEP }
        assertTrue("Sleep azkar should be populated", sleep.isNotEmpty())
    }

    @Test
    fun azkarItems_containValidArabicAndSomaliText() {
        for (item in AzkarCollection.allAzkar) {
            assertTrue(item.arabicText.isNotBlank())
            assertTrue(item.translationSomali.isNotBlank())
            assertTrue(item.translationEnglish.isNotBlank())
            assertTrue(item.reference.isNotBlank())
            assertTrue(item.repetitions > 0)
        }
    }

    @Test
    fun azkarRandomPickers_returnNonEmptyItems() {
        val randomDaily = AzkarCollection.getRandomDailyAzkar()
        assertNotNull(randomDaily)
        assertTrue(randomDaily.arabicText.isNotBlank())

        val randomMorning = AzkarCollection.getRandomAzkarForCategory(AzkarCategory.MORNING)
        assertNotNull(randomMorning)
        assertTrue(randomMorning.category == AzkarCategory.MORNING)
    }
}
