package com.dev.selenium;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class Selenium {

    public void getSeleniumDriver() {
        WebDriver driver = new ChromeDriver();

        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        String title = driver.getTitle();
        String url = driver.getCurrentUrl();

        WebElement textBox = driver.findElement(By.name("my-text"));
        WebElement submitButton = driver.findElement(By.cssSelector("button"));

        textBox.sendKeys("Vinay Kumar Singh");
//        submitButton.click();

        WebElement checkbox = driver.findElement(By.name("my-check"));
        checkbox.click();

        WebElement passwordInput = driver.findElement(By.name("my-password"));
        passwordInput.clear();
        passwordInput.sendKeys("123456");

        boolean isMyTextDisplayed = driver.findElement(By.name("my-text")).isDisplayed();
        log.info("isMyTextDisplayed {}", isMyTextDisplayed);

        Rectangle rectangle = driver.findElement(By.name("my-text")).getRect();
        log.info("x: {}, y: {}, dimension: {}, height: {}, width: {}", rectangle.getX(), rectangle.getY(), rectangle.getDimension(), rectangle.getHeight(), rectangle.getWidth());

        log.info("title, url {} {}", title, url);

    }

}
