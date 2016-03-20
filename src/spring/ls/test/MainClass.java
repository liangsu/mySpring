package spring.ls.test;

import spring.ls.bean.Dog;
import spring.ls.bean.SayHello;
import spring.ls.factory.ApplicationContext;
import spring.ls.factory.BeanFactory;

public class MainClass {

	public static void main(String[] args) {
		BeanFactory factory = new ApplicationContext("beans.properties");
		SayHello sayHello = (SayHello) factory.getBean("sayHello");
		sayHello.say();
		Dog dog = (Dog) factory.getBean("dog");
		dog.say();
		Dog dog2 = (Dog) factory.getBean("dog");
		System.out.println(dog);
		System.out.println(dog2);
	}
}
