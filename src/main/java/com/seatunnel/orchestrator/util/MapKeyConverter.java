package com.seatunnel.orchestrator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapKeyConverter {

  private static final String DOT = ".";
  private static final String HASH = "#";

  /**
   * Recursively converts dots to hash in map keys
   * @param map the map with keys potentially containing dots
   * @return new map with dots replaced by hash in all keys (recursively)
   */
  public static Map<String, Object> convertDotToHash(Map<String, Object> map) {
    if (map == null) {
      return null;
    }

    Map<String, Object> result = new HashMap<>();

    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String newKey = entry.getKey().replace(DOT, HASH);
      Object value = entry.getValue();

      // Recursively convert nested maps
      if (value instanceof Map) {
        result.put(newKey, convertDotToHash((Map<String, Object>) value));
      } 
      // Handle lists that might contain maps
      else if (value instanceof List) {
        result.put(newKey, convertListDotToHash((List<?>) value));
      } 
      else {
        result.put(newKey, value);
      }
    }

    return result;
  }

  /**
   * Recursively converts hash to dots in map keys
   * @param map the map with keys potentially containing hash
   * @return new map with hash replaced by dots in all keys (recursively)
   */
  public static Map<String, Object> convertHashToDot(Map<String, Object> map) {
    if (map == null) {
      return null;
    }

    Map<String, Object> result = new HashMap<>();

    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String newKey = entry.getKey().replace(HASH, DOT);
      Object value = entry.getValue();

      // Recursively convert nested maps
      if (value instanceof Map) {
        result.put(newKey, convertHashToDot((Map<String, Object>) value));
      } 
      // Handle lists that might contain maps
      else if (value instanceof List) {
        result.put(newKey, convertListHashToDot((List<?>) value));
      } 
      else {
        result.put(newKey, value);
      }
    }

    return result;
  }

  /**
   * Helper method to convert lists that might contain maps
   */
  private static List<?> convertListDotToHash(List<?> list) {
    List<Object> result = new ArrayList<>();
    for (Object item : list) {
      if (item instanceof Map) {
        result.add(convertDotToHash((Map<String, Object>) item));
      } else if (item instanceof List) {
        result.add(convertListDotToHash((List<?>) item));
      } else {
        result.add(item);
      }
    }
    return result;
  }

  /**
   * Helper method to convert lists that might contain maps
   */
  private static List<?> convertListHashToDot(List<?> list) {
    List<Object> result = new ArrayList<>();
    for (Object item : list) {
      if (item instanceof Map) {
        result.add(convertHashToDot((Map<String, Object>) item));
      } else if (item instanceof List) {
        result.add(convertListHashToDot((List<?>) item));
      } else {
        result.add(item);
      }
    }
    return result;
  }
}