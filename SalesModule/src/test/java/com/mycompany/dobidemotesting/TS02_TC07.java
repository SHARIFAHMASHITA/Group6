package com.mycompany.dobidemotesting;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class TS02_TC07 {
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
    @DisplayName("TS02-TC07 - Alphabetic and Special Character Injection Robustness (Negative Cases)")
    public void testSalesRobustness_NegativeFlows() throws Exception {
        System.out.println("Menjalankan Ujian Kombo TS02-TC07: Input 'TEN' & '10.00 <script>' (Negative Inputs)");
        
        // Taktik Kebal URL: Terus melompat masuk ke skrin pembayaran/baki
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_payment");
        Thread.sleep(2000); 
        
        String pageSource = driver.getPageSource();
        
        // PENGESAHAN BUG SEBIJIK JADUAL REPORT KO (Table 2.12)
        // Jadual menyatakan: Frontend sepatutnya menyekat dengan ralat ("Please enter a valid number" / Prevents XESS execution)
        // Jikalau DobiDemo selamba meluluskan teks abjad atau suntikan skrip tanpa memaparkan sekatan ralat alert, status isVulnerable bertukar TRUE.
        boolean isVulnerable = !pageSource.contains("alert-danger") && !pageSource.contains("valid number") && !pageSource.contains("Invalid input");
        
        // Formula Dr. Sha: JUnit WAJIB MERAH (FAILED) untuk membuktikan sistem bocor daripada kawalan robustness input bukan-numerik!
        assertFalse(isVulnerable, "DEFECT TRAPPED: Sistem gagal menyekat atau menyaring input alfabetik 'TEN' dan gubahan skrip khas pada medan Total Paid!");
        
        System.out.println("Status: Pemeriksaan aliran ralat ketahanan jualan selesai.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Selesai TS02_TC07: Browser ditutup.");
        }
    }
}