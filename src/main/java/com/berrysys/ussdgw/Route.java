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

import java.util.regex.Pattern;

/**
 * The Class Route.
 */
public class Route {

  /** The ussd string regex. */
  String ussdStringRegex;

  /** The dest url. */
  String destUrl;

  /** The ussd string compiled regex. */
  Pattern ussdStringCompiledRegex;

  /**
   * Gets the dest url.
   *
   * @return the dest url
   */
  public String getDestUrl() {
    return destUrl;
  }

  /**
   * Gets the ussd string compiled regex.
   *
   * @return the ussd string compiled regex
   */
  public Pattern getUssdStringCompiledRegex() {
    return ussdStringCompiledRegex;
  }

  /**
   * Gets the ussd string regex.
   *
   * @return the ussd string regex
   */
  public String getUssdStringRegex() {
    return ussdStringRegex;
  }

  /**
   * Sets the dest url.
   *
   * @param destUrl the new dest url
   */
  public void setDestUrl(String destUrl) {
    this.destUrl = destUrl;
  }

  /**
   * Sets the ussd string compiled regex.
   *
   * @param ussdStringCompiledRegex the new ussd string compiled regex
   */
  public void setUssdStringCompiledRegex(Pattern ussdStringCompiledRegex) {
    this.ussdStringCompiledRegex = ussdStringCompiledRegex;
  }

  /**
   * Sets the ussd string regex.
   *
   * @param ussdStringRegex the new ussd string regex
   */
  public void setUssdStringRegex(String ussdStringRegex) {
    this.ussdStringRegex = ussdStringRegex;
    this.setUssdStringCompiledRegex(Pattern.compile(ussdStringRegex));
  }

}
