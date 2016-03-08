package edu.tamu.app.config;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

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
	
	private List<GetItForMeButton> registeredButtons = new ArrayList<GetItForMeButton>();
	
	

	GIFMButtonConfig() {};
	
	public void registerButtons() {
		String buttonsPackage = environment.getProperty("buttonsPackage");
		String[] activeButtons = environment.getProperty("activeButtons",String[].class);
		System.out.println("\n\n\nCONFIGURING");
		for (String button:activeButtons) {
			System.out.println(button);
		}

		for (String activeButton:activeButtons) {
			try {
				String[] locationCodes = environment.getProperty(activeButton+".locationCodes").split(";");
				String[] itemTypeCodes = environment.getProperty(activeButton+".itemTypeCodes",String[].class);
				Integer[] itemStatusCodes = environment.getProperty(activeButton+".itemStatusCodes",Integer[].class);
				String linkText = environment.getProperty(activeButton+".linkText");
				GetItForMeButton c = (GetItForMeButton) Class.forName(buttonsPackage+"."+activeButton).getDeclaredConstructor(String[].class,String[].class,Integer[].class,String.class).newInstance(new Object[]{locationCodes,itemTypeCodes,itemStatusCodes, linkText});
				this.registeredButtons.add(c);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
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
	}

	@Bean(name="registeredButtons")
	public List<GetItForMeButton> getRegisteredButtons() {
		this.registerButtons();
		return new ArrayList<GetItForMeButton>(this.registeredButtons);
	}
}
