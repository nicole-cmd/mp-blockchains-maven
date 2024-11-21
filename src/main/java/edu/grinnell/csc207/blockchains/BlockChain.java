package edu.grinnell.csc207.blockchains;

import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.grinnell.csc207.util.Node1;

/**
 * A full blockchain.
 *
 * @author Khanh Do
 * @author Nicole Gorrell
 */
public class BlockChain implements Iterable<Transaction> {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  Node1<Block> first;

  Node1<Block> last;

  int size = 0;

  HashValidator check;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new blockchain using a validator to check elements.
   *
   * @param check The validator used to check elements.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public BlockChain(HashValidator check) {
    this.check = check;
    Transaction t = new Transaction("", "", 0);
    Hash h = new Hash(new byte[] {0});
    Block firstBlock = new Block(0, t, h, check);
    this.first = new Node1(firstBlock);
    this.last = this.first;
    ++size;
  } // BlockChain(HashValidator)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Mine for a new valid block for the end of the chain, returning that block.
   *
   * @param t The transaction that goes in the block.
   *
   * @return a new block with correct number, hashes, and such.
   */
  public Block mine(Transaction t)   {
    return new Block(10, t, new Hash(new byte[] {7}), 11); // STUB
  } // mine(Transaction)

  /**
   * Get the number of blocks curently in the chain.
   *
   * @return the number of blocks in the chain, including the initial block.
   */
  public int getSize() {
    return this.size;
  } // getSize()

  /**
   * Add a block to the end of the chain.
   *
   * @param blk The block to add to the end of the chain.
   *
   * @throws IllegalArgumentException if (a) the hash is not valid, (b) the hash is not appropriate
   *         for the contents, or (c) the previous hash is incorrect.
   */
  public void append(Block blk) {
    // (a) the hash is not valid
    if (!this.check.isValid(blk.getHash())) {
      throw new IllegalArgumentException("The hash is not valid.");
    } // if

    // (b) the hash is not appropriate for the contents
    Hash temp = new Hash(
        Block.computeHash(blk.getNum(), blk.getTransaction(), blk.getHash(), blk.getNonce()));
    if (!blk.getHash().equals(temp)) {
      throw new IllegalArgumentException("The hash is not appropriate for the contents.");
    } // if

    // (c) the previous hash is incorrect
    if (!this.last.getValue().getHash().equals(blk.getPrevHash())) {
      throw new IllegalArgumentException("The previous hash is incorrect.");
    } // if

    this.last = this.last.insertAfter(blk);
    ++size;
  } // append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's not removed) or true
   *         otherwise (in which case the last block is removed).
   */
  public boolean removeLast() {
    if (this.size == 1) {
      return false;
    } // if
    this.last.remove();
    --size;
    return true;
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last sblock in the chain.
   */
  public Hash getHash() {
    return this.last.getValue().getHash();
  } // getHash()

  /**
   * Determine if the blockchain is correct in that (a) the balances are legal/correct at every
   * step, (b) that every block has a correct previous hash field, (c) that every block has a hash
   * that is correct for its contents, and (d) that every block has a valid hash.
   *
   * @throws Exception If things are wrong at any block.
   */
  public boolean isCorrect() {
    return true; // STUB
  } // isCorrect()

  /**
   * Determine if the blockchain is correct in that (a) the balances are legal/correct at every
   * step, (b) that every block has a correct previous hash field, (c) that every block has a hash
   * that is correct for its contents, and (d) that every block has a valid hash.
   *
   * @throws Exception If things are wrong at any block.
   */
  public void check() throws Exception {
    // STUB
  } // check()

  /**
   * Return an iterator of all the people who participated in the system.
   *
   * @return an iterator of all the people in the system.
   */
  public Iterator<String> users() {
    return new Iterator<String>() {
      public boolean hasNext() {
        return false; // STUB
      } // hasNext()

      public String next() {
        throw new NoSuchElementException(); // STUB
      } // next()
    };
  } // users()

  /**
   * Find one user's balance.
   *
   * @param user The user whose balance we want to find.
   *
   * @return that user's balance (or 0, if the user is not in the system).
   */
  public int balance(String user) {
    return 0; // STUB
  } // balance()

  /**
   * Get an interator for all the blocks in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Block> blocks() {
    return new Iterator<Block>() {
      public boolean hasNext() {
        return false; // STUB
      } // hasNext()

      public Block next() {
        throw new NoSuchElementException(); // STUB
      } // next()
    };
  } // blocks()

  /**
   * Get an interator for all the transactions in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Transaction> iterator() {
    return new Iterator<Transaction>() {
      public boolean hasNext() {
        return false; // STUB
      } // hasNext()

      public Transaction next() {
        throw new NoSuchElementException(); // STUB
      } // next()
    };
  } // iterator()

} // class BlockChain
