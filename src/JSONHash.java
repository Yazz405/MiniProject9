package src;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * JSON hashes/objects.
 */
public class JSONHash implements JSONValue {

  // +-----------+-------------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The load factor for expanding the table.
   */
  static final double LOAD_FACTOR = 0.5;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /*
   * The amount of values in the hash table
   */
  int size = 0;

  /*
   * The array we use to store the ArrayList of key/value pairs
   */
  Object[] buckets;

  /*
   * Random number generator to expand the array
   */
  Random rand;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new hash table.
   */
  public JSONHash() {
    this.rand = new Random();
    this.buckets = new Object[41];
    this.size = 0;
  } // ChainedHashTable

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    String result = "";

    return "{" + result + "}"; // STUB
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {

    return true; // STUB
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    return this.buckets.hashCode();
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {
    pen.println(this.toString());
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public Iterator<KVPair<JSONString, JSONValue>> getValue() {
    return this.iterator();
  } // getValue()

  // +-------------------+-------------------------------------------
  // | Hashtable methods |
  // +-------------------+

  /**
   * Get the value associated with a key.
   */
  public JSONValue get(JSONString key) {
    int index = find(key);

    @SuppressWarnings("unchecked")
    ArrayList<KVPair<JSONString, JSONValue>> alist =
        (ArrayList<KVPair<JSONString, JSONValue>>) buckets[index];
    if (alist == null) {
      throw new IndexOutOfBoundsException("Invalid key: " + key);
    } else {
      KVPair<JSONString, JSONValue> pair = null;

      for (KVPair<JSONString, JSONValue> currentPair : alist) {
        if (currentPair.key().equals(key)) {
          pair = currentPair;
        } // if
      } // for

      if (pair == null) {
        throw new IndexOutOfBoundsException("Invalid key: " + key);
      } else {
        return pair.value();
      } // if-else
    } // if-else
  } // get(JSONString)

  /**
   * Get all of the key/value pairs.
   */
  public Iterator<KVPair<JSONString, JSONValue>> iterator() {
    return new Iterator<KVPair<JSONString, JSONValue>>() {

      int outerBucket = 0;
      int innerBucket = 0;

      @SuppressWarnings("unchecked")
      public boolean hasNext() {
        if (buckets == null || size == 0) {
          return false;
        } // if

        if (innerBucket >= outerBucket) {
          for (int i = 0; i < buckets.length; i++) {
            ArrayList<KVPair<JSONString, JSONValue>> alist =
                (ArrayList<KVPair<JSONString, JSONValue>>) buckets[i];

            if (alist == null) {
              outerBucket++;
              continue;
            } else {
              innerBucket = 0;
              for (int j = 0; j < alist.size(); j++) {
                if (alist.get(j) == null) {
                  continue;
                } else {
                  return true;
                } // if... else
              } // for

            } // if... else

          } // for
        } else if (innerBucket < outerBucket) {
          return true;
        } // if... else if

        return false;
      } // hasNext()

      @SuppressWarnings("unchecked")
      public KVPair<JSONString, JSONValue> next() {
        if (this.hasNext()) {
          ArrayList<KVPair<JSONString, JSONValue>> alist =
              (ArrayList<KVPair<JSONString, JSONValue>>) buckets[outerBucket];

          return (KVPair<JSONString, JSONValue>) alist.get(innerBucket);
        } else {
          throw new NoSuchElementException();
        }
      } // next()

    }; // new Iterator
  } // iterator()

  /**
   * Set the value associated with a key.
   */
  public void set(JSONString key, JSONValue value) {
    // if there are too many entries
    if (this.size > (this.buckets.length * LOAD_FACTOR)) {
      expand();
    }

    // Find out where the key belongs and put the pair there.
    int index = find(key);
    @SuppressWarnings("unchecked")
    ArrayList<KVPair<JSONString, JSONValue>> alist =
        (ArrayList<KVPair<JSONString, JSONValue>>) buckets[index];
    // Special case: Nothing there yet
    if (alist == null) {
      alist = new ArrayList<KVPair<JSONString, JSONValue>>();
      this.buckets[index] = alist;
    } // if

    // if the value already exists
    if (this.containsKey(key)) {
      for (KVPair<JSONString, JSONValue> pair : alist) {
        if (pair.key().equals(key)) {
          pair.set(value);
        } // if
      } // for
    } else {
      alist.add(new KVPair<JSONString, JSONValue>(key, value));
      ++this.size;
    } // if-else

  } // set(JSONString, JSONValue)

  /**
   * Find out how many key/value pairs are in the hash table.
   */
  public int size() {
    return this.size;
  } // size()

  // +---------+---------------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Expand the size of the table.
   */
  void expand() {

  }// expand()

  /**
   * Find the index of the entry with a given key. If there is no such entry, return the index of an
   * entry we can use to store that key.
   */
  int find(JSONString key) {
    return Math.abs(key.hashCode()) % this.buckets.length;
  } // find(K)

  /**
   * Determine if the hash table contains a particular key.
   */
  public boolean containsKey(JSONString key) {
    try {
      get(key);
      return true;
    } catch (Exception e) {
      return false;
    } // try/catch
  } // containsKey(K)

  public static void main(String[] args) {
    JSONHash test = new JSONHash();

    JSONString hello = new JSONString("hello");
    JSONString world = new JSONString("world");
    JSONString please = new JSONString("please");
    JSONString work = new JSONString("work");

    test.set(hello, world);
    test.set(please, work);
    JSONValue result = test.get(please);
    System.out.println(result);

    Iterator<KVPair<JSONString, JSONValue>> result2 = test.getValue();

    int result3 = test.size;
    System.out.println(result3);

    System.out.println(result2.next());
    System.out.println(result2.next());

  }

} // class JSONHash
