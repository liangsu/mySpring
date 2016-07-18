package spring.ls.test;

public class User {

	private String name;
	private int age;
	
	public User(){
		
	}
	
	public User(String name, int age){
		this.name = name;
		this.age = age;
	}
	
	public void showMe(){
		System.out.println("i am a user");
	}
	
	@Override
	public String toString() {
		return "[name="+name+"][age="+age+"]";
	}
}
