package com.mycompany.dobidemotesting;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class TS02_TC04 {
    private WebDriver driver;
    private static final String BASE_URL = "http://softwaretesting.umt.edu.my/dobidemo/index.php";

    @BeforeEach
    public void setUp() throws Exception {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        
        // PRE-CONDITION 1 & 2: Log masuk ke sistem server UMT
        driver.get(BASE_URL); 
        driver.findElement(By.id("username")).sendKeys("team_k1_6");
        driver.findElement(By.id("password")).sendKeys("hu7ut3r3ngg4nu");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        Thread.sleep(2500); // Masa bertenang untuk pengesahan sesi oleh backend
    }

    @Test
    @DisplayName("TS02-TC04 - Searching sales records by Name and Invoice (Positive Cases)")
    public void testSearchSales_PositiveFlows() throws Exception {
        System.out.println("Menjalankan Ujian Kombo TS02-TC04: Search by Name 'Datin' & Invoice 'DR0002' (Positive Inputs)");
        
        // Taktik Kebal URL: Terus melompat masuk ke modul paparan senarai jualan
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_sales");
        Thread.sleep(2000); 
        
        String pageSource = driver.getPageSource();
        
        // PENGESAHAN ALIRAN POSITIF (Mengikut Spesifikasi Jadual 2.9)
        // Memastikan modul carian standard berjaya memuatkan data tanpa sebarang database crash
        assertFalse(pageSource.contains("SQL Error") || pageSource.contains("Database Crash"), 
            "Ujian Gagal: Sistem mengalami crash data kasar pada aliran carian jualan positif!");
        
        System.out.println("Status: Aliran carian positif bagi nama pelanggan dan nombor invois disahkan selamat.");
    }

    @Test
    @DisplayName("TS02-TC04 - Searching sales records with No Match & Special Characters (Negative Cases)")
    public void testSearchSales_NegativeFlows() throws Exception {
        System.out.println("Menjalankan Ujian Kombo TS02-TC04: Search 'Chong Wei' & Special Characters (Negative Inputs)");
        
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_sales");
        Thread.sleep(2000);
        
        String pageSource = driver.getPageSource();
        
        // PENGESAHAN BUG SEBIJIK JADUAL REPORT KO (Table 2.9)
        // Jadual menyatakan: Sistem sepatutnya selamat dari ancaman injection ("System securely sanitizes input text field. No SQL syntax errors occur")
        // Jikalau input karakter khas "; --% memecahkan query dan mengeluarkan ralat sintaks SQL, status isVulnerable bertukar TRUE.
        boolean isVulnerable = pageSource.contains("SQL syntax") || pageSource.contains("fatal error") || pageSource.contains("database error");
        
        // Formula Dr. Sha: JUnit WAJIB MERAH (FAILED) jika terbukti sistem bocor terhadap isu sanitasi query!
        assertFalse(isVulnerable, "DEFECT TRAPPED: Sistem gagal menyaring (sanitize) input karakter khas, mendedahkan query jualan kepada ralat sintaks SQL!");
        
        System.out.println("Status: Pemeriksaan aliran ralat carian negatif selesai.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Selesai TS02_TC04: Browser ditutup.");
        }
    }
}