package se.test.MiTvAppTest;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MultiTouchAction;
import io.appium.java_client.TouchAction;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.gson.JsonParser;

public class AndroidApp {

	AppiumDriver driver=null;
	JsonParser parser=new JsonParser();

	@BeforeClass
	public void SetUp () throws Exception {
		String appdirectory = System.getProperty("user.dir")+"/src/test/resources";
		File appDir = new File(appdirectory);
		File app=new File(appDir, "miTv-debug.apk");
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("platformName", "Android");
		capabilities.setCapability("deviceName", "Google Nexus");
		capabilities.setCapability("appPackage", "com.mitv");
		capabilities.setCapability("app", app.getAbsolutePath());
		driver= new AppiumDriver(new URL("http://0.0.0.0:4723/wd/hub"), capabilities);
		implicitWait(driver,1,TimeUnit.MINUTES);

		Reporter.log("App launched",true);
		Thread.sleep(3000);
	}

	@AfterClass
	public void TearDown()
	{
		driver.quit();
	}

	@Test 
	public void test_0SplashScreen() throws InterruptedException {
		Reporter.log("SplashScreen",true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		WebElement firstScreen = driver.findElement(By.id("com.mitv:id/splash_screen_activity_info_text_tutorial"));
		Assert.assertTrue(firstScreen.isDisplayed());
		driver.findElement(By.id("com.mitv:id/button_splash_tutorial")).click();

		WebElement secondScreenHeader = driver.findElement(By.id("com.mitv:id/tutorial_header"));
		Assert.assertTrue(secondScreenHeader.isDisplayed());
		driver.findElement(By.id("com.mitv:id/button_tutorial_next")).click();

		//Swipe to move to the next tutorial screen
		((JavascriptExecutor)driver).executeScript("mobile: swipe", new HashMap() {{ put("touchCount", 1); put("startX", 538); put("startY", 1035); put("endX", 158); put("endY", 1024); put("duration", 0.5); }});
		((JavascriptExecutor)driver).executeScript("mobile: swipe", new HashMap() {{ put("touchCount", 1); put("startX", 538); put("startY", 1035); put("endX", 158); put("endY", 1024); put("duration", 0.5); }});
		((JavascriptExecutor)driver).executeScript("mobile: swipe", new HashMap() {{ put("touchCount", 1); put("startX", 538); put("startY", 1035); put("endX", 158); put("endY", 1024); put("duration", 0.5); }});
		driver.findElement(By.id("com.mitv:id/button_tutorial_next")).click();

		//click on start button, end of tutorial
		WebElement startButton = driver.findElement(By.id("com.mitv:id/start_primary_button_container"));
		Assert.assertTrue(startButton.isDisplayed());
		startButton.click();

		//verify the landing page after tutorial is finished
		WebElement dateIsVisible = driver.findElement(By.id("com.mitv:id/layout_actionbar_dropdown_list_date_header_name"));
		Assert.assertTrue(dateIsVisible.isDisplayed());
	}

	@Test(dataProvider="loginCredentials")
	public void test_1RegisterNewUser(String firstName, String lastName, String email, String password) throws InterruptedException {
		Reporter.log("Register new user test case",true);
		implicitWait(driver, 1, TimeUnit.MINUTES);
		profilePage();

		//check whether the user is logged in or not if logged in then display a message or else try to register new user.
		List<WebElement> loggedIn = driver.findElements(By.id("com.mitv:id/myprofile_person_container_signed_in"));
		if (loggedIn.size()>0) {
			Reporter.log("User is loggedIn", true);
		} else {
			WebElement signUp = driver.findElement(By.id("com.mitv:id/myprofile_signup_text"));
			Assert.assertTrue(signUp.isDisplayed());
			signUp.click();
			WebElement register = driver.findElement(By.id("com.mitv:id/signin_signup_email_title_tv"));
			Assert.assertTrue(register.isDisplayed());
			register.click();
			WebElement pageTitle = driver.findElement(By.id("android:id/action_bar_title"));
			Assert.assertEquals(pageTitle.getText(), "Regístrate");
			driver.findElement(By.id("com.mitv:id/signup_firstname_edittext")).sendKeys(firstName);
			driver.findElement(By.id("com.mitv:id/signup_lastname_edittext")).sendKeys(lastName);
			driver.findElement(By.id("com.mitv:id/signup_email_edittext")).sendKeys(email);
			driver.findElement(By.id("com.mitv:id/signup_password_edittext")).sendKeys(password);
			driver.findElement(By.id("com.mitv:id/mitv_sign_up_button_tv")).click();
			Thread.sleep(3000);

			//if the user is already registered then display a message and get back to login page or else verify the profile name and logout.
			List<WebElement> existingEmail = driver.findElements(By.id("com.mitv:id/signup_error_email_textview"));
			if(existingEmail.size()>0)
			{
				Reporter.log("Email id already registered", true);
				driver.findElement(By.name("Regístrate, Navigate up")).click();
				driver.findElement(By.name("Regístrate, Navigate up")).click();
			} else {
				WebElement profileName = driver.findElement(By.id("com.mitv:id/myprofile_name_tv"));
				Assert.assertEquals(profileName.getText(), "supriya"+ " " +"vivek");
				Reporter.log("New user registered successfully", true);
				testLogout();
			}
		}
	}

	@DataProvider
	public Object[][] loginCredentials() {
		return new Object[][] {{"supriya", "vivek", "supriya.v@indpro.se", "password"}};
	}

	@Test 
	public void test_2VerifyDates() throws InterruptedException {
		Reporter.log("verifyDates test case",true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		tvGuideTab();
		driver.findElement(By.name("Todos")).click();
		//verify the no. of days displayed in the dropdown
		WebElement daysAndDates = driver.findElement(By.id("android:id/action_bar_spinner"));
		daysAndDates.click();
		List<WebElement> dates = driver.findElements(By.id("com.mitv:id/layout_actionbar_dropdown_list_date_item_number"));
		Assert.assertTrue(dates.size()==7);
		Thread.sleep(2000);
		driver.findElement(By.id("com.mitv:id/layout_actionbar_dropdown_list_date_item_number")).click();
	}

	@Test 
	public void test_3MovieIcon() throws InterruptedException {
		Reporter.log("testMovieIcon test case",true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		tvGuideTab();
		//verify if the movie icon is displayed with the name in the first screen
		driver.findElement(By.name("Pelis")).click();
		List<WebElement> titleText = driver.findElements(By.id("com.mitv:id/element_poster_broadcast_title_tv"));
		for(int i=0; i<titleText.size(); i++) {
			String movieIcon = titleText.get(i).getText().substring(0, 2);
			Assert.assertEquals("΢ ", movieIcon);
		}
	}

	@Test 
	public void test_4SportsReminder() throws InterruptedException {
		Reporter.log("test sports test case",true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		boolean isBroadCast = false;
		profilePage();

		//click on reminder without login and verify if the reminder is empty or not
		WebElement reminder = driver.findElement(By.id("com.mitv:id/myprofile_reminders_container"));
		reminder.click();
		WebElement reminderText = driver.findElement(By.id("com.mitv:id/request_empty_details_tv"));
		Assert.assertEquals("No tienes recordatorios todavía", reminderText.getText());
		WebElement navigateBack = driver.findElement(By.name("Recordatorios, Navigate up"));
		navigateBack.click();

		//login, click on deportes and add reminder
		testLogin();
		tvGuideTab();
		WebElement deportes = driver.findElement(By.name("Deportes"));
		deportes.click();
		
		//select ligaPostobon sport
		List<WebElement> ligaPostobon = driver.findElements(By.id("com.mitv:id/banner_image_competition_frame_layout"));
		Assert.assertTrue(ligaPostobon.size()>0);
		ligaPostobon.get(0).click();
		Thread.sleep(2000);
		//select the first match and click on reminder, if reminder does not exist go back and select the next match or else assert reminder does not exist
		WebElement firstMatch = driver.findElement(By.xpath("//android.widget.ScrollView[1]/android.support.v4.view.ViewPager[1]/android.widget.RelativeLayout[1]"));
		firstMatch.click();
		List<WebElement> setReminder = driver.findElements(By.id("com.mitv:id/competition_event_row_reminder_view"));
		if(setReminder.size()>0) {
		setReminder.get(0).click();
		isBroadCast=true;
		Thread.sleep(3000);
		}

		// scroll and test how many share options are displayed 
		List<WebElement> shareIsDisplayed = driver.findElements(By.id("com.mitv:id/competition_element_social_buttons_share_button_iv"));
		Assert.assertTrue(shareIsDisplayed.size()>0);
		if (shareIsDisplayed.size()>0) {
			shareIsDisplayed.get(0).click();
		} else {
			WebElement scrollView = driver.findElement(By.id("com.mitv:id/event_page_scrollview"));
			scrollDown(scrollView, "Todos Contra Todos");
			Thread.sleep(3000);
			shareIsDisplayed.get(0).click();
		}
		WebElement alertTitle = driver.findElement(By.id("android:id/alertTitle"));
		Assert.assertEquals("Compartir con…", alertTitle.getText());
		List<WebElement> shareOptions = driver.findElements(By.id("android:id/text1"));
		Assert.assertTrue(shareOptions.size()==3);
		driver.navigate().back();

		//check if reminder is added or not, remove reminder and logout
		if(isBroadCast==true) {
		profilePage();
		reminder.click();
		List<WebElement> getReminder = driver.findElements(By.id("com.mitv:id/row_reminders_notification_iv"));
		Assert.assertTrue(getReminder.size()>0);
		getReminder.get(0).click();
		List<WebElement> noOption = driver.findElements(By.id("com.mitv:id/dialog_remove_notification_button_no"));
		Assert.assertTrue(noOption.size()>0);
		WebElement clickYes = driver.findElement(By.id("com.mitv:id/dialog_remove_notification_button_yes"));
		clickYes.click();
		navigateBack.click();
		WebElement reminderCount = driver.findElement(By.id("com.mitv:id/myprofile_reminders_count_tv"));
		Assert.assertEquals("(0)", reminderCount.getText());
		}
		testLogout();
	}

	@Test
	public void test_5verifyLikeTodos() throws InterruptedException {
		Reporter.log("Verify Todos like option", true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		profilePage();
		List<WebElement> login = driver.findElements(By.id("com.mitv:id/myprofile_login_container_text"));
		if(login.size()>0) {
			testLogin();
		}
		tvGuideTab();
		driver.findElement(By.name("Todos")).click();
		WebElement firstChannel = driver.findElement(By.id("com.mitv:id/tvguide_program_line_live"));
		firstChannel.click();
		Thread.sleep(3000);
		WebElement firstProgram = driver.findElement(By.id("com.mitv:id/channelpage_broadcast_details_title_tv"));
		firstProgram.click();
		WebElement broadcast = driver.findElement(By.id("com.mitv:id/broadcast_scroll"));
		scrollDown(broadcast, "Repeticiones de este programa");
		Thread.sleep(3000);
		WebElement like = driver.findElement(By.id("com.mitv:id/element_like_image_View"));
		like.click();	
		Thread.sleep(3000);
		verifyLikeExist();
		verifyLikeIsEmpty();
	} 

	@Test
	public void test_6verifyLikePelis() throws InterruptedException {
		Reporter.log("Verify pelis like option", true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		profilePage();
		List<WebElement> login = driver.findElements(By.id("com.mitv:id/myprofile_login_container_text"));
		if(login.size()>0) {
			testLogin();
		}
		tvGuideTab();
		driver.findElement(By.name("Pelis")).click();
		WebElement firstListedMovie = driver.findElement(By.id("com.mitv:id/element_poster_broadcast_container"));
		firstListedMovie.click();
		List<WebElement> like = driver.findElements(By.id("com.mitv:id/element_like_image_View"));
		if (like.size()>0) {
			like.get(0).click();
		} else {
		WebElement broadcast = driver.findElement(By.id("com.mitv:id/broadcast_scroll"));
		scrollDown(broadcast, "En el mismo tiempo");
		Thread.sleep(3000);
		WebElement clickOnLike = driver.findElement(By.id("com.mitv:id/element_like_image_View"));
		clickOnLike.click();
		}
		verifyLikeExist();
		verifyLikeIsEmpty();		
	}

	@Test
	public void test_7verifyLikeSeries() throws InterruptedException {
		Reporter.log("Verify Series like option", true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		profilePage();
		List<WebElement> login = driver.findElements(By.id("com.mitv:id/myprofile_login_container_text"));
		if(login.size()>0) {
			testLogin();
		}
		tvGuideTab();
		driver.findElement(By.name("Series")).click();
		WebElement firstListedSeries = driver.findElement(By.id("com.mitv:id/element_poster_broadcast_container"));
		firstListedSeries.click();
		WebElement broadcast = driver.findElement(By.id("com.mitv:id/broadcast_scroll"));
		scrollDown(broadcast, "Repeticiones de este programa");
		Thread.sleep(3000);
		WebElement like = driver.findElement(By.id("com.mitv:id/element_like_image_View"));
		like.click();
		Thread.sleep(3000);
		verifyLikeExist();
		verifyLikeIsEmpty();
	}

	@Test
	public void test_8verifyLikeDeportes() throws InterruptedException {
		Reporter.log("Verify Deportes like option", true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		profilePage();
		List<WebElement> login = driver.findElements(By.id("com.mitv:id/myprofile_login_container_text"));
		if(login.size()>0) {
			testLogin();
		}
		tvGuideTab();
		driver.findElement(By.name("Deportes")).click();
		WebElement firstListedSport = driver.findElement(By.id("com.mitv:id/element_poster_broadcast_container"));
		firstListedSport.click();
		WebElement broadcast = driver.findElement(By.id("com.mitv:id/broadcast_scroll"));
		scrollDown(broadcast, "Repeticiones de este programa");
		Thread.sleep(3000);
		WebElement like = driver.findElement(By.id("com.mitv:id/element_like_image_View"));
		like.click();
		Thread.sleep(3000);
		verifyLikeExist();
		verifyLikeIsEmpty();
	}

	@Test
	public void test_9verifyLikeNiños() throws InterruptedException {
		Reporter.log("Verify Niños like option", true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		profilePage();
		List<WebElement> login = driver.findElements(By.id("com.mitv:id/myprofile_login_container_text"));
		if(login.size()>0) {
			testLogin();
		}
		tvGuideTab();
		WebElement Niños = driver.findElement(By.name("Niños"));
		Niños.click();
		List<WebElement> NiñosListView = driver.findElements(By.id("com.mitv:id/fragment_tvguide_type_tag_listview"));
		if (NiñosListView.get(0).getText().isEmpty()) {
			Reporter.log("Niños tab isEmpty");
			Assert.assertFalse(NiñosListView.isEmpty());
			testLogout();
		} else {
		WebElement firstListedCartoon = driver.findElement(By.id("com.mitv:id/element_poster_broadcast_container"));
		firstListedCartoon.click();
		WebElement broadcast = driver.findElement(By.id("com.mitv:id/broadcast_scroll"));
		scrollDown(broadcast, "Repeticiones de este programa");
		Thread.sleep(3000);
		WebElement like = driver.findElement(By.id("com.mitv:id/element_like_image_View"));
		like.click();
		Thread.sleep(3000);
		verifyLikeExist();
		verifyLikeIsEmpty();
		}
	}

	@Test
	public void test_10DragAndDrop() throws InterruptedException {
		Reporter.log("Drag drop channels test case",true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		profilePage();
		testLogin();

		//search for AXN channel and add to the list of channels, check if the channel is already added or not
		driver.findElement(By.id("com.mitv:id/myprofile_channels_title_tv")).click();
		driver.findElement(By.id("com.mitv:id/tvchannels_user_edit_search_text")).sendKeys("AXN");
		List<WebElement> channelExists = driver.findElements(By.name("Remover"));
		if(channelExists.size()>0) {
			driver.findElement(By.xpath("//android.widget.HorizontalScrollView[1]/android.widget.TextView[2]")).click();
		} else  {
			List<WebElement> addChannel = driver.findElements(By.name("+  Agregar"));
			if (addChannel.size()>0) 
				driver.findElement(By.id("com.mitv:id/row_mychannels_channel_button_tv")).click();
				driver.findElement(By.xpath("//android.widget.HorizontalScrollView[1]/android.widget.TextView[2]")).click();
		}

		//Drag and drop AXN channel from last position to last 4th position
		WebElement list = driver.findElement(By.id("com.mitv:id/tvchannels_user_reorder_list"));
		scrollDown(list, "AXN");
		WebElement drag = driver.findElement(By.xpath("//android.support.v4.view.ViewPager[1]/android.widget.ListView[1]/android.view.View[6]/android.widget.TextView[2]"));
		WebElement drop = driver.findElement(By.xpath("//android.support.v4.view.ViewPager[1]/android.widget.ListView[1]/android.view.View[3]/android.widget.TextView[2]"));
		TouchAction dragNDrop=new TouchAction(driver).longPress(drag).moveTo(drop).release();
		dragNDrop.perform();
		Thread.sleep(3000);

		//Remove AXN channel from the group and logout
		driver.findElement(By.xpath("//android.widget.HorizontalScrollView[1]/android.widget.TextView[1]")).click();
		driver.findElement(By.id("com.mitv:id/tvchannels_user_edit_search_text")).sendKeys("AXN");
		driver.findElement(By.id("com.mitv:id/row_mychannels_channel_button")).click();
		driver.findElement(By.name("Organiza tus canales, Navigate up")).click();
		testLogout();
	}

	@Test
	public void test_11Activity() throws InterruptedException {
		Reporter.log("test activity test case",true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		profilePage();
		List<WebElement> signedIn = driver.findElements(By.id("com.mitv:id/myprofile_person_container_signed_in"));
		if(signedIn.size()>0) {
			testLogout();
		}

		//Click on activity tab before login and check for title
		WebElement activityTab = driver.findElement(By.id("com.mitv:id/tab_activity"));
		activityTab.click();
		List<WebElement> activityWithoutLogin = driver.findElements(By.id("com.mitv:id/activity_not_logged_in_title_details"));
		Assert.assertTrue(activityWithoutLogin.size()>0);

		//login and check landing page
		WebElement loginLink = driver.findElement(By.id("com.mitv:id/mitvlogin_forgot_password_text"));
		loginLink.click();
		driver.findElement(By.id("com.mitv:id/mitvlogin_login_email_edittext")).sendKeys("supriya.v@indpro.se");
		driver.findElement(By.id("com.mitv:id/mitvlogin_login_password_edittext")).sendKeys("password");
		WebElement scrollableViewForLogin = driver.findElement(By.xpath("//android.widget.ScrollView[1]"));
		String canScroll = scrollableViewForLogin.getAttribute("scrollable");
		if (canScroll.equals("true")) {
			driver.hideKeyboard("Done");
		}
		driver.findElement(By.id("com.mitv:id/mitvlogin_login_button")).click();
		Thread.sleep(5000);
		//		WebElement titleBar = driver.findElement(By.id("android:id/action_bar_title"));
		//		Assert.assertEquals("Actividad", titleBar.getText());

		//verify activity info click on comment and logout
		List<WebElement> activityInfo = driver.findElements(By.id("com.mitv:id/feed_top_info_text"));
		Assert.assertTrue(activityInfo.size()>0);
		driver.findElement(By.id("com.mitv:id/feed_top_info_button")).click();
		WebElement clickOnFirstProgHeader = driver.findElement(By.id("com.mitv:id/block_feed_liked_title_tv"));
		clickOnFirstProgHeader.click();
		WebElement scrollableView = driver.findElement(By.id("com.mitv:id/broadcast_scroll"));
		scrollDown(scrollableView, "Comentarios");
		List<WebElement> commentLink = driver.findElements(By.id("com.mitv:id/disqus_comments_header_text"));
		Assert.assertTrue(commentLink.size()>0);
		commentLink.get(0).click();
		driver.findElement(By.name("Comentarios Link")).click();
		driver.findElement(By.className("android.widget.EditText")).click();
		Thread.sleep(2000);
		driver.findElement(By.className("android.widget.EditText")).sendKeys("comment");
		Thread.sleep(2000);
		driver.findElement(By.name("Programa, Navigate up")).click();
		verifyShareOptions();
		testLogout();
	}

	@Test 
	public void test_12ResetPassword() throws InterruptedException {
		Reporter.log("testResetPassword test case",true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		profilePage();
		List<WebElement> signedIn = driver.findElements(By.id("com.mitv:id/myprofile_person_container_signed_in"));
		if(signedIn.size()>0) {
			testLogout();
		}

		//check login link is displayed and click on forgot password, enter email id and click on reset password
		List<WebElement> login = driver.findElements(By.id("com.mitv:id/myprofile_login_container_text"));
		Assert.assertTrue(login.size()>0);
		login.get(0).click();
		driver.findElement(By.id("com.mitv:id/mitvlogin_forgot_password_text")).click();
		List<WebElement> resetPassword = driver.findElements(By.id("com.mitv:id/resetpassword_email_edittext"));
		Assert.assertTrue(resetPassword.size()>0);
		resetPassword.get(0).sendKeys("supriya.v@indpro.se");
		driver.findElement(By.id("com.mitv:id/mitv_reset_password_button")).click();
		Thread.sleep(5000);

		//check reset password link 
		//		List<WebElement> resetPasswordLinkSent = driver.findElements(By.id("com.mitv:id/mitv_reset_password_button_tv"));
		//		Assert.assertTrue(resetPasswordLinkSent.size()>0);
		driver.findElement(By.name("Cambia tu contraseña, Navigate up")).click();
		driver.findElement(By.name("Inicia sesión, Navigate up")).click();
	}

	@Test
	public void test_13homeActivity() throws InterruptedException {
		tvGuideTab();
		implicitWait(driver, 60, TimeUnit.SECONDS);

		//swipe to check whether all the tabs are displayed or not
		driver.findElement(By.name("Todos")).click();
		((JavascriptExecutor)driver).executeScript("mobile: swipe", new HashMap() {{ put("touchCount", 1); put("startX", 538); put("startY", 1035); put("endX", 158); put("endY", 1024); put("duration", 0.5); }});
		Thread.sleep(2000);
		((JavascriptExecutor)driver).executeScript("mobile: swipe", new HashMap() {{ put("touchCount", 1); put("startX", 538); put("startY", 1035); put("endX", 158); put("endY", 1024); put("duration", 0.5); }});
		Thread.sleep(2000);
		((JavascriptExecutor)driver).executeScript("mobile: swipe", new HashMap() {{ put("touchCount", 1); put("startX", 538); put("startY", 1035); put("endX", 158); put("endY", 1024); put("duration", 0.5); }});
		Thread.sleep(2000);
		((JavascriptExecutor)driver).executeScript("mobile: swipe", new HashMap() {{ put("touchCount", 1); put("startX", 538); put("startY", 1035); put("endX", 158); put("endY", 1024); put("duration", 0.5); }});
		Thread.sleep(2000);
		((JavascriptExecutor)driver).executeScript("mobile: swipe", new HashMap() {{ put("touchCount", 1); put("startX", 538); put("startY", 1035); put("endX", 158); put("endY", 1024); put("duration", 0.5); }});
		Thread.sleep(2000);

		//click to check whether all the tabs are displayed or not
		driver.findElement(By.name("Todos")).click();
		List<WebElement> channelsList = driver.findElements(By.id("com.mitv:id/item_container"));
		Assert.assertTrue(channelsList.size()>0);
		driver.findElement(By.name("Pelis")).click();
		List<WebElement> movieList = driver.findElements(By.id("com.mitv:id/home_pager"));
		Assert.assertTrue(movieList.size()>0);
		driver.findElement(By.name("Series")).click();
		List<WebElement> seriesList = driver.findElements(By.id("com.mitv:id/home_pager"));
		Assert.assertTrue(seriesList.size()>0);
		driver.findElement(By.name("Deportes")).click();
		List<WebElement> sportsList = driver.findElements(By.id("com.mitv:id/home_pager"));
		Assert.assertTrue(sportsList.size()>0);
		driver.findElement(By.name("Niños")).click();
		List<WebElement> cartoonList = driver.findElements(By.id("com.mitv:id/fragment_tvguide_type_tag_listview"));
		Assert.assertTrue(cartoonList.size()>0);
		driver.findElement(By.name("Todos")).click();
	}

	public void tvGuideTab() {
		WebElement tvGuideTab = driver.findElement(By.id("com.mitv:id/tab_tv_guide"));
		tvGuideTab.click();
	}

	public void profilePage() {
		WebElement myProfile = driver.findElement(By.id("com.mitv:id/tab_me"));
		Assert.assertTrue(myProfile.isDisplayed());
		myProfile.click();
	}

	public void testLogin() throws InterruptedException { 
		Reporter.log("login test case",true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		profilePage();
		List<WebElement> signedIn = driver.findElements(By.id("com.mitv:id/myprofile_person_container_signed_in"));
		if(signedIn.size()>0) {
			Reporter.log("User is already logged in", true);
		} else {

			//click on login and check if facebook button is displayed
			List<WebElement> login = driver.findElements(By.id("com.mitv:id/myprofile_login_container_text"));
			Assert.assertTrue(login.get(0).isDisplayed());
			login.get(0).click();
			WebElement facebookLink = driver.findElement(By.id("com.mitv:id/mitvlogin_facebook_container"));
			Assert.assertTrue(facebookLink.isDisplayed());

			//enter username, password and login
			driver.findElement(By.id("com.mitv:id/mitvlogin_login_email_edittext")).sendKeys("supriya.v@indpro.se");
			WebElement pass = driver.findElement(By.id("com.mitv:id/mitvlogin_login_password_edittext"));
			pass.sendKeys("password");
			Thread.sleep(3000);
			WebElement scrollableView = driver.findElement(By.xpath("//android.widget.ScrollView[1]"));
			String canScroll = scrollableView.getAttribute("scrollable");
			if (canScroll.equals("true")) {
				driver.hideKeyboard("Done");
			}
			WebElement loginButton = driver.findElement(By.id("com.mitv:id/mitvlogin_login_button"));
			loginButton.click();
			Thread.sleep(5000);
		}
	}

	public void testLogout() {
		Reporter.log("logout test case",true);
		implicitWait(driver, 60, TimeUnit.SECONDS);

		//verify login and click on logout
		profilePage();
		WebElement signedIn = driver.findElement(By.id("com.mitv:id/myprofile_person_container_signed_in"));
		Assert.assertTrue(signedIn.isDisplayed());
		driver.findElement(By.id("com.mitv:id/element_tab_text_me")).click();
		driver.findElement(By.id("com.mitv:id/myprofile_logout_container_text")).click();
		List<WebElement> loginLink = driver.findElements(By.id("com.mitv:id/myprofile_login_container_text"));
		Assert.assertTrue(loginLink.size()>0);
	}

	public void verifyShareOptions() {
		implicitWait(driver, 60, TimeUnit.SECONDS);

		//check if all the 3 options are displayed
		List<WebElement> like = driver.findElements(By.id("com.mitv:id/element_social_buttons_like_view"));
		Assert.assertTrue(like.size()>0);
		List<WebElement> share = driver.findElements(By.id("com.mitv:id/element_social_buttons_share_button_container"));
		Assert.assertTrue(share.size()>0);
		List<WebElement> reminder = driver.findElements(By.id("com.mitv:id/element_social_buttons_reminder"));
		Assert.assertTrue(reminder.size()>0);
	}

	public void verifyLikeIsEmpty() throws InterruptedException {
		implicitWait(driver, 60, TimeUnit.SECONDS);
		Reporter.log("verify like empty", true);
		profilePage();
		List<WebElement> login = driver.findElements(By.id("com.mitv:id/myprofile_login_container_text"));
		if(login.size()>0) {
			testLogin();
		}

		//verify empty like screen
		WebElement likeCount = driver.findElement(By.id("com.mitv:id/myprofile_likes_count_tv"));
		Assert.assertEquals("(0)", likeCount.getText());
		testLogout();
	}

	public void clickBack() {
		WebElement clickBack = driver.findElement(By.name("Me gustan, Navigate up"));
		clickBack.click();
	}

	public void verifyLikeExist() throws InterruptedException {
		Reporter.log("verify like exists", true);
		implicitWait(driver, 60, TimeUnit.SECONDS);
		driver.findElement(By.id("com.mitv:id/tab_me")).click();
		driver.findElement(By.id("com.mitv:id/myprofile_likes_title_tv")).click();
		List<WebElement> likedProg = driver.findElements(By.id("com.mitv:id/row_likes_button_tv"));
		Assert.assertTrue(likedProg.size()>0);
		likedProg.get(0).click();
		List<WebElement> no = driver.findElements(By.id("com.mitv:id/dialog_remove_notification_button_no"));
		Assert.assertTrue(no.size()>0);
		driver.findElement(By.id("com.mitv:id/dialog_remove_notification_button_yes")).click();
		clickBack();
		testLogout();
	}

	public void programCount() throws InterruptedException {
		int count;
		driver.findElement(By.id("com.mitv:id/element_tab_text_guide")).click();
		driver.findElement(By.name("Todos")).click();
		Thread.sleep(3000);
		List<WebElement> progList = driver.findElements(By.id("com.mitv:id/tvguide_program_line_live"));
		count=progList.size();	
		String lastlink="Agrega canales a tu programación";		
		WebElement scrollTodos = driver.findElement(By.id("com.mitv:id/tvguide_table_listview"));
		scrollDown(scrollTodos, lastlink);
		count=count+progList.size();
		Reporter.log("Program count=" + count,true);
	}

	public void scrollDown(WebElement scroll, String tillLastLink) throws InterruptedException {
		implicitWait(driver, 1, TimeUnit.MINUTES);
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			HashMap<String, String> scrollObject = new HashMap<String, String>();
			scrollObject.put("direction", "down"); 
			scrollObject.put("text", tillLastLink);
			scrollObject.put("element",( (RemoteWebElement) scroll).getId());
			js.executeScript("mobile: scrollTo", scrollObject);
		} catch (Exception e) {
			System.out.println(e);
			Reporter.log("Scroll bar error", true);
		}
	}

	public void multiGestureSingleActionTest() throws InterruptedException {
		MultiTouchAction multiTouch = new MultiTouchAction(driver);
		TouchAction action0 = new TouchAction(driver).tap(100,300);
		multiTouch.add(action0).perform();
	}

	public void implicitWait(WebDriver driver, long i, TimeUnit minutes) {
		driver.manage().timeouts().implicitlyWait(i, minutes);	
	}

	public void explicitlyWait(WebDriver driver, long i, WebElement element) {
		WebDriverWait wait=new WebDriverWait(driver,i);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public String randomEmailId()
	{
		String alphabets="0123456789abcdefghijklmnopqrstuvwxyz";
		int n=alphabets.length();
		StringBuilder email=new StringBuilder();
		Random r=new Random();

		for(int i=0; i<10; i++) {
			email.append(alphabets.charAt(r.nextInt(n)));
		}

		email.append("Test@delete.com");
		return email.toString();
	}
}
