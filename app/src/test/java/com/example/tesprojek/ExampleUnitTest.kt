package com.example.tesprojek.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pengujian unit lokal (berjalan di komputer, tanpa emulator) untuk starsFor().
 *
 * Aturan di kode:
 *   persentase salah <= 15%  -> 3 bintang
 *   persentase salah <= 40%  -> 2 bintang
 *   selain itu               -> 1 bintang
 *
 * Kuis Penaka memakai 10 soal, jadi sebagian besar kasus memakai total = 10.
 */
class StarsForTest {

    // TC-U1: tanpa kesalahan mendapat 3 bintang
    @Test
    fun tanpaSalah_mendapatTigaBintang() {
        assertEquals(3, starsFor(mistakes = 0, total = 10))
    }

    // TC-U2: 1 salah dari 10 (10%) masih 3 bintang
    @Test
    fun satuSalahDariSepuluh_tigaBintang() {
        assertEquals(3, starsFor(mistakes = 1, total = 10))
    }

    // TC-U3: 2 salah dari 10 (20%) melewati batas 15%, jadi 2 bintang
    @Test
    fun duaSalahDariSepuluh_duaBintang() {
        assertEquals(2, starsFor(mistakes = 2, total = 10))
    }

    // TC-U4: 4 salah dari 10 (tepat 40%) masih 2 bintang (batas atas)
    @Test
    fun empatSalahDariSepuluh_duaBintang() {
        assertEquals(2, starsFor(mistakes = 4, total = 10))
    }

    // TC-U5: 5 salah dari 10 (50%) hanya 1 bintang
    @Test
    fun limaSalahDariSepuluh_satuBintang() {
        assertEquals(1, starsFor(mistakes = 5, total = 10))
    }

    // TC-U6: semua salah tetap 1 bintang, bukan 0
    @Test
    fun semuaSalah_tetapSatuBintang() {
        assertEquals(1, starsFor(mistakes = 10, total = 10))
    }

    // TC-U7: batas tepat 15% (3 salah dari 20) masih 3 bintang
    @Test
    fun batasLimaBelasPersen_tigaBintang() {
        assertEquals(3, starsFor(mistakes = 3, total = 20))
    }

    // TC-U8: sedikit di atas 15% (4 salah dari 20 = 20%) jadi 2 bintang
    @Test
    fun diAtasLimaBelasPersen_duaBintang() {
        assertEquals(2, starsFor(mistakes = 4, total = 20))
    }
}