package spring.ls.beans.factory.xml;

public interface NamespaceHandlerResolver {

	NamespaceHandler resolve(String namespaceUri);
}
