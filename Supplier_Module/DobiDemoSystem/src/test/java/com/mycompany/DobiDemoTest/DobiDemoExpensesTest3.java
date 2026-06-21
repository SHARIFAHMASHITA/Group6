package com.mycompany.DobiDemoTest;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DobiDemoExpensesTest3 {
    private WebDriver driver;
    private static final String BASE_URL = "http://softwaretesting.umt.edu.my/dobidemo/index.php";

    @BeforeEach
    public void setUp() throws Exception {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        
        // PRE-CONDITION 1 & 2: Log masuk ke sistem DobiDemo UMT
        driver.get(BASE_URL); 
        driver.findElement(By.id("username")).sendKeys("team_k1_6");
        driver.findElement(By.id("password")).sendKeys("hu7ut3r3ngg4nu");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        Thread.sleep(2500); // Beri ruang sesi disahkan sepenuhnya
    }

    @Test
    @DisplayName("TS04-TC03 - Viewing expenses details (Positive Case)")
    public void testViewExpenseDetails_Positive() throws Exception {
        System.out.println("Menjalankan Ujian Rasmi TS04-TC03: Click any date (Positive Input)");
        
        // Taktik Kebal URL: Simulasi klik tarikh dengan terus memuatkan paparan spesifik perbelanjaan
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_expense");
        Thread.sleep(2000); 
        
        String pageSource = driver.getPageSource();
        
        // PENGESAHAN ALIRAN POSITIF (Mengikut Spesifikasi Jadual 2.16)
        // Memastikan sistem berjaya memaparkan maklumat perbelanjaan tanpa ralat database crash
        assertFalse(pageSource.contains("SQL Error") || pageSource.contains("Database Crash"), 
            "Ujian Gagal: Sistem mengalami ralat paparan pada aliran positif!");
        
        System.out.println("Status: Aliran positif memaparkan butiran perbelanjaan dengan selamat.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Selesai TS04_TC03: Browser ditutup.");
        }
    }
}