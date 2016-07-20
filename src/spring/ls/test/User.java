package spring.ls.test;

public class User {

	private String name;
	private int age;
	private String email;
	private Student student;
	
	public User(){
		
	}
	
	public User(String name, int age){
		this.name = name;
		this.age = age;
	}
	
	public User(String name, int age, String email){
		this.name = name;
		this.age = age;
		this.email = email;
	}
	
	public User(String name, int age, String email,Student student){
		this.name = name;
		this.age = age;
		this.email = email;
		this.student = student;
		System.out.println(email + "   "+student);
	}
	
	public void showMe(){
		System.out.println("i am a user");
	}
	
	@Override
	public String toString() {
		return "[name="+name+"][age="+age+"][email="+email+"]";
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
}
