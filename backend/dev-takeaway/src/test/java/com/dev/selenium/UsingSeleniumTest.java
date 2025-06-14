package com.dev.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsingSeleniumTest {
    WebDriver driver;

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
    }
//    @Test
    public void eightComponents() {

/*        String actualTitle = "Web forms";
        String expectedTitle = "Web forms";
        String actualMessage = "Receivesd!"; // Intentional typo for demonstration
        String expectedMessage = "Received!";

        assertAll("Check multiple conditions",
                () -> assertEquals(expectedTitle, actualTitle, "Page title should be 'Web forms'"),
                () -> assertEquals(expectedMessage, actualMessage, "Message text should be 'Received!'")
        );*/

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");

        String title = driver.getTitle();
//        assertEquals("Web form", title);

        WebElement textBox = driver.findElement(By.name("my-text"));
        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        WebElement searchInput = driver.findElement(By.xpath("//textarea[@name='my-textarea']"));
        searchInput.sendKeys("Vinay Kumar Singh");

        textBox.sendKeys("Selenium");
//        submitButton.click();

//        WebElement message = driver.findElement(By.id("messages"));
//        String value = message.getText();
//        assertEquals("Received!", value);
    }

//    @Test
    public void testMultipleAssertions() {
        String actualTitle = "Web forms"; // Intentionally incorrect
        String expectedTitle = "Web forms";
        String actualMessage = "Receivesd!"; // Intentionally incorrect
        String expectedMessage = "Received!";

        assertAll("Check multiple conditions",
                () -> assertEquals(expectedTitle, actualTitle, "Page title should be 'Web forms'"),
                () -> assertEquals(expectedMessage, actualMessage, "Message text should be 'Received!'")
        );
    }

    @AfterEach
    public void teardown() {
        driver.quit();
    }
}
