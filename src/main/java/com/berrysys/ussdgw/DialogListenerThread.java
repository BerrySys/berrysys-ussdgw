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

import org.apache.logging.log4j.LogManager;

// TODO: Auto-generated Javadoc
/**
 * The Class DialogListenerThread.
 */
public class DialogListenerThread extends Thread {

  /** The log. */
  private static org.apache.logging.log4j.Logger log = LogManager
      .getLogger(DialogListenerThread.class);

  /** The dialog listener. */
  DialogListener dialogListener;

  /**
   * Gets the dialog listener.
   *
   * @return the dialog listener
   */
  public DialogListener getDialogListener() {
    return dialogListener;
  }

  /**
   * Sets the dialog listener.
   *
   * @param dialogListener the new dialog listener
   */
  public void setDialogListener(DialogListener dialogListener) {
    this.dialogListener = dialogListener;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    try {
      dialogListener.initializeStack();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      log.catching(e);
    }

  }

}
