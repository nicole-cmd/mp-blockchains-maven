package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Khanh Do
 * @author Nicole Gorrell
 * @author Samuel A. Rebelsky
 */
public class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The message digest used to compute hashes.
   */
  static MessageDigest md = null;

  /**
   * The byte buffer used for ints.
   */
  static ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);

  /**
   * The byte buffer used for longs.
   */
  static ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);

  /**
   * The number of the block.
   */
  int blockNum;

  /**
   * The transaction stored in the block.
   */
  Transaction transaction;

  /**
   * The hash of the previous block.
   */
  Hash prevHash;

  /**
   * The nonce of the block.
   */
  long nonce;

  /**
   * The hash of the block.
   */
  Hash hash;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and previous hash, mining to
   * choose a nonce that meets the requirements of the validator.
   *
   * @param numInput The number of the block.
   * @param transactionInput The transaction for the block.
   * @param prevHashInput The hash of the previous block.
   * @param checkInput The validator used to check the block.
   */
  public Block(int numInput, Transaction transactionInput, Hash prevHashInput,
      HashValidator checkInput) {
    this.blockNum = numInput;
    this.transaction = transactionInput;
    this.prevHash = prevHashInput;
    Random rand = new Random();
    long tempNonce = rand.nextLong();
    while (!checkInput
        .isValid(new Hash(computeHash(numInput, transactionInput, prevHashInput, tempNonce)))) {
      tempNonce = rand.nextLong();
    } // while
    this.nonce = tempNonce;
    this.hash = new Hash(computeHash(numInput, transactionInput, prevHashInput, this.nonce));
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param numInput The number of the block.
   * @param transactionInput The transaction for the block.
   * @param prevHashInput The hash of the previous block.
   * @param nonceInput The nonce of the block.
   */
  public Block(int numInput, Transaction transactionInput, Hash prevHashInput, long nonceInput) {
    this.blockNum = numInput;
    this.transaction = transactionInput;
    this.prevHash = prevHashInput;
    this.nonce = nonceInput;
    this.hash = new Hash(computeHash(numInput, transactionInput, prevHashInput, nonceInput));
  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+)

  /**
   * Compute the hash of the block given all the other info already stored in the block.
   *
   * @param num The number of the block.
   * @param transaction The transaction for the block.
   * @param prevHash The hash of the previous block.
   * @param nonce The nonce of the block.
   * @return The hash of the block.
   */
  static byte[] computeHash(int num, Transaction transaction, Hash prevHash, long nonce) {
    try {
      md = MessageDigest.getInstance("sha-256");
    } catch (NoSuchAlgorithmException e) {
      // Shouldn't happen
    } // try/catch

    md.update(intToBytes(num));
    md.update(transaction.getSource().getBytes());
    md.update(transaction.getTarget().getBytes());
    md.update(intToBytes(transaction.getAmount()));
    md.update(prevHash.getBytes());
    md.update(longToBytes(nonce));
    return md.digest();
  } // computeHash()

  /**
   * Convert a long into its bytes.
   *
   * @param l The long to convert.
   *
   * @return The bytes in that long.
   */
  static byte[] longToBytes(long l) {
    longBuffer.clear();
    return longBuffer.putLong(l).array();
  } // longToBytes()

  /**
   * Convert an integer into its bytes.
   *
   * @param i The integer to convert.
   *
   * @return The bytes of that integer.
   */
  static byte[] intToBytes(int i) {
    intBuffer.clear();
    return intBuffer.putInt(i).array();
  } // intToBytes(int)

  // /**
  // * Generate nonce numbers.
  // */
  // static long generateNonce() {

  // } // generateNonce()
  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Get the number of the block.
   *
   * @return the number of the block.
   */
  public int getNum() {
    return this.blockNum;
  } // getNum()

  /**
   * Get the transaction stored in this block.
   *
   * @return the transaction.
   */
  public Transaction getTransaction() {
    return this.transaction;
  } // getTransaction()

  /**
   * Get the nonce of this block.
   *
   * @return the nonce.
   */
  public long getNonce() {
    return this.nonce;
  } // getNonce()

  /**
   * Get the hash of the previous block.
   *
   * @return the hash of the previous block.
   */
  public Hash getPrevHash() {
    return this.prevHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  public Hash getHash() {
    return this.hash;
  } // getHash

  /**
   * Get a string representation of the block.
   *
   * @return a string representation of the block.
   */
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();

    str.append("Block " + getNum() + " (Transaction: [");
    if (this.transaction.getSource().equals("")) {
      str.append("Deposit");
    } else {
      str.append("Source: " + this.transaction.getSource());
    } // if/else

    str.append(", Target: " + this.transaction.getTarget() + ", Amount: "
        + this.transaction.getAmount() + "], Nonce: " + this.getNonce() + ", prevHash: "
        + this.getPrevHash() + ", hash: " + this.getHash() + ")");
    return str.toString();
  } // toString()
} // class Block
