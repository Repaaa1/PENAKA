package com.example.tesprojek

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pengujian UI berinstrumen (Jetpack Compose) untuk alur navigasi aplikasi Penaka.
 *
 * Elemen dicari lewat contentDescription yang sudah ada di kode:
 *   Home      : "Main" (tombol Play), "Informasi"
 *   Menu      : "Huruf", "Angka", "Kembali"
 *   Sub menu  : "Mengenal Huruf", "Mencocokkan Huruf",
 *               "Mengenal Angka", "Mencocokkan Angka", "Kembali"
 *
 * Tiga konstanta di bawah diambil dari PenakaTopBar dan InfoDialog.
 */
private const val DESC_SUARA_ON = "Matikan suara"      // label tombol saat suara sedang menyala
private const val DESC_SUARA_OFF = "Nyalakan suara"    // label tombol saat suara sedang mati
private const val TEKS_DIALOG_INFO = "Info Aplikasi"   // judul dialog di InfoDialog

private const val TUNGGU_MS = 15_000L

@RunWith(AndroidJUnit4::class)
class MenuPlayTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    // ---- fungsi bantu ----------------------------------------------------

    /** Tunggu sampai tepat satu elemen berdeskripsi [desc] ada (melewati splash dan transisi). */
    private fun tunggu(desc: String) {
        rule.waitUntil(timeoutMillis = TUNGGU_MS) {
            rule.onAllNodesWithContentDescription(desc).fetchSemanticsNodes().size == 1
        }
    }

    /** Tunggu sampai elemen berdeskripsi [desc] hilang dari layar. */
    private fun tungguHilang(desc: String) {
        rule.waitUntil(timeoutMillis = TUNGGU_MS) {
            rule.onAllNodesWithContentDescription(desc).fetchSemanticsNodes().isEmpty()
        }
    }

    private fun klik(desc: String) {
        tunggu(desc)
        rule.onNodeWithContentDescription(desc).performClick()
    }

    private fun tampil(desc: String) {
        tunggu(desc)
        rule.onNodeWithContentDescription(desc).assertIsDisplayed()
    }

    // ---- kasus uji -------------------------------------------------------

    // TC-01: setelah splash, klik Play menampilkan pilihan Huruf dan Angka
    @Test
    fun klikPlay_menampilkanPilihanHurufDanAngka() {
        klik("Main")
        tampil("Huruf")
        tampil("Angka")
    }

    // TC-02: pilih Huruf menampilkan Mengenal Huruf dan Mencocokkan Huruf
    @Test
    fun pilihHuruf_menampilkanMengenalDanMencocokkan() {
        klik("Main")
        klik("Huruf")
        tampil("Mengenal Huruf")
        tampil("Mencocokkan Huruf")
    }

    // TC-03: pilih Angka menampilkan Mengenal Angka dan Mencocokkan Angka
    @Test
    fun pilihAngka_menampilkanMengenalDanMencocokkan() {
        klik("Main")
        klik("Angka")
        tampil("Mengenal Angka")
        tampil("Mencocokkan Angka")
    }

    // TC-04: pilih Mengenal Huruf membuka layar belajar (sub menu hilang)
    @Test
    fun pilihMengenalHuruf_membukaLayarBelajar() {
        klik("Main")
        klik("Huruf")
        klik("Mengenal Huruf")
        tungguHilang("Mengenal Huruf")
    }

    // TC-05: pilih Mencocokkan Angka membuka layar kuis (sub menu hilang)
    @Test
    fun pilihMencocokkanAngka_membukaLayarKuis() {
        klik("Main")
        klik("Angka")
        klik("Mencocokkan Angka")
        tungguHilang("Mencocokkan Angka")
    }

    // TC-06: tombol Informasi membuka dialog info
    @Test
    fun klikInformasi_menampilkanDialog() {
        klik("Informasi")
        rule.waitUntil(timeoutMillis = TUNGGU_MS) {
            rule.onAllNodesWithText(TEKS_DIALOG_INFO).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText(TEKS_DIALOG_INFO).assertIsDisplayed()

        // Tombol Tutup menutup dialog
        rule.onNodeWithText("Tutup").performClick()
        rule.waitUntil(timeoutMillis = TUNGGU_MS) {
            rule.onAllNodesWithText(TEKS_DIALOG_INFO).fetchSemanticsNodes().isEmpty()
        }
    }

    // TC-07: tombol suara berganti status saat ditekan.
    // Bunyi musiknya tidak bisa diperiksa tes otomatis, jadi dengarkan sendiri
    // dan catat sebagai kasus uji manual di laporan.
    @Test
    fun klikSuara_mengubahStatusTombol() {
        klik(DESC_SUARA_ON)
        tampil(DESC_SUARA_OFF)
        klik(DESC_SUARA_OFF)
        tampil(DESC_SUARA_ON)
    }

    // TC-08: tombol Kembali di layar Menu menuju Home
    @Test
    fun klikKembali_dariMenu_kembaliKeHome() {
        klik("Main")
        tampil("Huruf")
        klik("Kembali")
        tampil("Main")
    }

    // TC-09: tombol back sistem (BackHandler) dari sub menu menuju Menu, lalu Home
    @Test
    fun tombolBackSistem_dariSubMenu_kembaliBertahap() {
        klik("Main")
        klik("Huruf")
        tampil("Mengenal Huruf")
        pressBack()
        tampil("Huruf")
        pressBack()
        tampil("Main")
    }
}