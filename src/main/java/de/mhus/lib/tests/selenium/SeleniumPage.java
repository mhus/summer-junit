/**
 * Copyright (C) 2002 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.tests.selenium;

import org.openqa.selenium.WebDriver;

import de.mhus.lib.internal.TThread;

public class SeleniumPage {

    protected WebDriver driver;

    public SeleniumPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean waitForText(String text, int sleep, int loops) {
        for (int i = 0; i < loops; i++) {
            TThread.sleep(sleep);
            if (driver.getPageSource().contains(text)) return true;
        }
        return false;
    }

    public void navigateTo(String url) {
        driver.navigate().to(url);
    }

    public boolean containsText(String text) {
        return driver.getPageSource().contains(text);
    }
}
