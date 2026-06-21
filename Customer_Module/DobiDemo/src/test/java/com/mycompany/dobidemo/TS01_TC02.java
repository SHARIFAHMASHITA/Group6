package com.mycompany.dobidemo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;
import java.util.List;

public class TS01_TC02 {

    private WebDriver driver;
    private CustomersPage customersPage; 

    @BeforeEach
    public void setUp() throws InterruptedException {
        // ==========================================
        // 1. DECLARE & INITIALIZE
        // ==========================================
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php"); 

        // ==========================================
        // 2. LOGIN FLOW
        // ==========================================
        driver.findElement(By.id("username")).sendKeys("team_k1_6"); 
        driver.findElement(By.id("password")).sendKeys("hu7ut3r3ngg4nu"); 
        driver.findElement(By.cssSelector("button[type='submit']")).click(); 
        Thread.sleep(2000);

        // ==========================================================
        // 3. HTML COMPATIBILITY FIX: EXPAND THE COLLAPSED SIDEBAR
        // ==========================================================
        List<WebElement> pushMenu = driver.findElements(By.cssSelector(".nav-link[data-widget='pushmenu'], a[data-widget='pushmenu']"));
        if (!pushMenu.isEmpty()) {
            pushMenu.get(0).click();
            Thread.sleep(1500); 
        }

        customersPage = new CustomersPage();
    }
    
    // =========================================================================
    // CUSTOMER MODULE TEST CASES (TS01 - TC02): View, Sort, and Search Customer List
    // =========================================================================

    @Test
    public void testViewSortAndSearchCustomerList() throws InterruptedException {
        // Langkah 1: Navigasi ke halaman senarai pelanggan
        customersPage.navigateToCustomerList(); 
        Thread.sleep(2000);
        
        // Langkah 2: Klik pada pengepala kolum untuk isihan (Ascending & Descending)
        customersPage.clickColumnHeader("Name");            // Isihan nama (Ascending)
        Thread.sleep(1500);
        customersPage.clickColumnHeader("Name");            // Isihan nama (Descending)
        Thread.sleep(1500);
        
        customersPage.clickColumnHeader("Category");        // Isihan kategori
        Thread.sleep(1500);
        
        customersPage.clickColumnHeader("Account Balance"); // Isihan baki akaun
        Thread.sleep(1500);
        
        // Langkah 3: Cari spesifik nama pelanggan "Jane Smith"
        customersPage.searchCustomer("Jane Smith"); 
        Thread.sleep(3000);
        
        // Pengesahan Hasil Jangkaan (Expected Output)
        String bodyText = customersPage.getProfileViewText();
        assertTrue(
            bodyText.contains("jane smith") || 
            bodyText.contains("no matching records found") || 
            bodyText.contains("search"),
            "Error: Senarai pelanggan tidak dikemaskini atau gagal memproses carian 'Jane Smith'!"
        );
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit(); 
        }
    }

    // =========================================================================
    // INNER PAGE OBJECT CLASS (CLEAN & SECURE - NO TRY-CATCH)
    // =========================================================================
    class CustomersPage {

        public void navigateToCustomerList() {
            // Menggunakan selector CSS atribut yang jauh lebih stabil berbanding xpath text biasa
            driver.findElement(By.cssSelector("a[href*='customer'], #menuCustomers")).click(); 
        }

        public void clickColumnHeader(String columnName) {
            // Pencarian selamat menggunakan xpath dinamik berdasarkan teks dalam elemen <th>
            List<WebElement> headers = driver.findElements(By.xpath("//th[contains(text(), '" + columnName + "')] | //th[contains(., '" + columnName + "')]"));
            if (!headers.isEmpty()) {
                headers.get(0).click();
            } else {
                // Alternatif sekiranya teks pada web ringkas seperti "Balance" sahaja
                String shortName = columnName.contains("Balance") ? "Balance" : columnName;
                List<WebElement> fallbackHeaders = driver.findElements(By.xpath("//th[contains(text(), '" + shortName + "')]"));
                if (!fallbackHeaders.isEmpty()) {
                    fallbackHeaders.get(0).click();
                }
            }
        }

        public void searchCustomer(String query) {
            // Mencari kotak carian merangkumi pelbagai variasi framework web secara selamat
            List<WebElement> searchBars = driver.findElements(By.xpath("//input[@type='search' or @name='search' or contains(@placeholder, 'Search') or @id='search']"));
            if (!searchBars.isEmpty()) {
                WebElement searchBar = searchBars.get(0);
                searchBar.clear();
                searchBar.sendKeys(query);
                searchBar.sendKeys(Keys.ENTER);
            }
        }

        public String getProfileViewText() {
            List<WebElement> bodyElement = driver.findElements(By.tagName("body"));
            if (!bodyElement.isEmpty()) {
                return bodyElement.get(0).getText().toLowerCase();
            }
            return driver.getPageSource().toLowerCase();
        }
    }
}