package com.app.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Test {
    public static void main(String[] args) {

        //checking whether changes made in the instance on a Map will be reflected in actual map or not

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> staticFields = new HashMap<>();
        System.out.println(map);
        map.put("staticFields", staticFields);

        Object val = "John Doe";
        Object draftedOn = "2024-06-06";

        // Accessing the sub-map
        Map<String, Object> extractedStaticFields = (Map<String, Object>) map.get("staticFields");
       // Updating the sub-map
        extractedStaticFields.put("CLAIMED_BY", val);
        extractedStaticFields.put("DRAFTED_ON", draftedOn);

        System.out.println(map);


  /*
        Map<String, Object> map = null;
        // Check if the map is null or empty using MapUtils
        if (MapUtils.isEmpty(map)) {
            System.out.println("The map is either null or empty.");
        } else {
            System.out.println("The map is not empty.");
        }

        // Example with a non-null empty map
        map = new HashMap<>();

        if (MapUtils.isEmpty(map)) {
            System.out.println("The map is either null or empty.");
        } else {
            System.out.println("The map is not empty.");
        }

        // Example with a non-empty map
        map.put("key", "value");

        if (MapUtils.isEmpty(map)) {
            System.out.println("The map is either null or empty.");
        } else {
            System.out.println("The map is not empty.");
        }
         */
    }
}
