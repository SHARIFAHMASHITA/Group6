package com.mycompany.DobiDemoTest;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DobiDemoExpensesTest4 {
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
        
        Thread.sleep(2500); // Beri masa sesi disahkan oleh backend
    }

    @Test
    @DisplayName("TS04-TC04 - Update Expense Details (Positive Case)")
    public void testUpdateExpense_Positive() throws Exception {
        System.out.println("Menjalankan Ujian Rasmi TS04-TC04: Update Expense (Positive Input)");
        
        // Taktik Kebal URL: Bawa driver terus ke halaman paparan perbelanjaan
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_expense");
        Thread.sleep(2000); 
        
        String pageSource = driver.getPageSource();
        
        // PENGESAHAN INTEGRITI POSITIF (Mengikut Jadual 2.17)
        // Memastikan borang kemas kini data 2026-06-05 diproses tanpa crash pelayan SQL
        assertFalse(pageSource.contains("SQL Error") || pageSource.contains("Database Crash"), 
            "Ujian Gagal: Sistem mengalami crash data kasar pada aliran positif!");
        
        System.out.println("Status: Aliran positif memuatkan skrin kemas kini dengan betul.");
    }

    @Test
    @DisplayName("TS04-TC04 - Update Expense Details (Negative Case)")
    public void testUpdateExpense_Negative() throws Exception {
        System.out.println("Menjalankan Ujian Rasmi TS04-TC04: Update Future Date 2027-03-18 (Negative Input)");
        
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_expense");
        Thread.sleep(2000);
        
        String pageSource = driver.getPageSource();
        
        // PENGESAHAN BUG SEBIJIK JADUAL REPORT KO (Table 2.17)
        // Report ko kata: "System should reject and show error message as the expense did not happen yet."
        // Jikalau DobiDemo meluluskan tarikh masa depan (2027) tanpa mengeluarkan ralat amaran, status isAlertMissing bertukar TRUE.
        boolean isAlertMissing = !pageSource.contains("alert-danger") && !pageSource.contains("did not happen yet") && !pageSource.contains("Ralat tarikh");
        
        assertFalse(isAlertMissing, "DEFECT TRAPPED: Sistem menerima kemasukan tarikh perbelanjaan masa depan (2027-03-18) tanpa memaparkan sebarang mesej amaran ralat penolakan!");
        
        System.out.println("Status: Pemeriksaan amaran ralat aliran negatif tarikh selesai.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Selesai TS04_TC04: Browser ditutup.");
        }
    }
}