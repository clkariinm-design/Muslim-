package com.example.data.tasbih

data class DhikrItem(
    val id: String,
    val arabicText: String,
    val transliteration: String,
    val englishMeaning: String,
    val somaliMeaning: String,
    val defaultTarget: Int
)

object TasbihPresets {
    val defaultDhikrList = listOf(
        DhikrItem(
            id = "subhanallah",
            arabicText = "سُبْحَانَ اللَّهِ",
            transliteration = "SubhanAllah",
            englishMeaning = "Glory be to Allah",
            somaliMeaning = "Nasahnaan Eebaa leh",
            defaultTarget = 33
        ),
        DhikrItem(
            id = "alhamdulillah",
            arabicText = "الْحَمْدُ لِلَّهِ",
            transliteration = "Alhamdulillah",
            englishMeaning = "All praise is for Allah",
            somaliMeaning = "Mahad oo dhan Eebaa leh",
            defaultTarget = 33
        ),
        DhikrItem(
            id = "allahu_akbar",
            arabicText = "اللَّهُ أَكْبَرُ",
            transliteration = "Allahu Akbar",
            englishMeaning = "Allah is the Greatest",
            somaliMeaning = "Eebaa Weyn",
            defaultTarget = 34
        ),
        DhikrItem(
            id = "astaghfirullah",
            arabicText = "أَسْتَغْفِرُ اللَّهَ",
            transliteration = "Astaghfirullah",
            englishMeaning = "I seek forgiveness from Allah",
            somaliMeaning = "Waxaan Eebe weydiisanayaa dambi dhaaf",
            defaultTarget = 100
        ),
        DhikrItem(
            id = "la_ilaha_illallah",
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ",
            transliteration = "La ilaha illallah",
            englishMeaning = "There is no god worthy of worship except Allah",
            somaliMeaning = "Ilaah kale ma jiro Eebe mooyee",
            defaultTarget = 100
        ),
        DhikrItem(
            id = "salawat",
            arabicText = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ",
            transliteration = "Allahumma Salli 'ala Muhammad",
            englishMeaning = "O Allah, send blessings upon Muhammad",
            somaliMeaning = "Ilaahow naxariiso Nabi Muxammad (NNKH)",
            defaultTarget = 100
        ),
        DhikrItem(
            id = "subhanallah_wa_bihamdihi",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            transliteration = "SubhanAllahi wa bihamdihi",
            englishMeaning = "Glory be to Allah and His is the praise",
            somaliMeaning = "Nasahnaan Eebaa leh oo mahaddiisaan ku bilaabayaa",
            defaultTarget = 100
        ),
        DhikrItem(
            id = "la_hawla",
            arabicText = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "La hawla wa la quwwata illa billah",
            englishMeaning = "There is no power nor strength except with Allah",
            somaliMeaning = "Awood iyo xeelad ma jirto Eebe mooyee",
            defaultTarget = 100
        )
    )
}
