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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The Class Starter.
 */
public class Starter {

  /** The log. */
  private static org.apache.logging.log4j.Logger log = LogManager
      .getLogger(Starter.class);

  /**
   * The main method.
   *
   * @param args the arguments
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void main(String[] args) throws IOException {
    StringBuilder banner = new StringBuilder();
    banner.append("\n")
        .append("  ____                       ____            \n")
        .append(" | __ )  ___ _ __ _ __ _   _/ ___| _   _ ___ \n")
        .append(" |  _ \\ / _ \\ '__| '__| | | \\___ \\| | | / __|\n")
        .append(" | |_) |  __/ |  | |  | |_| |___) | |_| \\__ \\\n")
        .append(" |____/ \\___|_| _|_|   \\__, |____/ \\__, |___/\n")
        .append(" / ___|(_) __ _| |_ _ _|___/_ _ __ |___/     \n")
        .append(" \\___ \\| |/ _` | __| '__/ _` | '_ \\          \n")
        .append("  ___) | | (_| | |_| | | (_| | | | |         \n")
        .append(" |____/|_|\\__, |\\__|_|  \\__,_|_| |_|         \n")
        .append("  _   _ __|___/__  ____                      \n")
        .append(" | | | / ___/ ___||  _ \\                     \n")
        .append(" | | | \\___ \\___ \\| | | |                    \n")
        .append(" | |_| |___) |__) | |_| |                    \n")
        .append("  \\___/|____/____/|____/                     \n")
        .append("  / ___| __ _| |_ _____      ____ _ _   _    \n")
        .append(" | |  _ / _` | __/ _ \\ \\ /\\ / / _` | | | |   \n")
        .append(" | |_| | (_| | ||  __/\\ V  V / (_| | |_| |   \n")
        .append("  \\____|\\__,_|\\__\\___| \\_/\\_/ \\__,_|\\__, |\n")
        .append("                                    |___/    \n");
    String berrysysUssdGW = banner.toString();

    log.info(berrysysUssdGW);
    ApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("/app-context.xml");

    List<DialogListener> serverList =
        (List<DialogListener>) applicationContext.getBean("sctpServerList");

    Iterator<DialogListener> i = serverList.iterator();
    while (i.hasNext()) {
      final DialogListener dialogListenerItem = i.next();
      DialogListenerThread dialogListenerThread = new DialogListenerThread();
      dialogListenerThread.setDialogListener(dialogListenerItem);
      dialogListenerThread.start();
      ThreadHolder.getInstance().getDialogListenerThreadList()
          .add(dialogListenerThread);
    }

    Iterator<DialogListenerThread> j =
        ThreadHolder.getInstance().getDialogListenerThreadList().iterator();
    while (j.hasNext()) {
      try {
        j.next().join();
      } catch (InterruptedException e) {
        log.catching(e);
      }

    }
  }
}
