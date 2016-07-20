package spring.ls.beans;

import java.beans.PropertyEditor;

/**
 * 属性编辑注册器
 * @author warhorse
 *
 */
public interface PropertyEditorRegistry {

	void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor);
	
	void registerCustomEditor(Class<?> requiredType, String propertyPath, PropertyEditor propertyEditor);
	
	PropertyEditor findCustomEditor(Class<?> requiredType, String propertyPath);
}
