package edu.grinnell.csc207.blockchains;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.HashMap;

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

  /**
   * The first block in the chain.
   */
  Node1 first;

  /**
   * The last block in the chain.
   */
  Node1 last;

  /**
   * The number of blocks in the chain.
   */
  int size;

  /**
   * The validator used to check elements.
   */
  HashValidator check;

  /**
   * The balances of all the users.
   */
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
    Hash h = new Hash(new byte[] {});
    Block firstBlock = new Block(0, t, h, check);
    this.first = new Node1(firstBlock);
    this.last = this.first;
    this.size = 1;

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
    Hash prevHash = this.last.getValue().getHash();
    Block newBlock = new Block(this.size, t, prevHash, this.check);
    return newBlock;
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
        Block.computeHash(blk.getNum(), blk.getTransaction(), blk.getPrevHash(), blk.getNonce()));
    if (!blk.getHash().equals(temp)) {
      throw new IllegalArgumentException("The hash is not appropriate for the contents.");
    } // if

    // (c) the previous hash is incorrect
    if (!this.last.getValue().getHash().equals(blk.getPrevHash())) {
      throw new IllegalArgumentException("The previous hash is incorrect.");
    } // if

    this.last = this.last.insertAfter(blk);
    ++size;

    if (blk.getTransaction().getSource().equals("")) {
      balances.put(blk.getTransaction().getTarget(),
          balances.getOrDefault(blk.getTransaction().getTarget(), 0)
              + blk.getTransaction().getAmount());
    } else {
      balances.put(blk.getTransaction().getSource(),
          balances.getOrDefault(blk.getTransaction().getSource(), 0)
              - blk.getTransaction().getAmount());
      balances.put(blk.getTransaction().getTarget(),
          balances.getOrDefault(blk.getTransaction().getTarget(), 0)
              + blk.getTransaction().getAmount());
    } // if/else
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
    Transaction t = this.last.getValue().getTransaction();
    if (!t.getSource().isEmpty()) {
      balances.put(t.getSource(), balances.get(t.getSource()) + t.getAmount());
    } // if
    balances.put(t.getTarget(), balances.get(t.getTarget()) - t.getAmount());

    // Find the second-to-last node
    Node1 current = this.first;
    while (current.next != this.last) {
      current = current.next;
    }
    // Remove the last node
    current.next = null;
    this.last = current;
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
    Node1 prev = this.first;
    Node1 current = this.first.next;
    Map<String, Integer> computedBalances = new HashMap<>();
    int maxIterations = size;
    int count = 0;
    while (current != null) {
      if (++count > maxIterations) {
        throw new Exception("Infinite loop detected in blockchain.");
      } // if
      Block currentBlock = current.getValue();
      Transaction transaction = currentBlock.getTransaction();

      // (a) Verify balances
      if (transaction.getAmount() < 0) {
        throw new Exception("Transaction amount is negative.");
      } // if

      if (!transaction.getSource().isEmpty()) {
        int sourceBalance = computedBalances.getOrDefault(transaction.getSource(), 0);
        if (sourceBalance < transaction.getAmount()) {
          throw new Exception("Insufficient balance for source: " + transaction.getSource());
        } // if
        // Deduct from the source
        computedBalances.put(transaction.getSource(), sourceBalance - transaction.getAmount());
      } // if

      // Add to the target
      computedBalances.put(transaction.getTarget(),
          computedBalances.getOrDefault(transaction.getTarget(), 0) + transaction.getAmount());

      // (b) that every block has a correct previous hash field
      if (prev != null && !currentBlock.getPrevHash().equals(prev.getValue().getHash())) {
        throw new Exception("Every block does not have a correct previous hash field.");
      } // if

      // (c) that every block has a hash that is correct for its contents
      Hash temp = new Hash(Block.computeHash(currentBlock.getNum(), transaction,
          currentBlock.getPrevHash(), currentBlock.getNonce()));
      if (!currentBlock.getHash().equals(temp)) {
        throw new Exception("Every block does not have a hash that is correct for its contents.");
      } // if

      // (d) that every block has a valid hash
      if (!this.check.isValid(currentBlock.getHash())) {
        throw new Exception("Every block does not have a valid hash.");
      } // if

      prev = current;
      current = current.next;
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
    int balance = 0;
    Iterator<Block> blocks = this.blocks();
    while (blocks.hasNext()) {
      Block block = blocks.next();
      Transaction transaction = block.getTransaction();
      if (transaction.getSource().equals(user)) {
        balance -= transaction.getAmount();
      } // if
      if (transaction.getTarget().equals(user)) {
        balance += transaction.getAmount();
      } // if
    } // while
    return balance;
  } // balance(String)


  /**
   * Get an interator for all the blocks in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Block> blocks() {
    return new Iterator<Block>() {
      int pos = 0;
      Node1 next = first;

      @Override
      public boolean hasNext() {
        return (pos < size);
      } // hasNext()

      @Override
      public Block next() {
        if (!this.hasNext()) {
          throw new NoSuchElementException();
        } // if
        Node1 current = this.next;
        this.next = this.next.next;
        ++this.pos;
        return current.getValue();
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
      int pos = 0;
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
        this.next = this.next.next;
        // Note the movement
        ++this.pos;

        // And return the value
        return this.update.getValue().getTransaction();
      } // next()
    };
  } // iterator()

} // class BlockChain
