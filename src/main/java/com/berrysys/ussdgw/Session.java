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

import org.apache.http.client.protocol.HttpClientContext;

/**
 * The Class Session.
 */
public class Session {

  /** The dialog id. */
  private String dialogId;

  /** The msisdn. */
  private String msisdn;

  /** The url. */
  private String url;

  /** The local context. */
  private HttpClientContext localContext;

  /**
   * Instantiates a new session.
   *
   * @param dialogId the dialog id
   */
  public Session(String dialogId) {
    this.dialogId = dialogId;
    // TODO Auto-generated constructor stub
  }

  /**
   * Gets the dialog id.
   *
   * @return the dialog id
   */
  public final String getDialogId() {
    return dialogId;
  }

  /**
   * Gets the local context.
   *
   * @return the local context
   */
  public final HttpClientContext getLocalContext() {
    return localContext;
  }

  /**
   * Gets the msisdn.
   *
   * @return the msisdn
   */
  public final String getMsisdn() {
    return msisdn;
  }

  /**
   * Gets the url.
   *
   * @return the url
   */
  public final String getUrl() {
    return url;
  }

  /**
   * Sets the dialog id.
   *
   * @param dialogId the new dialog id
   */
  public final void setDialogId(final String dialogId) {
    this.dialogId = dialogId;
  }

  /**
   * Sets the local context.
   *
   * @param localContext the new local context
   */
  public final void setLocalContext(final HttpClientContext localContext) {
    this.localContext = localContext;
  }

  /**
   * Sets the msisdn.
   *
   * @param msisdn the new msisdn
   */
  public final void setMsisdn(final String msisdn) {
    this.msisdn = msisdn;
  }

  /**
   * Sets the url.
   *
   * @param url the new url
   */
  public final void setUrl(final String url) {
    this.url = url;
  }

}
