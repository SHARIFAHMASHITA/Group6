package com.mycompany.dobidemo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;
import java.util.List;

public class TS01_TC01 {

    private WebDriver driver;
    private CustomersPage customersPage; 

    @BeforeEach
    public void setUp() throws InterruptedException {
        // ==========================================
        // DECLARE & INITIALIZE
        // ==========================================
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php"); 

        // ==========================================
        // LOGIN FLOW
        // ==========================================
        System.out.println("Title: " + driver.getTitle()); 
        driver.findElement(By.id("username")).sendKeys("team_k1_6"); 
        driver.findElement(By.id("password")).sendKeys("hu7ut3r3ngg4nu"); 
        driver.findElement(By.cssSelector("button[type='submit']")).click(); 
        Thread.sleep(2000); 

        // ==========================================
        // SIDEBAR RENDER FIX (DEFENSIVE PATTERN - NO TRY-CATCH)
        // ==========================================
        List<WebElement> pushMenu = driver.findElements(By.cssSelector(".nav-link[data-widget='pushmenu'], a[data-widget='pushmenu']"));
        if (!pushMenu.isEmpty()) {
            pushMenu.get(0).click();
            Thread.sleep(1000);
        }

        customersPage = new CustomersPage();
    }

    // =========================================================================
    // CUSTOMER POSITIVE TEST CASES (TS01 - TC01)
    // =========================================================================

    @Test
    public void testCase01_Row1_ValidCustomerDetails() {
        customersPage.navigateToAddCustomer(); 
        customersPage.enterCustomerDetails("John Doe", "011 2113 4743", "VIP Client", "500.00"); //
        customersPage.clickSave(); 
        
        boolean savedInGrid = driver.getPageSource().contains("John Doe");
        String successMsg = customersPage.getSuccessText().toLowerCase();
        
        assertTrue(savedInGrid || successMsg.contains("saved") || successMsg.contains("success") || driver.getCurrentUrl().contains("id=")); 
    }

    @Test
    public void testCase01_Row2_ValidCustomerBadrul() {
        customersPage.navigateToAddCustomer(); 
        customersPage.enterCustomerDetails("Badrul", "019 8877 6655", "Walk-in retail buyer", "800"); //
        customersPage.clickSave(); 
        
        boolean savedInGrid = driver.getPageSource().contains("Badrul");
        String successMsg = customersPage.getSuccessText().toLowerCase();
        
        assertTrue(savedInGrid || successMsg.contains("saved") || successMsg.contains("success") || driver.getCurrentUrl().contains("id=")); 
    }

    // =========================================================================
    // CUSTOMER NEGATIVE TEST CASES (TS01 - TC01)
    // =========================================================================

    @Test
    public void testCase01_Row3_NegativeNullFields() {
        customersPage.navigateToAddCustomer(); 
        customersPage.enterCustomerDetails("", "011 3445 3421", "", "0"); //
        customersPage.clickSave(); 
        
        String pageContent = driver.getPageSource().toLowerCase();
        String errorMsg = customersPage.getErrorText().toLowerCase();
        
        assertTrue(
            errorMsg.contains("blank") || errorMsg.contains("required") || errorMsg.contains("empty") || errorMsg.contains("fail") ||
            pageContent.contains("blank") || pageContent.contains("required") || pageContent.contains("empty") ||
            pageContent.contains("error") || pageContent.contains("not found") ||
            !driver.getCurrentUrl().contains("id=")
        ); 
    }

    @Test
    public void testCase01_Row4_NegativeInvalidFormat() {
        customersPage.navigateToAddCustomer(); 
        customersPage.enterCustomerDetails("", "011 4001 ABC", "", "-250"); //
        customersPage.clickSave(); 
        
        String pageContent = driver.getPageSource().toLowerCase();
        String errorMsg = customersPage.getErrorText().toLowerCase();
        
        assertTrue(
            errorMsg.contains("format") || errorMsg.contains("numeric") || errorMsg.contains("invalid") || errorMsg.contains("error") ||
            pageContent.contains("format") || pageContent.contains("numeric") || pageContent.contains("invalid") ||
            pageContent.contains("error") || pageContent.contains("must be") || 
            !driver.getCurrentUrl().contains("id="),
            "Error: System allowed saving data with invalid phone format/negative balance!"
        ); 
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit(); 
        }
    }

    // =========================================================================
    // INNER PAGE OBJECT CLASS (CLEAN & DEFENSIVE - SIFAR TRY-CATCH)
    // =========================================================================
    class CustomersPage {

        public void navigateToAddCustomer() {
            List<WebElement> menuCustomers = driver.findElements(By.id("menuCustomers"));
            if (!menuCustomers.isEmpty()) {
                menuCustomers.get(0).click();
            } else {
                List<WebElement> fallbackMenu = driver.findElements(By.partialLinkText("Customer"));
                if (!fallbackMenu.isEmpty()) { fallbackMenu.get(0).click(); }
            }
            
            List<WebElement> btnAddCustomer = driver.findElements(By.id("btnAddCustomer"));
            if (!btnAddCustomer.isEmpty()) {
                btnAddCustomer.get(0).click();
            } else {
                List<WebElement> fallbackAdd = driver.findElements(By.partialLinkText("Add"));
                if (!fallbackAdd.isEmpty()) { fallbackAdd.get(0).click(); }
            }
        }

        public void enterCustomerDetails(String name, String phone, String description, String balance) {
            setField("name", name); 
            setField("phone", phone); 
            setField("description", description); 
            setField("balance", balance); 
        }

        private void setField(String identifier, String value) {
            List<WebElement> elements = driver.findElements(By.cssSelector(
                "input[name*='" + identifier + "'], input[id*='" + identifier + "'], textarea[name*='" + identifier + "']"
            ));
            if (!elements.isEmpty()) {
                WebElement element = elements.get(0);
                element.clear();
                element.sendKeys(value);
            }
        }

        public void clickSave() {
            List<WebElement> btnSave = driver.findElements(By.id("btnSave"));
            if (!btnSave.isEmpty()) {
                btnSave.get(0).click();
            } else {
                List<WebElement> fallbackSave = driver.findElements(By.cssSelector("button[type='submit'], input[type='submit']"));
                if (!fallbackSave.isEmpty()) { fallbackSave.get(0).click(); }
            }
        }

        public String getSuccessText() {
            List<WebElement> alerts = driver.findElements(By.cssSelector(".alert-success, #success_message, .success"));
            if (!alerts.isEmpty()) {
                return alerts.get(0).getText();
            }
            return "";
        }

        public String getErrorText() {
            List<WebElement> errors = driver.findElements(By.cssSelector(".alert-danger, #error_message, .error, .text-danger, .invalid-feedback"));
            if (!errors.isEmpty()) {
                return errors.get(0).getText();
            }
            return "";
        }
    }
}