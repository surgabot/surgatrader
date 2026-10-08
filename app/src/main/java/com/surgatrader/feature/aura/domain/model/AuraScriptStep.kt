package com.surgatrader.feature.aura.domain.model

data class AuraScriptStep(
    val stage: String,
    val topic: String,
    val progress: Int,
    val speakerId: String,
    val text: String
)

val DefaultAuraScript = listOf(
    AuraScriptStep(
        stage = "FASE 1: INGESTION LIKUIDITAS MAKRO & DIVERGENSI YIELD",
        topic = "Deteksi Korelasi US Treasury 10-Tahun & Pembelian Bank Sentral",
        progress = 18,
        speakerId = "oracle",
        text = "Model ekonometrik kuantum mendeteksi penurunan imbal hasil US Treasury sepuluh tahun sebesar delapan basis poin bersamaan dengan akselerasi akumulasi emas fisik oleh bank sentral global. Divergensi bullish masif resmi terkonfirmasi pada struktur grafik harian emas XAU/USD."
    ),
    AuraScriptStep(
        stage = "FASE 2: STRUKTUR ORDER FLOW & HEATMAP LIKUIDITAS COMEX",
        topic = "Identifikasi Kluster Stop-Loss & Pemasangan Algoritma Iceberg",
        progress = 35,
        speakerId = "hft",
        text = "Heatmap order book COMEX dan London Bullion Market Association terpetakan secara presisi. Kluster likuiditas buy-stop raksasa teridentifikasi di level dua ribu enam ratus enam puluh lima dolar per troy ounce. Algoritma kami siap meluncurkan iceberg orders sub-milidetik tanpa memicu jejak slippage pasar."
    ),
    AuraScriptStep(
        stage = "FASE 3: KALKULASI VOLATILITAS & MANAJEMEN RISIKO DINGIN",
        topic = "Stress-Testing Monte Carlo 100.000 Iterasi & Proteksi Modal",
        progress = 54,
        speakerId = "risk",
        text = "Value at Risk dan simulasi Monte Carlo seratus ribu iterasi telah tuntas. Probabilitas drawdown modal dikunci di bawah nol koma nol delapan persen. Alokasi leverage dinamis disesuaikan secara real-time guna menyerap potensi lonjakan volatilitas rilis data inflasi Amerika Serikat."
    ),
    AuraScriptStep(
        stage = "FASE 4: ANALISIS SENTIMEN GEOPOLITIK & SAFE-HAVEN INFLOWS",
        topic = "Pemindaian 40.000 Transmisi Diplomatik & Aliran Modal Safe-Haven",
        progress = 72,
        speakerId = "sentiment",
        text = "Pemrosesan empat puluh ribu sumber intelijen satelit dan diplomatik mengonfirmasi peningkatan eskalasi geopolitik di rute pasokan energi Timur Tengah. Aliran dana safe-haven institusional diproyeksikan membanjiri pasar derivatif emas dalam hitungan detik."
    ),
    AuraScriptStep(
        stage = "FASE 5: EKSEKUSI KUANTUM DARK POOL & ALOKASI ORDER BESAR",
        topic = "Aktivasi Jalur DMA & Likuidasi Posisi Short Bandar Konvensional",
        progress = 88,
        speakerId = "execution",
        text = "Koneksi direct market access ke empat belas dark pool dan electronic communication network utama aktif penuh. Algoritma eksekusi adaptif telah menyerap likuiditas pasar dan memicu short squeeze. Posisi beli lima ratus ribu lot XAU/USD terkunci dengan rasio akurasi sembilan puluh sembilan persen."
    ),
    AuraScriptStep(
        stage = "FASE 6: DISTRIBUSI ALPHA & REKAPITULASI EMAS FISIK",
        topic = "Panen Alpha +$142 Juta & Konversi ke Khasanah Zurich",
        progress = 100,
        speakerId = "oracle",
        text = "Operasi sukses sempurna. Alpha hari ini bertambah seratus empat puluh dua juta dolar dengan Sharpe Ratio lima koma empat. Seluruh keuntungan pokok telah diamankan dan dikonversi kembali ke cadangan emas batangan fisik bersertifikasi di kubah Zurich."
    )
)
