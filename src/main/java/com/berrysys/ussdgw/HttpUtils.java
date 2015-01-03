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
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.mobicents.protocols.ss7.map.api.MAPException;
import org.mobicents.protocols.ss7.map.api.service.supplementary.MAPDialogSupplementary;
import org.mobicents.protocols.ss7.map.api.service.supplementary.ProcessUnstructuredSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSNotifyRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSRequest;
import org.mobicents.ussdgateway.Dialog;
import org.mobicents.ussdgateway.EventsSerializeFactory;

/**
 * The Class HttpUtils.
 */
public class HttpUtils {

  /** The log. */
  private static org.apache.logging.log4j.Logger log = LogManager
      .getLogger(HttpUtils.class);

  /**
   * Do post.
   *
   * @param req the req
   * @param resp the resp
   * @param dialogListener the dialog listener
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void doPost(HttpServletRequest req, HttpServletResponse resp,
      final DialogListener dialogListener) throws IOException {

    boolean available =
        GlobalNetworkInitiatedSemaphore.getInstance().getSemaphore()
            .tryAcquire();

    if (!available) {
      queueFullLogic(resp);
      return;
    }

    final String ussdAppUrl = req.getHeader("ussd-app-url");

    log.trace(String.format("ussd-app-url: %s", ussdAppUrl));

    final String payload = getPayload(req);

    log.trace(String.format("payload: %s", payload));

    HawtDispatchUtil.getInstance().queueExecute(new Runnable() {

      @Override
      public void run() {
        processHttpRequest(payload, ussdAppUrl, dialogListener);
      }

    });

    String response = "Request received successfully.";

    resp.setContentType("text/html");
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getWriter().println(response);

  }

  /**
   * Queue full logic.
   *
   * @param resp the resp
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private static void queueFullLogic(HttpServletResponse resp)
      throws IOException {
    // TODO Auto-generated method stub
    String response = "Message Queue full";
    resp.setContentType("text/html");
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getWriter().println(response);

  }

  /**
   * Gets the payload.
   *
   * @param req the req
   * @return the payload
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private static String getPayload(HttpServletRequest req) throws IOException {
    InputStream is = req.getInputStream();
    StringWriter writer = new StringWriter();
    try {
      IOUtils.copy(is, writer);
    } catch (IOException e1) {
      log.catching(e1);
    } finally {
      try {
        is.close();
      } catch (IOException e1) {
        log.catching(e1);
      }
    }

    final String payload = writer.toString();
    return payload;
  }

  /**
   * Process http request.
   *
   * @param payload the payload
   * @param ussdAppUrl the ussd app url
   * @param dialogListenerInstance the dialog listener instance
   */
  static void processHttpRequest(final String payload, final String ussdAppUrl,
      final DialogListener dialogListenerInstance) {
    log.entry();
    try {

      EventsSerializeFactory eventsSerializeFactory = null;

      try {
        eventsSerializeFactory = new EventsSerializeFactory();
      } catch (XMLStreamException e) {
        // TODO Auto-generated catch block
        log.catching(e);
      }
      Dialog dialog = null;
      try {
        dialog = eventsSerializeFactory.deserialize(payload.getBytes());
      } catch (XMLStreamException e) {

        log.catching(e);
      }

      MAPDialogSupplementary mapDialogSupplementary = null;


      switch (dialog.getMAPMessage().getMessageType()) {
        
        case unstructuredSSRequest_Request:

          UnstructuredSSRequest unstructuredSSRequestInd = null;
          unstructuredSSRequestInd =
              (UnstructuredSSRequest) dialog.getMAPMessage();
          unstructuredSSRequestInd.setInvokeId(1);
          mapDialogSupplementary = unstructuredSSRequestInd.getMAPDialog();

          try {
            dialogListenerInstance.addUnstructuredSSRequestNetworkInitiated(
                unstructuredSSRequestInd, mapDialogSupplementary, ussdAppUrl,
                dialog);
          } catch (Exception e1) {
            // TODO Auto-generated catch block
            log.catching(e1);
          }


        
        case unstructuredSSNotify_Request:

          UnstructuredSSNotifyRequest unstructuredSSNotifyRequest =
              (UnstructuredSSNotifyRequest) dialog.getMAPMessage();

          mapDialogSupplementary = unstructuredSSNotifyRequest.getMAPDialog();

          unstructuredSSNotifyRequest.setInvokeId(1);
          try {
            dialogListenerInstance.addUnstructuredSSNotifyRequest(
                unstructuredSSNotifyRequest, mapDialogSupplementary,
                ussdAppUrl, dialog);
          } catch (MAPException e) {
            log.catching(e);
          }
          break;
        default:
          log.info(String.format("Unknown message: %s", dialog));
          break;
      }
    } catch (Exception e) {
      log.catching(e);
    } finally {
      GlobalNetworkInitiatedSemaphore.getInstance().getSemaphore().release();
    }
    log.exit();
  }

}
