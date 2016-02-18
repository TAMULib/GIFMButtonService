package edu.tamu.app.model.impl;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
public class UserTest {
	
	@Autowired
	private AppUserRepo userRepo;
	
	@Before
	public void setUp() {
		userRepo.deleteAll();
	}
	
	@Test
	public void testMethod() {
		
		// Test create user
		AppUser testUser1 = userRepo.create(Long.parseLong("123456789"));		
		AppUser assertUser = userRepo.findByUin(Long.parseLong("123456789"));		
		Assert.assertEquals("Test User1 was not added.", testUser1.getUin(), assertUser.getUin());
	
		// Test disallow duplicate UINs
		userRepo.create(Long.parseLong("123456789"));		
		List<AppUser> allUsers = (List<AppUser>) userRepo.findAll();		
		Assert.assertEquals("Duplicate UIN found.", 1, allUsers.size());
				
		// Test delete user
		userRepo.delete(testUser1);		
		allUsers = (List<AppUser>) userRepo.findAll();		
		Assert.assertEquals("Test User1 was not removed.", 0, allUsers.size());
		
	}
	
	@After
	public void cleanUp() {
		userRepo.deleteAll();
	}
	
}
