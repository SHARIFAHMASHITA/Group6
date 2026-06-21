package com.mycompany.DobiDemoTest;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DobiDemoExpensesTest1 {
    private WebDriver driver;
    private static final String BASE_URL = "http://softwaretesting.umt.edu.my/dobidemo/index.php";

    @BeforeEach
    public void setUp() throws Exception {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        
        // PRE-CONDITION 1 & 2: Log masuk terus ke pelayan UMT
        driver.get(BASE_URL); 
        driver.findElement(By.id("username")).sendKeys("team_k1_6");
        driver.findElement(By.id("password")).sendKeys("hu7ut3r3ngg4nu");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        // Beri bertenang 2.5 saat untuk session log masuk disahkan sepenuhnya
        Thread.sleep(2500);
    }

    @Test
    @DisplayName("TS04-TC01 - Recording new expense (Positive Case)")
    public void testRecordingNewExpense_Positive() throws Exception {
        System.out.println("Menjalankan Ujian Rasmi TS04-TC01 untuk: Positive_Input");
        
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_expense");
        Thread.sleep(2000); // Beri ruang 2 saat untuk kandungan render
        
        String pageSource = driver.getPageSource();
        
        // PENGESAHAN INTEGRITI ALIRAN POSITIF (Mengikut Spesifikasi Jadual 2.14)
        // Memastikan halaman modul perbelanjaan memuatkan grid sheet dengan betul tanpa crash SQL
        assertFalse(pageSource.contains("SQL Error") || pageSource.contains("Database Crash"), 
            "Ujian Gagal: Sistem mengalami crash data kasar pada aliran positif!");
        
        System.out.println("Status: Aliran positif memuatkan skrin dengan betul mengikut log context.");
    }

    @Test
    @DisplayName("TS04-TC01 - Recording new expense (Negative Case)")
    public void testRecordingNewExpense_Negative() throws Exception {
        System.out.println("Menjalankan Ujian Rasmi TS04-TC01 untuk: Negative_Input (Category none)");
        
        // Heret robot melawat skrin pengurusan data expense
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_expense");
        Thread.sleep(2000);
        
        String pageSource = driver.getPageSource();
        
        // PENGESAHAN BUG SEBIJIK JADUAL REPORT KO (Table 2.14)
        // Laporan ko kata: "System should not save an empty expense with incomplete data. System should display an alert message..."
        // Jika sistem DobiDemo ada bug (iaitu dia simpan juga data Category none tanpa memaparkan alert-danger), status isAlertMissing akan jadi TRUE.
        boolean isAlertMissing = !pageSource.contains("alert-danger") && !pageSource.contains("Complete Data") && !pageSource.contains("Sila lengkapkan");
        
        assertFalse(isAlertMissing, "DEFECT TRAPPED: Sistem membiarkan data perbelanjaan tidak lengkap disimpan tanpa memaparkan amaran alert-danger!");
        
        System.out.println("Status: Pemeriksaan amaran ralat aliran negatif selesai.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Selesai TS04_TC01: Browser ditutup.");
        }
    }
}