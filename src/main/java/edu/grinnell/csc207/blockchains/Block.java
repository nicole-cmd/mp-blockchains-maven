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

  int blockNum;

  Transaction trans;

  Hash prevHash;

  int nonce;

  Hash hash;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and previous hash, mining to
   * choose a nonce that meets the requirements of the validator.
   *
   * @param num The number of the block.
   * @param transaction The transaction for the block.
   * @param prevHash The hash of the previous block.
   * @param check The validator used to check the block.
   */
  public Block(int num, Transaction transaction, Hash prevHash, HashValidator check) {
    this.blockNum = num;
    this.trans = transaction;
    this.prevHash = prevHash;
    long tempNonce = 0;
    HashValidator standardValidator =
        (h) -> (h.length() >= 3) && (h.get(0) == 0) && (h.get(1) == 0) && (h.get(2) == 0);
    while (standardValidator
        .isValid(new Hash(computeHash(num, transaction, prevHash, tempNonce))) == false) {
      Random rand = new Random();
      tempNonce = rand.nextLong();
    } // while
    this.nonce = (int) tempNonce;
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param num The number of the block.
   * @param transaction The transaction for the block.
   * @param prevHash The hash of the previous block.
   * @param nonce The nonce of the block.
   */
  public Block(int num, Transaction transaction, Hash prevHash, long nonce) {
    this.blockNum = num;
    this.trans = transaction;
    this.prevHash = prevHash;
    this.nonce = (int) nonce;
    this.hash = new Hash(computeHash(num, transaction, prevHash, nonce));
  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+)

  /**
   * Compute the hash of the block given all the other info already stored in the block.
   */
  static byte[] computeHash(int num, Transaction transaction, Hash prevHash, long nonce) {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("sha-256");
    } catch (NoSuchAlgorithmException e) {
      // Shouldn't happen
    } // try/catch

    ByteBuffer numByte = ByteBuffer.allocate(Integer.BYTES);
    numByte.putInt(num);
    md.update(numByte.array());
    md.update(transaction.getSource().getBytes());
    md.update(transaction.getTarget().getBytes());

    ByteBuffer amountByte = ByteBuffer.allocate(Integer.BYTES);
    amountByte.putInt(transaction.getAmount());
    md.update(amountByte.array());

    md.update(prevHash.toString().getBytes());

    ByteBuffer nonceByte = ByteBuffer.allocate(Long.BYTES);
    nonceByte.putLong(nonce);
    md.update(nonceByte.array());

    return md.digest();
  } // computeHash()

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
    return this.trans;
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
  Hash getPrevHash() {
    return this.prevHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  Hash getHash() {
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
    if (this.trans.getSource().equals("")) {
      str.append("Deposit ");
    } else {
      str.append("Source: " + this.trans.getSource());
    } // if/else

    str.append(", Target: " + this.trans.getTarget() + ", Amount: " + this.trans.getAmount()
        + "], Nonce: " + this.getNonce() + ", prevHash: " + this.getPrevHash() + ", hash: "
        + this.getHash() + ")");
    return str.toString();
  } // toString()
} // class Block
