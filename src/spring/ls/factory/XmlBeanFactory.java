package spring.ls.factory;

import spring.ls.io.PropertiesReader;

public class XmlBeanFactory extends AbstractBeanFactory{

	@Override
	public void loadBeans() {
		//1.通过配置文件获取要管理的clazz
		Class[] clazzes = PropertiesReader.getClasses();
		for (Class clazz : clazzes) {
			try {
				super.addBean(clazz,super.getScope());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
