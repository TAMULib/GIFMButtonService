package edu.tamu.app.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import edu.tamu.app.model.GetItForMeButton;

@Configuration
@PropertySource("classpath:config/buttons.properties")
public class GIFMButtonConfig {
	
	@Resource
	Environment environment;
	
	private List<GetItForMeButton> registeredButtons;

	@Resource
	private ApplicationContext applicationContext;
	
	

	GIFMButtonConfig() {};
	
	@PostConstruct
	public void registerButtons() {
		String buttonsPackage = environment.getProperty("buttonsPackage");
		String[] activeButtons = environment.getProperty("activeButtons",String[].class);
		System.out.println("\n\n\nCONFIGURING");
		AutowireCapableBeanFactory bf = this.applicationContext.getAutowireCapableBeanFactory();

		for (String activeButton:activeButtons) {
			System.out.println("Registering: "+activeButton);
			try {
				String rawLocationCodes = environment.getProperty(activeButton+".locationCodes");
				String[] itemTypeCodes = environment.getProperty(activeButton+".itemTypeCodes",String[].class);
				Integer[] itemStatusCodes = environment.getProperty(activeButton+".itemStatusCodes",Integer[].class);
				String linkText = environment.getProperty(activeButton+".linkText");
				GetItForMeButton c = (GetItForMeButton) Class.forName(buttonsPackage+"."+activeButton).newInstance();
				if (rawLocationCodes != null) {
					c.setLocationCodes(rawLocationCodes.split(";"));
				}
				if (itemTypeCodes != null) {
					c.setItemTypeCodes(itemTypeCodes);
				}
				if (itemStatusCodes != null) {
					c.setItemStatusCodes(itemStatusCodes);
				}
				if (linkText != null) {
					c.setLinkText(linkText);
				}
				System.out.println("Registered: "+activeButton);
//				GetItForMeButton newBean = bf.createBean(c.getClass());
				GetItForMeButton newBean = (GetItForMeButton) bf.autowire(c.getClass(), AutowireCapableBeanFactory.AUTOWIRE_BY_NAME,true);
				this.registeredButtons.add((GetItForMeButton) bf.initializeBean(newBean, activeButton));
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		bf.autowire(ArrayList.class, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME,true);
		bf.initializeBean(this.registeredButtons, "registeredButtons");			
	}
/*
	@Bean(name="registeredButtons")
	public List<GetItForMeButton> getRegisteredButtons() {
		System.out.println("Button Count: "+this.registeredButtons.size());
		return new ArrayList<GetItForMeButton>(this.registeredButtons);
	}
	*/
}
