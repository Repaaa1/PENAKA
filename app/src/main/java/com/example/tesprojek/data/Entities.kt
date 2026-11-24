package com.example.tesprojek.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "kategori")
data class Kategori(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_kategori") val idKategori: Int = 0,
    @ColumnInfo(name = "nama_kategori") val namaKategori: String,
    @ColumnInfo(name = "jenis_kategori") val jenisKategori: String,
    @ColumnInfo(name = "audio_panduan_menu") val audioPanduanMenu: String
)

@Entity(
    tableName = "materi",
    foreignKeys = [ForeignKey(
        entity = Kategori::class,
        parentColumns = ["id_kategori"],
        childColumns = ["kategori_id_kategori"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("kategori_id_kategori")]
)
data class Materi(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_materi") val idMateri: Int = 0,
    @ColumnInfo(name = "karakter_item") val karakterItem: String,
    @ColumnInfo(name = "image_rest_name") val imageRestName: String,
    @ColumnInfo(name = "audio_rest_name") val audioRestName: String,
    @ColumnInfo(name = "urutan_halaman") val urutanHalaman: Int,
    @ColumnInfo(name = "nama_objek") val namaObjek: String? = null,
    @ColumnInfo(name = "image_objek_name") val imageObjekName: String,
    @ColumnInfo(name = "kalimat_pil") val kalimatPil: String,
    @ColumnInfo(name = "audio_panduan_langkah") val audioPanduanLangkah: String,
    @ColumnInfo(name = "kategori_id_kategori") val kategoriId: Int
)

@Entity(
    tableName = "kuis",
    foreignKeys = [ForeignKey(
        entity = Kategori::class,
        parentColumns = ["id_kategori"],
        childColumns = ["kategori_id_kategori"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("kategori_id_kategori")]
)
data class Kuis(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_kuis") val idKuis: Int = 0,
    @ColumnInfo(name = "jenis_kuis") val jenisKuis: String,
    @ColumnInfo(name = "target_image_rest") val targetImageRest: String,
    @ColumnInfo(name = "audio_respon_benar") val audioResponBenar: String,
    @ColumnInfo(name = "audio_respon_salah") val audioResponSalah: String,
    @ColumnInfo(name = "audio_instruksi_soal") val audioInstruksiSoal: String,
    @ColumnInfo(name = "kategori_id_kategori") val kategoriId: Int
)

@Entity(
    tableName = "pilihan_kuis",
    foreignKeys = [ForeignKey(
        entity = Kuis::class,
        parentColumns = ["id_kuis"],
        childColumns = ["kuis_id_kuis"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("kuis_id_kuis")]
)
data class PilihanKuis(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_pilihan") val idPilihan: Int = 0,
    @ColumnInfo(name = "nilai_pilihan") val nilaiPilihan: String,
    @ColumnInfo(name = "apakah_benar") val apakahBenar: Boolean,
    @ColumnInfo(name = "kuis_id_kuis") val kuisId: Int
)

@Entity(
    tableName = "hasil_kuis",
    foreignKeys = [ForeignKey(
        entity = Kategori::class,
        parentColumns = ["id_kategori"],
        childColumns = ["kategori_id_kategori"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("kategori_id_kategori")]
)
data class HasilKuis(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_hasil") val idHasil: Int = 0,
    @ColumnInfo(name = "total_benar") val totalBenar: Int,
    @ColumnInfo(name = "total_salah") val totalSalah: Int,
    @ColumnInfo(name = "bintang_didapat") val bintangDidapat: Int,
    @ColumnInfo(name = "durasi_detik") val durasiDetik: Int,
    @ColumnInfo(name = "waktu_selesai") val waktuSelesai: Long,
    @ColumnInfo(name = "kategori_id_kategori") val kategoriId: Int
)