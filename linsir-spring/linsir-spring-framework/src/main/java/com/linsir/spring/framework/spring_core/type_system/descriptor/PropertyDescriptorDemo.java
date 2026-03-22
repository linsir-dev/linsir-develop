package com.linsir.spring.framework.spring_core.type_system.descriptor;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Demo: PropertyDescriptor - Accessing JavaBean properties
 * Demonstrates how Spring's BeanWrapper uses property descriptors
 */
public class PropertyDescriptorDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("===== PropertyDescriptor Demo =====\n");

        // Demo 1: Get all property descriptors
        demoAllProperties();

        // Demo 2: Access specific property
        demoSpecificProperty();

        // Demo 3: Read and write properties
        demoPropertyAccess();

        // Demo 4: Property types
        demoPropertyTypes();

        System.out.println("\n===== Demo Complete =====");
    }

    /**
     * Demo 1: Get all property descriptors for a class
     */
    private static void demoAllProperties() throws Exception {
        System.out.println("1. All Properties of UserService:");

        java.beans.BeanInfo beanInfo = Introspector.getBeanInfo(UserService.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        System.out.println("   Found " + propertyDescriptors.length + " properties:");
        for (PropertyDescriptor pd : propertyDescriptors) {
            System.out.println("   - " + pd.getName() + " (" + pd.getPropertyType().getSimpleName() + ")");
        }

        System.out.println();
    }

    /**
     * Demo 2: Access a specific property descriptor
     */
    private static void demoSpecificProperty() throws Exception {
        System.out.println("2. Specific Property Access:");

        java.beans.BeanInfo beanInfo = Introspector.getBeanInfo(UserService.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        // Find serviceName property
        for (PropertyDescriptor pd : propertyDescriptors) {
            if ("serviceName".equals(pd.getName())) {
                System.out.println("   Property: serviceName");
                System.out.println("   Type: " + pd.getPropertyType().getName());
                System.out.println("   Read method: " + (pd.getReadMethod() != null ? pd.getReadMethod().getName() : "null"));
                System.out.println("   Write method: " + (pd.getWriteMethod() != null ? pd.getWriteMethod().getName() : "null"));
                break;
            }
        }

        System.out.println();
    }

    /**
     * Demo 3: Read and write property values
     */
    private static void demoPropertyAccess() throws Exception {
        System.out.println("3. Property Read/Write:");

        UserService userService = new UserService("MyService", 50);

        java.beans.BeanInfo beanInfo = Introspector.getBeanInfo(UserService.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        // Read serviceName
        for (PropertyDescriptor pd : propertyDescriptors) {
            if ("serviceName".equals(pd.getName()) && pd.getReadMethod() != null) {
                Object value = pd.getReadMethod().invoke(userService);
                System.out.println("   Read serviceName: " + value);
                break;
            }
        }

        // Read maxUsers
        for (PropertyDescriptor pd : propertyDescriptors) {
            if ("maxUsers".equals(pd.getName()) && pd.getReadMethod() != null) {
                Object value = pd.getReadMethod().invoke(userService);
                System.out.println("   Read maxUsers: " + value);
                break;
            }
        }

        // Write serviceName
        for (PropertyDescriptor pd : propertyDescriptors) {
            if ("serviceName".equals(pd.getName()) && pd.getWriteMethod() != null) {
                pd.getWriteMethod().invoke(userService, "UpdatedService");
                System.out.println("   Updated serviceName to: UpdatedService");
                break;
            }
        }

        // Verify update
        for (PropertyDescriptor pd : propertyDescriptors) {
            if ("serviceName".equals(pd.getName()) && pd.getReadMethod() != null) {
                Object value = pd.getReadMethod().invoke(userService);
                System.out.println("   Verify serviceName: " + value);
                break;
            }
        }

        System.out.println();
    }

    /**
     * Demo 4: Property type information
     */
    private static void demoPropertyTypes() throws Exception {
        System.out.println("4. Property Type Information:");

        java.beans.BeanInfo beanInfo = Introspector.getBeanInfo(UserService.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        System.out.println("   Property types:");
        for (PropertyDescriptor pd : propertyDescriptors) {
            String name = pd.getName();
            Class<?> type = pd.getPropertyType();
            boolean isPrimitive = type.isPrimitive();
            boolean isCollection = java.util.Collection.class.isAssignableFrom(type);
            boolean isMap = java.util.Map.class.isAssignableFrom(type);

            System.out.println("   - " + name + ":");
            System.out.println("     Type: " + type.getSimpleName());
            System.out.println("     Is primitive: " + isPrimitive);
            System.out.println("     Is collection: " + isCollection);
            System.out.println("     Is map: " + isMap);
        }

        System.out.println();
    }
}
