package com.itech.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonUtils {

    public static List<String> convertStringTolist(String str, String reg) {
        String[] splits = str.split(reg);
        return new ArrayList<>(Arrays.asList(splits));
    }
    
    public static String converListToString(List<String> list) {
        return String.join(",",list);
    }
    
}
