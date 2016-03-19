package spring.ls.annotation;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import spring.ls.bean.BeanDefinition;
import spring.ls.util.ClassUtils;
import spring.ls.util.PathUtil;

/**
 * 通过注解扫描bean
 * @author lenovo
 *
 */
public class ScanfAnnotation {
	private String basePackage;
	private String filePath;
	private List<BeanDefinition> beanDefinitionsHoldersList = new ArrayList<BeanDefinition>();
	private ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
	
	public ScanfAnnotation(String basePath){
		this.basePackage = basePath;
		filePath = PathUtil.getProjectPath() + File.separator + basePath.replace(".", File.separator);
		scanfPackage(filePath,basePackage);
	}
	
	/**
	 * 获取bean的定义
	 * @return
	 */
	public BeanDefinition[] getBeanDefinitions(){
		int size = beanDefinitionsHoldersList.size();
		BeanDefinition[] beandefinitionsHolders = beanDefinitionsHoldersList.toArray(new BeanDefinition[size]);
		return beandefinitionsHolders;
	}
	
	/**
	 * 扫描包
	 * @param filePath
	 * @param basePackage
	 */
	private void scanfPackage(String filePath,String basePackage){
		File baseFile = new File(filePath);
		if(baseFile.isDirectory()){
			File[] children = baseFile.listFiles();
			for (File child : children) {
				if(child.isFile()){
					handleFile(basePackage + "." + child.getName());
				}else if(child.isDirectory()){
					scanfPackage(filePath+File.separator+child.getName(), basePackage+"."+child.getName());
				}
			}
		}
	}
	
	/**
	 * 处理扫描的文件
	 * @param classPath
	 */
	private void handleFile(String classPath){
		try {
			String className = classPath.substring(0, classPath.lastIndexOf(".class"));
			Class<?> clazz = ClassUtils.forName(className, classLoader);
			Annotation annotation = clazz.getAnnotation(Component.class);
			if(annotation != null){
				addBeanDefinition((Component) annotation,clazz);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void addBeanDefinition(Component component,Class<?> clazz){
		BeanDefinition beanDefinition = new BeanDefinition();
		beanDefinition.setBeanClass(clazz);
		beanDefinition.setName( getBeanName(component,clazz) );
		beanDefinition.setScope( getScope(clazz) );
		beanDefinitionsHoldersList.add(beanDefinition);
	}
	
	private String getScope(Class<?> clazz) {
		String scope = "";
		Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
		if(scopeAnnotation != null){
			scope = scopeAnnotation.value();
		}
		if(scope.equals("")){
			scope = "prototype";
		}
		return scope;
	}
	
	private String getBeanName(Component component,Class clazz) {
		String beanName = component.value();
		if(beanName == null || beanName.length() == 0){
			beanName = clazz.getSimpleName();
			beanName = beanName.toLowerCase().charAt(0) + beanName.substring(1);
		}
		return beanName;
	}

	public static void main(String[] args) {
		ScanfAnnotation sa = new ScanfAnnotation("spring.ls");
		BeanDefinition[] beanDefinitionsHolders = sa.getBeanDefinitions();
		for (BeanDefinition beanDefinitionsHolder : beanDefinitionsHolders) {
			System.out.println("name:"+beanDefinitionsHolder.getName());
			System.out.println("clazz:"+beanDefinitionsHolder.getBeanClass());
			System.out.println("scope:"+beanDefinitionsHolder.getScope());
		}
	}
}
