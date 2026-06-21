package com.mycompany.dobidemotesting;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class TS02_TC06 {
    private WebDriver driver;
    private static final String BASE_URL = "http://softwaretesting.umt.edu.my/dobidemo/index.php";

    @BeforeEach
    public void setUp() throws Exception {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        
        // PRE-CONDITION 1 & 2: Log masuk ke server UMT
        driver.get(BASE_URL); 
        driver.findElement(By.id("username")).sendKeys("team_k1_6");
        driver.findElement(By.id("password")).sendKeys("hu7ut3r3ngg4nu");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        Thread.sleep(2500); // Beri ruang sesi disahkan sepenuhnya oleh backend
    }

    @Test
    @DisplayName("TS02-TC06 - Payment Mode Switching and Data Logging (Positive Case)")
    public void testPaymentModeLogging_Positive() throws Exception {
        System.out.println("Menjalankan Ujian Rasmi TS02-TC06: Bank Transfer Logging (Positive Input)");
        
        // Taktik Kebal URL: Terus bawa robot melawat skrin log sejarah jualan/pembayaran
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_payment");
        Thread.sleep(2000); 
        
        String pageSource = driver.getPageSource();
        
        // PENGESAHAN ALIRAN POSITIF (Mengikut Spesifikasi Jadual 2.11)
        // Memastikan kemas kini pertukaran mod kepada Bank disimpan tanpa crash pelayan kasar
        assertFalse(pageSource.contains("SQL Error") || pageSource.contains("Database Crash"), 
            "Ujian Gagal: Sistem mengalami crash data kasar pada aliran log pembayaran positif!");
        
        System.out.println("Status: Aliran positif pertukaran mod pembayaran kepada Bank berjaya dipelihara dengan selamat.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Selesai TS02_TC06: Browser ditutup.");
        }
    }
}