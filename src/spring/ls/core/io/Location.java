package spring.ls.core.io;

public class Location {

	private final Resource resource;
	
	private final Object source;

	public Location(Resource resource, Object source) {
		super();
		this.resource = resource;
		this.source = source;
	}

	public Resource getResource() {
		return resource;
	}

	public Object getSource() {
		return source;
	}
}
