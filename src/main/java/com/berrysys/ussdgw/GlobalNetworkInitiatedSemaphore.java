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

import java.util.concurrent.Semaphore;

/**
 * The Class GlobalNetworkInitiatedSemaphore.
 */
public class GlobalNetworkInitiatedSemaphore {

  /**
   * Gets the single instance of GlobalNetworkInitiatedSemaphore.
   *
   * @return single instance of GlobalNetworkInitiatedSemaphore
   */
  public static GlobalNetworkInitiatedSemaphore getInstance() {
    return instance;
  }

  /** The Constant instance. */
  private static final GlobalNetworkInitiatedSemaphore instance =
      new GlobalNetworkInitiatedSemaphore();

  /** The semaphore. */
  Semaphore semaphore = new Semaphore(Runtime.getRuntime()
      .availableProcessors() * Integer.getInteger("ussdgw.pqueue.size", 1000));

  /**
   * Gets the semaphore.
   *
   * @return the semaphore
   */
  public Semaphore getSemaphore() {
    return semaphore;
  }

  /**
   * Sets the semaphore.
   *
   * @param semaphore the new semaphore
   */
  public void setSemaphore(Semaphore semaphore) {
    this.semaphore = semaphore;
  }

}
