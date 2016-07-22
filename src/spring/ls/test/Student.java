package spring.ls.test;

public class Student {

	private User user;
	
	private String name;
	
	private int grade;
	
	public Student(){
		
	}
	
	public void showMe(){
		System.out.println("i am a student,my name is "+this.name+" and i'm in grade "+grade+" and my user is "+user);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}
}
