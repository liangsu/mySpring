package spring.ls.io;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import spring.ls.bean.BeanDefinition;
import spring.ls.bean.BeanDefinitionReader;
import spring.ls.core.env.Environment;
import spring.ls.core.io.EncodedResource;
import spring.ls.core.io.Resource;
import spring.ls.util.ClassUtils;
import spring.ls.util.StringUtils;

public class XmlBeanDefinitionReader implements BeanDefinitionReader{

	private Environment environment;
	
	public XmlBeanDefinitionReader(Environment environment) {
		this.environment = environment;
	}
	
//	private Class<?>[] getClasses(Properties prop){
//		List<Class<?>> list = new ArrayList<Class>();
//		Enumeration<Object> enumeration = prop.elements();
//		String clazzName = null;
//		Class<?> clazz = null;
//		while(enumeration.hasMoreElements()){
//			clazzName = (String) enumeration.nextElement();
//			try {
//				list.add(ClassUtils.forName(clazzName,ClassUtils.getDefaultClassLoader()));
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
//		}
//		int size = list.size();
//		Class<?>[] clazzes = list.toArray(new Class[size]);
//		
//		return clazzes;
//	}
	
//	private BeanDefinition[] getBeanDefinitions(Properties prop) {
//		//1.通过配置文件获取要管理的clazz
//		Class[] clazzes = this.getClasses(prop);
//		BeanDefinition[] beanDefinitions = new BeanDefinition[clazzes.length];
//		BeanDefinition beanDefinition = null;
//		for (int i = 0; i < clazzes.length; i++) {
//			beanDefinition = new BeanDefinition();
//			beanDefinition.setBeanClass(clazzes[i]);
//			beanDefinition.setName(getBeanName(clazzes[i]));
//			beanDefinition.setScope("prototype");
//			beanDefinitions[i] = beanDefinition;
//		}
//		return beanDefinitions;
//	}

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
		
		EncodedResource encodedResource = new EncodedResource(resource,"utf-8");
		InputStream is = encodedResource.getResource().getInputStream();
		InputSource inputSource = new InputSource(is);
		inputSource.setEncoding(encodedResource.getEncoding());
		LoadBeanDefinitions(inputSource);
//		Properties prop = new Properties();
//		try {
//			prop.load(resource.getInputStream());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		BeanDefinition[] beanDefinitions = getBeanDefinitions(prop);
//		for (BeanDefinition beanDefinition : beanDefinitions) {
//			environment.registerBeanDefinition(beanDefinition);
//		}
	}
	
	public void LoadBeanDefinitions(InputSource inputSource) throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(inputSource);
		
		Element root = document.getDocumentElement();
		NodeList nodeList = root.getChildNodes();
		for(int i  = 0; i < nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			if(node instanceof Element){
				Element ele = (Element) node;
				parseBeanDefinition(ele);
			}
		}
	}

	private void parseBeanDefinition(Element ele) throws ClassNotFoundException {
		String id = ele.getAttribute("id");
		String name = ele.getAttribute("name");
		String classpath = ele.getAttribute("class");
		String scope = ele.getAttribute("scope");
		
		if(StringUtils.hasLength(id)){
			name = id;
		}
		
		try {
			BeanDefinition beanDefinition = new BeanDefinition();
			beanDefinition.setBeanClass(ClassUtils.forName(classpath, ClassUtils.getDefaultClassLoader()));
			beanDefinition.setName(name);
			beanDefinition.setScope(scope);
			environment.registerBeanDefinition(beanDefinition);
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException(ele.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
