package be.kdg.distrib.util;

import java.rmi.server.ExportException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael
 * @project distrib
 */
public class CastUtil {

    public static Object castResultToGivenType(String result, Class aClass) {

        if (aClass.getName().equals(Integer.TYPE.getName()) || aClass.getName().equals(Integer.class.getName())) {
            return Integer.valueOf(result);
        }
        if (aClass.getName().equals(Character.TYPE.getName()) || aClass.getName().equals(Character.class.getName())) {
            //TODO Is this okey?
            return result.toCharArray()[0];
        }
        if (aClass.getName().equals(Boolean.TYPE.getName()) || aClass.getName().equals(Boolean.class.getName())) {
            return Boolean.valueOf(result);
        }
        if (aClass.getName().equals(Long.TYPE.getName()) || aClass.getName().equals(Long.class.getName())) {
            return Long.valueOf(result);
        }
        if (aClass.getName().equals(Float.TYPE.getName()) || aClass.getName().equals(Float.class.getName())) {
            return Float.valueOf(result);
        }
        if (aClass.getName().equals(Short.TYPE.getName()) || aClass.getName().equals(Short.class.getName())) {
            return Short.valueOf(result);
        }
        if (aClass.getName().equals(Byte.TYPE.getName()) || aClass.getName().equals(Byte.class.getName())) {
            return Byte.valueOf(result);
        }
        if (aClass.getName().equals(Double.TYPE.getName()) || aClass.getName().equals(Double.class.getName())) {
            return Double.valueOf(result);
        }
        if (aClass.getName().equals("java.lang.String")) {
            return result;
        }
        return null;
    }

    public static String classForNameWrapPrimitives(Class<?> aClass) throws Exception {
        if(!aClass.isPrimitive()) {
            return aClass.getName();
        }
        else if(aClass.getName().equals("int")) {
            String s = Integer.class.getName();
            return  s;
        }
        else if(aClass.getName().equals("float")) {
            return  Float.class.getName();
        }
        else if(aClass.getName().equals("double")) {
            return  Double.class.getName();
        }
        else if(aClass.getName().equals("long")) {
            return  Long.class.getName();
        }
        else if(aClass.getName().equals("char")) {
            return  Character.class.getName();
        }
        else if(aClass.getName().equals("byte")) {
            return  Byte.class.getName();
        }
        else if(aClass.getName().equals("short")) {
            return  Short.class.getName();
        }
        else if(aClass.getName().equals("boolean")) {
            return  Boolean.class.getName();
        }
        else {
            throw new Exception(aClass.getName() + " className does not exist");
        }
    }

    /**
     * Checks if the argument is a primitive type or wrapper or String
     *
     * @param object
     * @return
     */
    public static boolean isPrimitiveOrWrapper(Object object) {
        if (object.getClass().isPrimitive()) {
            return true;
        }
        Set<String> wrapperClasses = new HashSet<>();
        wrapperClasses.add(Integer.class.getName());
        wrapperClasses.add(Float.class.getName());
        wrapperClasses.add(Double.class.getName());
        wrapperClasses.add(Boolean.class.getName());
        wrapperClasses.add(Short.class.getName());
        wrapperClasses.add(Long.class.getName());
        wrapperClasses.add(Character.class.getName());
        wrapperClasses.add(Byte.class.getName());
        //Not a wrapper class but one to check
        wrapperClasses.add(String.class.getName());
        if (wrapperClasses.contains(object.getClass().getName())) {
            return true;
        }
        return false;
    }

    public static boolean isPrimitiveOrWrapper(Class aClass) {

        if (aClass.getName().equals(Integer.TYPE.getName()) || aClass.getName().equals(Integer.class.getName())) {
            return true;
        }
        if (aClass.getName().equals(Character.TYPE.getName()) || aClass.getName().equals(Character.class.getName())) {
            //TODO Is this okey?
            return true;
        }
        if (aClass.getName().equals(Boolean.TYPE.getName()) || aClass.getName().equals(Boolean.class.getName())) {
            return true;
        }
        if (aClass.getName().equals(Long.TYPE.getName()) || aClass.getName().equals(Long.class.getName())) {
            return true;
        }
        if (aClass.getName().equals(Float.TYPE.getName()) || aClass.getName().equals(Float.class.getName())) {
            return true;
        }
        if (aClass.getName().equals(Short.TYPE.getName()) || aClass.getName().equals(Short.class.getName())) {
            return true;
        }
        if (aClass.getName().equals(Byte.TYPE.getName()) || aClass.getName().equals(Byte.class.getName())) {
            return true;
        }
        if (aClass.getName().equals(Double.TYPE.getName()) || aClass.getName().equals(Double.class.getName())) {
            return true;
        }
        if (aClass.getName().equals("java.lang.String")) {
            return true;
        }
        return false;
    }
}
