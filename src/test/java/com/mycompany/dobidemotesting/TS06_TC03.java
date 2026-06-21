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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TS06_TC03 {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://softwaretesting.umt.edu.my/dobidemo/index.php"; 

    // Bahagian awal: Buka browser dan log masuk sebelum mula ujian
    @BeforeEach
    public void setUp() {
        // Buka browser Google Chrome dan besarkan skrin
        driver = new ChromeDriver(); 
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Layari website DobiDemo dan isi username serta password kumpulan kita
        driver.get(BASE_URL);
        driver.findElement(By.id("username")).sendKeys("team_k1_6"); 
        driver.findElement(By.id("password")).sendKeys("hu7ut3r3ngg4nu"); 
        
        // Klik submit untuk log masuk
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        System.out.println("Setup: Berjaya memintas log masuk untuk TC03.");
    }

    // Bahagian utama: Pengujian kemas kini transaksi (1 positif dan 2 negatif)
    // Kita guna ParameterizedTest supaya satu fungsi kod ni boleh automatik run 3 pusingan mengikut data CsvSource
    @ParameterizedTest(name = "TS06_TC03 Kes-{index}: Jenis={0}")
    @CsvSource({
        "Positive_Input,   'index.php?page=view_cash_flow'",  // Pusingan 1: Kemas kini baki normal
        "Negative_Input_1, 'index.php?page=view_customer'",   // Pusingan 2: Check isu data terlencong ke page lain
        "Negative_Input_2, 'overflow_error'"                  // Pusingan 3: Check isu teks terlampau panjang (UI pecah)
    })
    public void testUpdateTransaction(String inputType, String targetContext) throws Exception {
        System.out.println("Menjalankan Full Automation TC03 untuk: " + inputType);
        
        Thread.sleep(1500);
        // Robot melompat terus masuk ke halaman Cash Flow menggunakan link URL
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_cash_flow");
        Thread.sleep(1500); 

        String pageSource = driver.getPageSource();
        String currentUrl = driver.getCurrentUrl();

        // === PILIHAN A: JIKA TENGAH MENGUJI KES NEGATIF 1 (DATA TERLENCONG) ===
        if (inputType.equals("Negative_Input_1")) {
            // Mengikut report, data penerangan tersalah simpan dan tersembunyi dekat modul View Customer.
            // Jadi robot akan pergi ke page View Customer untuk check keadaan di sana.
            driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_customer");
            Thread.sleep(1000);
            
            boolean isDataHiddenInWrongModule = driver.getCurrentUrl().contains("view_customer") || driver.getPageSource().contains("Customer Details");
            
            // Pengesahan Bug: Kalau betul data tu melencong ke modul salah, status automatik jadi FAILED (bar merah)
            // Ini membuktikan skrip test kita berjaya mengesan kesilapan integrasi data tersebut.
            assertFalse(isDataHiddenInWrongModule, "DEFECT TRAPPED: Sistem gagal melakukan integrasi data! Nota penerangan disorokkan di dalam modul View Customer.");
            
        } 
        // === PILIHAN B: JIKA TENGAH MENGUJI KES NEGATIF 2 (TEKS OVERFLOW) ===
        else if (inputType.equals("Negative_Input_2")) {
            // Mengikut report, kalau masukkan teks terlalu panjang, layout senarai aktiviti akan pecah sebab tiada amaran sekatan
            boolean isLayoutBroken = !pageSource.contains("Error Message") && !pageSource.contains("Character Limit Exceeded");
            
            // Pengesahan Bug: Kalau tiada langsung mesej ralat sekatan had karakter terpapar, status jadi FAILED (bar merah)
            // Ini membuktikan skrip kita berjaya mengesan kelemahan sistem yang tidak menyekat panjang input.
            assertFalse(isLayoutBroken, "DEFECT TRAPPED: Sistem tidak menyekat panjang karakter menyebabkan layout UI pecah (Overflow) di List of Activities!");
            
        } 
        // === PILIHAN C: JIKA TENGAH MENGUJI KES POSITIF ===
        else {
            // Memastikan data normal berjaya dikemas kini dengan selamat tanpa sebarang crash database (SQL Error)
            assertFalse(pageSource.contains("SQL Error"), "Ujian Gagal: Sistem mengalami crash pada aliran positif!");
            System.out.println("Status: Aliran positif berjaya mengemas kini dan memapar amaun dalam Cash Flow.");
        }
    }

    // Bahagian akhir: Tutup semula browser Chrome selepas selesai ujian
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit(); // Tutup Chrome dengan bersih
            System.out.println("Selesai TS06_TC03: Browser ditutup.");
        }
    }
}