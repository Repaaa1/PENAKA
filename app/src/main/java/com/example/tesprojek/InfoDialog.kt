package com.example.tesprojek

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Anggota(
    val nama: String,
    val nim: String,
    val tugas: String
)

// GANTI nama aplikasi dan deskripsi di dua baris ini
const val NAMA_APLIKASI = "PENAKA"
const val DESKRIPSI_APLIKASI =
    "Game edukasi sederhana yang dibuat menggunakan Android Studio. " +
            "Pemain dapat bermain, belajar, dan mengumpulkan skor dengan tampilan yang menarik."

val daftarAnggota = listOf(
    Anggota("Sri Wahyuni", "246651011", ""),
    Anggota("Reviana Isnaina Arofa", "246651012", ""),
    Anggota("Dakhirotun Naja", "246651015", ""),
    Anggota("Azera Putri", "246651018", ""),
    Anggota("Dinda Triamita", "246651028", "")
)

@Composable
fun InfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Info Aplikasi") },
        text = {
            LazyColumn {
                item {
                    Text("Nama Aplikasi", fontWeight = FontWeight.Bold)
                    Text(NAMA_APLIKASI)
                    Spacer(Modifier.height(12.dp))

                    Text("Deskripsi", fontWeight = FontWeight.Bold)
                    Text(DESKRIPSI_APLIKASI)
                    Spacer(Modifier.height(12.dp))

                    Text("Teknologi", fontWeight = FontWeight.Bold)
                    Text("Android Studio, Kotlin, Jetpack Compose (tanpa layout XML)")
                    Spacer(Modifier.height(12.dp))

                    Text("Tugas Projek Pak Tomi - Versi 1.0")
                    Spacer(Modifier.height(12.dp))

                    Text("Anggota Kelompok", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                }
                items(daftarAnggota) { a ->
                    Column(Modifier.padding(bottom = 10.dp)) {
                        Text(a.nama, fontWeight = FontWeight.SemiBold)
                        Text("NIM: ${a.nim}")
                        if (a.tugas.isNotBlank()) {
                            Text("Tugas: ${a.tugas}")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        }
    )
}