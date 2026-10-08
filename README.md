# AURA QUANTUM • Surga Trader 🥇⚡
> **Sindikat Super Intelijen Trading Emas (XAU/USD)**  
> *Native Android Command Center powered by Kotlin, Jetpack Compose, 2D Holographic Canvas, AI Voice Synthesis, and MetaTrader 5 Real Cent Integration.*

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-Material_3-purple.svg)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min_SDK-26_(Android_8.0)-orange.svg)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target_SDK-34_(Android_14)-red.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-brightgreen.svg)](LICENSE)

Terinspirasi dari sistem intelijen kuantum **[AURA QUANTUM](https://surga.gitlab.io/a)**, aplikasi Android **Surga Trader** dirancang sebagai pusat komando kuantitatif tingkat tinggi (*high-frequency gold trading terminal*) untuk instrumen **XAU/USD (Gold)**. Dilengkapi dengan orkestrasi 5 entitas AI Dewan Kuantum dalam **Bahasa Indonesia**, simulasi arbitrase likuiditas global, visualisasi canvas 2D multi-orbit, serta sinkronisasi langsung dengan broker **Exness (Akun Standar Cent Real)** via **MetaTrader 5 MCP Bridge**.

---

## 🔮 Arsitektur Visual & Fitur Unggulan

### 1. 🌐 Canvas Holografik 2D Interaktif (`AuraQuantumCanvas`)
- **Central Core (`XAU/USD CORE - High-Frequency Liquidity Pool`)**:
  - 4 Cincin orbit elips multi-sumbu dengan satelit energi berputar pada kecepatan independen.
  - **16 Revolving 3D Candlesticks**: Bar candlestick bullish (*hijau neon*) dan bearish (*merah*) berputar secara tiga dimensi mengelilingi inti likuiditas emas.
  - Inti heksagonal berputar dengan denyut nadi fluktuasi pasar emas.
- **5 Entitas Dewan AI Kuantum**:
  1. 👁️ **ALPHA-ORACLE** (`#FFD700` Emas) — *Arsitek Model Makro Kuantitatif & Likuiditas Global*
  2. ⚡ **CHRONOS-HFT** (`#00F2FE` Sian) — *Eksekusi Frekuensi Tinggi & Arbitrase Mikrostruktur COMEX*
  3. 🛡️ **VOLATILITY-GAIA** (`#00FF88` Hijau) — *Manajemen Risiko Termodinamika & Dynamic Delta Hedging*
  4. 🔮 **SENTIMENT-ATHENA** (`#FA709A` Pink) — *Analisis Sentimen Geopolitik & Pemrosesan Berita Satelit*
  5. ⚔️ **AEGIS-EXECUTION** (`#B176F7` Ungu) — *Infrastruktur Mesin Eksekusi Kuantum & Dark Pool*
- **Jaringan Sinapsis & Foton Data**:
  - Garis koneksi neon antar-entitas dan inti likuiditas.
  - Saat entitas berbicara, berkas energi berdaya tinggi menyala dan paket data meluncur deras ke pusat inti.
- **Interaksi Sentuh (Touch Gesture)**: Pengguna dapat menyentuh langsung avatar dewan pada canvas untuk memicu transmisi dan analisis entitas tersebut.

---

### 2. 📊 Header & Ticker Bar Pasar Real-Time (`AuraHeader`)
- **Kotak Merek Aura Quantum**: Tipografi Orbitron dengan efek *pulsing dot* emas.
- **Live Market Ticker Bar**:
  - 🥇 Harga XAU/USD (animasi tick naik/turun real-time dalam USD dan USC)
  - Spread: `0.1 PIP`
  - Latency: `0.038 MS`
  - Daily Alpha: `+$4,820,350`
  - Sharpe Ratio: `5.42`
- **Badge Koneksi Akun Real Exness Cent**: Menampilkan saldo live `2,604.60 USC` (~$26.05 USD) dari server `Exness-MT5Real37`.

---

### 3. 📡 Panel Kiri: Transmisi Sinyal Trading (`AuraLeftPanel`)
- Indikator status transmisi algoritmik aktif dengan nomor fase (`1/6`).
- **Waveform Visualizer**: 6 bar frekuensi audio berdenyut dinamis mengikuti pelafalan suara.
- Transkripsi teks orkestrasi 6 fase trading emas:
  - **Fase 1**: Ingestion Likuiditas Makro & Divergensi Yield US Treasury 10-Tahun
  - **Fase 2**: Struktur Order Flow & Heatmap Likuiditas COMEX / LBMA
  - **Fase 3**: Kalkulasi Volatilitas & Stress-Testing Monte Carlo 100.000 Iterasi
  - **Fase 4**: Analisis Sentimen Geopolitik & Aliran Modal Safe-Haven
  - **Fase 5**: Eksekusi Kuantum Dark Pool & Alokasi Order Besar
  - **Fase 6**: Distribusi Alpha & Rekapitulasi Emas Fisik Zurich Vault
- Tombol Navigasi: `[⏮️ Mundur]`, `[🔄 Ulangi]`, `[⏭️ Lanjut]`.
- **Dukungan Minimize**: Tombol `[−]` untuk mengecilkan panel menjadi tombol terapung `[📊 ALPHA-ORACLE (BUKA SINYAL) ⛶]` agar tampilan grafik canvas leluasa.

---

### 4. 🎛️ Panel Kanan: Kontrol Audio & Eksekusi MT5 (`AuraRightPanel`)
- **Sakelar Mode Alur**: `PUTAR TERUS (OTOMATIS)` vs `BERHENTI PER TEKS (JEDA)`.
- **Aksi Cepat Audio**: `[⏹️ Stop Suara]` dan `[🔊 Suara Aktif / 🔇 Bisu]`.
- **Pengatur Kecepatan Bicara**: `0.85x`, `1.0x (Normal)`, `1.2x (Cepat)`.
- **Penggeser Volume Suara**: `0%` hingga `100%`.
- **Integrasi Akun Exness Cent**: Menampilkan Saldo, Ekuitas, Margin Bebas.
- **Tombol Eksekusi Cepat**: `[⚡ EKSEKUSI SINYAL QUANTUM (0.01 LOT)]` untuk perutean order instan ke akun trading.
- **Dukungan Minimize**: Tombol `[−]` untuk mengecilkan panel menjadi tombol terapung `[⚙️ KONTROL SUARA (BUKA) ⛶]`.

---

### 5. 📟 Dock Bawah & Terminal Drawer Audit HFT (`AuraBottomDock`, `AuraTerminalDrawer`)
- 5 Chip pemilihan entitas dewan dengan animasi skala dan glow berdenyut saat aktif.
- Tombol **`📟 AUDIT HFT`** untuk membuka laci log audit algoritma kuantum real-time dengan penanda waktu (*timestamp*), label pembicara berwarna, dan riwayat transmisi eksekusi order.

---

### 6. 🎙️ Mesin Audio Sintesis & CyberSynth
- **Bahasa Indonesia Alami (`AndroidIndonesianVoiceEngine`)**:
  - Ditenagai engine native `android.speech.tts.TextToSpeech` dengan lokalisasi `Locale("id", "ID")`.
  - Pelafalan tuntas sesuai teks dengan kontrol kecepatan per entitas.
- **CyberSynth Audio Synthesizer (`CyberSynthPlayer`)**:
  - Menghasilkan akord nada harmonik gelombang sinus sci-fi secara real-time via `AudioTrack` pada setiap pergantian entitas atau eksekusi order:
    - *Oracle*: 587.33 Hz, 739.99 Hz, 880.0 Hz (D5, F#5, A5)
    - *Chronos*: 523.25 Hz, 659.25 Hz, 783.99 Hz (C5, E5, G5)
    - *Gaia*: 440.0 Hz, 554.37 Hz, 659.25 Hz (A4, C#5, E5)
    - *Athena*: 659.25 Hz, 830.61 Hz, 987.77 Hz (E5, G#5, B5)
    - *Aegis*: 493.88 Hz, 622.25 Hz, 739.99 Hz (B4, D#5, F#5)

---

### 7. 🔌 Sinkronisasi MetaTrader 5 MCP Bridge
- Terintegrasi dengan bridge lokal MetaTrader 5 via protokol Model Context Protocol (MCP) JSON-RPC 2.0.
- Mendukung alamat emulator (`http://10.0.2.2:22346/mcp`), perangkat fisik di jaringan lokal LAN (`http://192.168.1.x:22346/mcp`), dan localhost.
- Mengambil saldo live, ekuitas, margin bebas, dan detail akun `Exness-MT5Real37` secara otomatis.

---

## 🛠️ Stack Teknologi

| Komponen | Teknologi |
|---|---|
| **Bahasa** | Kotlin (1.9.23), JDK 21 |
| **Framework UI** | Jetpack Compose (Material 3, BOM 2024.05.00) |
| **Graphic Rendering** | Custom 2D Graphics Canvas via Compose `DrawScope` & `Paint` |
| **Arsitektur** | MVVM + Clean Architecture |
| **Dependency Injection**| Dagger Hilt (2.51.1) |
| **Asinkron & Reaktif** | Kotlin Coroutines, StateFlow, SharedFlow |
| **Penyimpanan Lokal** | Room Database (2.6.1) + DataStore Preferences |
| **Jaringan & REST** | Retrofit 2, OkHttp 4.12, Gson |
| **Speech & Audio** | Native Android TTS (`Locale("id", "ID")`), Low-latency PCM `AudioTrack` |
| **Testing** | JUnit 4, Google Truth, Coroutines Test (100% Pass) |
| **Kompatibilitas** | Min SDK 26 (Android 8.0) — Target SDK 34 (Android 14) |

---

## 🚀 Cara Menjalankan Proyek

### 1. Clone Repositori
```bash
git clone https://github.com/surgabot/surgatrader.git
cd surgatrader
```

### 2. Buka di Android Studio
1. Buka Android Studio (versi terbaru: Hedgehog, Iguana, Koala, atau Ladybug).
2. Pilih **Open** lalu arahkan ke folder `SurgaTrader`.
3. Biarkan Gradle melakukan sinkronisasi otomatis.

### 3. Jalankan Aplikasi
1. Hubungkan HP fisik (USB Debugging aktif) atau jalankan Android Emulator.
2. Klik tombol **Run 'app'** (`Shift + F10`) atau tombol segitiga hijau ▶️ di toolbar atas.
3. Pada modal pembuka, klik tombol emas **"MULAI ORKESTRASI TRADING"**.
4. Nikmati antarmuka canvas holografik, audio cyber synth, dan suara orkestrasi trading berbahasa Indonesia!

### 4. Build APK Mandiri via Terminal
```powershell
# Windows PowerShell
$env:JAVA_HOME = "C:\Program Files\Android\openjdk\jdk-21.0.8"
.\gradlew.bat assembleDebug
```
File APK debug akan dihasilkan di:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📂 Struktur Paket Proyek

```
com.surgatrader/
├── MainActivity.kt                      # Main Activity peluncur Aura Quantum
├── SurgaTraderApp.kt                    # Application class (Hilt initialization)
├── core/
│   ├── database/                        # Room Database & Entities
│   ├── di/                              # Hilt Dependency Injection Modules
│   ├── navigation/                      # Navigation Graph & Destinations
│   ├── theme/                           # Color, Theme, & Type (Aura Cyberpunk)
│   └── util/                            # Formatter mata uang & tanggal
└── feature/
    ├── advanced/                        # MetaTrader 5 MCP Bridge Client & Screen
    ├── aura/                            # Fitur Utama Aura Quantum Command Center
    │   ├── audio/                       # Indonesian TTS Engine & CyberSynth Player
    │   ├── domain/model/                # AuraEntity, AuraScriptStep, AuraState
    │   └── presentation/                # ViewModel & UI Components
    │       ├── AuraQuantumScreen.kt     # Layar Utama Orkestrasi Kuantum
    │       ├── AuraQuantumViewModel.kt  # StateFlow & Logika Kontrol
    │       └── components/              # Canvas, Header, Panels, Dock, Modal
    ├── riskradar/                       # Radar Risiko & Kalkulator Lot Exness Cent
    ├── roadmap/                         # Roadmap Belajar Trader Pro
    ├── journal/                         # Jurnal Trading & Evaluasi Kinerja
    ├── calendar/                        # Kalender Berita Ekonomi
    └── dashboard/                       # Ringkasan Saldo & Metrik
```

---

## 📄 Lisensi
Hak Cipta © 2026 **Mochamad Tabrani** & **Antigravity AI**.  
Didedikasikan untuk komunitas trader emas dan kuantitatif Indonesia.
