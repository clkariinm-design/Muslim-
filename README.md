# 🕌 Athan: Prayer Times, Quran & Islamic Utilities

<p align="center">
  <strong>Codsiga Casriga ah ee Salaadaha, Quraanka, Qiblada, Tusbaxa iyo Adkaarta (Offline Android App)</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0-blue.svg?logo=kotlin" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-M3-green.svg?logo=android" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Platform-Android%2024%2B-brightgreen.svg?logo=android" alt="Android" />
  <img src="https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-blue.svg?logo=github-actions" alt="GitHub Actions" />
  <img src="https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg" alt="License" />
</p>

---

## 📌 Dulmar Guud (Overview)

**Athan** waa app casri ah oo u shaqeeya hab dhaxalgal ah oo ku dhisan **100% Offline** (iyadoon loo baahnayn khadka internet-ka). Waxaa loo dhisay si gaar ah bulshada Muslimka ah si ay ugu fududaato gudashada waajibaadka diiniga ah meel kasta oo ay joogaan.

App-ku waxa uu isku xirayaa xisaabinta saxda ah ee xilliyada salaadda, jiheeyaha Qiblada ee ku shaqeeya dareemayaasha qalabka (Sensors), akhrinta Quraanka Kariimka oo leh keydinta aayadaha (Bookmarks), Tusbax dijitaal ah oo gariira (Haptic feedback), iyo Adkaar maalinle ah oo leh ogeysiisyo.

---

## ✨ Astaamaha Ugu Muhiimsan (Key Features)

### 1. 🕋 Waqtiyada Salaadaha (Accurate Prayer Times)
* **Xisaabin Cilmaysan**: Xisaabiya xilliyada saxda ah ee **Fajr, Qorrax soo-bax (Sunrise), Dhuhr, Asr, Maghrib, iyo Isha**.
* **Hababka Caalamiga ah**: Taageeraya hababka ugu waaweyn caalamka sida:
  * *Umm al-Qura (Makkah)*
  * *ISNA (Waqooyiga Ameerika)*
  * *Muslim World League (MWL)*
  * *Egyptian General Authority of Survey*
  * *University of Islamic Sciences, Karachi*
* **Goobta (Location)**: Helitaanka GPS toos ah ama xulasho gacanta ah oo si buuxda offline u shaqaysa.

### 2. 🔊 Ogeysiisyada Aadaanka (Audible Adhan Notifications)
* Digniin ogeysiis ah isla daqiiqadda ay salaaddu gasho.
* Dhawaaq aadaan ah oo loo sameeyay inuu offline ku shaqeeyo isagoo isticmaalaya audio synthesizer iyo notification channels.

### 3. 🧭 Jiheeyaha Qiblada (Real-time Qibla Compass)
* Waxa uu isticmaalaa dareemayaasha mobaylka (`Sensor.TYPE_ROTATION_VECTOR` iyo `MAGNETIC_FIELD`).
* Waxa uu si toos ah u tilmaamayaa jihada Kacbada sharafta leh (Makkah Al-Mukarramah) meel kasta oo aad adduunka ka joogto.

### 4. 📖 Akhrinta Quraanka Kariimka (Holy Quran Reader)
* Liiska dhamaan 114-ka Suuradood ee Quraanka Kariimka.
* Akhris aayad-aayad ah oo leh qoraal Carabi ah oo aad u cad.
* Nidaam keydin (`Bookmark`) oo ku dhisan **Jetpack Room Database** si aad u calaamadsato meesha aad akhriska marinayso.

### 5. 📿 Tusbax Dijitaal ah (Digital Tasbih Counter)
* Badhan fudud oo tirinta dambi-dhaaf-warsiga iyo tasbiixda (`Subhanallah`, `Alhamdulillah`, `Allahu Akbar`).
* **Haptic Feedback**: Gariir jilicsan marka la taabto si aad u dareento adigoon shaashadda eegin.
* Yoolal xaddidan (33, 99, 100) iyo badhan dib-u-bilaabid ah (`Reset`).

### 6. 🤲 Adkaarta Maalinlaha ah (Daily Azkar & Reminders)
* Adkaarta Subaxa, Galabta, Salaadaha kadib, iyo Hurdo tagga.
* Ogeysiisyo xasuusin ah xilliyada maalinlaha ah.

---

## 🛠️ Farsamada & Qaab-dhismeedka (Tech Stack & Architecture)

* **Luqadda**: 100% Kotlin
* **UI Framework**: Jetpack Compose (Material Design 3)
* **Architecture**: MVVM (Model-View-ViewModel) oo leh unidirectional data flow (UDF)
* **State Management**: Kotlin Coroutines & `StateFlow`
* **Local Database**: Android Jetpack Room (KSP code-gen)
* **Sensors**: Android `SensorManager` ee jihada Qiblada
* **Build System**: Gradle Kotlin DSL (`build.gradle.kts`)

---

## 🚀 Sida Loogu Sameeyo APK GitHub Actions (CI/CD Automated Build)

Mashruucan waxaa horey loogu habeeyay workflow-ga **GitHub Actions** (`.github/workflows/android-build-apk.yml`). Markaad code-ka ku riixdo (`push`) GitHub, APK-ga wuxuu ku diyaarsamayaa dhowr daqiiqo gudahood.

### Tallaabooyinka Lagu Helo APK-ga:
1. **Push garee code-ka**: Ku riix keydkaaga GitHub (`git push origin main`).
2. **Tag barta GitHub**: Fur repository-gaaga bogga GitHub.
3. **Guji tab-ka "Actions"**: Waxaad arkaysaa shaqada socota ee cinwaankeedu yahay **"Build Android APK"**.
4. **Guji Workflow-ga**: Marka uu dhameeyo (calaamad cagaaran ✅):
   * Hoos ugu dhaadhac qaybta hoose ee bogga: **Artifacts**.
   * Guji **`Athan-Debug-APK`** si aad u soo degsato faylka ZIP-ka ah.
5. **Ku rakibo Taleefanka**: Fur ZIP-ka, waxaad ka dhex helaysaa `app-debug.apk`. Ku shub taleefankaaga Android oo ku rakibo!

### Sida gacanta loogu kiciyo (Manual Trigger):
Waxaad sidoo kale riixi kartaa badhanka **"Run workflow"** ee ku yaal tab-ka Actions wakhti kasta oo aad rabto inaad soo saarto APK cusub adigoon wax commit ah samayn.

---

## 💻 Sida Gudaha Loogu Dhiso (Building Locally)

Haddii aad rabto inaad kombiyuutarkaaga dhexdiisa ku dhistid:

```bash
# 1. Hubi in Java 21 uu kugu rakiban yahay
java -version

# 2. Sii ogolaansho faylka gradlew
chmod +x gradlew

# 3. Samee baaritaanka tijaabada (Run Unit Tests)
./gradlew testDebugUnitTest

# 4. Samee APK-ga Debug-ka ah
./gradlew assembleDebug
```

Faylka APK wuxuu diyaar ku noqon doonaa dariiqan:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🔒 Ruqsadaha Loo Baahan Yahay (Android Permissions)

| Ruqsadda (Permission) | Ujeedada |
|---|---|
| `ACCESS_FINE_LOCATION` / `COARSE` | Si loo ogaado coordinates-ka goobta loona xisaabiyo waqtiyada saxda ah ee salaadda. |
| `POST_NOTIFICATIONS` | Si loo soo diro digniinta xilliyada salaadda iyo xasuusinta adkaarta. |
| `VIBRATE` | Si loogu sameeyo dareenka gariirka (haptic feedback) tusbaxa dijitaalka ah. |

---

## 📄 Ruqsadda Mashruuca (License)

Mashruucan waxaa lagu daabacay rukhsadda **Apache License 2.0**. Waxaa loo ogolyahay qof kasta inuu wax ka barto, horumariyo, ama faafiyo.
