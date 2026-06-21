package com.mycompany.dobidemotesting;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TS06_TC01 {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://softwaretesting.umt.edu.my/dobidemo/index.php"; 

    // Bahagian awal: Sediakan browser dan navigasi pergi ke page Cash Flow
    @BeforeEach
    public void setUp() {
        // Buka browser Google Chrome dan besarkan skrin
        driver = new ChromeDriver(); 
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        
        // Layari website DobiDemo dan masukkan username serta password group kita
        driver.get(BASE_URL);
        driver.findElement(By.id("username")).sendKeys("team_k1_6"); 
        driver.findElement(By.id("password")).sendKeys("hu7ut3r3ngg4nu"); 
        
        // Klik submit log masuk
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        // Tunggu sehingga menu Cash Flow boleh diklik, kemudian klik menu tersebut
        WebElement cashFlowMenu = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[contains(@href, 'cash_flow') or contains(., 'Cash Flow')]")
        ));
        cashFlowMenu.click();
        System.out.println("Setup: Selamat sampai ke halaman Cash Flow.");
    }

    // Bahagian utama: Pengujian 3 jenis data entry (2 positif dan 1 negatif) mengikut baris CsvSource
    @ParameterizedTest(name = "TS06_TC01 Kes-{index}: Jenis={0}")
    @CsvSource({
        "Positive_Input_1, Date, 'Jun',                  'Jun'",                     // Pusingan 1: Cari bulan Jun
        "Positive_Input_2, '',   '!@#$',                  'No matching records found生'", // Pusingan 2: Cari simbol pelik
        "Negative_Input,   RM,   '',                      'SQL Error'"                // Pusingan 3: Klik sort pada lajur RM
    })
    public void TS06_TC01_FullAutomation(String inputType, String headerToClick, String searchKeyword, String expectedText) {
        System.out.println("Menjalankan Full Automation TC01 untuk: " + inputType);
        
        // Langkah 1: Kalau parameter headerToClick ada isi, robot akan klik tajuk lajur jadual tu untuk fungsi sort
        if (!headerToClick.isEmpty()) {
            WebElement header = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//th[contains(text(),'" + headerToClick + "') or contains(.,'" + headerToClick + "')]")
            ));
            header.click();
        }
        
        // Langkah 2: Kalau parameter searchKeyword ada isi, robot akan masukkan teks tu dalam kotak search bar
        if (!searchKeyword.isEmpty()) {
            WebElement searchBar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='search']")));
            searchBar.clear();
            searchBar.sendKeys(searchKeyword);
        }
        
        // Ambil data HTML halaman semasa untuk buat semakan output
        String pageSource = driver.getPageSource();
        
        // === PILIHAN A: JIKA TENGAH MENGUJI KES NEGATIF ===
        if (inputType.equals("Negative_Input")) {
            // Kita pastikan server tidak crash teruk sampai keluar tulisan ralat database
            assertFalse(pageSource.contains("SQL Error"), "Ujian Gagal: Server mengalami SQL Crash!");
            
            // Robot tengok nilai pada baris pertama, lajur ketiga (medan amaun RM) di dalam jadual
            WebElement firstRowAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tbody//tr[1]/td[3]")
            ));
            String firstRowText = firstRowAmount.getText().trim();
            System.out.println("🤖 Nilai duit teratas jadual selepas di-sort RM: " + firstRowText);
            
            // Kita semak logik: Kalau susunan sorting tu rosak, nilai negatif (ada tanda minus '-') akan tersalah duduk dekat atas sekali
            boolean isSortingAlgorithmBroken = firstRowText.contains("-") || firstRowText.contains("(");
            
            // Formula Dr. Sha: Kalau algoritma sorting tu terbukti rosak, status jadi bar merah (FAILED) tanda defect berjaya ditangkap
            assertFalse(isSortingAlgorithmBroken, "DEFECT TRAPPED: Algoritma sorting RM gagal! Nilai perbelanjaan negatif muncul di atas sekali.");
            System.out.println("Status: Sistem berjaya ditangkap mengandungi bug susunan.");
        } 
        // === PILIHAN B: JIKA TENGAH MENGUJI KES POSITIF ===
        else {
            // Sub-pilihan 1: Semakan untuk carian simbol pelik (sepatutnya jadual kosong dan keluar mesej no matching)
            if (inputType.equals("Positive_Input_2")) {
                WebElement emptyRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(@class,'dataTables_empty') or contains(.,'No matching')]")));
                assertTrue(emptyRow.isDisplayed(), "Mesej carian tidak ditemui!");
            } 
            // Sub-pilihan 2: Semakan carian standard bulan Jun (sepatutnya teks bulan Jun ada terpapar dalam jadual)
            else {
                assertTrue(pageSource.contains(expectedText), "Ujian Gagal: Kandungan '" + expectedText + "' tidak ditemui!");
            }
        }
    }

    // Bahagian akhir: Selesai test, tutup browser Chrome
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit(); 
            System.out.println("Selesai: Browser ditutup.");
        }
    }
}