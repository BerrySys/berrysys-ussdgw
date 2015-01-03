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

import org.fusesource.hawtdispatch.Dispatch;
import org.fusesource.hawtdispatch.DispatchQueue;

/**
 * The Class HawtDispatchUtil.
 */
public class HawtDispatchUtil {


  /**
   * Gets the single instance of HawtDispatchUtil.
   *
   * @return single instance of HawtDispatchUtil
   */
  public static HawtDispatchUtil getInstance() {
    return HawtDispatchUtil.instance;
  }

  /** The Constant instance. */
  private final static HawtDispatchUtil instance = new HawtDispatchUtil();

  /** The queue. */
  DispatchQueue queue = Dispatch.getGlobalQueue(Dispatch.HIGH);



  /**
   * Queue execute.
   *
   * @param runnable the runnable
   */
  public void queueExecute(final Runnable runnable) {
    this.queue.execute(runnable);
  }



}
