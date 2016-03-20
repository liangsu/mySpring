package spring.ls.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import spring.ls.bean.BeanDefinition;
import spring.ls.bean.BeanDefinitionReader;
import spring.ls.core.env.Environment;
import spring.ls.core.io.Resource;
import spring.ls.util.ClassUtils;

public class XmlBeanDefinitionReader implements BeanDefinitionReader{

	private Environment environment;
	
	public XmlBeanDefinitionReader(Environment environment) {
		this.environment = environment;
	}
	
	private Class[] getClasses(Properties prop){
		List<Class> list = new ArrayList<Class>();
		Enumeration<Object> enumeration = prop.elements();
		String clazzName = null;
		Class clazz = null;
		while(enumeration.hasMoreElements()){
			clazzName = (String) enumeration.nextElement();
			try {
				list.add(ClassUtils.forName(clazzName,ClassUtils.getDefaultClassLoader()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		int size = list.size();
		Class[] clazzes = list.toArray(new Class[size]);
		
		return clazzes;
	}
	
	private BeanDefinition[] getBeanDefinitions(Properties prop) {
		//1.通过配置文件获取要管理的clazz
		Class[] clazzes = this.getClasses(prop);
		BeanDefinition[] beanDefinitions = new BeanDefinition[clazzes.length];
		BeanDefinition beanDefinition = null;
		for (int i = 0; i < clazzes.length; i++) {
			beanDefinition = new BeanDefinition();
			beanDefinition.setBeanClass(clazzes[i]);
			beanDefinition.setName(getBeanName(clazzes[i]));
			beanDefinition.setScope("prototype");
			beanDefinitions[i] = beanDefinition;
		}
		return beanDefinitions;
	}

	private String getBeanName(Class<?> clazz){
		String beanName = clazz.getSimpleName();
		beanName = beanName.toLowerCase().charAt(0) + beanName.substring(1);
		return beanName;
	}

	@Override
	public void LoadBeanDefinitions(Resource resource) throws Exception {
		if(!resource.exists()){
			throw new FileNotFoundException(resource.getDescription());
		}
		
		Properties prop = new Properties();
		try {
			prop.load(resource.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		BeanDefinition[] beanDefinitions = getBeanDefinitions(prop);
		for (BeanDefinition beanDefinition : beanDefinitions) {
			environment.registerBeanDefinition(beanDefinition);
		}
	}

}
