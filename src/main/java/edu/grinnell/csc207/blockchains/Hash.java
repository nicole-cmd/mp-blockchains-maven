package edu.grinnell.csc207.blockchains;

import java.util.Arrays;

/**
 * Encapsulated hashes.
 *
 * @author Khanh Do
 * @author Nicole Gorrell
 * @author Samuel A. Rebelsky
 */
public class Hash {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The hash.
   */
  byte[] copy;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new encapsulated hash.
   *
   * @param data The data to copy into the hash.
   */
  public Hash(byte[] data) {
    if (data == null) {
      throw new IllegalArgumentException("Data cannot be null.");
    } // if
    this.copy = Arrays.copyOf(data, data.length);
  } // Hash(byte[])

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Determine how many bytes are in the hash.
   *
   * @return the number of bytes in the hash.
   */
  public int length() {
    return this.copy.length;
  } // length()

  /**
   * Get the ith byte.
   *
   * @param i The index of the byte to get, between 0 (inclusive) and length() (exclusive).
   *
   * @return the ith byte
   */
  public byte get(int i) {
    return this.copy[i];
  } // get()

  /**
   * Get a copy of the bytes in the hash. We make a copy so that the client cannot change them.
   *
   * @return a copy of the bytes in the hash.
   */
  public byte[] getBytes() {
    byte[] newCopy = Arrays.copyOf(this.copy, this.copy.length);
    return newCopy;
  } // getBytes()

  /**
   * Convert to a hex string.
   *
   * @return the hash as a hex string.
   */
  public String toString() {
    StringBuilder sb = new StringBuilder(this.copy.length * 2);
    for (byte b : this.copy) {
      int newB = Byte.toUnsignedInt(b);
      sb.append((String.format("%02x", newB)).toUpperCase());
    } // for
    return sb.toString();
  } // toString()

  /**
   * Determine if this is equal to another object.
   *
   * @param other The object to compare to.
   *
   * @return true if the two objects are conceptually equal and false otherwise.
   */
  @Override
  public boolean equals(Object other) {
    if (other instanceof Hash otherHash) {
      return Arrays.equals(this.copy, otherHash.copy);
    } // if
    return false;
  } // equals(Object)

  /**
   * Get the hash code of this object.
   *
   * @return the hash code.
   */
  public int hashCode() {
    return this.toString().hashCode();
  } // hashCode()
} // class Hash
