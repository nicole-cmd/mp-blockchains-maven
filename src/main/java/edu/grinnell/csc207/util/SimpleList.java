package edu.grinnell.csc207.util;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * Very simple lists. These lists can only be manipulated by their iterators.
 *
 * @author
 *   Samuel A. Rebelsky.
 *
 * @param <T>
 *   The type of elements stored in the list.
 */
public interface SimpleList<T> extends Iterable<T> {
  /**
   * Get an iterator for the list.
   *
   * @return an iterator for the list.
   */
  public Iterator<T> iterator();

  /**
   * Get a list iterator for the list.
   *
   * @return a list iterator for the list.
   */
  public ListIterator<T> listIterator();
} // interface SimpleList<T>
