# Surga Trader 📈🛡️

Aplikasi Android Native modern berbasis **Kotlin** & **Jetpack Compose (Material 3)** yang dirancang khusus untuk trader Forex, Emas (XAUUSD), dan Kripto pengguna **MetaTrader 5**. 

Dioptimalkan secara khusus untuk **Akun Cent (USC)** broker **Exness** dan akun Standar dengan presisi perhitungan hingga 3 digit desimal (1 point = 0.001).

---

## ✨ Fitur Utama

### 1. 🛡️ Radar Risiko (Risk Radar) & Spider Chart
- **Skor Risiko Akun 0–100**: Menganalisis kondisi kesehatan akun secara komprehensif berdasarkan:
  - *Drawdown Risk* (Rasio floating drawdown vs daily loss limit)
  - *Jarak Stop Out / Margin Call* (Deteksi dini bahaya likuidasi $\le 200\text{ pts}$ atau Margin Level $\le 150\%$)
  - *Risiko per Trade* (% modal per transaksi)
  - *Exposure & Posisi Terbuka* (Akumulasi volume lot)
  - *Korelasi Portofolio* (Deteksi tumpang tindih risiko antar pair)
  - *Volatilitas Harian XAUUSD (ATR)* & Jam Rawan Pasar
- **Visual Spider Chart Interaktif**: Menggunakan custom Jetpack Compose Canvas dengan kode warna indikator (*Hijau Aman*, *Kuning Waspada*, *Merah Bahaya*).
- **Peringatan Batas Harian (Daily Loss Limit)**: Alert banner otomatis jika drawdown menyentuh batas toleransi.
- **Heatmap Korelasi Pasangan Mata Uang & Emas**: Matriks korelasi XAU, EUR, GBP, JPY, dan DXY (US Dollar Index).

### 2. 🧮 Kalkulator Ukuran Lot Presisi (Exness Cent Ready)
- **Dukungan Dual-Currency**: Saldo dalam **USC (US Cents)** dan konversi otomatis ke **USD** ($100\text{ USC} = \$1.00\text{ USD}$).
- **Fleksibilitas Input Stop Loss (SL)**:
  - Jarak Points / Pips
  - Level Harga Eksak
  - Nominal Uang Toleransi Rugi (USC/USD)
- **Target Profit (TP)**: Rasio Risk:Reward (1:2, 1:3), Points, atau Level Harga.
- **Otomatisasi Biaya Broker**: Memperhitungkan *Spread* dan *Swap* ke dalam jarak SL/TP aktual.
- **Normalisasi Lot**: Otomatis menyesuaikan *lot step* (0.01) dan batasan *min/max lot* broker.
- **Estimasi Margin**: Menghitung kebutuhan margin sebelum order dieksekusi.

### 3. ⚙️ Spesifikasi Simbol Dinamis (Room Database)
- Tidak ada angka yang di-hardcode. Seluruh parameter instrumen dapat dikonfigurasi melalui layar **Spesifikasi Simbol** sesuai jendela *MT5 Market Watch > Specification*:
  - Contract Size (misal 100 oz)
  - Digits Desimal (misal 3 digit untuk harga `4125.500`)
  - Point (misal `0.001`)
  - Tick Size & Tick Value (USC per point per lot)
  - Minimal Lot, Langkah Lot, Maksimal Lot, dan Leverage (1:2000 / Unlimited)
  - Profil bawaan: `XAUUSDc Exness Cent` & `XAUUSD Standard (USD)`.

### 4. ⏰ Pemantau Jam Rawan Emas (WIB)
Menyorot periode dengan volatilitas ekstrem dan potensi pelebaran spread tajam:
- **Pembukaan Sesi London** (14:00 – 16:00 WIB)
- **Rilis Data AS (NFP, CPI, PPI, Retail Sales) & Wall Street Open** (19:15 – 21:30 WIB)
- **Keputusan Suku Bunga & Rilis FOMC** (01:00 – 02:30 WIB)
- **Rollover & Daily Settlement Exness** (04:50 – 05:30 WIB)

### 5. 🗺️ Roadmap Belajar Trader Pro
- Jalur belajar bertahap: **Pemula**, **Menengah**, dan **Mahir**.
- Modul: Dasar Pasar & MT5, Manajemen Risiko, Analisis Teknikal Emas, Psikologi Trading, hingga Strategi Otomatis / Expert Advisor (EA).
- Checklist interaktif tersimpan otomatis di Room database.

### 6. 📓 Jurnal Trading & Statistik Evaluasi
- Ringkasan metrik kinerja: *Win Rate*, *Profit Factor*, *Expectancy*, *Rata-rata R:R*, dan *Max Drawdown*.
- Tag emosi (*Disiplin*, *FOMO*, *Revenge Trade*) dan catatan setup.
- Siap untuk integrasi impor laporan CSV MT5.

### 7. 📅 Kalender Ekonomi
- Rilis berita ekonomi berkala dengan filter tingkat dampak (*High Impact*).

---

## 🛠️ Arsitektur & Teknologi

- **Bahasa**: Kotlin (1.9.23)
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Arsitektur**: MVVM + Clean Architecture (Data, Domain, Presentation)
- **Dependency Injection**: Dagger Hilt (2.51.1)
- **Database Lokal**: Room (2.6.1) dengan KSP Compiler
- **Pengaturan & Keamanan**: AndroidX DataStore, Biometric, Android Keystore
- **Networking**: Retrofit, OkHttp 4.12
- **Testing**: JUnit 4, Google Truth, Kotlinx Coroutines Test (100% Pass)
- **Compatibility**: Min SDK 26 (Android 8.0), Target SDK 34 (Android 14)

---

## 🚀 Cara Menjalankan Proyek

1. Clone repositori ini:
   ```bash
   git clone https://github.com/surgabot/surgatrader.git
   ```
2. Buka folder proyek di **Android Studio** (Hedgehog / Iguana / Koala / terbaru).
3. Biarkan Android Studio menyelesaikan proses **Gradle Sync**.
4. Jalankan aplikasi dengan menekan tombol **Run 'app'** (`Shift + F10`) di emulator atau perangkat fisik Anda.

Untuk menjalankan unit test via terminal:
```bash
./gradlew testDebugUnitTest
```

---

## 📄 Lisensi
Hak Cipta © 2026 Mochamad Tabrani & Antigravity AI. Dikembangkan untuk komunitas trader Indonesia.
