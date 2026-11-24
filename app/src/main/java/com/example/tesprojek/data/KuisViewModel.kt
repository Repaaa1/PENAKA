package com.example.tesprojek.data

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class KuisViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PenakaRepository(PenakaDatabase.getInstance(application).dao())

    var soal by mutableStateOf<List<SoalKuis>>(emptyList())
        private set
    var materiHuruf by mutableStateOf<List<Materi>>(emptyList())
        private set
    var materiAngka by mutableStateOf<List<Materi>>(emptyList())
        private set
    var rekor by mutableStateOf<HasilKuis?>(null)
        private set

    /** Dipanggil saat layar kuis dibuka. jenis = "huruf" atau "angka". */
    fun muatSoal(jenis: String, jumlah: Int = 10) {
        soal = emptyList()
        rekor = null
        viewModelScope.launch {
            val id = repo.cariKategoriId(jenis) ?: return@launch
            soal = repo.ambilSoal(id, jumlah)
        }
    }

    /** Dipanggil saat halaman belajar dibuka. jenis = "huruf" atau "angka". */
    fun muatMateri(jenis: String) {
        viewModelScope.launch {
            val daftar = repo.ambilMateri(jenis)
            if (jenis == "huruf") materiHuruf = daftar else materiAngka = daftar
        }
    }

    /** Dipanggil dari onFinished di MatchScreen. Setelah tersimpan, rekor terbaik dimuat ulang. */
    fun simpanHasil(jenis: String, totalBenar: Int, totalSalah: Int, bintang: Int, durasiDetik: Int) {
        viewModelScope.launch {
            val id = repo.cariKategoriId(jenis) ?: return@launch
            repo.simpanHasil(id, totalBenar, totalSalah, bintang, durasiDetik)
            rekor = repo.rekorTerbaik(jenis)
        }
    }
}