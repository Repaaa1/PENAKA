package com.example.tesprojek.data

import com.example.tesprojek.ui.components.LearnItem
import com.example.tesprojek.ui.components.LearnKind

/** Mengubah baris tabel materi menjadi LearnItem yang dipakai LearnScreen. */
internal fun List<Materi>.keLearnItems(kind: LearnKind): List<LearnItem> = map { m ->
    val asal = kind.items.firstOrNull { it.glyphName == m.imageRestName }
    LearnItem(
        glyphName = m.imageRestName,
        objectName = m.imageObjekName,
        glyphText = m.karakterItem,
        label = m.namaObjek ?: "",
        phrase = m.kalimatPil,
        chipName = asal?.chipName ?: "pilih_${m.imageRestName}",
        chipText = asal?.chipText ?: m.karakterItem
    )
}