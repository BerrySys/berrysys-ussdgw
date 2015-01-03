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

import org.apache.logging.log4j.LogManager;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * The Class Utils.
 */
public class Utils {

  /** The log. */
  private static org.apache.logging.log4j.Logger log = LogManager
      .getLogger(Utils.class);

  /** The Constant instance. */
  private final static Utils instance = new Utils();

  /**
   * Gets the single instance of Utils.
   *
   * @return single instance of Utils
   */
  public static Utils getInstance() {
    return instance;
  }

  /**
   * Convert2 json.
   *
   * @param object the object
   * @return the string
   */
  public String convert2Json(Object object) {
    String response = null;

    ObjectMapper mapper = new ObjectMapper();

    mapper
        .configure(
            org.codehaus.jackson.map.SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
            false);

    try {
      response = mapper.writeValueAsString(object);
    } catch (IOException e) {
      log.catching(e);
    }
    return response;
  }

}
