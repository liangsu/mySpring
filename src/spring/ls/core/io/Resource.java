package spring.ls.core.io;

import java.io.File;
import java.io.IOException;

public interface Resource extends InputStreamSource{

	boolean exists();
	
	boolean isOpen();

	File getFile() throws IOException;
	
	String getFilename() throws IOException;
	
	String getDescription();
}
