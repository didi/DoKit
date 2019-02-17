package com.didichuxing.doraemonkit.kit.network.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * The header fields of a single HTTP message. Values are uninterpreted strings; use {@code Request}
 * and {@code Response} for interpreted headers. This class maintains the order of the header fields
 * within the HTTP message.
 *
 * <p>This class tracks header values line-by-line. A field with multiple comma- separated values on
 * the same line will be treated as a field with a single value by this class. It is the caller's
 * responsibility to detect and split on commas if their field permits multiple values. This
 * simplifies use of single-valued fields whose values routinely contain commas, such as cookies or
 * dates.
 *
 * <p>This class trims whitespace from values. It never returns values with leading or trailing
 * whitespace.
 *
 * <p>Instances of this class are immutable. Use {@link Builder} to create instances.
 */
public final class CommonHeaders {

  CommonHeaders(Builder builder) {
  }

  private CommonHeaders(String[] namesAndValues) {
  }

  /** Returns the last value corresponding to the specified field, or null. */
  public String get(String name) {
    return null;
  }

  /**
   * Returns the last value corresponding to the specified field parsed as an HTTP date, or null if
   * either the field is absent or cannot be parsed as a date.
   */
  public Date getDate(String name) {
    return null;
  }

  /** Returns the number of field values. */
  public int size() {
    return 0;
  }

  /** Returns the field at {@code position}. */
  public String name(int index) {
    return null;
  }

  /** Returns the value at {@code index}. */
  public String value(int index) {
    return null;
  }

  /** Returns an immutable case-insensitive set of header names. */
  public Set<String> names() {
    return null;
  }

  /** Returns an immutable list of the header values for {@code name}. */
  public List<String> values(String name) {
    return null;
  }

  public Builder newBuilder() {
    Builder result = new Builder();
    return result;
  }

  /**
   * Returns true if {@code other} is a {@code Headers} object with the same headers, with the same
   * casing, in the same order. Note that two headers instances may be <i>semantically</i> equal
   * but not equal according to this method. In particular, none of the following sets of headers
   * are equal according to this method: <pre>   {@code
   *
   *   1. Original
   *   Content-Type: text/html
   *   Content-Length: 50
   *
   *   2. Different order
   *   Content-Length: 50
   *   Content-Type: text/html
   *
   *   3. Different case
   *   content-type: text/html
   *   content-length: 50
   *
   *   4. Different values
   *   Content-Type: text/html
   *   Content-Length: 050
   * }</pre>
   *
   * Applications that require semantically equal headers should convert them into a canonical form
   * before comparing them for equality.
   */
  @Override public boolean equals(Object other) {
    return false;
  }

  @Override public int hashCode() {
    return 0;
  }

  @Override public String toString() {
    StringBuilder result = new StringBuilder();
    for (int i = 0, size = size(); i < size; i++) {
      result.append(name(i)).append(": ").append(value(i)).append("\n");
    }
    return result.toString();
  }

  public Map<String, List<String>> toMultimap() {
    Map<String, List<String>> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    for (int i = 0, size = size(); i < size; i++) {
      String name = name(i).toLowerCase(Locale.US);
      List<String> values = result.get(name);
      if (values == null) {
        values = new ArrayList<>(2);
        result.put(name, values);
      }
      values.add(value(i));
    }
    return result;
  }

  private static String get(String[] namesAndValues, String name) {
    for (int i = namesAndValues.length - 2; i >= 0; i -= 2) {
      if (name.equalsIgnoreCase(namesAndValues[i])) {
        return namesAndValues[i + 1];
      }
    }
    return null;
  }

  /**
   * Returns headers for the alternating header names and values. There must be an even number of
   * arguments, and they must alternate between header names and values.
   */
  public static CommonHeaders of(String... namesAndValues) {
    if (namesAndValues == null) throw new NullPointerException("namesAndValues == null");
    if (namesAndValues.length % 2 != 0) {
      throw new IllegalArgumentException("Expected alternating header names and values");
    }

    // Make a defensive copy and clean it up.
    namesAndValues = namesAndValues.clone();
    for (int i = 0; i < namesAndValues.length; i++) {
      if (namesAndValues[i] == null) throw new IllegalArgumentException("Headers cannot be null");
      namesAndValues[i] = namesAndValues[i].trim();
    }

    // Check for malformed headers.
    for (int i = 0; i < namesAndValues.length; i += 2) {
      String name = namesAndValues[i];
      String value = namesAndValues[i + 1];
      if (name.length() == 0 || name.indexOf('\0') != -1 || value.indexOf('\0') != -1) {
        throw new IllegalArgumentException("Unexpected header: " + name + ": " + value);
      }
    }

    return new CommonHeaders(namesAndValues);
  }

  /**
   * Returns headers for the header names and values in the {@link Map}.
   */
  public static CommonHeaders of(Map<String, String> headers) {
    if (headers == null) throw new NullPointerException("headers == null");

    // Make a defensive copy and clean it up.
    String[] namesAndValues = new String[headers.size() * 2];
    int i = 0;
    for (Map.Entry<String, String> header : headers.entrySet()) {
      if (header.getKey() == null || header.getValue() == null) {
        throw new IllegalArgumentException("Headers cannot be null");
      }
      String name = header.getKey().trim();
      String value = header.getValue().trim();
      if (name.length() == 0 || name.indexOf('\0') != -1 || value.indexOf('\0') != -1) {
        throw new IllegalArgumentException("Unexpected header: " + name + ": " + value);
      }
      namesAndValues[i] = name;
      namesAndValues[i + 1] = value;
      i += 2;
    }

    return new CommonHeaders(namesAndValues);
  }

  public static final class Builder {
    final List<String> namesAndValues = new ArrayList<>(20);

    /**
     * Add a header line without any validation. Only appropriate for headers from the remote peer
     * or cache.
     */
    Builder addLenient(String line) {
      int index = line.indexOf(":", 1);
      if (index != -1) {
        return addLenient(line.substring(0, index), line.substring(index + 1));
      } else if (line.startsWith(":")) {
        // Work around empty header names and header names that start with a
        // colon (created by old broken SPDY versions of the response cache).
        return addLenient("", line.substring(1)); // Empty header name.
      } else {
        return addLenient("", line); // No header name.
      }
    }

    /** Add an header line containing a field name, a literal colon, and a value. */
    public Builder add(String line) {
      int index = line.indexOf(":");
      if (index == -1) {
        throw new IllegalArgumentException("Unexpected header: " + line);
      }
      return add(line.substring(0, index).trim(), line.substring(index + 1));
    }

    /** Add a field with the specified value. */
    public Builder add(String name, String value) {
      return addLenient(name, value);
    }

    /**
     * Add a field with the specified value without any validation. Only appropriate for headers
     * from the remote peer or cache.
     */
    Builder addLenient(String name, String value) {
      namesAndValues.add(name);
      namesAndValues.add(value.trim());
      return this;
    }

    public Builder removeAll(String name) {
      for (int i = 0; i < namesAndValues.size(); i += 2) {
        if (name.equalsIgnoreCase(namesAndValues.get(i))) {
          namesAndValues.remove(i); // name
          namesAndValues.remove(i); // value
          i -= 2;
        }
      }
      return this;
    }

    /**
     * Set a field with the specified value. If the field is not found, it is added. If the field is
     * found, the existing values are replaced.
     */
    public Builder set(String name, String value) {
      return this;
    }


    /** Equivalent to {@code build().get(name)}, but potentially faster. */
    public String get(String name) {
      for (int i = namesAndValues.size() - 2; i >= 0; i -= 2) {
        if (name.equalsIgnoreCase(namesAndValues.get(i))) {
          return namesAndValues.get(i + 1);
        }
      }
      return null;
    }

    public CommonHeaders build() {
      return new CommonHeaders(this);
    }
  }
}