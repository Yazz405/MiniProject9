package src;
import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 * JSON reals.
 * @author: Alma Ordaz, Joshua Delarosa, Sam Rebelsky
 * 
 */
public class JSONReal implements JSONValue{

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The underlying double.
   */
  BigDecimal value;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new real given the underlying string.
   */
  public JSONReal(String str) {
    this.value = new BigDecimal(str);
  } // JSONReal(String)

  /**
   * Create a new real given a BigDecimal.
   */
  public JSONReal(BigDecimal value) {
    this.value = value;
  } // JSONReal(BigDecimal)

  /**
   * Create a new real given a double.
   */
  public JSONReal(double d) {
    this.value = BigDecimal.valueOf(d);
  } // JSONReal(double)

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    return this.value.toString();
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    return ( ( (other instanceof JSONReal) 
               && (this.value.equals(((JSONReal) other).value)) )
             || (this.value.equals(other)) );
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    return this.value.hashCode();
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {
    pen.println(this.value.toString());
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public BigDecimal getValue() {
    return this.value;
  } // getValue()

} // class JSONReal
