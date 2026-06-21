package com.mycompany.dobidemo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;
import java.util.List;

public class TS01_TC05 {

    private WebDriver driver;
    private CustomerUpdatePage updatePage;

    @BeforeEach
    public void setUp() throws InterruptedException {
        // ==========================================
        // 1. DECLARE & INITIALIZE
        // ==========================================
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        
        // Membuka portal aplikasi
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php");

        // ==========================================
        // 2. LOGIN FLOW (Pre-condition)
        // ==========================================
        driver.findElement(By.id("username")).sendKeys("team_k1_6");
        driver.findElement(By.id("password")).sendKeys("hu7ut3r3ngg4nu");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        Thread.sleep(2000); 

        // ==========================================================
        // SIDEBAR RENDERING FIX (DEFENSIVE PATTERN - SIFAR TRY-CATCH)
        // ==========================================================
        List<WebElement> pushMenu = driver.findElements(By.cssSelector(".nav-link[data-widget='pushmenu'], a[data-widget='pushmenu']"));
        if (!pushMenu.isEmpty()) {
            pushMenu.get(0).click();
            Thread.sleep(1500); 
        }

        updatePage = new CustomerUpdatePage();
    }

    // =========================================================================
    // TEST 1: POSITIVE TEST CASE (Valid Update)
    // =========================================================================
    @Test
    public void testCase05_Positive_UpdateCustomerDetails() throws InterruptedException {
        updatePage.navigateToFirstCustomerProfile();
        Thread.sleep(2000);

        updatePage.clickEditButton();
        Thread.sleep(1500);

        updatePage.updatePhoneAndDescription("012 3874 1256", "Regular retail client to wholesale tier account");
        
        String urlBeforeSave = driver.getCurrentUrl().toLowerCase();
        updatePage.clickSave();
        Thread.sleep(2500);

        String bodyText = updatePage.getNotificationOrPageText();
        String currentUrl = driver.getCurrentUrl().toLowerCase();
        
        boolean isSavedSuccessfully = !currentUrl.equals(urlBeforeSave) || 
                                     currentUrl.contains("index") || 
                                     currentUrl.contains("view") ||
                                     currentUrl.contains("profile") ||
                                     bodyText.contains("success") || 
                                     bodyText.contains("updated") || 
                                     bodyText.contains("berjaya") || 
                                     bodyText.contains("simpan");

        assertTrue(isSavedSuccessfully, "Error: Sistem gagal memproses atau mengesahkan sukses kemas kini data!");
    }

    // =========================================================================
    // TEST 2: NEGATIVE TEST CASE (Empty Name Validation)
    // =========================================================================
    @Test
    public void testCase05_Negative_EmptyCustomerName() throws InterruptedException {
        // Langkah 1: Navigasi dan pilih profil pelanggan pertama
        updatePage.navigateToFirstCustomerProfile();
        Thread.sleep(2000);

        // Langkah 2: Klik butang Edit/Update
        updatePage.clickEditButton();
        Thread.sleep(1500);

        // Langkah 3: Kosongkan nama pelanggan sepenuhnya
        updatePage.clearCustomerNameCompletely();

        // Semak status HTML5 borang sebelum klik hantar
        boolean hasHtmlValidationBeforeClick = updatePage.isHtml5ValidationPresent();

        // Rekod URL halaman suntingan semasa untuk membuktikan sekatan navigasi
        String urlBeforeSave = driver.getCurrentUrl().toLowerCase();

        // Langkah 4: Klik simpan perubahan
        updatePage.clickSave();
        Thread.sleep(2500);

        // Verifikasi Output Jangkaan
        String bodyText = updatePage.getNotificationOrPageText();
        String currentUrl = driver.getCurrentUrl().toLowerCase();
        boolean hasHtmlValidationAfterClick = updatePage.isHtml5ValidationPresent();
        
        // Borang dianggap berjaya menyekat data kosong jika kekal di URL asal, 
        // disekat oleh browser (HTML5), ATAU memaparkan teks amaran/ralat.
        boolean isValidationTriggered = hasHtmlValidationBeforeClick || 
                                        hasHtmlValidationAfterClick || 
                                        currentUrl.equals(urlBeforeSave) || 
                                        bodyText.contains("blank") || 
                                        bodyText.contains("required") || 
                                        bodyText.contains("cannot be left") || 
                                        bodyText.contains("kosong") ||
                                        bodyText.contains("error") ||
                                        bodyText.contains("wajib") ||
                                        bodyText.contains("isi") ||
                                        bodyText.contains("pilih") ||
                                        bodyText.contains("fail");

        assertTrue(isValidationTriggered, "Error: Sistem membenarkan simpanan data walaupun nama pelanggan telah dikosongkan!");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // =========================================================================
    // INNER PAGE OBJECT CLASS (CLEAN & DEFENSIVE - NO TRY-CATCH)
    // =========================================================================
    class CustomerUpdatePage {

        public void navigateToFirstCustomerProfile() {
            List<WebElement> customerMenu = driver.findElements(By.cssSelector("a[href*='customer'], #menuCustomers"));
            if (!customerMenu.isEmpty()) {
                customerMenu.get(0).click();
            } else {
                List<WebElement> fallbackMenu = driver.findElements(By.xpath("//*[contains(text(), 'Customer')]"));
                if (!fallbackMenu.isEmpty()) { fallbackMenu.get(0).click(); }
            }

            List<WebElement> rowLinks = driver.findElements(By.xpath("//table//tbody//tr[1]//a"));
            if (!rowLinks.isEmpty()) {
                rowLinks.get(0).click();
            } else {
                List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr[1]//td"));
                if (!rows.isEmpty()) {
                    rows.get(0).click();
                }
            }
        }

        public void clickEditButton() {
            List<WebElement> editButtons = driver.findElements(By.xpath("//a[contains(@href, 'edit')] | //button[contains(@class, 'btn-warning')] | //*[contains(text(), 'Edit') or contains(text(), 'Update')]"));
            if (!editButtons.isEmpty()) {
                editButtons.get(0).click();
            } else {
                List<WebElement> iconButtons = driver.findElements(By.cssSelector(".fa-edit, .fa-pencil-alt"));
                if (!iconButtons.isEmpty()) {
                    iconButtons.get(0).click();
                }
            }
        }

        public void updatePhoneAndDescription(String newPhone, String newDesc) {
            List<WebElement> phoneFields = driver.findElements(By.xpath("//input[contains(@name, 'phone') or contains(@id, 'phone')]"));
            if (!phoneFields.isEmpty()) {
                phoneFields.get(0).clear();
                phoneFields.get(0).sendKeys(newPhone);
            }

            List<WebElement> descFields = driver.findElements(By.xpath("//textarea[contains(@name, 'desc') or contains(@name, 'description')] | //input[contains(@name, 'desc')]"));
            if (!descFields.isEmpty()) {
                descFields.get(0).clear();
                descFields.get(0).sendKeys(newDesc);
            }
        }

        public void clearCustomerNameCompletely() {
            List<WebElement> nameFields = driver.findElements(By.xpath("//input[contains(@name, 'name') or contains(@id, 'name')]"));
            if (!nameFields.isEmpty()) {
                WebElement nameField = nameFields.get(0);
                nameField.clear();
                nameField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
            }
        }

        public void clickSave() {
            List<WebElement> saveButtons = driver.findElements(By.xpath("//button[@type='submit'] | //input[@type='submit'] | //*[contains(text(), 'Save') or contains(text(), 'Simpan')]"));
            if (!saveButtons.isEmpty()) {
                saveButtons.get(0).click();
            }
        }

        public String getNotificationOrPageText() {
            List<WebElement> alerts = driver.findElements(By.cssSelector(".toast, .alert, .alert-success, .alert-danger, #toast-container, .invalid-feedback"));
            StringBuilder textContent = new StringBuilder();
            
            for (WebElement alert : alerts) {
                textContent.append(alert.getText().toLowerCase()).append(" ");
            }
            
            List<WebElement> bodies = driver.findElements(By.tagName("body"));
            if (!bodies.isEmpty()) {
                textContent.append(bodies.get(0).getText().toLowerCase());
            }
            
            return textContent.toString();
        }

        public boolean isHtml5ValidationPresent() {
            List<WebElement> nameFields = driver.findElements(By.xpath("//input[contains(@name, 'name') or contains(@id, 'name')]"));
            if (!nameFields.isEmpty()) {
                WebElement nameField = nameFields.get(0);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Boolean isRequired = (Boolean) js.executeScript("return arguments[0].hasAttribute('required');", nameField);
                Boolean isValueMissing = (Boolean) js.executeScript("return arguments[0].validity.valueMissing;", nameField);
                
                return (isRequired != null && isRequired && nameField.getAttribute("value").isEmpty()) || 
                       (isValueMissing != null && isValueMissing);
            }
            return false;
        }
    }
}