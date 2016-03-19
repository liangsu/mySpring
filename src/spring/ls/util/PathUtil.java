package spring.ls.util;

public class PathUtil {

	/**
	 * 获取项目路径
	 * @return D:/workspace_jee/mySpring/build/classes/
	 */
	public static String getProjectPath(){
		String path = null;
		path = PathUtil.class.getClassLoader().getResource("").getPath();
		path = path.substring(1);
		path = path.replace("%20", " ");
		return path;
	}
	
	public static void main(String[] args) {
		System.out.println(getProjectPath());
	}
}
