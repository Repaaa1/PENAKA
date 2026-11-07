package com.example.tesprojek.data

import com.example.tesprojek.ui.components.LearnKind

/** Mengisi database dari LearnContent hanya kalau masih kosong (jadi aman dipanggil tiap aplikasi dibuka). */
suspend fun PenakaDao.isiDataAwalJikaKosong() {
    if (hitungKategori() > 0) return
    LearnKind.entries.forEach { isiSatuKategori(it) }
}

private suspend fun PenakaDao.isiSatuKategori(kind: LearnKind) {
    val kategoriId = insertKategori(
        Kategori(
            namaKategori = kind.title,
            jenisKategori = kind.fixedLabel.lowercase(),
            audioPanduanMenu = "" // TODO: isi dengan nama file audio menu dari MenuScreen/SubMenuScreen
        )
    ).toInt()

    insertMateri(
        kind.items.mapIndexed { i, item ->
            Materi(
                karakterItem = item.glyphText,
                imageRestName = item.glyphName,
                audioRestName = if (kind == LearnKind.Letters)
                    "suara_untuk_${item.glyphName.removePrefix("huruf_")}"
                else
                    "suara_${item.glyphName}",
                urutanHalaman = i + 1,
                namaObjek = item.label,
                imageObjekName = item.objectName,
                kalimatPil = item.phrase,
                audioPanduanLangkah = "",    // TODO: nama audio panduan langkah
                kategoriId = kategoriId
            )
        }
    )

    kind.items.forEachIndexed { round, item ->
        val kuisId = insertKuis(
            Kuis(
                jenisKuis = "cocokan_${kind.fixedLabel.lowercase()}",
                targetImageRest = item.objectName,
                audioResponBenar = "reaksi_benar",
                audioResponSalah = "reaksi_salah",
                audioInstruksiSoal = "kuis_${item.glyphName}",
                kategoriId = kategoriId
            )
        ).toInt()

        insertPilihan(
            indeksPilihan(kind.items.size, round).map { idx ->
                PilihanKuis(
                    nilaiPilihan = kind.items[idx].chipText,
                    apakahBenar = idx == round,
                    kuisId = kuisId
                )
            }
        )
    }
}

/** Salinan rumus matchOptions() di MatchScreen: jawaban benar + 2 pengecoh, posisi jawaban bergilir. */
private fun indeksPilihan(n: Int, round: Int): List<Int> {
    val (a, b) = if (n <= 10) 3 to 6 else 9 to 17
    val list = mutableListOf((round + a) % n, (round + b) % n)
    list.add(round % 3, round)
    return list
}