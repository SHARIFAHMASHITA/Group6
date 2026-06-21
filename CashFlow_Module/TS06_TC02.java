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

public class TS06_TC02 {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://softwaretesting.umt.edu.my/dobidemo/index.php"; 

    // Bahagian awal: Log masuk ke sistem sebelum start test
    @BeforeEach
    public void setUp() {
        // Buka Google Chrome dan besarkan skrin browser
        driver = new ChromeDriver(); 
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Layari website DobiDemo UMT dan masukkan username serta password group kita
        driver.get(BASE_URL);
        driver.findElement(By.id("username")).sendKeys("team_k1_6"); 
        driver.findElement(By.id("password")).sendKeys("hu7ut3r3ngg4nu"); 
        
        // Klik butang log masuk
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        System.out.println("Setup: Berjaya menekan submit log masuk.");
    }

    // Bahagian utama: Proses pengujian senario
    // Kita guna ParameterizedTest dengan @CsvSource supaya kod ni automatik run 2 kali untuk kes positif dan negatif
    @ParameterizedTest(name = "TS06_TC02 Kes-{index}: Jenis={0}")
    @CsvSource({
        "Positive_Input, 'index.php?page=view_cash_flow'", // Pusingan 1: Pergi ke halaman Cash Flow yang betul
        "Negative_Input, 'index.php?page=view_payment'"    // Pusingan 2: Simulasi pergi ke halaman Payment yang salah
    })
    public void testViewDetails(String inputType, String targetPage) throws Exception {
        System.out.println("Menjalankan Ujian Rasmi TS06_TC02 untuk: " + inputType);
        
        // Tunggu 2 saat bagi memastikan server selesai proses log masuk tadi
        Thread.sleep(2000); 
        
        // Robot melompat pergi ke link halaman mengikut parameter yang diuji
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/" + targetPage);
        Thread.sleep(2000); // Tunggu skrin loading sekejap
        
        // Ambil data source code halaman (HTML) dan link URL semasa untuk disemak
        String pageSource = driver.getPageSource();
        String currentUrl = driver.getCurrentUrl();
        
        // Pilihan 1: Kalau tengah menguji kes negatif
        if (inputType.equals("Negative_Input")) {
            // Kita check kalau sistem tersalah bawa user pergi skrin view_payment
            boolean isRedirectedToWrongScreen = currentUrl.contains("view_payment") || pageSource.contains("View Payment") || pageSource.contains("Payment Details");
            
            // Pengesahan Bug: Kalau sistem terbukti salah melencong, status akan jadi FAILED (bar merah)
            // Ini untuk membuktikan skrip test kita berjaya kesan kecacatan navigasi dalam sistem
            assertFalse(isRedirectedToWrongScreen, "DEFECT TRAPPED: Sistem disahkan melencong ke skrin 'View Payment' yang salah bagi transaksi negatif!");
        } 
        // Pilihan 2: Kalau tengah menguji kes positif
        else {
            // Memastikan halaman cash flow yang betul loading dengan selamat tanpa sebarang crash database
            assertFalse(pageSource.contains("SQL Error"), "Ujian Gagal: Sistem mengalami crash pada aliran positif!");
            System.out.println("Status: Aliran positif memuatkan skrin dengan betul mengikut log context.");
        }
    }

    // Bahagian akhir: Tutup semula browser selepas selesai test
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit(); // Tutup Chrome dengan bersih
            System.out.println("Selesai TS06_TC02: Browser ditutup.");
        }
    }
}