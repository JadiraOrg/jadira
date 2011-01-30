package org.jadira.usertype.dateandtime.shared.arrayutils;

import org.hibernate.type.Type;

public class ArrayHelper {

    private ArrayHelper() {
    }
    
    public static final String[] copyOf(String[] array) {
        
        String[] result = new String[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }
    
    public static final Type[] copyOf(Type[] array) {
        
        Type[] result = new Type[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }
}
