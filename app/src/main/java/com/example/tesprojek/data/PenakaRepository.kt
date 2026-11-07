package com.example.tesprojek.data

data class PilihanSoal(val nilai: String, val apakahBenar: Boolean)

data class SoalKuis(
    val idKuis: Int,
    val targetImage: String,
    val audioInstruksi: String,
    val audioBenar: String,
    val audioSalah: String,
    val pilihan: List<PilihanSoal>
)

class PenakaRepository(private val dao: PenakaDao) {

    /** jenis: "huruf" atau "angka". Mengisi data awal dulu kalau database masih kosong. */
    suspend fun cariKategoriId(jenis: String): Int? {
        dao.isiDataAwalJikaKosong()
        return dao.ambilSemuaKategori().firstOrNull { it.jenisKategori == jenis }?.idKategori
    }

    suspend fun ambilSoal(kategoriId: Int, jumlah: Int = 10): List<SoalKuis> =
        dao.ambilKuis(kategoriId).shuffled().take(jumlah).map { kuis ->
            SoalKuis(
                idKuis = kuis.idKuis,
                targetImage = kuis.targetImageRest,
                audioInstruksi = kuis.audioInstruksiSoal,
                audioBenar = kuis.audioResponBenar,
                audioSalah = kuis.audioResponSalah,
                pilihan = dao.ambilPilihan(kuis.idKuis).map {
                    PilihanSoal(it.nilaiPilihan, it.apakahBenar)
                }
            )
        }

    suspend fun ambilMateri(jenis: String): List<Materi> {
        val id = cariKategoriId(jenis) ?: return emptyList()
        return dao.ambilMateri(id)
    }

    /** Hasil terbaik: bintang terbanyak, kalau sama yang waktunya tercepat. */
    suspend fun rekorTerbaik(jenis: String): HasilKuis? {
        val id = cariKategoriId(jenis) ?: return null
        return dao.ambilHasil(id)
            .sortedWith(compareByDescending<HasilKuis> { it.bintangDidapat }.thenBy { it.durasiDetik })
            .firstOrNull()
    }

    suspend fun simpanHasil(
        kategoriId: Int, totalBenar: Int, totalSalah: Int, bintang: Int, durasiDetik: Int
    ) {
        dao.insertHasil(
            HasilKuis(
                totalBenar = totalBenar,
                totalSalah = totalSalah,
                bintangDidapat = bintang,
                durasiDetik = durasiDetik,
                waktuSelesai = System.currentTimeMillis(),
                kategoriId = kategoriId
            )
        )
    }
}