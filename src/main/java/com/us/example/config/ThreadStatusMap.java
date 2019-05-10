package com.us.example.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadStatusMap {
       public static Map<String ,Boolean> map = new ConcurrentHashMap<String ,Boolean>();
       public static Map<String , List<String>> dtuIdDevice= new ConcurrentHashMap<>();
}
