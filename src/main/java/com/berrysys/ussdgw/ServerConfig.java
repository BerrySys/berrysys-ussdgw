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

import java.util.List;

import org.mobicents.protocols.ss7.indicator.RoutingIndicator;
import org.mobicents.protocols.ss7.m3ua.ExchangeType;
import org.mobicents.protocols.ss7.m3ua.Functionality;
import org.mobicents.protocols.ss7.m3ua.IPSPType;
import org.mobicents.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.mobicents.protocols.ss7.sccp.impl.parameter.GlobalTitle0100Impl;
import org.mobicents.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.mobicents.protocols.ss7.sccp.parameter.GlobalTitle;
import org.mobicents.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * The Class ServerConfig holds the ussdgw configuration.
 */
public class ServerConfig {

  /** The route list. */
  List<Route> routeList;

  /** The route si. */
  int routeSi = -1;

  /**
   * Gets the route si.
   *
   * @return the route si
   */
  public int getRouteSi() {
    return routeSi;
  }

  /**
   * Sets the route si.
   *
   * @param routeSi the new route si
   */
  public void setRouteSi(int routeSi) {
    this.routeSi = routeSi;
  }

  /**
   * Gets the route opc.
   *
   * @return the route opc
   */
  public int getRouteOpc() {
    return routeOpc;
  }

  /**
   * Sets the route opc.
   *
   * @param routeOpc the new route opc
   */
  public void setRouteOpc(int routeOpc) {
    this.routeOpc = routeOpc;
  }

  /** The route opc. */
  int routeOpc = -1;

  /** The sccp mtp3 dest dest id. */
  int sccpMtp3DestDestId = 1;

  /**
   * Gets the sccp mtp3 dest dest id.
   *
   * @return the sccp mtp3 dest dest id
   */
  public int getSccpMtp3DestDestId() {
    return sccpMtp3DestDestId;
  }

  /**
   * Sets the sccp mtp3 dest dest id.
   *
   * @param sccpMtp3DestDestId the new sccp mtp3 dest dest id
   */
  public void setSccpMtp3DestDestId(int sccpMtp3DestDestId) {
    this.sccpMtp3DestDestId = sccpMtp3DestDestId;
  }

  /** The sccp mtp3 dest sap id. */
  int sccpMtp3DestSapId = 1;

  /**
   * Gets the sccp mtp3 dest sap id.
   *
   * @return the sccp mtp3 dest sap id
   */
  public int getSccpMtp3DestSapId() {
    return sccpMtp3DestSapId;
  }

  /**
   * Sets the sccp mtp3 dest sap id.
   *
   * @param sccpMtp3DestSapId the new sccp mtp3 dest sap id
   */
  public void setSccpMtp3DestSapId(int sccpMtp3DestSapId) {
    this.sccpMtp3DestSapId = sccpMtp3DestSapId;
  }

  /** The mtp3 service point id. */
  int mtp3ServicePointId = 1;

  /** The mtp3 id. */
  int mtp3Id = 1;

  /** The first sls. */
  private int firstSls = 0;

  /** The last sls. */
  private int lastSls = 255;

  /** The sls mask. */
  private int slsMask = 255;

  /** The min asp active for loadbalance. */
  int minAspActiveForLoadbalance = 1;

  /** The ipsp type. */
  IPSPType ipspType = IPSPType.CLIENT;

  /** The exchange type. */
  ExchangeType exchangeType = ExchangeType.SE;

  /** The m3ua funcionality. */
  Functionality m3uaFuncionality = Functionality.SGW;

  /** The m3ua traffic mode type. */
  int m3uaTrafficModeType = TrafficModeType.Loadshare;

  /** The m3ua routing context. */
  long[] m3uaRoutingContext = new long[] {100l};

  /** The sctp single thread. */
  boolean sctpSingleThread = false;

  /** The sctp connect delay. */
  int sctpConnectDelay = 10000;

  /** The sccp remote ssnid. */
  int sccpRemoteSsnid = 0;

  /** The sccp remote ssn flag. */
  int sccpRemoteSsnFlag = 0;

  /** The sccp remote ssn mark prohibited when spc resuming. */
  boolean sccpRemoteSSNMarkProhibitedWhenSpcResuming;

  /** The sccp remote spc id. */
  int sccpRemoteSpcId = 0;

  /** The sccp remote spc flag. */
  int sccpRemoteSpcFlag = 0;

  /** The sccp remote spc mask. */
  int sccpRemoteSpcMask = 0;

  /** The sccp mark prohibited when spc resuming. */
  boolean sccpMarkProhibitedWhenSpcResuming = false;

  /** The sccp remote scp. */
  int sccpRemoteScp = 0;

  /** The sccp remote ssn flag. */
  int sccpRemoteSSNFlag = 0;

  /** The sccp mask. */
  int sccpMask = 0;

  /** The mtp3 user part id. */
  int mtp3UserPartId = 1;

  /** The http port. */
  int httpPort = 8000;

  // MTP Details
  /** The client spc. */
  int clientSpc = 1;

  /** The server spc. */
  int serverSpc = 2;

  GlobalTitle clientGt = new GlobalTitle0100Impl();
          GlobalTitle serverGT =  new GlobalTitle0100Impl();;

  /** The network indicator. */
  int networkIndicator = 2;

  /** The service indicator. */
  int serviceIndicator = 3;

  /** The ssn. */
  int ssn = 8;

  /** The client ssn. */
  int clientSSN = 8;

  /** The server ssn. */
  int serverSSN = 8;

  /** The client ip. */
  String clientIP = "127.0.0.1";

  /** The client port. */
  int clientPort = 2345;

  /** The server ip. */
  String serverIP = "127.0.0.1";

  /** The server post. */
  int serverPort = 3434;

  /** The routing context. */
  int routingContext = 100;

  /** The server association name. */
  String serverAssociationName = "serverAssociation";

  /** The client association name. */
  String clientAssociationName = "clientAssociation";

  /** The server name. */
  String serverName = "testserver";

  /** The sccp client address. */
  SccpAddress sccpClientAddress = new SccpAddressImpl(
      RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, clientGt, 0, ssn);

  /** The sccp server address. */
  SccpAddress sccpServerAddress = new SccpAddressImpl(
      RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, serverGT, 0, ssn);

  private String[] extraHostAddresses = null;

  public String[] getExtraHostAddresses() {
    return extraHostAddresses;
  }

  public void setExtraHostAddresses(String[] extraHostAddresses) {
    this.extraHostAddresses = extraHostAddresses;
  }

  /**
   * Gets the client association name.
   *
   * @return the client association name
   */
  public String getClientAssociationName() {
    return clientAssociationName;
  }

  /**
   * Gets the client ip.
   *
   * @return the client ip
   */
  public String getClientIP() {
    return clientIP;
  }

  /**
   * Gets the client port.
   *
   * @return the client port
   */
  public int getClientPort() {
    return clientPort;
  }

  /**
   * Gets the client spc.
   *
   * @return the client spc
   */
  public int getClientSpc() {
    return clientSpc;
  }

  /**
   * Gets the client ssn.
   *
   * @return the client ssn
   */
  public int getClientSSN() {
    return clientSSN;
  }

  /**
   * Gets the exchange type.
   *
   * @return the exchange type
   */
  public ExchangeType getExchangeType() {
    return exchangeType;
  }

  /**
   * Gets the first sls.
   *
   * @return the first sls
   */
  public int getFirstSls() {
    return firstSls;
  }

  /**
   * Gets the http port.
   *
   * @return the http port
   */
  public int getHttpPort() {
    return httpPort;
  }

  /**
   * Gets the ipsp type.
   *
   * @return the ipsp type
   */
  public IPSPType getIpspType() {
    return ipspType;
  }

  /**
   * Gets the last sls.
   *
   * @return the last sls
   */
  public int getLastSls() {
    return lastSls;
  }

  /**
   * Gets the m3ua funcionality.
   *
   * @return the m3ua funcionality
   */
  public Functionality getM3uaFuncionality() {
    return m3uaFuncionality;
  }

  /**
   * Gets the m3ua routing context.
   *
   * @return the m3ua routing context
   */
  public long[] getM3uaRoutingContext() {
    return m3uaRoutingContext;
  }

  /**
   * Gets the m3ua traffic mode type.
   *
   * @return the m3ua traffic mode type
   */
  public int getM3uaTrafficModeType() {
    return m3uaTrafficModeType;
  }

  /**
   * Gets the min asp active for loadbalance.
   *
   * @return the min asp active for loadbalance
   */
  public int getMinAspActiveForLoadbalance() {
    return minAspActiveForLoadbalance;
  }

  /**
   * Gets the mtp3 id.
   *
   * @return the mtp3 id
   */
  public int getMtp3Id() {
    return mtp3Id;
  }

  /**
   * Gets the mtp3 service point id.
   *
   * @return the mtp3 service point id
   */
  public int getMtp3ServicePointId() {
    return mtp3ServicePointId;
  }

  /**
   * Gets the mtp3 user part id.
   *
   * @return the mtp3 user part id
   */
  public int getMtp3UserPartId() {
    return mtp3UserPartId;
  }

  /**
   * Gets the network indicator.
   *
   * @return the network indicator
   */
  public int getNetworkIndicator() {
    return networkIndicator;
  }

  /**
   * Gets the route list.
   *
   * @return the route list
   */
  public List<Route> getRouteList() {
    return routeList;
  }

  /**
   * Gets the routing context.
   *
   * @return the routing context
   */
  public int getRoutingContext() {
    return routingContext;
  }

  /**
   * Gets the sccp client address.
   *
   * @return the sccp client address
   */
  public SccpAddress getSccpClientAddress() {
    return sccpClientAddress;
  }

  /**
   * Gets the sccp server address.
   *
   * @return the sccp server address
   */
  public SccpAddress getSccpServerAddress() {
    return sccpServerAddress;
  }

  /**
   * Gets the sccp mask.
   *
   * @return the sccp mask
   */
  public int getSccpMask() {
    return sccpMask;
  }

  /**
   * Gets the sccp remote scp.
   *
   * @return the sccp remote scp
   */
  public int getSccpRemoteScp() {
    return sccpRemoteScp;
  }

  /**
   * Gets the sccp remote spc flag.
   *
   * @return the sccp remote spc flag
   */
  public int getSccpRemoteSpcFlag() {
    return sccpRemoteSpcFlag;
  }

  /**
   * Gets the sccp remote spc id.
   *
   * @return the sccp remote spc id
   */
  public int getSccpRemoteSpcId() {
    return sccpRemoteSpcId;
  }

  /**
   * Gets the sccp remote spc mask.
   *
   * @return the sccp remote spc mask
   */
  public int getSccpRemoteSpcMask() {
    return sccpRemoteSpcMask;
  }

  /**
   * Gets the sccp remote ssn flag.
   *
   * @return the sccp remote ssn flag
   */
  public int getSccpRemoteSsnFlag() {
    return sccpRemoteSsnFlag;
  }

  /**
   * Gets the sccp remote ssn flag.
   *
   * @return the sccp remote ssn flag
   */
  public int getSccpRemoteSSNFlag() {
    return sccpRemoteSSNFlag;
  }

  /**
   * Gets the sccp remote ssnid.
   *
   * @return the sccp remote ssnid
   */
  public int getSccpRemoteSsnid() {
    return sccpRemoteSsnid;
  }

  /**
   * Gets the sctp connect delay.
   *
   * @return the sctp connect delay
   */
  public int getSctpConnectDelay() {
    return sctpConnectDelay;
  }

  /**
   * Gets the server association name.
   *
   * @return the server association name
   */
  public String getServerAssociationName() {
    return serverAssociationName;
  }

  /**
   * Gets the server ip.
   *
   * @return the server ip
   */
  public String getServerIP() {
    return serverIP;
  }

  /**
   * Gets the server name.
   *
   * @return the server name
   */
  public String getServerName() {
    return serverName;
  }

  /**
   * Gets the server post.
   *
   * @return the server post
   */
  public int getServerPort() {
    return serverPort;
  }

  /**
   * Gets the server ssn.
   *
   * @return the server ssn
   */
  public int getServerSSN() {
    return serverSSN;
  }

  /**
   * Gets the server spc.
   *
   * @return the server spc
   */
  public int getServerSpc() {
    return serverSpc;
  }

  /**
   * Gets the service indicator.
   *
   * @return the service indicator
   */
  public int getServiceIndicator() {
    return serviceIndicator;
  }

  /**
   * Gets the sls mask.
   *
   * @return the sls mask
   */
  public int getSlsMask() {
    return slsMask;
  }

  /**
   * Gets the ssn.
   *
   * @return the ssn
   */
  public int getSsn() {
    return ssn;
  }

  /**
   * Checks if is sccp mark prohibited when spc resuming.
   *
   * @return true, if is sccp mark prohibited when spc resuming
   */
  public boolean isSccpMarkProhibitedWhenSpcResuming() {
    return sccpMarkProhibitedWhenSpcResuming;
  }

  /**
   * Checks if is sccp remote ssn mark prohibited when spc resuming.
   *
   * @return true, if is sccp remote ssn mark prohibited when spc resuming
   */
  public boolean isSccpRemoteSSNMarkProhibitedWhenSpcResuming() {
    return sccpRemoteSSNMarkProhibitedWhenSpcResuming;
  }

  /**
   * Checks if is sctp single thread.
   *
   * @return true, if is sctp single thread
   */
  public boolean isSctpSingleThread() {
    return sctpSingleThread;
  }

  /**
   * Sets the client association name.
   *
   * @param clientAssociationName the new client association name
   */
  public void setClientAssociationName(String clientAssociationName) {
    this.clientAssociationName = clientAssociationName;
  }

  /**
   * Sets the client ip.
   *
   * @param clientIP the new client ip
   */
  public void setClientIP(String clientIP) {
    this.clientIP = clientIP;
  }

  /**
   * Sets the client port.
   *
   * @param clientPort the new client port
   */
  public void setClientPort(int clientPort) {
    this.clientPort = clientPort;
  }

  /**
   * Sets the client spc.
   *
   * @param clientSpc the new client spc
   */
  public void setClientSpc(int clientSpc) {
    this.clientSpc = clientSpc;
  }

  /**
   * Sets the client ssn.
   *
   * @param clientSSN the new client ssn
   */
  public void setClientSSN(int clientSSN) {
    this.clientSSN = clientSSN;
  }

  /**
   * Sets the exchange type.
   *
   * @param exchangeType the new exchange type
   */
  public void setExchangeType(ExchangeType exchangeType) {
    this.exchangeType = exchangeType;
  }

  /**
   * Sets the first sls.
   *
   * @param firstSls the new first sls
   */
  public void setFirstSls(int firstSls) {
    this.firstSls = firstSls;
  }

  /**
   * Sets the http port.
   *
   * @param httpPort the new http port
   */
  public void setHttpPort(int httpPort) {
    this.httpPort = httpPort;
  }

  /**
   * Sets the ipsp type.
   *
   * @param ipspType the new ipsp type
   */
  public void setIpspType(IPSPType ipspType) {
    this.ipspType = ipspType;
  }

  /**
   * Sets the last sls.
   *
   * @param lastSls the new last sls
   */
  public void setLastSls(int lastSls) {
    this.lastSls = lastSls;
  }

  /**
   * Sets the m3ua funcionality.
   *
   * @param m3uaFuncionality the new m3ua funcionality
   */
  public void setM3uaFuncionality(Functionality m3uaFuncionality) {
    this.m3uaFuncionality = m3uaFuncionality;
  }

  /**
   * Sets the m3ua routing context.
   *
   * @param m3uaRoutingContext the new m3ua routing context
   */
  public void setM3uaRoutingContext(long[] m3uaRoutingContext) {
    this.m3uaRoutingContext = m3uaRoutingContext;
  }

  /**
   * Sets the m3ua traffic mode type.
   *
   * @param m3uaTrafficModeType the new m3ua traffic mode type
   */
  public void setM3uaTrafficModeType(int m3uaTrafficModeType) {
    this.m3uaTrafficModeType = m3uaTrafficModeType;
  }

  /**
   * Sets the min asp active for loadbalance.
   *
   * @param minAspActiveForLoadbalance the new min asp active for loadbalance
   */
  public void setMinAspActiveForLoadbalance(int minAspActiveForLoadbalance) {
    this.minAspActiveForLoadbalance = minAspActiveForLoadbalance;
  }

  /**
   * Sets the mtp3 id.
   *
   * @param mtp3Id the new mtp3 id
   */
  public void setMtp3Id(int mtp3Id) {
    this.mtp3Id = mtp3Id;
  }

  /**
   * Sets the mtp3 service point id.
   *
   * @param mtp3ServicePointId the new mtp3 service point id
   */
  public void setMtp3ServicePointId(int mtp3ServicePointId) {
    this.mtp3ServicePointId = mtp3ServicePointId;
  }

  /**
   * Sets the mtp3 user part id.
   *
   * @param mtp3UserPartId the new mtp3 user part id
   */
  public void setMtp3UserPartId(int mtp3UserPartId) {
    this.mtp3UserPartId = mtp3UserPartId;
  }

  /**
   * Sets the network indicator.
   *
   * @param nETWORK_INDICATOR the new network indicator
   */
  public void setNetworkIndicator(int nETWORK_INDICATOR) {
    networkIndicator = nETWORK_INDICATOR;
  }

  /**
   * Sets the route list.
   *
   * @param routeList the new route list
   */
  public void setRouteList(List<Route> routeList) {
    this.routeList = routeList;
  }

  /**
   * Sets the routing context.
   *
   * @param rc the new routing context
   */
  public void setRoutingContext(int rc) {
    routingContext = rc;
  }

  /**
   * Sets the sccp client address.
   *
   * @param sca the new sccp client address
   */
  public void setSccpClientAddress(SccpAddress sca) {
    sccpClientAddress = sca;
  }

  /**
   * Sets the sccp server address.
   *
   * @param ssa the new sccp server address
   */
  public void setSccpServerAddress(SccpAddress ssa) {
    sccpServerAddress = ssa;
  }

  /**
   * Sets the sccp mark prohibited when spc resuming.
   *
   * @param sccpMarkProhibitedWhenSpcResuming the new sccp mark prohibited when spc resuming
   */
  public void setSccpMarkProhibitedWhenSpcResuming(
      boolean sccpMarkProhibitedWhenSpcResuming) {
    this.sccpMarkProhibitedWhenSpcResuming = sccpMarkProhibitedWhenSpcResuming;
  }

  /**
   * Sets the sccp mask.
   *
   * @param sccpMask the new sccp mask
   */
  public void setSccpMask(int sccpMask) {
    this.sccpMask = sccpMask;
  }

  /**
   * Sets the sccp remote scp.
   *
   * @param sccpRemoteScp the new sccp remote scp
   */
  public void setSccpRemoteScp(int sccpRemoteScp) {
    this.sccpRemoteScp = sccpRemoteScp;
  }

  /**
   * Sets the sccp remote spc flag.
   *
   * @param sccpRemoteSpcFlag the new sccp remote spc flag
   */
  public void setSccpRemoteSpcFlag(int sccpRemoteSpcFlag) {
    this.sccpRemoteSpcFlag = sccpRemoteSpcFlag;
  }

  /**
   * Sets the sccp remote spc id.
   *
   * @param sccpRemoteSpcId the new sccp remote spc id
   */
  public void setSccpRemoteSpcId(int sccpRemoteSpcId) {
    this.sccpRemoteSpcId = sccpRemoteSpcId;
  }

  /**
   * Sets the sccp remote spc mask.
   *
   * @param sccpRemoteSpcMask the new sccp remote spc mask
   */
  public void setSccpRemoteSpcMask(int sccpRemoteSpcMask) {
    this.sccpRemoteSpcMask = sccpRemoteSpcMask;
  }

  /**
   * Sets the sccp remote ssn flag.
   *
   * @param sccpRemoteSsnFlag the new sccp remote ssn flag
   */
  public void setSccpRemoteSsnFlag(int sccpRemoteSsnFlag) {
    this.sccpRemoteSsnFlag = sccpRemoteSsnFlag;
  }

  /**
   * Sets the sccp remote ssn flag.
   *
   * @param sccpRemoteSSNFlag the new sccp remote ssn flag
   */
  public void setSccpRemoteSSNFlag(int sccpRemoteSSNFlag) {
    this.sccpRemoteSSNFlag = sccpRemoteSSNFlag;
  }

  /**
   * Sets the sccp remote ssnid.
   *
   * @param sccpRemoteSsnid the new sccp remote ssnid
   */
  public void setSccpRemoteSsnid(int sccpRemoteSsnid) {
    this.sccpRemoteSsnid = sccpRemoteSsnid;
  }

  /**
   * Sets the sccp remote ssn mark prohibited when spc resuming.
   *
   * @param sccpRemoteSSNMarkProhibitedWhenSpcResuming the new sccp remote ssn mark prohibited when
   *        spc resuming
   */
  public void setSccpRemoteSSNMarkProhibitedWhenSpcResuming(
      boolean sccpRemoteSSNMarkProhibitedWhenSpcResuming) {
    this.sccpRemoteSSNMarkProhibitedWhenSpcResuming =
        sccpRemoteSSNMarkProhibitedWhenSpcResuming;
  }

  /**
   * Sets the sctp connect delay.
   *
   * @param sctpConnectDelay the new sctp connect delay
   */
  public void setSctpConnectDelay(int sctpConnectDelay) {
    this.sctpConnectDelay = sctpConnectDelay;
  }

  /**
   * Sets the sctp single thread.
   *
   * @param sctpSingleThread the new sctp single thread
   */
  public void setSctpSingleThread(boolean sctpSingleThread) {
    this.sctpSingleThread = sctpSingleThread;
  }

  /**
   * Sets the server association name.
   *
   * @param san the new server association name
   */
  public void setServerAssociationName(String san) {
    serverAssociationName = san;
  }

  /**
   * Sets the server ip.
   *
   * @param si the new server ip
   */
  public void setServerIP(String si) {
    serverIP = si;
  }

  /**
   * Sets the server name.
   *
   * @param sn the new server name
   */
  public void setServerName(String sn) {
    serverName = sn;
  }

  /**
   * Sets the server post.
   *
   * @param sp the new server post
   */
  public void setServerPort(int sp) {
    serverPort = sp;
  }

  /**
   * Sets the server ssn.
   *
   * @param ss the new server ssn
   */
  public void setServerSSN(int ss) {
    serverSSN = ss;
  }

  /**
   * Sets the server spc.
   *
   * @param ss the new server spc
   */
  public void setServerSpc(int ss) {
    serverSpc = ss;
  }

  /**
   * Sets the service indicator.
   *
   * @param si the new service indicator
   */
  public void setServiceIndicator(int si) {
    serviceIndicator = si;
  }

  /**
   * Sets the sls mask.
   *
   * @param slsMask the new sls mask
   */
  public void setSlsMask(int slsMask) {
    this.slsMask = slsMask;
  }

  /**
   * Sets the ssn.
   *
   * @param ssn the new ssn
   */
  public void setSsn(int ssn) {
    this.ssn = ssn;
  }

}
