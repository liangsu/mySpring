package spring.ls.annotation;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import spring.ls.beans.factory.config.BeanDefinition;
import spring.ls.beans.factory.config.BeanDefinitionHolder;
import spring.ls.beans.factory.config.GenericBeanDefinition;
import spring.ls.beans.factory.support.BeanDefinitionRegistry;
import spring.ls.beans.factory.support.BeanDefinitionRegistryUtils;
import spring.ls.util.ClassUtils;
import spring.ls.util.PathUtil;

/**
 * 通过注解扫描bean
 * @author lenovo
 *
 */
public class ScanfAnnotation{
	private BeanDefinitionRegistry beanDefinitionRegistry;
	private String basePackage;
	private String filePath;
	private List<BeanDefinitionHolder> beanDefinitionHolders = new ArrayList<BeanDefinitionHolder>();
	private ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
	
	public ScanfAnnotation(BeanDefinitionRegistry beanDefinitionRegistry,String basePath){
		this.beanDefinitionRegistry = beanDefinitionRegistry;
		this.basePackage = basePath;
		filePath = PathUtil.getProjectPath() + File.separator + basePath.replace(".", File.separator);
		scanfPackage(filePath,basePackage);
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
		BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(getBeanName(component,clazz), null);
		BeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(clazz);
		beanDefinition.setScope( getScope(clazz) );
		beanDefinitionHolder.setBeanDefinition(beanDefinition);
		beanDefinitionHolders.add(beanDefinitionHolder);
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

	public void register() throws Exception {
		for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
			BeanDefinitionRegistryUtils.registerBeanDefinition(beanDefinitionHolder,beanDefinitionRegistry);
		}
	}
	
//	public static void main(String[] args) {
//	ScanfAnnotation sa = new ScanfAnnotation(null,"spring.ls");
//	BeanDefinition[] beanDefinitionsHolders = sa.getBeanDefinitions();
//	for (BeanDefinition beanDefinitionsHolder : beanDefinitionsHolders) {
//		System.out.println("name:"+beanDefinitionsHolder.getName());
//		System.out.println("clazz:"+beanDefinitionsHolder.getBeanClass());
//		System.out.println("scope:"+beanDefinitionsHolder.getScope());
//	}
//}

}
