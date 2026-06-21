package com.mycompany.dobidemotesting;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DobiDemoTest {

    @Test
    public void openGoogle() {

        
//        DECLARE
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://softwaretesting.umt.edu.my/dobidemo/index.php");

        
//        LOGIN
        System.out.println("Title: " + driver.getTitle());
        driver.findElement(By.id("username")).sendKeys("team_k1_6");
        driver.findElement(By.id("password")).sendKeys("hu7ut3r3ngg4nu");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        
//        PRODUCTS
        driver.findElement(By.id("menuProducts")).click();
        String info = driver.findElement(By.id("product_info")).getText();
        System.out.println(info);

  
//        QUIT
        driver.quit();
    }
}