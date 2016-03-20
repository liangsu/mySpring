package spring.ls.core.io;

public class ResourceUtils {

	public final static String FILE_DESCRIPTION_SEPERATE = ":";
	public final static String FILE_CLASSPATH = "classpath";
	
	public static Resource getResource(String fileLocation){
		Resource resource = null;
		int pos = fileLocation.indexOf(FILE_DESCRIPTION_SEPERATE);
		if(pos >= 0){
			String resourceLoader = fileLocation.substring(0, pos);
			String filepath = fileLocation.substring(pos + 1);
			if(resourceLoader.equals(FILE_CLASSPATH)){
				resource = new ClassPathResource(filepath);
			}
		}else{
			String filepath = fileLocation.substring(pos + 1);
			resource = new ClassPathResource(filepath);
		}
		return resource;
	}
}
