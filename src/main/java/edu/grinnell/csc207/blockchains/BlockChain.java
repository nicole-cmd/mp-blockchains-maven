package edu.grinnell.csc207.blockchains;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.HashMap;

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
  Node1 first;

  Node1 last;

  int size = 0;

  HashValidator check;

  HashMap<String, Integer> balances;

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

    this.balances = new HashMap();
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
  public Block mine(Transaction t) {
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
    balances.put(blk.getTransaction().getSource(), blk.getTransaction().getAmount());
  } // append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's not removed) or true
   *         otherwise (in which case the last block is removed).
   */
  public boolean removeLast() {
    String tmp = this.last.getValue().getTransaction().getSource();
    if (this.size == 1) {
      return false;
    } // if
    balances.remove(tmp);
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
    try {
      this.check();
      return true;
    } catch (Exception e) {
      return false;
    } // try/catch
  } // isCorrect()

  /**
   * Determine if the blockchain is correct in that (a) the balances are legal/correct at every
   * step, (b) that every block has a correct previous hash field, (c) that every block has a hash
   * that is correct for its contents, and (d) that every block has a valid hash.
   *
   * @throws Exception If things are wrong at any block.
   */
  public void check() throws Exception {
    Iterator<Block> iterator = this.blocks();
    while (iterator.hasNext()) {
      Block current = iterator.next();
      // (a) the balances are legal/correct at every step
      if (current.getTransaction().getAmount() > this.balances
          .get(current.getTransaction().getSource())) {
        throw new Exception("The balances are not legal/correct at every step.");
      } // if

      // (b) every block has a correct previous hash field
      if (!current.getPrevHash().equals(current.getPrevHash())) {
        throw new Exception("Every block has a correct previous hash field.");
      } // if

      // (c) every block has a hash that is correct for its contents
      Hash temp = new Hash(Block.computeHash(current.getNum(), current.getTransaction(),
          current.getHash(), current.getNonce()));
      if (!current.getHash().equals(temp)) {
        throw new Exception("Every block has a hash that is correct for its contents.");
      } // if

      // (d) every block has a valid hash
      if (!this.check.isValid(current.getHash())) {
        throw new Exception("Every block has a valid hash.");
      } // if
    } // while
  } // check()

  /**
   * Return an iterator of all the people who participated in the system.
   *
   * @return an iterator of all the people in the system.
   */
  public Iterator<String> users() {
    return new Iterator<String>() {
      private final Iterator<Map.Entry<String, Integer>> iterator = balances.entrySet().iterator();
      private Map.Entry<String, Integer> currentEntry;

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      } // hasNext()

      @Override
      public String next() {
        if (!this.hasNext()) {
          throw new NoSuchElementException();
        } // if
        currentEntry = iterator.next();
        return currentEntry.getKey();
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
    if (!this.balances.containsKey(user)) {
      return 0;
    } // if
    return this.balances.get(user);
  } // balance()

  /**
   * Get an interator for all the blocks in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Block> blocks() {
    return new Iterator<Block>() {
      int pos;
      Node1 next;
      Node1 update;

      @Override
      public boolean hasNext() {
        return (pos < size);
      } // hasNext()

      @Override
      public Block next() {
        if (!this.hasNext()) {
          throw new NoSuchElementException();
        } // if

        // Identify the node to update
        this.update = this.next;
        this.next = this.next.getNext();
        // Note the movement
        ++this.pos;

        // And return the value
        return this.update.getValue();
      } // next()
    };
  } // blocks()

  /**
   * Get an interator for all the transactions in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  @Override
  public Iterator<Transaction> iterator() {
    return new Iterator<Transaction>() {
      int pos;
      Node1 next;
      Node1 update;

      @Override
      public boolean hasNext() {
        return (pos < size);
      } // hasNext()

      @Override
      public Transaction next() {
        if (!this.hasNext()) {
          throw new NoSuchElementException();
        } // if

        // Identify the node to update
        this.update = this.next;
        this.next = this.next.getNext();
        // Note the movement
        ++this.pos;

        // And return the value
        return this.update.getValue().getTransaction();
      } // next()
    };
  } // iterator()

} // class BlockChain
