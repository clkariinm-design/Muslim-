package com.example.data.azkar

enum class AzkarCategory(val titleEnglish: String, val titleSomali: String, val icon: String) {
    MORNING("Morning Azkar", "Adkaarta Subaxda", "🌅"),
    EVENING("Evening Azkar", "Adkaarta Galabta", "🌇"),
    BEFORE_SLEEP("Before Sleeping", "Adkaarta Hurdada", "🌙"),
    WAKING_UP("Waking Up", "Toositaanka Hurdada", "☀️"),
    POST_PRAYER("After Prayer", "Salaadda Kadib", "🕌"),
    GENERAL("Daily Remembrance", "Dhikr Guud", "📿")
}

data class AzkarItem(
    val id: String,
    val category: AzkarCategory,
    val arabicText: String,
    val transliteration: String,
    val translationSomali: String,
    val translationEnglish: String,
    val repetitions: Int = 1,
    val reference: String,
    val virtue: String = ""
)

object AzkarCollection {

    val allAzkar: List<AzkarItem> = listOf(
        // Morning Azkar
        AzkarItem(
            id = "m_1",
            category = AzkarCategory.MORNING,
            arabicText = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "Asbahnaa wa asbahal-mulku lillaah, wal-hamdu lillaah, laa ilaaha illallaahu wahdahu laa shariika lah, lahul-mulku wa lahul-hamdu wa Huwa 'alaa kulli shay'in Qadiir",
            translationSomali = "Waxaan waabariisannay iyadoo boqortooyada oo dhan Eebaa iska leh, mahadna Eebaa iska leh. Ilaah kale ma jiro Eebe mooyee keligiis oo aan shariik lahayn. Isagaa xukunka iyo mahaddaba leh, wax kastaana wuu karaa.",
            translationEnglish = "We have entered a new day and with it all dominion belongs to Allah. Praise be to Allah; there is no deity worthy of worship except Allah, alone with no partner. His is the sovereignty and His is all praise, and He has power over all things.",
            repetitions = 1,
            reference = "Sahih Muslim",
            virtue = "Sunnah upon waking into morning"
        ),
        AzkarItem(
            id = "m_2",
            category = AzkarCategory.MORNING,
            arabicText = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ",
            transliteration = "Allaahumma bika asbahnaa, wa bika amsaynaa, wa bika nahyaa, wa bika namuutu, wa ilaykan-nushuur",
            translationSomali = "Ilaahow Adiga dartaa ayaan ku waabariisannay, Adiga dartaana waxaan ku galbaysannay, Adiga dartaana waxaan u noolnahay, Adiga dartaana waxaan u dhimanaynaa, xaggaagana waa soo noolayntii.",
            translationEnglish = "O Allah, by You we enter the morning, by You we enter the evening, by You we live, by You we die, and to You is the final Resurrection.",
            repetitions = 1,
            reference = "Sunan at-Tirmidhi (Sahih)",
            virtue = "Protects the servant through the morning hours"
        ),
        AzkarItem(
            id = "m_3",
            category = AzkarCategory.MORNING,
            arabicText = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            transliteration = "Bismillaahil-ladhii laa yadurru ma'as-mihii shay'un fil-ardi wa laa fis-samaa'i wa Huwas-Samii'ul-'Aliim",
            translationSomali = "Magaca Eebe yaan ku bilaabayaa kaas oo uusan magiciisa waxba ku waxyeelaynayn wax dhulka ku jira iyo wax cirka ku jira toona, waana Maqlaha wax walba Og.",
            translationEnglish = "In the Name of Allah, with Whose Name nothing can cause harm in the earth nor in the heavens, and He is the All-Hearing, All-Knowing.",
            repetitions = 3,
            reference = "Sunan Abu Dawud & At-Tirmidhi",
            virtue = "Whoever recites this 3 times in the morning and evening, nothing will harm him."
        ),
        AzkarItem(
            id = "m_4",
            category = AzkarCategory.MORNING,
            arabicText = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا",
            transliteration = "Radiitu billaahi Rabbaa, wa bil-Islaami diinaa, wa bi-Muhammadin sallallaahu 'alayhi wa sallama Nabiyyaa",
            translationSomali = "Waxaan ku raalli noqday Eebe inuu Rabbi ii yahay, Islaamkana diin ii yahay, Muxammadna (NNKH) uu Nebi ii yahay.",
            translationEnglish = "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad (peace and blessings be upon him) as my Prophet.",
            repetitions = 3,
            reference = "Sunan Abu Dawud & Ahmad",
            virtue = "Allah has promised to please whoever recites this 3 times morning & evening."
        ),

        // Evening Azkar
        AzkarItem(
            id = "e_1",
            category = AzkarCategory.EVENING,
            arabicText = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "Amsaynaa wa amsal-mulku lillaah, wal-hamdu lillaah, laa ilaaha illallaahu wahdahu laa shariika lah, lahul-mulku wa lahul-hamdu wa Huwa 'alaa kulli shay'in Qadiir",
            translationSomali = "Waxaan galbaysannay iyadoo boqortooyada oo dhan Eebaa iska leh, mahadna Eebaa iska leh. Ilaah kale ma jiro Eebe mooyee keligiis oo aan shariik lahayn.",
            translationEnglish = "We have reached the evening and all dominion belongs to Allah. All praise is for Allah; there is no deity worthy of worship except Allah, alone with no partner.",
            repetitions = 1,
            reference = "Sahih Muslim",
            virtue = "Sunnah upon reaching evening"
        ),
        AzkarItem(
            id = "e_2",
            category = AzkarCategory.EVENING,
            arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
            transliteration = "A'uudhu bi-kalimaatillaahit-taammaati min sharri maa khalaq",
            translationSomali = "Waxaan Eebe ka magangalayaa erayadiisa taamka ah sharka wixii uu abuuray.",
            translationEnglish = "I seek refuge in the perfect words of Allah from the evil of what He has created.",
            repetitions = 3,
            reference = "Sahih Muslim",
            virtue = "No poisonous creature or harm will touch whoever recites this in the evening."
        ),

        // Before Sleep
        AzkarItem(
            id = "s_1",
            category = AzkarCategory.BEFORE_SLEEP,
            arabicText = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
            transliteration = "Bismika Rabbii wada'tu janbii wa bika arfa'uh, fa-in amsakta nafsii farhamhaa, wa in arsaltahaa fahfazhaa bimaa tahfazu bihii 'ibaadakas-saalihiin",
            translationSomali = "Magacaaga Rabbiyow ayaan ku dhigay dhinacayga, magacaagana ayaan ku qaadayaa. Haddaad naftayda qabatid u naxariiso, haddaad soo daysana ku ilaali waxaad ku ilaaliso addoomahaaga wanaagsan.",
            translationEnglish = "In Your Name, my Lord, I lay down my side and in Your Name I raise it. If You take my soul, forgive it; and if You send it back, protect it as You protect Your righteous servants.",
            repetitions = 1,
            reference = "Sahih al-Bukhari & Muslim",
            virtue = "Recited upon going to bed"
        ),
        AzkarItem(
            id = "s_2",
            category = AzkarCategory.BEFORE_SLEEP,
            arabicText = "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ",
            transliteration = "Allaahumma qinii 'adhaabaka yawma tab'athu 'ibaadak",
            translationSomali = "Ilaahow iga nabadgeli cadaabkaaga maalinta aad soo noolaynayso addoomahaaga.",
            translationEnglish = "O Allah, save me from Your punishment on the Day when You resurrect Your servants.",
            repetitions = 3,
            reference = "Sunan Abu Dawud (Sahih)",
            virtue = "Prophet (pbuh) placed his right hand under his cheek and said this 3 times."
        ),

        // Waking Up
        AzkarItem(
            id = "w_1",
            category = AzkarCategory.WAKING_UP,
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            transliteration = "Al-hamdu lillaahil-ladhii ahyaanaa ba'da maa amaatanaa wa ilayhin-nushuur",
            translationSomali = "Mahad oo dhan Eebaa leh kaas oo na nooleeyay ka dib markuu na dilay (hurdo), xaggiisana waa soo noolayntii.",
            translationEnglish = "All praise is for Allah Who gave us life after having caused us to die, and unto Him is the resurrection.",
            repetitions = 1,
            reference = "Sahih al-Bukhari",
            virtue = "First words upon waking from sleep"
        ),

        // General / Heart Remembrances
        AzkarItem(
            id = "g_1",
            category = AzkarCategory.GENERAL,
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ",
            transliteration = "Subhaanallaahi wa bihamdihi, 'adada khalqihi, wa ridaa nafsihi, wa zinata 'Arshihi, wa midaada kalimaatih",
            translationSomali = "Nasahnaan Eebaa leh oo mahaddiisaan ku bilaabayaa, tirada uunkiisa le'eg, raallinimada naftiisa le'eg, miisaanka carshigiisa le'eg, iyo tirada erayadiisa le'eg.",
            translationEnglish = "Glory be to Allah and all praise is His, by the number of His creation, by the pleasure of Himself, by the weight of His Throne, and by the ink of His words.",
            repetitions = 3,
            reference = "Sahih Muslim",
            virtue = "Weighs more than hours of continual dhikr in reward."
        ),
        AzkarItem(
            id = "g_2",
            category = AzkarCategory.GENERAL,
            arabicText = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ، وَلَا تَكِلْنِي إِلَىٰ نَفْسِي طَرْفَةَ عَيْنٍ",
            transliteration = "Yaa Hayyu yaa Qayyuumu bi-rahmatika astaghiith, aslih lii sha'nii kullah, wa laa takilnii ilaa nafsii tarfata 'ayn",
            translationSomali = "Nooloow Joogtoow, naxariistaada ayaan gargaar ku weydiisanayaa; hagaaji xaalkayga oo dhan, hal ilbiriqsina naftayda ha ii daynin.",
            translationEnglish = "O Ever-Living, O Self-Subsisting, by Your mercy I seek assistance. Rectify for me all of my affairs and do not leave me to myself even for the blink of an eye.",
            repetitions = 1,
            reference = "Mustadrak al-Hakim (Hasan)",
            virtue = "Comprehensive supplication for guidance and divine aid."
        )
    )

    fun getRandomAzkarForCategory(category: AzkarCategory): AzkarItem {
        val filtered = allAzkar.filter { it.category == category }
        return if (filtered.isNotEmpty()) filtered.random() else allAzkar.random()
    }

    fun getRandomDailyAzkar(): AzkarItem {
        return allAzkar.random()
    }
}
