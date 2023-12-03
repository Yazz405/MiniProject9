package src;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

/**
 * Utilities for our simple implementation of JSON.
 */
public class JSON {
  // +---------------+-----------------------------------------------
  // | Static fields |
  // +---------------+

  /**
   * The current position in the input.
   */
  static int pos;

  /**
   * Standard size
   */
  static int size = 1000;

  // +----------------+----------------------------------------------
  // | Static methods |
  // +----------------+

  /**
   * Parse a string into JSON.
   */
  public static JSONValue parse(String source) throws ParseException, IOException {
    return parse(new StringReader(source));
  } // parse(String)

  /**
   * Parse a file into JSON.
   */
  public static JSONValue parseFile(String filename) throws ParseException, IOException {
    FileReader reader = new FileReader(filename);
    JSONValue result = parse(reader);
    reader.close();
    return result;
  } // parseFile(String)

  /**
   * Parse JSON from a reader.
   */
  public static JSONValue parse(Reader source) throws ParseException, IOException {
    pos = 0;
    JSONValue result = parseKernel(source);
    if (-1 != skipWhitespace(source)) {
      throw new ParseException("Characters remain at end", pos);
    }
    return result;
  } // parse(Reader)

  // +---------------+-----------------------------------------------
  // | Local helpers |
  // +---------------+

  /**
   * Parse JSON from a reader, keeping track of the current position
   */
  static JSONValue parseKernel(Reader source) throws ParseException, IOException {
    int ch;
    ch = skipWhitespace(source);
    if (-1 == ch) {
      throw new ParseException("Unexpected end of file", pos);
    }

    if (ch == '"') {
      return stringParser(source, ch);
    } else if (ch == '[') {
      JSONArray arr = new JSONArray();
      arrayParser(source, ch, arr);
      return arr;
    } else if (ch == '{') {
      JSONHash hash = new JSONHash();
      hashParser(source, ch, hash);
      return hash;
    } else if (ch == '-' || Character.isDigit(ch)) {
      String numResult = numParser(source, ch);
      if (numResult.contains("E") || numResult.contains(".") || numResult.contains("+")
          || numResult.contains("-")) {
        JSONReal num = new JSONReal(numResult.substring(0, numResult.length() - 1));
        return num;
      } else {
        JSONInteger num = new JSONInteger(numResult.substring(0, numResult.length() - 1));
        return num;
      }
    } else if (ch == 't' || ch == 'f' || ch == 'n') {
      String constantRes = constantParser(source, ch);
      if (constantRes.contains("true")) {
        return JSONConstant.TRUE;
      } else if (constantRes.contains("false")) {
        return JSONConstant.FALSE;
      } else if (constantRes.contains("null")) {
        return JSONConstant.NULL;
      } else {
        throw new IOException("Could not parse: " + ch);
      }
    }

    throw new ParseException("Could not parse", pos);
  } // parseKernel

  /**
   * Get the next character from source, skipping over whitespace.
   */
  static int skipWhitespace(Reader source) throws IOException {
    int ch;
    do {
      ch = source.read();
      ++pos;
    } while (isWhitespace(ch));
    return ch;
  } // skipWhitespace(Reader)

  /**
   * Determine if a character is JSON whitespace (newline, carriage return, space, or tab).
   */
  static boolean isWhitespace(int ch) {
    return (' ' == ch) || ('\n' == ch) || ('\r' == ch) || ('\t' == ch);
  } // isWhiteSpace(int)

  static JSONString stringParser(Reader source, int ch) throws IOException {
    String result = "";
    result += (char) ch;

    do {
      ch = source.read();
      result += (char) ch;
      ++pos;
    } while (ch != '"'); // do... while

    return new JSONString(result.substring(1, result.length() - 1));
  } // stringParser(Reader source, int ch)

  static void arrayParser(Reader source, int ch, JSONArray arr) throws IOException {

    while (ch != ']') {
      ch = source.read();

      if (ch == ',' || isWhitespace(ch)) {
        continue;
      }

      if (ch == '"') {
        JSONString resString = stringParser(source, ch);
        arr.add(resString);
      } else if (ch == '[') {
        JSONArray arrNew = new JSONArray();
        arrayParser(source, ch, arrNew);
        arr.add(arrNew);
      } else if (ch == '{') {
        JSONHash hash = new JSONHash();
        hashParser(source, ch, hash);
        arr.add(hash);
      } else if (ch == '-' || Character.isDigit(ch)) {
        String numResult = numParser(source, ch);
        if (numResult.contains("E") || numResult.contains(".") || numResult.contains("+")
            || numResult.contains("-")) {
          JSONReal num = new JSONReal(numResult.substring(0, numResult.length() - 1));
          arr.add(num);
        } else {
          JSONInteger num = new JSONInteger(numResult.substring(0, numResult.length() - 1));
          arr.add(num);
        }
        if (numResult.charAt(numResult.length() - 1) == ']') {
          break;
        }
      } else if ((ch == 't' || ch == 'f' || ch == 'n')) {
        String constantRes = constantParser(source, ch);
        if (constantRes.contains("true")) {
          arr.add(JSONConstant.TRUE);
        } else if (constantRes.contains("false")) {
          arr.add(JSONConstant.FALSE);
        } else if (constantRes.contains("null")) {
          arr.add(JSONConstant.NULL);
        } else {
          throw new IOException("Could not parse: " + ch);
        }

        if (constantRes.charAt(constantRes.length() - 1) == ']') {
          break;
        }
      }

      ++pos;
    }
  } // arrayParser(Reader source, int ch, JSONArray arr) throws IOException

  static void hashParser(Reader source, int ch, JSONHash hash) throws IOException {
    String result = "";
    JSONString[] keys = new JSONString[size];
    int i = 0;

    do {
      ch = source.read();

      if (isWhitespace(ch) || ch == ',') {
        continue;
      }

      result += (char) ch;

      if (ch == ':') {
        keys[i] = new JSONString(result.substring(1, result.length() - 2));

        ch = source.read();

        while (isWhitespace(ch)) {
          ch = source.read();
        }

        if (ch == '"') {
          JSONString resString = stringParser(source, ch);
          hash.set(keys[i], resString);
        } else if (ch == '[') {
          JSONArray arr = new JSONArray();
          arrayParser(source, ch, arr);
          hash.set(keys[i], arr);
        } else if (ch == '{') {
          JSONHash newHash = new JSONHash();
          hashParser(source, ch, newHash);
          hash.set(keys[i], newHash);
        } else if (ch == '-' || Character.isDigit(ch)) {
          String numResult = numParser(source, ch);
          if (numResult.contains("E") || numResult.contains(".") || numResult.contains("+")
              || numResult.contains("-")) {
            JSONReal num = new JSONReal(numResult.substring(0, numResult.length() - 1));
            hash.set(keys[i], num);
          } else {
            JSONInteger num = new JSONInteger(numResult.substring(0, numResult.length() - 1));
            hash.set(keys[i], num);
          }
          if (numResult.charAt(numResult.length() - 1) == '}') {
            break;
          }
        } else if (ch == 't' || ch == 'f' || ch == 'n') {
          String constantRes = constantParser(source, ch);
          if (constantRes.contains("true")) {
            hash.set(keys[i], JSONConstant.TRUE);
          } else if (constantRes.contains("false")) {
            hash.set(keys[i], JSONConstant.FALSE);
          } else if (constantRes.contains("null")) {
            hash.set(keys[i], JSONConstant.NULL);
          } else {
            throw new IOException("Could not parse: " + ch);
          }

          if (constantRes.charAt(constantRes.length() - 1) == '}') {
            break;
          }
        }

        ++pos;
        i++;
        result = "";
      }

      ++pos;
    } while (ch != '}');

  } // hashParser(Reader source, int ch, JSONHash hash) throws IOException

  static String numParser(Reader source, int ch) throws IOException {
    String result = "";

    do {
      result += (char) ch;
      ch = source.read();

      ++pos;
    } while (Character.isDigit((char) ch) || ch == 'E' || ch == '.' || ch == '+' || ch == '-');

    result += (char) ch;

    return result;
  } // numParser(Reader source, int ch)

  static String constantParser(Reader source, int ch) throws IOException {
    String result = "";
    result += (char) ch;

    do {
      ch = source.read();
      result += (char) ch;
    } while (Character.isAlphabetic(ch));

    result = result.substring(0);
    result = result.toLowerCase();

    return result;
  } // constantParser(Reader source, int ch) throws IOException

  public static void main(String[] args) throws ParseException, IOException {
    JSONValue result = JSON.parseFile("JSONEx.txt");

    System.out.println(result);
  }
}
// class JSON

/*
 * static JSONValue kernalHelper(Reader source, int ch, JSONArray arr, JSONHash hash) throws
 * IOException{ if (ch == '"') { return stringParser(source, ch); } else if (ch == '[') { JSONArray
 * arrNew = new JSONArray(); arrayParser(source, ch, arr); return arr; } else if (ch == '{') {
 * JSONHash hashNew = new JSONHash(); hashParser(source, ch, hash); return hash; } else if (ch ==
 * '-' || Character.isDigit(ch)) {
 * 
 * } }
 */

/*
 * switch (ch) { case '"': JSONString resString = stringParser(source, ch); arr.add(resString);
 * break;
 * 
 * case '[': JSONArray arrNew = new JSONArray(); arrayParser(source, ch, arrNew); arr.add(arrNew);
 * break; case '{': JSONHash newHash = new JSONHash(); hashParser(source, ch, newHash);
 * arr.add(newHash); break; }
 */

/*
 * switch (ch) { case '"': JSONString resString = stringParser(source, ch); hash.set(keys[i],
 * resString); break; case '[': JSONArray arr = new JSONArray(); arrayParser(source, ch, arr);
 * hash.set(keys[i], arr); break; case '{': JSONHash newHash = new JSONHash(); hashParser(source,
 * ch, newHash); hash.set(keys[i], newHash); break; }
 */
