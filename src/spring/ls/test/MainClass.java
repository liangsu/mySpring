package spring.ls.test;

import spring.ls.beans.Dog;
import spring.ls.beans.factory.ApplicationContext;
import spring.ls.beans.factory.BeanFactory;
import spring.ls.beans.factory.XmlBeanFactory;
import spring.ls.core.io.ClassPathResource;

public class MainClass {

	public static void main(String[] args) throws Exception {
//		BeanFactory factory = new ApplicationContext("beans.xml");
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
		SayHello sayHello = (SayHello) factory.getBean("sayHello");
		sayHello.say();
//		Dog dog = (Dog) factory.getBean("dog");
//		dog.say();
//		Dog dog2 = (Dog) factory.getBean("dog");
//		System.out.println(dog);
//		System.out.println(dog2);
		
//		User user = (User)factory.getBean("teacher");
//		user.showMe();
		
		User zhangsan = (User) factory.getBean("zhangsan");
		System.out.println(zhangsan);
		System.out.println(factory.getBean("zhangsan") == factory.getBean("zhangsan"));
		
		Student student = (Student) factory.getBean("student");
		student.showMe();
	}
}
