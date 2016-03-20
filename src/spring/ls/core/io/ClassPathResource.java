package spring.ls.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import spring.ls.util.ClassUtils;

public class ClassPathResource extends AbstractResource{

	private String filepath;
	private ClassLoader classLoader;
	
	public ClassPathResource(String filepath) {
		this.filepath = filepath;
		classLoader = ClassUtils.getDefaultClassLoader();
	}
	
	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public File getFile() throws IOException {
		String classPath = null;
		try {
			classPath = classLoader.getResource(filepath).toURI().getSchemeSpecificPart();
			classPath = classPath.substring(1);
		} catch (Exception e) {
			throw new FileNotFoundException(filepath);
		}
		File file = new File(classPath);
		return file;
	}

	@Override
	public InputStream getInputStream() {
		return classLoader.getResourceAsStream(filepath);
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

//	public static void main(String[] args) {
//		Resource resource = new ClassPathResource("beans2.xml");
//		System.out.println(resource.exists());
//	}
}
