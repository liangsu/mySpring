package spring.ls.core.io;

import java.nio.charset.Charset;

public class EncodedResource {
	private Resource resource;
	private String encoding;
	private Charset charset;
	
	public EncodedResource(Resource resource){
		this(resource,null,null);
	}
	
	public EncodedResource(Resource resource,String encoding){
		this(resource,encoding,null);
	}
	
	public EncodedResource(Resource resource,Charset charset){
		this(resource,null,charset);
	}
	
	public EncodedResource(Resource resource, String encoding, Charset charset) {
		this.resource = resource;
		this.encoding = encoding;
		this.charset = charset;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
}
