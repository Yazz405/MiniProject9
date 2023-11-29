import java.io.PrintWriter;
import java.util.Iterator;
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
    return ""; // STUB
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
    return 0; // STUB
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
    return null; // STUB
  } // get(JSONString)

  /**
   * Get all of the key/value pairs.
   */
  public Iterator<KVPair<JSONString, JSONValue>> iterator() {
    return null; // STUB
  } // iterator()

  /**
   * Set the value associated with a key.
   */
  public void set(JSONString key, JSONValue value) {
    // STUB
  } // set(JSONString, JSONValue)

  /**
   * Find out how many key/value pairs are in the hash table.
   */
  public int size() {
    return 0; // STUB
  } // size()

} // class JSONHash
