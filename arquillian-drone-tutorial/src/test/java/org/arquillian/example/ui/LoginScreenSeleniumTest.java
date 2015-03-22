/*
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arquillian.example.ui;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 * @author <a href="http://community.jboss.org/people/kpiwko">Karel Piwko</a>
 */
@RunWith(Arquillian.class)
public class LoginScreenSeleniumTest {
    private static final String WEBAPP_SRC = "src/main/webapp";
            
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
    	System.out.println("************************************* 1");
    	File[] libs = null;
    	try {
    		libs = Maven.resolver()
                .loadPomFromFile("pom.xml").resolve("org.springframework:spring-webmvc")
                .withTransitivity().as(File.class); 
    		
    		System.out.println("************************************* LIBS ");
    		System.out.println(Arrays.toString(libs));
    		System.out.println("************************************* LIBS ");
    	} catch(Exception e){
    		System.out.println("************************************* exception = "+e);
    	}
    	
    	System.out.println("************************************* 2");
    	
    	WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "login.war")
            .addClasses(LoginController.class, User.class, Credentials.class)
            .addAsLibraries(libs)
            // .addAsWebResource(new File(WEBAPP_SRC), "login.xhtml")
            // .addAsWebResource(new File(WEBAPP_SRC), "home.xhtml")
            .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                .importDirectory(WEBAPP_SRC).as(GenericArchive.class),
                "/", Filters.include(".*\\.xhtml$"))
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsWebInfResource("faces-config.xml")
                .setWebXML("web.xml")
                .addAsWebInfResource("applicationContext.xml");
        
        System.out.println(webArchive.toString(true));
        webArchive.as(ExplodedExporter.class).exportExploded(new File("C:\\sergio\\aaaa"));
        
        return webArchive;
    }
    
    @Drone
    DefaultSelenium browser;
//    @Drone
//    WebDriver browser;
    
    @ArquillianResource
    URL deploymentUrl;
    
    @Test
    public void should_login_with_valid_credentials() {
//        browser.get(deploymentUrl.toString().replaceFirst("/$", "") + "/login.jsf");
        
    	browser.open(deploymentUrl.toString().replaceFirst("/$", "") + "/login.jsf");
		browser.type("id=loginForm:username", "user1");
		browser.type("id=loginForm:password", "demo");
		browser.click("id=loginForm:login");
		browser.waitForPageToLoad("15000");

		Assert.assertTrue("User should be logged in!", browser
				.isElementPresent("xpath=//li[contains(text(),'Welcome')]"));
    }
}
