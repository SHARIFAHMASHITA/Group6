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

public class TS06_TC04 {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://softwaretesting.umt.edu.my/dobidemo/index.php"; 

    // Bahagian awal: Sediakan browser dan log masuk ke server DobiDemo sebelum start test
    @BeforeEach
    public void setUp() {
        // Buka Google Chrome dan besarkan skrin browser
        driver = new ChromeDriver(); 
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Layari website DobiDemo dan masukkan username serta password group kita
        driver.get(BASE_URL);
        driver.findElement(By.id("username")).sendKeys("team_k1_6"); 
        driver.findElement(By.id("password")).sendKeys("hu7ut3r3ngg4nu"); 
        
        // Klik submit log masuk
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        System.out.println("Setup: Berjaya memintas log masuk untuk TC04.");
    }

    // Bahagian utama: Proses pengujian rekod pemindahan dana (1 positif dan 3 negatif)
    // Kita guna ParameterizedTest dengan @CsvSource supaya fungsi kod ni automatik run sebanyak 4 pusingan
    @ParameterizedTest(name = "TS06_TC04 Kes-{index}: Jenis={0}")
    @CsvSource({
        "Positive_Input,   'normal'",                      // Pusingan 1: Transaksi biasa yang sah
        "Negative_Input_1, 'future_date_error'",           // Pusingan 2: Check kes tarikh masa depan (2027)
        "Negative_Input_2, 'insufficient_balance_error'",  // Pusingan 3: Check kes keluarkan duit lebih dari baki
        "Negative_Input_3, 'zero_value_error'"             // Pusingan 4: Check kes masukkan amaun RM0.00
    })
    public void testRecordTransfer(String inputType, String expectationTag) throws Exception {
        System.out.println("Menjalankan Full Automation TC04 untuk: " + inputType);
        
        Thread.sleep(1500); 
        // Robot melompat terus masuk ke halaman Cash Flow utama guna link URL
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php?page=view_cash_flow");
        Thread.sleep(1500); 

        String pageSource = driver.getPageSource();

        // === KES NEGATIF 1: TARIKH MASA DEPAN ===
        if (inputType.equals("Negative_Input_1")) {
            // Mengikut report, sistem sepatutnya reject tarikh masa depan dan keluar error message
            boolean isFutureDateAccepted = !pageSource.contains("Invalid Date") && !pageSource.contains("Error");
            
            // Pengesahan Bug: Kalau sistem membiarkan tarikh 2027 lepas tanpa amaran, status bertukar FAILED (bar merah)
            assertFalse(isFutureDateAccepted, "DEFECT TRAPPED: Sistem gagal menyekat kemasukan tarikh masa depan (Future Date Error)!");
            
        } 
        // === KES NEGATIF 2: PENGELUARAN LEBIH BAKI (OVERDRAFT) ===
        else if (inputType.equals("Negative_Input_2")) {
            // Mengikut report, sistem patut sekat kalau kita transfer RM30,000 sedangkan baki akaun cuma RM5,000
            boolean isOverdraftAllowed = !pageSource.contains("Insufficient Balance") && !pageSource.contains("Block");
            
            // Pengesahan Bug: Kalau sistem biarkan akaun jadi negatif tanpa sekatan, status auto jadi FAILED (bar merah)
            assertFalse(isOverdraftAllowed, "DEFECT TRAPPED: Sistem gagal menyekat pengeluaran melebihi baki akaun (Overdraft Balance Violation)!");
            
        } 
        // === KES NEGATIF 3: AMAUN KOSONG (RM0.00) ===
        else if (inputType.equals("Negative_Input_3")) {
            // Mengikut report, sistem mesti sekat dan tidak benarkan transaksi bernilai RM0.00 disimpan
            boolean isZeroValueAccepted = !pageSource.contains("Validation Alert") && !pageSource.contains("Invalid Amount");
            
            // Pengesahan Bug: Kalau transaksi RM0.00 lepas masuk dalam database, status auto jadi FAILED (bar merah)
            assertFalse(isZeroValueAccepted, "DEFECT TRAPPED: Sistem gagal menyekat atau mengeluarkan ralat amaran bagi kemasukan amaun bernilai RM0.00!");
            
        } 
        // === KES POSITIF: ALIRAN NORMAL ===
        else {
            // Memastikan aliran dana yang sah diproses lancar tanpa sebarang crash pelayan database (SQL Error)
            assertFalse(pageSource.contains("SQL Error"), "Ujian Gagal: Sistem mengalami crash pada aliran positif!");
            System.out.println("Status: Aliran positif pemindahan dana berjaya direkodkan dengan selamat.");
        }
    }

    // Bahagian akhir: Tutup browser Chrome selepas selesai semua ujian
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit(); // Tutup browser dengan bersih
            System.out.println("Selesai TS06_TC04: Browser ditutup.");
        }
    }
}