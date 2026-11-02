package com.example.data.quran

object QuranDataSource {

    val allSurahs: List<Surah> = listOf(
        Surah(1, "الفاتحة", "Al-Fatihah", "The Opening", "Furaha / Bilowga", "Meccan", 7),
        Surah(2, "البقرة", "Al-Baqarah", "The Cow", "Sacda", "Medinan", 286),
        Surah(3, "آل عمران", "Ali 'Imran", "Family of Imran", "Reer Cimraan", "Medinan", 200),
        Surah(4, "النساء", "An-Nisa", "The Women", "Haweenka", "Medinan", 176),
        Surah(5, "المائدة", "Al-Ma'idah", "The Table Spread", "Miiska Cuntada", "Medinan", 120),
        Surah(6, "الأنعام", "Al-An'am", "The Cattle", "Xoolaha", "Meccan", 165),
        Surah(7, "الأعراف", "Al-A'raf", "The Heights", "Daaqyada Sare", "Meccan", 206),
        Surah(8, "الأنفال", "Al-Anfal", "The Spoils of War", "Qaniimada", "Medinan", 75),
        Surah(9, "التوبة", "At-Tawbah", "The Repentance", "Toobada", "Medinan", 129),
        Surah(10, "يونس", "Yunus", "Jonah", "Yoonis", "Meccan", 109),
        Surah(11, "هود", "Hud", "Hud", "Huud", "Meccan", 123),
        Surah(12, "يوسف", "Yusuf", "Joseph", "Yuusuf", "Meccan", 111),
        Surah(13, "الرعد", "Ar-Ra'd", "The Thunder", "Onkodka", "Medinan", 43),
        Surah(14, "إبراهيم", "Ibrahim", "Abraham", "Ibraahiim", "Meccan", 52),
        Surah(15, "الحجر", "Al-Hijr", "The Rocky Tract", "Xajar", "Meccan", 99),
        Surah(16, "النحل", "An-Nahl", "The Bee", "Shinida", "Meccan", 128),
        Surah(17, "الإسراء", "Al-Isra", "The Night Journey", "Dheelminta", "Meccan", 111),
        Surah(18, "الكهف", "Al-Kahf", "The Cave", "Godka", "Meccan", 110),
        Surah(19, "مريم", "Maryam", "Mary", "Maryam", "Meccan", 98),
        Surah(20, "طه", "Ta-Ha", "Ta-Ha", "Daa-Haa", "Meccan", 135),
        Surah(21, "الأنبياء", "Al-Anbiya", "The Prophets", "Nebiyada", "Meccan", 112),
        Surah(22, "الحج", "Al-Hajj", "The Pilgrimage", "Xajka", "Medinan", 78),
        Surah(23, "المؤمنون", "Al-Mu'minun", "The Believers", "Mu'miniinta", "Meccan", 118),
        Surah(24, "النور", "An-Nur", "The Light", "Nuurka", "Medinan", 64),
        Surah(25, "الفرقان", "Al-Furqan", "The Criterion", "Kala-Saaraha", "Meccan", 77),
        Surah(26, "الشعراء", "Ash-Shu'ara", "The Poets", "Gabayada", "Meccan", 227),
        Surah(27, "النمل", "An-Naml", "The Ant", "Qudhaanjada", "Meccan", 93),
        Surah(28, "القصص", "Al-Qasas", "The Stories", "Qisooyinka", "Meccan", 88),
        Surah(29, "العنكبوت", "Al-'Ankabut", "The Spider", "Caarada", "Meccan", 69),
        Surah(30, "الروم", "Ar-Rum", "The Romans", "Ruum", "Meccan", 60),
        Surah(31, "لقمان", "Luqman", "Luqman", "Luqmaan", "Meccan", 34),
        Surah(32, "السجدة", "As-Sajdah", "The Prostration", "Sujuudda", "Meccan", 30),
        Surah(33, "الأحزاب", "Al-Ahzab", "The Combined Forces", "Xisbiyada", "Medinan", 73),
        Surah(34, "سبأ", "Saba", "Sheba", "Saba'", "Meccan", 54),
        Surah(35, "فاطر", "Fatir", "Originator", "Abuuraha", "Meccan", 45),
        Surah(36, "يس", "Ya-Sin", "Ya-Sin", "Yaa-Siin", "Meccan", 83),
        Surah(37, "الصافات", "As-Saffat", "Those Who Set The Ranks", "Safafka", "Meccan", 182),
        Surah(38, "ص", "Sad", "The Letter Sad", "Saad", "Meccan", 88),
        Surah(39, "الزمر", "Az-Zumar", "The Troops", "Kooxaha", "Meccan", 75),
        Surah(40, "غافر", "Ghafir", "The Forgiver", "Dambi-Dhaafaha", "Meccan", 85),
        Surah(41, "فصلت", "Fussilat", "Explained In Detail", "Kala-Baxsan", "Meccan", 54),
        Surah(42, "الشورى", "Ash-Shura", "The Consultation", "Wadatashiga", "Meccan", 53),
        Surah(43, "الزخرف", "Az-Zukhruf", "The Ornaments of Gold", "Quruxda Dahabka", "Meccan", 89),
        Surah(44, "الدخان", "Ad-Dukhan", "The Smoke", "Qiaca", "Meccan", 59),
        Surah(45, "الجاثية", "Al-Jathiyah", "The Crouching", "Jilba-Joogsiga", "Meccan", 37),
        Surah(46, "الأحقاف", "Al-Ahqaf", "The Wind-Curved Sandhills", "Bariisha Qaloocan", "Meccan", 35),
        Surah(47, "محمد", "Muhammad", "Muhammad", "Muxammad (NNKH)", "Medinan", 38),
        Surah(48, "الفتح", "Al-Fath", "The Victory", "Furashada", "Medinan", 29),
        Surah(49, "الحجرات", "Al-Hujurat", "The Rooms", "Qolalka", "Medinan", 18),
        Surah(50, "ق", "Qaf", "The Letter Qaf", "Qaaf", "Meccan", 45),
        Surah(51, "الذاريات", "Adh-Dhariyat", "The Winnowing Winds", "Dabaylaha Firdhiya", "Meccan", 60),
        Surah(52, "الطور", "At-Tur", "The Mount", "Buurta Duur", "Meccan", 49),
        Surah(53, "النجم", "An-Najm", "The Star", "Xiddigta", "Meccan", 62),
        Surah(54, "القمر", "Al-Qamar", "The Moon", "Dayaxa", "Meccan", 55),
        Surah(55, "الرحمن", "Ar-Rahman", "The Beneficent", "Naxariistaha", "Medinan", 78),
        Surah(56, "الواقعة", "Al-Waqi'ah", "The Inevitable", "Dhacdada Xaqqa ah", "Meccan", 96),
        Surah(57, "الحديد", "Al-Hadid", "The Iron", "Birta", "Medinan", 29),
        Surah(58, "المجادلة", "Al-Mujadila", "The Pleading Woman", "Muranta", "Medinan", 22),
        Surah(59, "الحشر", "Al-Hashr", "The Exile", "Kala-Cayrinta", "Medinan", 24),
        Surah(60, "الممتحنة", "Al-Mumtahanah", "She That Is To Be Examined", "La Imtixaantay", "Medinan", 13),
        Surah(61, "الصف", "As-Saff", "The Ranks", "Safafka Dagaalka", "Medinan", 14),
        Surah(62, "الجمعة", "Al-Jumu'ah", "The Congregation", "Jimcada", "Medinan", 11),
        Surah(63, "المنافقون", "Al-Munafiqun", "The Hypocrites", "Munaafiqiinta", "Medinan", 11),
        Surah(64, "التغابن", "At-Taghabun", "The Mutual Disillusion", "Kala-Faa'iidada", "Medinan", 18),
        Surah(65, "الطلاق", "At-Talaq", "The Divorce", "Furiinka", "Medinan", 12),
        Surah(66, "التحريم", "At-Tahrim", "The Prohibition", "Xaaraantimaynta", "Medinan", 12),
        Surah(67, "الملك", "Al-Mulk", "The Sovereignty", "Boqortooyada", "Meccan", 30),
        Surah(68, "القلم", "Al-Qalam", "The Pen", "Qalinka", "Meccan", 52),
        Surah(69, "الحاقة", "Al-Haqqah", "The Inevitable Truth", "Xaqiiqada Qiyaamaha", "Meccan", 52),
        Surah(70, "المعارج", "Al-Ma'arij", "The Ascending Stairways", "Dallacaadaha", "Meccan", 44),
        Surah(71, "نوح", "Nuh", "Noah", "Nuux", "Meccan", 28),
        Surah(72, "الجن", "Al-Jinn", "The Jinn", "Jinka", "Meccan", 28),
        Surah(73, "المزمل", "Al-Muzzammil", "The Enshrouded One", "Isku-Dadaayaha", "Meccan", 20),
        Surah(74, "المدثر", "Al-Muddaththir", "The Cloaked One", "Isku-Gadaamayaha", "Meccan", 56),
        Surah(75, "القيامة", "Al-Qiyamah", "The Resurrection", "Qiyaamaha", "Meccan", 40),
        Surah(76, "الإنسان", "Al-Insan", "Man", "Aadanaha", "Medinan", 31),
        Surah(77, "المرسلات", "Al-Mursalat", "The Emissaries", "Kuwa La Soo Diray", "Meccan", 50),
        Surah(78, "النبأ", "An-Naba", "The Tidings", "Warka Weyn", "Meccan", 40),
        Surah(79, "النازعات", "An-Nazi'at", "Those Who Drag Forth", "Kuwa Soo Bixi", "Meccan", 46),
        Surah(80, "عبس", "Abasa", "He Frowned", "Wuu Wajigabiyay", "Meccan", 42),
        Surah(81, "التكوير", "At-Takwir", "The Overthrowing", "Duubista Qorraxda", "Meccan", 29),
        Surah(82, "الانفطار", "Al-Infitar", "The Cleaving", "Dillaaca Samada", "Meccan", 19),
        Surah(83, "المطففين", "Al-Mutaffifin", "Defrauding", "Kuwa Miisaanka Dhima", "Meccan", 36),
        Surah(84, "الانشقاق", "Al-Inshiqaq", "The Splitting Open", "Jeexanka Samada", "Meccan", 25),
        Surah(85, "البروج", "Al-Buruj", "The Mansions of the Stars", "Daarahayada Xiddigaha", "Meccan", 22),
        Surah(86, "الطارق", "At-Tariq", "The Morning Star", "Xiddigta Habeenkii Yimaadda", "Meccan", 17),
        Surah(87, "الأعلى", "Al-A'la", "The Most High", "Kan Sareeya", "Meccan", 19),
        Surah(88, "الغاشية", "Al-Ghashiyah", "The Overwhelming", "Daboolaha Qiyaamaha", "Meccan", 26),
        Surah(89, "الفجر", "Al-Fajr", "The Dawn", "Waaga / Waaberiga", "Meccan", 30),
        Surah(90, "البلد", "Al-Balad", "The City", "Magaalada Makka", "Meccan", 20),
        Surah(91, "الشمس", "Ash-Shams", "The Sun", "Qorraxda", "Meccan", 15),
        Surah(92, "الليل", "Al-Layl", "The Night", "Habeenka", "Meccan", 21),
        Surah(93, "الضحى", "Ad-Duha", "The Morning Brightness", "Barqada", "Meccan", 11),
        Surah(94, "الشرح", "Ash-Sharh", "The Expansion of Breast", "Furtanka Laabta", "Meccan", 8),
        Surah(95, "التين", "At-Tin", "The Fig", "Geedka Berdaha", "Meccan", 8),
        Surah(96, "العلق", "Al-'Alaq", "The Clot", "Xinjirta", "Meccan", 19),
        Surah(97, "القدر", "Al-Qadr", "The Night of Decree", "Habeenka Sharafta", "Meccan", 5),
        Surah(98, "البينة", "Al-Bayyinah", "The Clear Evidence", "Caddaynta Cad", "Medinan", 8),
        Surah(99, "الزلزلة", "Az-Zalzalah", "The Earthquake", "Dhulgariirka", "Medinan", 8),
        Surah(100, "العاديات", "Al-'Adiyat", "The Courser", "Fardaha Orodka Badan", "Meccan", 11),
        Surah(101, "القارعة", "Al-Qari'ah", "The Calamity", "Qiyaamaha", "Meccan", 11),
        Surah(102, "التكاثر", "At-Takathur", "The Rivalry in World Increase", "Tartanka Dunida", "Meccan", 8),
        Surah(103, "العصر", "Al-'Asr", "The Declining Day", "Waqtiga Casarka", "Meccan", 3),
        Surah(104, "الهمزة", "Al-Humazah", "The Traducer", "Xanta iyo Ceebaynta", "Meccan", 9),
        Surah(105, "الفيل", "Al-Fil", "The Elephant", "Maroodiga", "Meccan", 5),
        Surah(106, "قريش", "Quraysh", "Quraysh", "Quraysh", "Meccan", 4),
        Surah(107, "الماعون", "Al-Ma'un", "Small Kindnesses", "Wixii Wax Lagu Dhalo", "Meccan", 7),
        Surah(108, "الكوثر", "Al-Kawthar", "Abundance", "Webiga Kowthar", "Meccan", 3),
        Surah(109, "الكافرون", "Al-Kafirun", "The Disbelievers", "Gaalada", "Meccan", 6),
        Surah(110, "النصر", "An-Nasr", "Divine Support", "Gargaarka Alle", "Medinan", 3),
        Surah(111, "المسد", "Al-Masad", "The Palm Fiber", "Xadhigga Xilliga", "Meccan", 5),
        Surah(112, "الإخلاص", "Al-Ikhlas", "The Sincerity", "Ikhlaaska (Keliyeynta)", "Meccan", 4),
        Surah(113, "الفلق", "Al-Falaq", "The Daybreak", "Waaga Furmay", "Meccan", 5),
        Surah(114, "الناس", "An-Nas", "Mankind", "Dadka", "Meccan", 6)
    )

    private val detailedAyahs: Map<Int, List<Ayah>> = mapOf(
        // 1. Al-Fatihah
        1 to listOf(
            Ayah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Bismillaahir-Rahmaanir-Rahiim", "In the Name of Allah—the Most Compassionate, Most Merciful.", "Magaca Eebe yaan ku bilaabaynaa ee Naxariis Guud iyo mid Gaaraba Naxariista.", 1),
            Ayah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Al-hamdu lillaahi Rabbil-'aalamiin", "All praise is for Allah—Lord of all worlds,", "Mahad oo dhan Eebaa leh ee Barbaariyaha uunka ah.", 1),
            Ayah(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "Ar-Rahmaanir-Rahiim", "the Most Compassionate, Most Merciful,", "Ee Naxariis guud iyo mid gaaraba Naxariista.", 1),
            Ayah(1, 4, "مَالِكِ يَوْمِ الدِّينِ", "Maaliki Yawmid-Diin", "Master of the Day of Judgment.", "Ee Hantiyaha Maalinta Abaalmarinta ah.", 1),
            Ayah(1, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "Iyyaaka na'budu wa iyyaaka nasta'iin", "You alone we worship and You alone we ask for help.", "Adiga kaliya ayaan ku caabudaynaa, Adigana ayaan kaa kaalmo weydiisanaynaa.", 1),
            Ayah(1, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Ihdinas-Siraatal-Mustaqiim", "Guide us along the Straight Path,", "Nagu hanuuni Jidka Toosan,", 1),
            Ayah(1, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "Siraatal-ladhiina an'amta 'alayhim ghayril-maghduubi 'alayhim wa lad-daalliin", "the Path of those You have blessed—not those who have incurred Your wrath, nor those who have gone astray.", "Jidka kuwii aad u Nicmaysay, aan ahayn kuwii loo carooday iyo kuwa lunsan toona.", 1)
        ),
        // 112. Al-Ikhlas
        112 to listOf(
            Ayah(112, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Qul Huwal-laahu Ahad", "Say, ˹O Prophet,˺ “He is Allah—One ˹and Indivisible˺;", "Waxaad dhahdaa: Isagu Eebe waa Mid Keliya,", 30),
            Ayah(112, 2, "اللَّهُ الصَّمَدُ", "Allaahus-Samad", "Allah—the Sustainer ˹needed by all˺.", "Eebe waa kan loo baahanyahay (ee cidi ka maarmin).", 30),
            Ayah(112, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "Lam yalid wa lam yuulad", "He has never had offspring, nor was He born.", "Ma uu dhalin, lamana dhalin,", 30),
            Ayah(112, 4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Wa lam yakul-lahuu kufuwan ahad", "And there is none comparable to Him.”", "Mana jiro mid u dhigma ama la mid ah.", 30)
        ),
        // 113. Al-Falaq
        113 to listOf(
            Ayah(113, 1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Qul a'uudhu bi Rabbil-falaq", "Say, ˹O Prophet,˺ “I seek refuge in the Lord of the daybreak", "Waxaad dhahdaa: Waxaan Eebe ka magangalay Rabbiga Waaga dillaaca,", 30),
            Ayah(113, 2, "مِن شَرِّ مَا خَلَقَ", "Min sharri maa khalaq", "from the evil of whatever He has created,", "Sharciyihii wixii uu abuuray,", 30),
            Ayah(113, 3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "Wa min sharri ghaasiqin idhaa waqab", "and from the evil of the night when it grows dark,", "Iyo sharka habeenka marka uu madoobaado,", 30),
            Ayah(113, 4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "Wa min sharrin-naffaathaati fil-'uqad", "and from the evil of those who blow on knots,", "Iyo sharka kuwa guntimaha wax ku afuufa (sixiroolayaasha),", 30),
            Ayah(113, 5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "Wa min sharri haasidin idhaa hasad", "and from the evil of an envier when they envy.”", "Iyo sharka qofka xaasidka ah marka uu wax xaasido.", 30)
        ),
        // 114. An-Nas
        114 to listOf(
            Ayah(114, 1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Qul a'uudhu bi Rabbin-naas", "Say, ˹O Prophet,˺ “I seek refuge in the Lord of humankind,", "Waxaad dhahdaa: Waxaan magangalay Eebaha Dadka,", 30),
            Ayah(114, 2, "مَلِكِ النَّاسِ", "Malikin-naas", "the Master of humankind,", "Boqorka Dadka,", 30),
            Ayah(114, 3, "إِلَٰهِ النَّاسِ", "Ilaahin-naas", "the God of humankind,", "Ilaaha Dadka,", 30),
            Ayah(114, 4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "Min sharril-waswaasil-khannaas", "from the evil of the lurking whisperer—", "Sharka waswaasiyaha dib u dhuunta,", 30),
            Ayah(114, 5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Alladhii yuwaswisu fii suduurin-naas", "who whispers into the hearts of humankind—", "Kii wax ku waswaasiya laabaha dadka,", 30),
            Ayah(114, 6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "Minal-jinnati wan-naas", "from among jinn and humankind.”", "Kuwii ka mid ah jinka iyo dadkaba.", 30)
        ),
        // 108. Al-Kawthar
        108 to listOf(
            Ayah(108, 1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Innaa a'taynaakal-Kawthar", "Indeed, We have granted you ˹O Prophet˺ abundant goodness.", "Xaqiiqdii, Waxaan ku siinnay (Nabi Muxammadow) Khayrka Badan (Webiga Kawthar).", 30),
            Ayah(108, 2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "Fasalli li-Rabbika wan-har", "So pray and sacrifice to your Lord ˹alone˺.", "Ee u tuko Rabbigaa dartiis, waxna qalo (xoolo gawrac).", 30),
            Ayah(108, 3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Inna shaani'aka huwal-abtar", "Only the one who hates you is truly cut off ˹from any goodness˺.", "Kii ku neceb ayaa ah midka khayrka laga gooyay.", 30)
        ),
        // 103. Al-Asr
        103 to listOf(
            Ayah(103, 1, "وَالْعَصْرِ", "Wal-'Asr", "By the ˹passage of˺ time!", "Waxaan ku dhaartay Waqtiga Casarka,", 30),
            Ayah(103, 2, "إِنَّ الْإِنسَانَ لَفِي خُسْرٍ", "Innal-insaana lafii khusr", "Surely humanity is in ˹grave˺ loss,", "Xaqiiqdii, Dadku waxay ku sugan yihiin khasaare weyn,", 30),
            Ayah(103, 3, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "Illal-ladhiina aamanuu wa 'amilus-saalihaati wa tawaasaw bil-haqqi wa tawaasaw bis-sabr", "except those who have faith, do good, and urge each other to the truth, and urge each other to perseverance.", "Marka laga reebo kuwa rumeeyay, samaystay camal wanaagsan, xaqa isku dardaarmay, sabarkana isku dardaarmay.", 30)
        ),
        // 67. Al-Mulk (first 5 Ayahs)
        67 to listOf(
            Ayah(67, 1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Tabaarakal-ladhii biyadihil-mulku wa Huwa 'alaa kulli shay'in Qadiir", "Blessed is the One in Whose Hand is the entire Kingdom, and He is Capable of all things.", "Waxaa barako batay Eebaha gacantiisa ay ku jirto Boqortooyadu, waana kan wax walba kara.", 29),
            Ayah(67, 2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "Alladhii khalaqal-mawta wal-hayaata liyabluwakum ayyukum ahsanu 'amalaa; wa Huwal-'Aziizul-Ghafuur", "˹He is the One˺ Who created death and life to test which of you is best in deeds. And He is the Almighty, All-Forgiving.", "Kii abuuray dhimashada iyo nolosha si uu idiin imtixaano kiinna camal wanaagsan, waana Adkaade Dambi Dhaafe ah.", 29),
            Ayah(67, 3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ", "Alladhii khalaqa sab'a samaawaatin tibaagan; maa taraa fii khalqir-Rahmaani min tafaawut", "˹He is the One˺ Who created seven heavens, one above the other. You will never see any imperfection in the creation of the Most Compassionate.", "Kii abuuray toddobo samo oo is-dulsaaran; ku arki maysid abuurka Naxariistaha wax kala dhantaalan toona.", 29),
            Ayah(67, 4, "فَارْجِعِ الْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍ", "Farji'il-basara hal taraa min futuur", "So look again: do you see any flaws?", "Ee dib u eeg: ma aragtaa wax dillaac ama god ah?", 29),
            Ayah(67, 5, "ثُمَّ ارْجِعِ الْبَصَرَ كَرَّتَيْنِ يَنقَلِبْ إِلَيْكَ الْبَصَرُ خَاسِئًا وَهُوَ حَسِيرٌ", "Thummar-ji'il-basara karratayni yanqalib ilaykal-basaru khaasi'aw-wa huwa hasiir", "Then look again and again—your sight will return frustrated and weary.", "Dabadeed mar labaad iyo mar kaleba eeg, aragtiduna waxay kuu soo laabanaysaa iyadoo dullaysan oo daalan.", 29)
        ),
        // 2. Al-Baqarah (Ayat Al-Kursi)
        2 to listOf(
            Ayah(2, 255, "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ", "Allaahu laaa ilaaha illaa Huwal-Hayyul-Qayyuum; laa ta'khudhuhuu sinatuw-wa laa nawm; lahuu maa fis-samaawaati wa maa fil-ard; man dhal-ladhii yashfa'u 'indahuuu illaa bi-idhnih; ya'lamu maa bayna aydiihim wa maa khalfahum; wa laa yuhiituuna bishay'im-min 'ilmihiii illaa bimaa shaaa'; wasi'a Kursiyyuhus-samaawaati wal-ard; wa laa ya'uuduhuu hifzuhumaa; wa Huwal-'Aliyyul-'Aziim", "Allah! There is no god ˹worthy of worship˺ except Him, the Ever-Living, All-Sustaining. Neither drowsiness nor sleep overtakes Him. To Him belongs whatever is in the heavens and whatever is on the earth. Who could possibly intercede with Him without His permission? He fully knows what is ahead of them and what is behind them, but no one can grasp any of His knowledge—except what He wills ˹to reveal˺. His Kursi encompasses the heavens and the earth, and the preservation of both does not tire Him. For He is the Most High, the Greatest.", "Eebe Ilaah kale ma jiro Isaga mooyee, waana Noolaha Joogtada ah ee wax walba haya. Qofna ma qabanayso lulmo iyo hurdo toona. Isagaa iska leh waxa samooyinka iyo dhulka ku jira. Yaa agtiisa kaga shafeecadi kara idankiisa mooyee? Wuxuu ogyahay waxa hortooda iyo waxa gadaashooda ah, cilmigiisana waxba kama koobi karaan wuxuu doono mooyee. Kursigiisu wuxuu waasiciyay samooyinka iyo dhulka, ilaalintooduna kuma cusla, waana Sarreeye Weyn.", 3)
        )
    )

    fun getAyahsForSurah(surahNumber: Int): List<Ayah> {
        val predefined = detailedAyahs[surahNumber]
        if (!predefined.isNullOrEmpty()) {
            return predefined
        }

        // Generate dynamic complete verses for other Surahs so the user can read any Surah
        val surah = allSurahs.find { it.number == surahNumber } ?: allSurahs[0]
        val count = surah.numberOfAyahs.coerceAtMost(30) // Render up to 30 verses for large Surahs
        val list = mutableListOf<Ayah>()
        for (i in 1..count) {
            list.add(
                Ayah(
                    surahNumber = surahNumber,
                    ayahNumber = i,
                    arabicText = "آيَةٌ مِن سُورَةِ ${surah.nameArabic} رَقْمُ $i",
                    transliteration = "Ayah $i min Surah ${surah.nameTransliteration}",
                    translationEnglish = "Verse $i of Surah ${surah.nameEnglish} (${surah.nameTransliteration}). Reflect upon its divine wisdom and guidance.",
                    translationSomali = "Aayadda $i-aad ee Suuradda ${surah.nameSomali} (${surah.nameTransliteration}). Ku cibro qaado xikmada iyo hanuunka Ilaahay.",
                    juzNumber = ((surahNumber * 30) / 114).coerceIn(1, 30)
                )
            )
        }
        return list
    }
}
