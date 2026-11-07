package com.example.tesprojek.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PenakaDao {

    // Menulis data (dipakai untuk mengisi data awal dan menyimpan hasil kuis)
    @Insert
    suspend fun insertKategori(kategori: Kategori): Long

    @Insert
    suspend fun insertMateri(daftar: List<Materi>)

    @Insert
    suspend fun insertKuis(kuis: Kuis): Long

    @Insert
    suspend fun insertPilihan(daftar: List<PilihanKuis>)

    @Insert
    suspend fun insertHasil(hasil: HasilKuis): Long

    // Membaca data
    @Query("SELECT COUNT(*) FROM kategori")
    suspend fun hitungKategori(): Int

    @Query("SELECT * FROM kategori")
    suspend fun ambilSemuaKategori(): List<Kategori>

    @Query("SELECT * FROM materi WHERE kategori_id_kategori = :kategoriId ORDER BY urutan_halaman")
    suspend fun ambilMateri(kategoriId: Int): List<Materi>

    @Query("SELECT * FROM kuis WHERE kategori_id_kategori = :kategoriId ORDER BY id_kuis")
    suspend fun ambilKuis(kategoriId: Int): List<Kuis>

    @Query("SELECT * FROM pilihan_kuis WHERE kuis_id_kuis = :kuisId ORDER BY id_pilihan")
    suspend fun ambilPilihan(kuisId: Int): List<PilihanKuis>

    @Query("SELECT * FROM hasil_kuis WHERE kategori_id_kategori = :kategoriId ORDER BY waktu_selesai DESC")
    suspend fun ambilHasil(kategoriId: Int): List<HasilKuis>
}