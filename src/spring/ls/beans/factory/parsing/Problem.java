package spring.ls.beans.factory.parsing;

import spring.ls.core.io.Location;

public class Problem {

	private final String message;
	
	private final Location location;
	
	private final Throwable rootCause;

	public Problem(String message, Location location, Throwable rootCause) {
		super();
		this.message = message;
		this.location = location;
		this.rootCause = rootCause;
	}

	public String getMessage() {
		return message;
	}

	public Location getLocation() {
		return location;
	}

	public Throwable getRootCause() {
		return rootCause;
	}

	public String getResourceDescription() {
		return getLocation().getResource().getDescription();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Configuration problem: ");
		sb.append(getMessage());
		sb.append("\nOffending resource: ").append(getResourceDescription());
//		if (getParseState() != null) {
//			sb.append('\n').append(getParseState());
//		}
		return sb.toString();
	}
}
