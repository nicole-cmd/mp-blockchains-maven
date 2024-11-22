package edu.grinnell.csc207.util;

import edu.grinnell.csc207.blockchains.Block;

/**
 * Nodes for singly-linked structures.
 *
 * @author
 *
 * @param <T> The type of value stored in the nodes (and structures built from the nodes).
 */
public class Node1 {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The stored value.
   */
  Block value;

  /**
   * The next node.
   */
  Node1 next;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new node.
   *
   * @param val The value to be stored in the node.
   * @param nextNode The next node in the list (or null, if it's the end of the list).
   */
  public Node1(Block val, Node1 nextNode) {
    this.value = val;
    this.next = nextNode;
  } // Node1(Block, Node1<Block>)

  /**
   * Create a new node with no next link (e.g., if it's at the end of the list). Included primarily
   * for symmetry.
   *
   * @param prevNode The previous node in the list (or null, if it's the front of the list).
   * @param val The value to be stored in the node.
   */
  public Node1(Block val) {
    this(val, null);
  } // Node1(Block))

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Get the value stored in this node.
   *
   * @return the value stored in the node.
   */
  public Block getValue() {
    return this.value;
  } // getValue()

  /**
   * Get the next node.
   *
   * @return the next node.
   */
  public Node1 getNext() {
    return this.next;
  } // getNext()

  /**
   * Insert a new value after this node.
   *
   * @param val The value to insert.
   * @return The newly created node that contains the value.
   */
  public Node1 insertAfter(Block val) {
    Node1 tmp;
    if (this.next == null) {
      tmp = new Node1(val);
    } else {
      tmp = new Node1(val, this.next);
    }
    this.next = tmp;
    return tmp;
  } // insertAfter

  /**
   * Remove this node.
   */
  public void remove() {
    if (this.next == null) {
      this.value = null;
    } else {
      this.value = this.next.value;
      this.next = this.next.next;
    } // if/else
  } // remove()
} // Node1<Block>
