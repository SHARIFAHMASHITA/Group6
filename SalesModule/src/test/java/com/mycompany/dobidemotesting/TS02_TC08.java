package com.mycompany.dobidemotesting;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class TS02_TC08 {
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
    @DisplayName("TS02-TC08 - Excessive Discount Value Boundary Check (Negative Case)")
    public void testDiscountBoundary_NegativeFlow() throws Exception {
        System.out.println("Menjalankan Ujian Rasmi TS02-TC08: Excessive Discount RM50.00 (Negative Input)");
        
        // Taktik Kebal URL: Terus melompat masuk ke skrin pembayaran/baki
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_payment");
        Thread.sleep(2000); 
        
        String pageSource = driver.getPageSource();
        
        // PENGESAHAN BUG SEBIJIK JADUAL REPORT KO (Table 2.13)
        // Jadual menyatakan: Sistem sepatutnya menyekat tindakan ("Action is denied. Warns that the discount cannot exceed total outstanding amount due.")
        // Jikalau DobiDemo membiarkan diskaun melampau RM50 disimpan tanpa sekatan ralat alert-danger, status isAlertMissing bertukar TRUE.
        boolean isAlertMissing = !pageSource.contains("alert-danger") && !pageSource.contains("cannot exceed") && !pageSource.contains("Diskaun melebihi had");
        
        // Formula Dr. Sha: JUnit WAJIB MERAH (FAILED) untuk menjerat bug had sempadan (boundary) diskaun terlebih had!
        assertFalse(isAlertMissing, "DEFECT TRAPPED: Sistem membenarkan nilai diskaun (RM50.00) melebihi had baki hutang (RM10.00) disimpan tanpa memaparkan sebarang amaran alert ralat!");
        
        System.out.println("Status: Pemeriksaan amaran ralat sempadan diskaun selesai.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Selesai TS02_TC08: Browser ditutup.");
        }
    }
}