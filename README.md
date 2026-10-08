# AURA QUANTUM • Surga Trader 🥇⚡
> **Sindikat Super Intelijen Trading Emas (XAU/USD)**  
> *Native Android Command Center powered by Kotlin, Jetpack Compose Material 3, 2D Holographic Canvas, Offline AI Indonesian Voice Engine, and MetaTrader 5 Real Cent Integration.*

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-Material_3-purple.svg)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min_SDK-26_(Android_8.0)-orange.svg)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target_SDK-34_(Android_14)-red.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-brightgreen.svg)](LICENSE)

Terinspirasi dari antarmuka web visual kuantum **[AURA QUANTUM](https://surga.gitlab.io/a)**, aplikasi Android **Surga Trader** dirancang sebagai pusat komando kuantitatif (*high-frequency gold trading command deck*) untuk instrumen **XAU/USD (Gold)**.

> [!IMPORTANT]
> **Prinsip Integritas Kuantitatif (Zero Fake Metrics):**  
> SEMUA angka, indikator teknikal (ATR, EMA, Pivot Points, RSI, MACD), sinyal bias dewan, rekomendasi lot, narasi suara, dan log audit dihitung secara **matematis dari data pasar riil**. Tidak ada metrik rekayasa atau klaim profit palsu. Saat server bridge terputus atau dalam pengujian, aplikasi secara transparan menampilkan badge mencolok **`MODE DEMO • BUKAN DATA ASLI`**.

---

## 💎 Spesifikasi Pasar & Akun Trader
- **Instrumen Pasar**: XAU/USD (Gold vs US Dollar).
- **Broker & Tipe Akun**: Exness (Akun Standard Cent / MT5 Pro).
- **Simbol MT5**: `XAUUSDc` (Gold Cent).
- **Presisi Harga**: 3 desimal, contoh `4,100.234` (1 point = `0.001`).
- **Mata Uang Akun**: **USC** (US Cent), di mana `100 USC = 1.00 USD`.
- **Ukuran Kontrak Cent**: 1 Lot = 100 troy oz (1 pip = 0.01 = $10 pada lot standar, atau 1 point = 0.001 = 0.10 USC per 0.01 lot cent).
- **Zona Waktu Pasar**: **WIB (Waktu Indonesia Barat / Asia/Jakarta, UTC+7)** dengan konversi otomatis jam bursa London/New York.

---

## 🏛️ Arsitektur 7 Tahap Pengembangan

### 🛡️ Tahap 1: Keamanan Tingkat Militer & Arsitektur Zero-Secret
- **Android Keystore & EncryptedSharedPreferences**: Kredensial URL MT5 Bridge dan Token API dienkripsi di hardware security module perangkat pengguna.
- **Kebijakan Nol Rahasia (Zero-Secret Policy)**: Tidak ada token, password, IP jaringan, atau nomor login yang tersimpan di kode sumber maupun repositori git.
- **Network Security Config**: Pembatasan lalu lintas cleartext HTTP hanya untuk subnet jaringan lokal (LAN/WiFi lokal). Header otorisasi disamarkan (*masked*) pada log debug.
- **Badge Mode Data Transparan**: Indikator real-time `LIVE • TERHUBUNG` vs `MODE DEMO • BUKAN DATA ASLI`.
- **Integrasi CI/CD**: GitHub Actions workflow (`.github/workflows/android.yml`) otomatis memverifikasi `assembleDebug` dan `testDebugUnitTest` pada setiap push.

### 🎨 Tahap 2: Cyber Glassmorphism & Sistem Tipografi Kuantum
- **Tipografi Sci-Fi Berlisensi Google Fonts**:
  - `Orbitron`: Angka harga, header merek, dan ticker komando.
  - `Rajdhani`: Teks narasi analisis teknikal dan panel navigasi.
  - `Share Tech Mono`: Cap waktu WIB, latency bridge (ms), dan log audit HFT.
- **Palet Warna Hologram Aura**:
  - `AuraGoldPrimary` (`#FFD700`) & `AuraGoldLight` (`#FFEA79`)
  - `AuraCyan` (`#00F2FE`)
  - `AuraGreenBull` (`#00FF88`) & `AuraRedBear` (`#FF3366`)
  - `AuraGlassBg` (`rgba(4, 10, 26, 0.88)`) dengan border neon bercahaya.

### 🌐 Tahap 3: Canvas Hologram 2D & Top Ticker Dinamis
- **Central Liquidity Core di Canvas**:
  - Cincin orbit elips multi-sumbu yang kecepatan putarannya terikat secara dinamis dengan **ATR (Average True Range)** volatilitas saat ini.
  - **16 Lilin M5 Riil**: Candlestick orbit 3D yang dirender dari 16 candle terakhir pasar.
  - **Warna Inti Reaktif**: Berubah dinamis sesuai konsensus dewan (Hijau = Bullish, Merah = Bearish, Emas = Netral).
- **Top Ticker Kapsul**:
  - Harga Bid/Ask 3 desimal (`4,100.234`), Spread dalam points.
  - Perubahan harian %, Ekuitas riil USC (beserta konversi USD).
  - Floating P/L berwarna real-time, Latency round-trip terukur (ms), Jam WIB, dan status sesi pasar aktif (Asia / London / New York).

### 🎙️ Tahap 4: Dewan 5 Entitas & Suara AI Indonesia Offline
Analisis kuantitatif komprehensif tanpa naskah rekayasa, dihitung dari formula indikator riil:
1. 👁️ **ALPHA-ORACLE** (Makro & Tren): Multi-timeframe trend analysis, EMA 20/50/200, deteksi swing high/low, support/resistance.
2. ⚡ **CHRONOS** (Waktu & Momentum): Deteksi sesi pasar (Asia/London/NY) berbasis `ZoneId` otomatis, RSI(14), MACD histogram, breakout range Tokyo/Asia.
3. 🛡️ **VOLATILITY-GAIA** (Risiko & Volatilitas): ATR(14) multi-timeframe, deviasi spread vs rata-rata, rekomendasi jarak SL minimum berbasis volatilitas.
4. 🔮 **SENTIMENT-ATHENA** (Berita & Kalender): Kalender ekonomi dampak tinggi (NFP, CPI, FOMC, Suku Bunga Fed), status "Zona Bahaya Berita" countdown WIB.
5. ⚔️ **AEGIS-EXECUTION** (Penjaga Risiko & Sintesis Konsensus): Menghitung skor konsolidasi (0–100), level harga Entry, Target TP1/TP2, batas Stop Loss, serta lot anjuran.
- **Mesin Narasi Suara Offline**: Menggunakan native `android.speech.tts.TextToSpeech` dengan lokalisasi `Locale("id", "ID")`. 100% offline, privat, tanpa mengirim audio ke server luar.
- **CyberSynth Audio Synthesizer**: Menghasilkan nada akord sci-fi polifonik via PCM low-latency `AudioTrack` pada setiap perpindahan entitas.

### 🧮 Tahap 5: Kalkulator Lot Exness Cent & Radar Risiko 5D/6D
- **Kalkulator Lot Exness Cent (`LotSizeCalculator`)**:
  - Input Saldo akun (USC & USD), Toleransi Risiko (1% atau 2%), dan Jarak SL (Points).
  - Menghitung nilai per point Exness Cent secara presisi.
  - Rekomendasi Lot Cent yang aman serta estimasi risiko moneter.
  - **Perhitungan Margin Buffer & Stop Out Buffer**: Menghitung ketahanan akun hingga margin call / stop out broker.
  - Aksi **⚡ IMPOR SINYAL AEGIS** untuk mengisi otomatis parameter dari dewan kuantum.
- **Radar Risiko 5D/6D Hologram (`RadarSpiderChart`)**:
  - Canvas spider chart 5 sumbu dengan ring konsentris neon (20% s/d 100%).
  - Menganalisis 5 pilar risiko: *Leverage Strain*, *Volatility Exposure*, *Drawdown Threat*, *Correlation Overlap*, dan *News Shock Hazard*.

### 📈 Tahap 6: Grafik Lilin Interaktif & Jurnal Trading Room SQLite
- **Interactive Candlestick Canvas (`CandlestickCanvas`)**:
  - Pinch-to-zoom (mengatur ketebalan dan jarak candle) & geser horizontal (pan gesture).
  - Snap Crosshair interaktif: pill harga 3 desimal di sumbu Y dan penanda waktu WIB di sumbu X.
  - Garis indikator teknikal: **EMA 20** (Sian), **EMA 50** (Emas), **EMA 200** (Ungu).
  - **Classic Floor Pivot Points**: $R_2$, $R_1$, $P$, $S_1$, $S_2$ dihitung dari high, low, close.
  - **Level Target Aegis**: Garis horizontal putus-putus untuk TP1, TP2, dan SL.
- **Jurnal Trading Room SQLite (`TradeJournalDao`, `AppDatabase` v2)**:
  - Database terstruktur lokal dengan statistik kinerja riil: **Win Rate (%)**, **Profit Factor**, **Net PnL (USC & USD)**, **Mathematical Expectancy**, dan **Max Drawdown (%)**.
  - Form pencatatan transaksi manual maupun **⚡ Catat dari Sinyal Aegis**.
  - **Fitur Ekspor CSV**: Salin seluruh riwayat transaksi ke clipboard untuk analisis spreadsheet.

### 📱 Tahap 7: Responsivitas Layar & Pengalaman Pengguna
- **Layout Tiga Kolom Tablet / Lanskap**: Panel Sinyal Dewan di kiri, Hologram Inti di tengah, Kontrol Risiko & Lot di kanan (meniru layout web command deck).
- **Layout Fleksibel Ponsel Portret**: Panel kiri dan kanan dapat diminimalkan menjadi floating pill button dengan sekali sentuh agar canvas hologram tampil leluasa.
- **Bilah Navigasi Bawah (`BottomNavBar`)**: Akses cepat ke 5 fitur utama (*Komando*, *Chart*, *Radar*, *Lot Calc*, *Jurnal*).
- **Laci Terminal Audit HFT (`AuraTerminalDrawer`)**: Laci log audit algoritmik bercap waktu WIB yang dapat dibuka kapan saja.

---

## 🛠️ Stack Teknologi

| Komponen | Spesifikasi & Library |
|---|---|
| **Bahasa Pemrograman** | Kotlin 1.9.23 (Target JVM 21) |
| **UI Framework** | Jetpack Compose (Material 3, Compose BOM 2024.05.00) |
| **Graphic Engine** | Custom 2D Graphics Canvas via Compose `DrawScope` & Android Native `Paint` |
| **Arsitektur** | MVVM + Clean Architecture (Multi-module ready) |
| **Dependency Injection**| Dagger Hilt 2.51.1 (`@HiltViewModel`, `@AndroidEntryPoint`) |
| **Asinkron & Reaktif** | Kotlin Coroutines, StateFlow, SharedFlow |
| **Database Lokal** | Room SQLite 2.6.1 (Version 2, Schema Migration Support) |
| **Keamanan Kredensial**| Android Keystore, EncryptedSharedPreferences (Security Crypto 1.1.0-alpha06) |
| **Jaringan & Protokol**| Retrofit 2, OkHttp 4.12, JSON-RPC 2.0 (MCP Bridge) |
| **Audio & Sintesis** | Native Android TTS (`Locale("id", "ID")`), AudioTrack PCM Synthesizer |
| **Unit Testing** | JUnit 4, Google Truth, Kotlinx Coroutines Test (100% Pass) |
| **Kompatibilitas SDK** | Min SDK 26 (Android 8.0 Oreo) — Target SDK 34 (Android 14) |

---

## 🚀 Cara Menjalankan & Memasang di HP

### 1. Prasyarat Lingkungan
- Komputer dengan OS Windows, macOS, atau Linux.
- **JDK 21** terpasang (contoh: `C:\Program Files\Android\openjdk\jdk-21.0.8`).
- Android Studio (Koala / Ladybug / Jellyfish) atau Android SDK command-line tools.

### 2. Kloning Repositori
```bash
git clone https://github.com/surgabot/surgatrader.git
cd surgatrader
```

### 3. Build APK Debug
Jalankan perintah berikut di terminal:
```bash
# Windows PowerShell
$env:JAVA_HOME = "C:\Program Files\Android\openjdk\jdk-21.0.8"
.\gradlew.bat assembleDebug

# Linux / macOS
export JAVA_HOME="/path/to/jdk-21"
./gradlew assembleDebug
```
File APK siap dipasang akan dibuat di:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 4. Pasang ke HP Android
Hubungkan smartphone Android Anda dengan kabel USB (pastikan *USB Debugging* aktif), lalu jalankan:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
Atau salin file `app-debug.apk` ke penyimpanan smartphone Anda dan buka file manager untuk melakukan instalasi langsung.

### 5. Menjalankan Unit Test
Untuk memverifikasi seluruh kalkulasi matematis lot, ATR, pivot point, dan database:
```bash
.\gradlew.bat testDebugUnitTest
```

---

## 🔌 Konfigurasi MetaTrader 5 MCP Bridge
1. Buka aplikasi Surga Trader di HP.
2. Di layar komando atau menu navigasi, buka **Koneksi**.
3. Masukkan URL Bridge MT5 Anda:
   - Emulator: `http://10.0.2.2:22346/mcp`
   - HP Fisik via WiFi LAN: `http://192.168.1.xxx:22346/mcp` (sesuaikan dengan IP komputer MT5 Anda)
4. Masukkan Token API Bridge (jika diaktifkan).
5. Klik **"UJI KONEKSI"** lalu **"SIMPAN KREDENSIAL"**.
6. Kredensial akan disimpan dengan enkripsi hardware Keystore dan aplikasi otomatis beralih ke mode **`LIVE`**.

---

## 📂 Struktur Direktori Proyek

```
com.surgatrader/
├── MainActivity.kt                      # Activity Utama dengan Scaffold & BottomNavBar
├── SurgaTraderApp.kt                    # Application class (Hilt initialization)
├── core/
│   ├── database/                        # Room Database v2 (TradeJournal, Symbol, Migrations)
│   ├── di/                              # Hilt Dependency Injection Modules (Database, Network)
│   ├── navigation/                      # NavGraph, Screen destinations & BottomNavBar
│   ├── security/                        # SecurePreferencesManager (Android Keystore Encrypted)
│   ├── theme/                           # Color, Theme, & Typeface (Orbitron, Rajdhani, Mono)
│   └── util/                            # Formatter angka 3 desimal & waktu WIB
└── feature/
    ├── aura/                            # Fitur Utama Aura Quantum Command Deck
    │   ├── audio/                       # Indonesian TTS Voice Engine & CyberSynth Player
    │   ├── domain/
    │   │   ├── council/                 # Modul Analisis Riil 5 Entitas (Oracle, Chronos, Gaia, Athena, Aegis)
    │   │   └── model/                   # AuraEntity, MarketCandle, AuraState, AuraTerminalLog
    │   └── presentation/                # Canvas Hologram 2D, Panels, Ticker Header, Dock, Drawer
    ├── chart/                           # Grafik Candlestick Interaktif di Canvas (Pan, Zoom, Crosshair, EMA, Pivots)
    ├── riskradar/                       # Radar Risiko 5D/6D & Kalkulator Lot Exness Cent
    ├── journal/                         # Jurnal Trading SQLite Room (Win Rate, Profit Factor, Export CSV)
    ├── connection/                      # Pengaturan Bridge MT5 & Keamanan Keystore
    ├── calendar/                        # Kalender Berita Ekonomi Geopolitik
    └── roadmap/                         # Roadmap Belajar Trading Kuantitatif
```

---

## 📄 Lisensi
Hak Cipta © 2026 **Mochamad Tabrani** & **Antigravity AI**.  
Didedikasikan untuk komunitas trader emas dan pengembang kuantitatif Indonesia.
