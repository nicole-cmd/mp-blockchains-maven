package edu.grinnell.csc207.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;

import edu.grinnell.csc207.blockchains.Block;
import edu.grinnell.csc207.blockchains.BlockChain;
import edu.grinnell.csc207.blockchains.HashValidator;
import edu.grinnell.csc207.blockchains.Transaction;
import edu.grinnell.csc207.util.IOUtils;
import edu.grinnell.csc207.blockchains.Hash;
import edu.grinnell.csc207.blockchains.Node1;

/**
 * A simple UI for our BlockChain class.
 *
 * @author Khanh Do
 * @author Nicole Gorrell
 * @author Samuel A. Rebelsky
 */
public class BlockChainUI {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The number of bytes we validate. Should be set to 3 before submitting.
   */
  static final int VALIDATOR_BYTES = 0;

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Print out the instructions.
   *
   * @param pen The pen used for printing instructions.
   */
  public static void instructions(PrintWriter pen) {
    pen.println("""
        Valid commands:
          mine: discovers the nonce for a given transaction
          append: appends a new block onto the end of the chain
          remove: removes the last block from the end of the chain
          check: checks that the block chain is valid
          users: prints a list of users
          balance: finds a user's balance
          transactions: prints out the chain of transactions
          blocks: prints out the chain of blocks (for debugging only)
          help: prints this list of commands
          quit: quits the program""");
  } // instructions(PrintWriter)

  // +------+--------------------------------------------------------
  // | Main |
  // +------+

  /**
   * Run the UI.
   *
   * @param args Command-line arguments (currently ignored).
   */
  @SuppressWarnings("ConvertToTryWithResources")
  public static void main(String[] args) throws Exception {
    PrintWriter pen = new PrintWriter(System.out, true);
    BufferedReader eyes = new BufferedReader(new InputStreamReader(System.in));

    // Set up our blockchain.
    HashValidator validator = (h) -> {
      if (h.length() < VALIDATOR_BYTES) {
        return false;
      } // if
      for (int v = 0; v < VALIDATOR_BYTES; v++) {
        if (h.get(v) != 0) {
          return false;
        } // if
      } // for
      return true;
    };
    BlockChain chain = new BlockChain(validator);

    instructions(pen);

    boolean done = false;

    String source = "";
    String target = "";
    String user = "";
    int amount = 0;
    int mineCount = 1;
    Block minedBlock = new Block(chain.getSize(), new Transaction(source, target, amount),
                                 new Hash(new byte[] {}), validator);

    while (!done) {
      pen.print("\nCommand: ");
      pen.flush();
      String command = eyes.readLine();
      if (command == null) {
        command = "quit";
      } // if

      switch (command.toLowerCase()) {
        case "append":
          source = IOUtils.readLine(pen, eyes, "Source (return for deposit): ");
          target = IOUtils.readLine(pen, eyes, "Target: ");
          amount = IOUtils.readInt(pen, eyes, "Amount: ");
          Block toAppend = new Block(chain.getSize(), new Transaction(source, target, amount),
              chain.getHash(), minedBlock.getNonce());
          pen.println("Nonce: " + minedBlock.getNonce());

          if (!(mineCount == chain.getSize() + 1)
              || !(minedBlock.getTransaction().equals(toAppend.getTransaction()))) {
            pen.println("Could not append: Invalid hash in appended block: " + toAppend.getHash());
            break;
          } // if
          chain.append(toAppend);
          pen.println("Appended: " + toAppend.toString());
          break;

        case "balance":
          user = IOUtils.readLine(pen, eyes, "User: ");
          pen.println(user + "'s balance is " + chain.balance(user));
          break;

        case "blocks":
          Iterator<Block> iter = chain.blocks();
          while (iter.hasNext()) {
            pen.println(iter.next().toString());
          } // while
          break;

        case "check":
          Node1 current = chain.getCurrent();
          Node1 next = chain.getNext();
          while (current != null && next != null) {
            if (!(current.getValue().getTransaction().getTarget()).equals(
                  next.getValue().getTransaction().getSource())) {
              pen.println("Unknown source in block " + next.getValue().getNum() + ": \""
                          + current.getValue().getTransaction().getSource() + "\"");
            } else if (current.getValue().getTransaction().getAmount()
                       < next.getValue().getTransaction().getAmount()) {
              pen.println("Insufficient balance for"
                          + current.getValue().getTransaction().getSource()
                          + "in block" + next.getValue().getNum() + ": Has"
                          + current.getValue().getTransaction().getAmount()
                          + ", needs " + next.getValue().getTransaction().getAmount());
            } else if (current.getValue().getTransaction().getAmount() < 0) {
              pen.println("Negative amount in block" + current.getValue().getNum() + ": "
                          + current.getValue().getTransaction().getAmount());
            } // if/elif/elif
            current = next;
            next = chain.getNext();
          } // while

          if (chain.isCorrect()) {
            pen.println("The blockchain checks out.");
          } else {
            pen.println("The blockchain does NOT check out.");
          } // if/else
          break;

        case "help":
          instructions(pen);
          break;

        case "mine":
          source = IOUtils.readLine(pen, eyes, "Source (return for deposit): ");
          target = IOUtils.readLine(pen, eyes, "Target: ");
          amount = IOUtils.readInt(pen, eyes, "Amount: ");
          minedBlock = chain.mine(new Transaction(source, target, amount));
          pen.println("\nUse nonce: " + minedBlock.getNonce());
          mineCount++;
          break;

        case "quit":
          done = true;
          break;

        case "remove":
          pen.printf("Removing last block entry...\n", command);
          if (chain.removeLast()) {
            pen.println("Successfully removed block.");
          } else {
            pen.println("Failed to remove block.");
          } // if/else
          break;

        case "transactions":
          Iterator<Transaction> transIter = chain.iterator();
          if (transIter.hasNext()) {
            transIter.next(); // Skip the first transaction
          } // if
          while (transIter.hasNext()) {
            pen.println(transIter.next().toString());
          } // while
          break;

        case "users":
          Iterator<String> userIter = chain.users();
          while (userIter.hasNext()) {
            pen.println(userIter.next());
          } // while
          break;

        default:
          pen.printf("invalid command: '%s'. Try again.\n", command);
          break;
      } // switch
    } // while

    pen.printf("\nGoodbye\n");
    eyes.close();
    pen.close();
  } // main(String[])
} // class BlockChainUI
