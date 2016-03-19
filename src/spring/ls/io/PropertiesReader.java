package spring.ls.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class PropertiesReader {

	private static Properties prop = null;
	
	static{
		prop = new Properties();
		try {
			prop.load(PropertiesReader.class.getClassLoader().getResourceAsStream("beans.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Class[] getClasses(){
		List<Class> list = new ArrayList<Class>();
		
		Enumeration<Object> enumeration = prop.elements();
		String clazzName = null;
		Class clazz = null;
		while(enumeration.hasMoreElements()){
			clazzName = (String) enumeration.nextElement();
			try {
				list.add(PropertiesReader.class.getClassLoader().loadClass(clazzName));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		int size = list.size();
		Class[] clazzes = list.toArray(new Class[size]);
		
		return clazzes;
	}
	
	
}
