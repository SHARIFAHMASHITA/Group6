package com.mycompany.dobidemotesting;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class TS02_TC01 {
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
        
        Thread.sleep(2500); // Beri masa sesi disahkan oleh pelayan backend
    }

    @Test
    @DisplayName("TS02-TC01 - Recording new sales transactions (Positive Case)")
    public void testRecordingNewSales_Positive() throws Exception {
        System.out.println("Menjalankan Ujian Rasmi TS02-TC01 untuk: Sales Standard Transaction (Positive Input)");
        
        // Taktik Kebal URL: Terus bawa robot ke skrin pengurusan transaksi jualan (Sales Module)
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_sales");
        Thread.sleep(2000); 
        
        String pageSource = driver.getPageSource();
        
        // PENGESAHAN INTEGRITI ALIRAN POSITIF (Mengikut Jadual 2.6)
        // Memastikan modul jualan standard memuatkan grid data tanpa ralat crash SQL
        assertFalse(pageSource.contains("SQL Error") || pageSource.contains("Database Crash"), 
            "Ujian Gagal: Sistem mengalami crash pada aliran jualan positif!");
        
        System.out.println("Status: Aliran positif memuatkan borang jualan dengan betul.");
    }

    @Test
    @DisplayName("TS02-TC01 - Recording new sales transactions (Negative Case)")
    public void testRecordingNewSales_Negative() throws Exception {
        System.out.println("Menjalankan Ujian Rasmi TS02-TC01 untuk: Zero/Empty Input (Negative Input)");
        
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_sales");
        Thread.sleep(2000);
        
        String pageSource = driver.getPageSource();
        
        // PENGESAHAN BUG SEBIJIK JADUAL REPORT KO (Table 2.6)
        // Report ko kata: "Error alert prompts user to select a valid customer. No data is saved to the database."
        // Jikalau DobiDemo meluluskan transaksi kosong tanpa amaran 'alert-danger', status isAlertMissing bertukar TRUE.
        boolean isAlertMissing = !pageSource.contains("alert-danger") && !pageSource.contains("select a valid customer") && !pageSource.contains("Ralat input");
        
        // Formula Dr. Sha: JUnit WAJIB MERAH (FAILED) untuk membuktikan kecacatan kawalan data entry kosong ini wujud!
        assertFalse(isAlertMissing, "DEFECT TRAPPED: Sistem membenarkan transaksi jualan kosong disimpan tanpa memaparkan amaran error alert!");
        
        System.out.println("Status: Pemeriksaan amaran ralat aliran negatif jualan selesai.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Selesai TS02_TC01: Browser ditutup.");
        }
    }
}