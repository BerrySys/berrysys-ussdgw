/*
 * BerrySys SigTran USSDGW
 * Copyright (C) 2015 BerrySys S.A. de C.V. 
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.berrysys.ussdgw;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class ThreadHolder.
 */
public class ThreadHolder {

  /** The instance. */
  private static ThreadHolder instance = new ThreadHolder();

  /**
   * Gets the single instance of ThreadHolder.
   *
   * @return single instance of ThreadHolder
   */
  public static ThreadHolder getInstance() {
    return instance;
  }

  /**
   * Sets the instance.
   *
   * @param instance the new instance
   */
  public static void setInstance(ThreadHolder instance) {
    ThreadHolder.instance = instance;
  }

  /** The dialog listener thread list. */
  private final List<DialogListenerThread> dialogListenerThreadList =
      new ArrayList<DialogListenerThread>();

  /**
   * Gets the dialog listener thread list.
   *
   * @return the dialog listener thread list
   */
  public List<DialogListenerThread> getDialogListenerThreadList() {
    return dialogListenerThreadList;
  }

}
