package com.example.tesprojek.ui.components

/**
 * Data halaman "Mengenal". Gambar dicari lewat NAMA file di res/drawable (bukan R.drawable.xxx),
 * jadi aset yang belum ada tidak membuat build error: halaman otomatis menampilkan teks sebagai gantinya.
 *
 * Nama file yang dicari (taruh di res/drawable, huruf kecil semua):
 *   Huruf : huruf_a .. huruf_z        -> gambar "Aa"
 *           benda_a  .. benda_z       -> gambar benda (A = ayam, B = bebek, ...)
 *           pilih_huruf_a .. _z       -> 1 huruf besar berwarna (kartu yang diseret di halaman Cocokkan)
 *   Angka : angka_1 .. angka_10       -> gambar angka
 *           benda_angka_1 .. _10      -> gambar benda sejumlah angka itu
 *           pilih_angka_1 .. _10      -> 1 angka berwarna (kartu yang diseret di halaman Cocokkan)
 */
internal data class LearnItem(
    val glyphName: String,
    val objectName: String,
    val glyphText: String,
    val label: String,
    /** Teks pil di bawah kartu benda, mis. "A untuk Ayam" / "mirip dengan Tongkat". */
    val phrase: String,
    /** Kartu pilihan yang diseret di halaman Cocokkan (1 huruf / 1 angka berwarna). */
    val chipName: String,
    val chipText: String
)

internal enum class LearnKind(
    val title: String,
    val matchTitle: String,
    val dragHint: String,
    val fixedLabel: String, val items: List<LearnItem>) {
    Letters(
        title = "Mengenal Huruf",
        matchTitle = "Cocokan Huruf",
        dragHint = "(drag huruf)",
        fixedLabel = "Huruf",
        items = listOf(
            "Ayam", "Buku", "Cicak", "Dadu", "Elang", "Flamingo", "Gunting", "Hidung", "Ikan",
            "Jagung", "Kardus", "Lilin", "Mangga", "Nasi", "Obor", "Pisang", "Quokka", "Rusa",
            "Sapi", "Tikus", "Ulat", "Vas", "Wortel", "Xilofon", "Yoyo", "Zebra"
        ).mapIndexed { i, word ->
            val c = 'a' + i
            LearnItem("huruf_$c", "benda_$c", "${c.uppercaseChar()}$c", word, "${c.uppercaseChar()} untuk $word",
                "pilih_huruf_$c", "${c.uppercaseChar()}")
        }
    ),
    Numbers(
        title = "Mengenal Angka",
        matchTitle = "Cocokan Angka",
        dragHint = "(drag angka)",
        fixedLabel = "Angka",
        items = listOf(
            "Tongkat", "Angsa", "Sepatu", "Kursi", "Bintang", "Payung", "Pensil", "Laba-laba", "Balon", "Roda"
        ).mapIndexed { i, word ->
            val n = i + 1
            LearnItem("angka_$n", "benda_angka_$n", "$n", word, "mirip dengan $word",
                "pilih_angka_$n", "$n")
        }
    )
}