package spring.ls.core.io;

import java.io.IOException;

public abstract class AbstractResource implements Resource{

	@Override
	public boolean exists() {
		try {
			return getFile().exists();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String getFilename() throws IOException {
		return getFile().getName();
	}
	
	@Override
	public String getDescription() {
		return null;
	}
}
