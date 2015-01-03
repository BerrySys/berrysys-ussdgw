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
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import javolution.xml.stream.XMLStreamException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.ManagementImpl;
import org.mobicents.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.mobicents.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;
import org.mobicents.protocols.ss7.m3ua.parameter.RoutingContext;
import org.mobicents.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.mobicents.protocols.ss7.map.MAPStackImpl;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContext;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContextName;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.mobicents.protocols.ss7.map.api.MAPDialog;
import org.mobicents.protocols.ss7.map.api.MAPDialogListener;
import org.mobicents.protocols.ss7.map.api.MAPException;
import org.mobicents.protocols.ss7.map.api.MAPMessage;
import org.mobicents.protocols.ss7.map.api.MAPParameterFactory;
import org.mobicents.protocols.ss7.map.api.MAPProvider;
import org.mobicents.protocols.ss7.map.api.dialog.MAPAbortProviderReason;
import org.mobicents.protocols.ss7.map.api.dialog.MAPAbortSource;
import org.mobicents.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic;
import org.mobicents.protocols.ss7.map.api.dialog.MAPRefuseReason;
import org.mobicents.protocols.ss7.map.api.dialog.MAPUserAbortChoice;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.mobicents.protocols.ss7.map.api.primitives.AddressString;
import org.mobicents.protocols.ss7.map.api.primitives.IMSI;
import org.mobicents.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.mobicents.protocols.ss7.map.api.service.supplementary.MAPDialogSupplementary;
import org.mobicents.protocols.ss7.map.api.service.supplementary.MAPServiceSupplementaryListener;
import org.mobicents.protocols.ss7.map.api.service.supplementary.ProcessUnstructuredSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.ProcessUnstructuredSSResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSNotifyRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSNotifyResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSResponse;
import org.mobicents.protocols.ss7.sccp.impl.SccpStackImpl;
import org.mobicents.protocols.ss7.tcap.api.MessageType;
import org.mobicents.protocols.ss7.tcap.asn.ApplicationContextName;
import org.mobicents.protocols.ss7.tcap.asn.comp.Problem;
import org.mobicents.ussdgateway.Dialog;
import org.mobicents.ussdgateway.DialogType;
import org.mobicents.ussdgateway.EventsSerializeFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.ByteStreams;

/**
 * The listener interface for receiving dialog events. The class that is interested in processing a
 * dialog event implements this interface, and the object created with that class is registered with
 * a component using the component's <code>addDialogListener<code> method. When
 * the dialog event occurs, that object's appropriate
 * method is invoked.
 *
 * @see DialogEvent
 */
public class DialogListener implements MAPDialogListener,
    MAPServiceSupplementaryListener {

  /**
   * Gets the cache.
   *
   * @return the cache
   */
  public static Cache<String, Session> getCache() {
    log.entry();
    return log.exit(__cache);
  }

  /**
   * Sets the cache.
   *
   * @param cache the cache
   */
  public static void setCache(Cache<String, Session> cache) {
    log.entry(cache);
    DialogListener.__cache = cache;
    log.exit();
  }


  /** The log. */
  private static org.apache.logging.log4j.Logger log = LogManager
      .getLogger(DialogListener.class);

  /** The server config. */
  ServerConfig serverConfig;

  /** The factory. */
  ParameterFactoryImpl factory = new ParameterFactoryImpl();

  /** The __cache. */
  public static Cache<String, Session> __cache = CacheBuilder
      .newBuilder()
      .expireAfterWrite(Integer.getInteger("ussdgw.session.timeout", 600),
          TimeUnit.SECONDS)
      .maximumSize(Integer.getInteger("ussdgw.cache.size", 32768)).build();

  /** The xml factory. */
  EventsSerializeFactory xmlFactory = null;

  /** The synchronization object. */
  private static Object synchronizationObject = new Object();

  /** The management impl instance. */
  private ManagementImpl managementImplInstance;

  /** The m3ua management impl instance. */
  private M3UAManagementImpl m3uaManagementImplInstance;

  /** The sccp stack impl instance. */
  private SccpStackImpl sccpStackImplInstance;

  /** The map stack impl instance. */
  private MAPStackImpl mapStackImplInstance;

  /** The map provider instance. */
  private MAPProvider mapProviderInstance;


  /**
   * Adds the unstructured ss notify request.
   *
   * @param unstructuredSSNotifyRequest the unstructured ss notify request
   * @param mapDialogSupplementary the map dialog supplementary
   * @param url the url
   * @param dialog the dialog
   * @throws MAPException the MAP exception
   */
  protected void addUnstructuredSSNotifyRequest(
      UnstructuredSSNotifyRequest unstructuredSSNotifyRequest,
      MAPDialogSupplementary mapDialogSupplementary, String url, Dialog dialog)
      throws MAPException {

    log.entry(unstructuredSSNotifyRequest, mapDialogSupplementary, url, dialog);

    if (mapDialogSupplementary == null) {
      MAPParameterFactory mapParameterFactory =
          this.mapProviderInstance.getMAPParameterFactory();
      ISDNAddressString origReference =
          mapParameterFactory.createISDNAddressString(dialog.getOrigReference()
              .getAddressNature(),
              dialog.getOrigReference().getNumberingPlan(), dialog
                  .getOrigReference().getAddress());
      ISDNAddressString destReference =
          mapParameterFactory.createISDNAddressString(dialog.getDestReference()
              .getAddressNature(),
              dialog.getDestReference().getNumberingPlan(), dialog
                  .getDestReference().getAddress());

      mapDialogSupplementary =
          this.mapProviderInstance.getMAPServiceSupplementary()
              .createNewDialog(
                  MAPApplicationContext.getInstance(
                      MAPApplicationContextName.networkUnstructuredSsContext,
                      MAPApplicationContextVersion.version2),
                  this.getServerConfig().getSccpServerAddress(), origReference,
                  this.getServerConfig().getSccpClientAddress(), destReference);

    }
    String dialogId = String.valueOf(mapDialogSupplementary.getLocalDialogId());
    Session session = null;
    try {
      session =
          getSession(dialogId, dialog.getDestReference().getAddress(), url);
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      log.catching(e);
    }

    this.putSession(dialogId, session);
    mapDialogSupplementary.addUnstructuredSSNotifyRequest(
        unstructuredSSNotifyRequest.getDataCodingScheme(),
        unstructuredSSNotifyRequest.getUSSDString(),
        unstructuredSSNotifyRequest.getAlertingPattern(),
        unstructuredSSNotifyRequest.getMSISDNAddressString());

    try {
      mapDialogSupplementary.send();
    } catch (Exception e) {
      log.catching(e);
    }
    log.exit();
  }

  /**
   * Adds the unstructured ss request.
   *
   * @param unstructuredSSRequestIndication the unstructured ss request indication
   * @param mapDialogSupplementary the map dialog supplementary
   * @throws MAPException the MAP exception
   */
  protected void addUnstructuredSSRequest(
      UnstructuredSSRequest unstructuredSSRequestIndication,
      MAPDialogSupplementary mapDialogSupplementary) throws MAPException {
    log.entry(unstructuredSSRequestIndication, mapDialogSupplementary);

    if (mapDialogSupplementary == null) {
      log.exit();
      return;
    }
    mapDialogSupplementary.addUnstructuredSSRequest(
        unstructuredSSRequestIndication.getDataCodingScheme(),
        unstructuredSSRequestIndication.getUSSDString(), null, null);
    try {
      mapDialogSupplementary.send();
    } finally {
    }
    log.exit();
  }


  /**
   * Gets the m3ua management impl instance.
   *
   * @return the m3ua management impl instance
   */
  public M3UAManagementImpl getM3uaManagementImplInstance() {
    return m3uaManagementImplInstance;
  }

  /**
   * Gets the management impl instance.
   *
   * @return the management impl instance
   */
  public ManagementImpl getManagementImplInstance() {
    return managementImplInstance;
  }

  /**
   * Gets the map provider instance.
   *
   * @return the map provider instance
   */
  public MAPProvider getMapProviderInstance() {
    return mapProviderInstance;
  }

  /**
   * Gets the map stack impl instance.
   *
   * @return the map stack impl instance
   */
  public MAPStackImpl getMapStackImplInstance() {
    return mapStackImplInstance;
  }

  /**
   * Gets the sccp stack impl instance.
   *
   * @return the sccp stack impl instance
   */
  public SccpStackImpl getSccpStackImplInstance() {
    return sccpStackImplInstance;
  }

  /**
   * Gets the server config.
   *
   * @return the server config
   */
  public ServerConfig getServerConfig() {
    log.entry();
    return log.exit(serverConfig);
  }

  /**
   * Gets the session.
   *
   * @param dialogId the dialog id
   * @param msisdn the msisdn
   * @param url the url
   * @return the session
   * @throws ExecutionException the execution exception
   */
  private Session getSession(final String dialogId, final String msisdn,
      final String url) throws ExecutionException {
    log.entry(dialogId, msisdn, url);

    Session session =
        DialogListener.getCache().get(dialogId, new Callable<Session>() {
          @Override
          public Session call() {
            log.entry();
            return log.exit(new Session(dialogId));
          }
        });

    if (session.getLocalContext() == null) {

      session.setLocalContext(HttpClientContext.create());
      session.getLocalContext().setCookieStore(new BasicCookieStore());

    }

    if (session.getMsisdn() == null) {
      session.setMsisdn(msisdn);
    }
    if (session.getUrl() == null) {
      session.setUrl(url);
    }
    try {
      log.debug("Session: %s", Utils.getInstance().convert2Json(session));
    } catch (Exception e) {
      log.catching(e);
    }
    return log.exit(session);
  }

  /**
   * Gets the URL from ussd request.
   *
   * @param ussdString the ussd string
   * @return the URL from ussd request
   */
  private String getURLFromUSSDRequest(String ussdString) {
    // TODO Auto-generated method stub
    log.entry(ussdString);
    Iterator<Route> i = this.getServerConfig().getRouteList().iterator();
    while (i.hasNext()) {
      Route route = i.next();
      if (log.isDebugEnabled())
        log.debug(String.format("Comparing ussdString %s with regex: %s",
            ussdString, route.getUssdStringCompiledRegex().pattern()));
      Matcher m = route.getUssdStringCompiledRegex().matcher(ussdString);
      if (m.matches())
        return log.exit(route.getDestUrl());
    }
    return log.exit(null);
  }

  /**
   * Gets the xml dialog type.
   *
   * @param mt the mt
   * @return the xml dialog type
   */
  private DialogType getXmlDialogType(MessageType mt) {
    log.entry(mt);
    MessageType mtCase = MessageType.Begin;

    if (mt.equals(mtCase)) {
      return log.exit(DialogType.BEGIN);
    }

    mtCase = MessageType.Continue;

    if (mt.equals(mtCase)) {
      return log.exit(DialogType.CONTINUE);
    }

    mtCase = MessageType.End;

    if (mt.equals(mtCase)) {
      return log.exit(DialogType.END);
    }

    mtCase = MessageType.Abort;

    if (mt.equals(mtCase)) {
      return log.exit(DialogType.ABORT);
    }
    return log.exit(null);
  }

  /**
   * Gets the xml factory.
   *
   * @return the xml factory
   */
  public EventsSerializeFactory getXmlFactory() {
    if (xmlFactory == null) {
      synchronized (synchronizationObject) {
        if (xmlFactory == null)
          try {
            xmlFactory = new EventsSerializeFactory();
          } catch (XMLStreamException e) {

            log.catching(e);
          }

      }
    }
    return xmlFactory;
  }

  /**
   * Inits the http server.
   */
  private void initHttpServer() {
    final int httpPort = this.getServerConfig().getHttpPort();
    final DialogListener dialogListener = this;
    Server server = new Server(httpPort);

    ServletHandler handler = new ServletHandler();
    server.setHandler(handler);

    ServletHolder sh = new ServletHolder();
    EmbeddedServlet embeddedServlet = new EmbeddedServlet();

    embeddedServlet.setDialogListener(dialogListener);
    sh.setServlet(embeddedServlet);
    handler.addServletWithMapping(sh, "/ussdgw");

    HttpServerThread httpServerThread = new HttpServerThread();
    httpServerThread.setJettyServer(server);
    httpServerThread.start();
  }

  /**
   * Initialize stack.
   *
   * @throws Exception the exception
   */
  protected void initializeStack() throws Exception {
    log.entry();
    initHttpServer();
    initSctpStack();
    initM3uaStack();
    initSccpStack();
    initMapStack();

    m3uaManagementImplInstance.startAsp("RASP_"
        + this.getServerConfig().getServerName());

    log.info(String.format("Started. ServerName: %s",
        this.serverConfig.getServerName()));
    log.exit();
  }

  /**
   * Inits the m3ua stack.
   *
   * @throws Exception the exception
   */
  private void initM3uaStack() throws Exception {
    log.entry();
    this.m3uaManagementImplInstance =
        new M3UAManagementImpl(this.serverConfig.getServerName());
    this.m3uaManagementImplInstance
        .setTransportManagement(this.managementImplInstance);
    this.m3uaManagementImplInstance.start();
    this.m3uaManagementImplInstance.removeAllResourses();

    RoutingContext rc =
        factory.createRoutingContext(this.serverConfig.getM3uaRoutingContext());
    TrafficModeType trafficModeType =
        factory.createTrafficModeType(this.serverConfig
            .getM3uaTrafficModeType());
    this.m3uaManagementImplInstance.createAs(
        String.format("RAS_%s", this.getServerConfig().getServerName()),
        this.serverConfig.getM3uaFuncionality(),
        this.serverConfig.getExchangeType(), this.serverConfig.getIpspType(),
        rc, trafficModeType, this.serverConfig.getMinAspActiveForLoadbalance(),
        null);

    this.m3uaManagementImplInstance.createAspFactory(String.format("RASP_%s",
        this.getServerConfig().getServerName()), this.getServerConfig()
        .getServerAssociationName());

    this.m3uaManagementImplInstance.assignAspToAs(
        String.format("RAS_%s", this.getServerConfig().getServerName()),
        String.format("RASP_%s", this.getServerConfig().getServerName()));

    this.m3uaManagementImplInstance.addRoute(this.serverConfig.getClientSpc(),
        this.serverConfig.getRouteOpc(), this.serverConfig.getRouteSi(),
        String.format("RAS_%s", this.getServerConfig().getServerName()));
    log.exit();
  }

  /**
   * Inits the map stack.
   */
  private void initMapStack() {
    log.entry();
    this.mapStackImplInstance =
        new MAPStackImpl(this.serverConfig.getClientAssociationName(),
            this.sccpStackImplInstance.getSccpProvider(),
            this.serverConfig.getClientSSN());
    this.mapProviderInstance = this.mapStackImplInstance.getMAPProvider();

    this.mapProviderInstance.addMAPDialogListener(this);
    this.mapProviderInstance.getMAPServiceSupplementary()
        .addMAPServiceListener(this);

    this.mapProviderInstance.getMAPServiceSupplementary().acivate();

    try {
      this.mapStackImplInstance.start();
    } catch (Exception e) {
      log.catching(e);
    }
    log.exit();
  }

  /**
   * Inits the sccp stack.
   */
  private void initSccpStack() {
    log.entry();
    this.sccpStackImplInstance =
        new SccpStackImpl(String.format("SCCPStackCache_%s", this
            .getServerConfig().getServerName()));

    this.sccpStackImplInstance.setMtp3UserPart(this.getServerConfig()
        .getMtp3UserPartId(), this.m3uaManagementImplInstance);

    this.sccpStackImplInstance.start();
    this.sccpStackImplInstance.removeAllResourses();


    try {
      this.sccpStackImplInstance.getSccpResource().addRemoteSpc(
          this.getServerConfig().getSccpRemoteSpcId(),
          this.getServerConfig().getClientSpc(),
          this.getServerConfig().getSccpRemoteSpcFlag(),
          this.getServerConfig().getSccpRemoteSpcMask());
    } catch (Exception e1) {

      log.catching(e1);

    }
    try {
      this.sccpStackImplInstance.getSccpResource()
          .addRemoteSsn(
              this.getServerConfig().getSccpRemoteSsnid(),
              this.getServerConfig().getClientSpc(),
              this.getServerConfig().getSsn(),
              this.getServerConfig().getSccpRemoteSsnFlag(),
              this.getServerConfig()
                  .isSccpRemoteSSNMarkProhibitedWhenSpcResuming());
    } catch (Exception e1) {

      log.catching(e1);

    }


    try {
      this.sccpStackImplInstance.getRouter().addMtp3ServiceAccessPoint(
          this.getServerConfig().getMtp3ServicePointId(),
          this.getServerConfig().getMtp3Id(),
          this.getServerConfig().getServerSpc(),
          this.getServerConfig().getNetworkIndicator());
    } catch (Exception e) {
      log.catching(e);

    }

    try {
      this.sccpStackImplInstance.getRouter().addMtp3Destination(
          this.getServerConfig().getSccpMtp3DestSapId(),
          this.getServerConfig().getSccpMtp3DestDestId(),
          this.getServerConfig().getClientSpc(),
          this.getServerConfig().getClientSpc(),
          this.getServerConfig().getFirstSls(),
          this.getServerConfig().getLastSls(),
          this.getServerConfig().getSlsMask());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      log.catching(e);
    }

    log.exit();
  }

  /**
   * Inits the sctp stack.
   *
   * @throws Exception the exception
   */
  private void initSctpStack() throws Exception {
    log.entry();
    this.managementImplInstance =
        new ManagementImpl(this.getServerConfig().getServerName());
    this.managementImplInstance.setSingleThread(this.getServerConfig()
        .isSctpSingleThread());
    this.managementImplInstance.setConnectDelay(this.getServerConfig()
        .getSctpConnectDelay());
    this.managementImplInstance.start();
    this.managementImplInstance.removeAllResourses();

    managementImplInstance.addServer(this.getServerConfig().getServerName(),
        this.getServerConfig().getServerIP(), this.getServerConfig()
            .getServerPort(), IpChannelType.SCTP, this.getServerConfig()
            .getExtraHostAddresses());

    managementImplInstance.addServerAssociation(this.getServerConfig()
        .getClientIP(), this.getServerConfig().getClientPort(), this
        .getServerConfig().getServerName(), this.getServerConfig()
        .getServerAssociationName(), IpChannelType.SCTP);

    managementImplInstance.startServer(this.getServerConfig().getServerName());
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogAccept(
   * org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer)
   */
  @Override
  public void onDialogAccept(MAPDialog mapDialog,
      MAPExtensionContainer extensionContainer) {
    log.entry(mapDialog, extensionContainer);

    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogClose(org
   * .mobicents.protocols.ss7.map.api.MAPDialog)
   */
  @Override
  public void onDialogClose(MAPDialog mapDialog) {
    log.entry(mapDialog);
    this.__cache.invalidate(mapDialog.getLocalDialogId());
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogDelimiter
   * (org.mobicents.protocols.ss7.map.api.MAPDialog)
   */
  @Override
  public void onDialogDelimiter(MAPDialog mapDialog) {
    log.entry(mapDialog);
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogNotice(
   * org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic)
   */
  @Override
  public void onDialogNotice(MAPDialog mapDialog,
      MAPNoticeProblemDiagnostic noticeProblemDiagnostic) {
    log.entry(mapDialog, noticeProblemDiagnostic);
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogProviderAbort
   * (org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.dialog.MAPAbortProviderReason,
   * org.mobicents.protocols.ss7.map.api.dialog.MAPAbortSource,
   * org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer)
   */
  @Override
  public void onDialogProviderAbort(MAPDialog mapDialog,
      MAPAbortProviderReason abortProviderReason, MAPAbortSource abortSource,
      MAPExtensionContainer extensionContainer) {
    log.entry(mapDialog, abortProviderReason, abortSource, extensionContainer);
    this.__cache.invalidate(mapDialog.getLocalDialogId());
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogReject(
   * org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.dialog.MAPRefuseReason,
   * org.mobicents.protocols.ss7.map.api.dialog.MAPProviderError,
   * org.mobicents.protocols.ss7.tcap.asn.ApplicationContextName,
   * org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer)
   */

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogReject(
   * org.mobicents.protocols .ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.dialog.MAPRefuseReason,
   * org.mobicents.protocols.ss7.tcap.asn.ApplicationContextName,
   * org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer)
   */
  @Override
  public void onDialogReject(MAPDialog mapDialog, MAPRefuseReason refuseReason,
      ApplicationContextName alternativeApplicationContext,
      MAPExtensionContainer extensionContainer) {
    log.entry(mapDialog, refuseReason, alternativeApplicationContext,
        extensionContainer);
    this.__cache.invalidate(mapDialog.getLocalDialogId());
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogRelease
   * (org.mobicents.protocols.ss7.map.api.MAPDialog)
   */
  @Override
  public void onDialogRelease(MAPDialog mapDialog) {
    log.entry(mapDialog);
    this.__cache.invalidate(mapDialog.getLocalDialogId());
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogRequest
   * (org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.primitives.AddressString,
   * org.mobicents.protocols.ss7.map.api.primitives.AddressString,
   * org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer)
   */
  @Override
  public void onDialogRequest(MAPDialog mapDialog, AddressString destReference,
      AddressString origReference, MAPExtensionContainer extensionContainer) {
    log.entry(mapDialog, destReference, origReference, extensionContainer);
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogRequestEricsson
   * (org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.primitives.AddressString,
   * org.mobicents.protocols.ss7.map.api.primitives.AddressString,
   * org.mobicents.protocols.ss7.map.api.primitives.IMSI,
   * org.mobicents.protocols.ss7.map.api.primitives.AddressString)
   */
  @Override
  public void onDialogRequestEricsson(MAPDialog mapDialog,
      AddressString destReference, AddressString origReference, IMSI imsi,
      AddressString vlr) {
    log.entry(mapDialog, destReference, origReference, imsi, vlr);
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogTimeout
   * (org.mobicents.protocols.ss7.map.api.MAPDialog)
   */
  @Override
  public void onDialogTimeout(MAPDialog mapDialog) {
    log.entry(mapDialog);
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogUserAbort
   * (org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.dialog.MAPUserAbortChoice,
   * org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer)
   */
  @Override
  public void onDialogUserAbort(MAPDialog mapDialog,
      MAPUserAbortChoice userReason, MAPExtensionContainer extensionContainer) {
    log.entry(mapDialog, userReason, extensionContainer);
    this.__cache.invalidate(mapDialog.getLocalDialogId());
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPServiceListener#onErrorComponent
   * (org.mobicents.protocols.ss7.map.api.MAPDialog, java.lang.Long,
   * org.mobicents.protocols.ss7.map.api.errors.MAPErrorMessage)
   */
  @Override
  public void onErrorComponent(MAPDialog mapDialog, Long invokeId,
      MAPErrorMessage mapErrorMessage) {
    log.entry(mapDialog, invokeId, mapErrorMessage);
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPServiceListener#onInvokeTimeout
   * (org.mobicents.protocols.ss7.map.api.MAPDialog, java.lang.Long)
   */
  @Override
  public void onInvokeTimeout(MAPDialog mapDialog, Long invokeId) {
    log.entry(mapDialog, invokeId);
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPServiceListener#onMAPMessage(org
   * .mobicents.protocols.ss7.map.api.MAPMessage)
   */
  @Override
  public void onMAPMessage(MAPMessage mapMessage) {
    log.entry(mapMessage);
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.service.supplementary. MAPServiceSupplementaryListener
   * #onProcessUnstructuredSSRequest(org.mobicents .protocols.ss7.map.api.service
   * .supplementary.ProcessUnstructuredSSRequest)
   */
  @Override
  public void onProcessUnstructuredSSRequest(
      ProcessUnstructuredSSRequest procUnstrReqInd) {
    log.entry(procUnstrReqInd);
    MAPDialogSupplementary dialog = procUnstrReqInd.getMAPDialog();

    org.mobicents.ussdgateway.Dialog xmlDialog =
        new org.mobicents.ussdgateway.Dialog(this.getXmlDialogType(dialog
            .getTCAPMessageType()), dialog.getLocalDialogId(),
            dialog.getReceivedDestReference(),
            dialog.getReceivedOrigReference(), procUnstrReqInd);

    byte[] serializedEvent = null;
    try {
      serializedEvent = this.getXmlFactory().serialize(xmlDialog);
      log.info(String.format("Request to HTTP Application:\n %s", new String(
          serializedEvent)));
    } catch (XMLStreamException e) {
      log.catching(e);
    }

    try {

      String url =
          this.getURLFromUSSDRequest(procUnstrReqInd.getUSSDString().getString(
              Charset.defaultCharset()));

      byte[] xmlPayLoad =
          this.sendHttpRequest(serializedEvent, String.valueOf(dialog
              .getLocalDialogId()), procUnstrReqInd.getMSISDNAddressString()
              .getAddress(), url);

      if (xmlPayLoad == null || xmlPayLoad.length <= 0) {
        log.error("Received invalid payload from http server");
      }
      Dialog dialogResponse = this.getXmlFactory().deserialize(xmlPayLoad);
      if (dialogResponse == null) {
        log.error("Received Success Response but couldn't deserialize to Dialog. Dialog is null");
      }

      MAPMessage mapMessage = null;
      if (dialogResponse != null) {
        mapMessage = dialogResponse.getMAPMessage();
      }

      switch (mapMessage.getMessageType()) {
        case unstructuredSSRequest_Request:

          this.addUnstructuredSSRequest((UnstructuredSSRequest) mapMessage,
              dialog);
          break;
        case processUnstructuredSSRequest_Response:
          
          this.__cache.invalidate(dialog.getLocalDialogId());
          ProcessUnstructuredSSResponse ussdResponse =
              (ProcessUnstructuredSSResponse) mapMessage;

          
          dialog.addProcessUnstructuredSSResponse(ussdResponse.getInvokeId(),
              ussdResponse.getDataCodingScheme(), ussdResponse.getUSSDString());
          break;

        default:
          log.error("Received Success Response but unidentified response body");
          break;
      }

    } catch (Exception e) {
      log.catching(e);
    }
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.service.supplementary. MAPServiceSupplementaryListener
   * #onProcessUnstructuredSSResponse(org.mobicents .protocols.ss7.map.api.service
   * .supplementary.ProcessUnstructuredSSResponse)
   */
  @Override
  public void onProcessUnstructuredSSResponse(
      ProcessUnstructuredSSResponse procUnstrResInd) {
    log.entry(procUnstrResInd);
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPServiceListener#
   * onProviderErrorComponent(org.mobicents.protocols.ss7.map.api.MAPDialog, java.lang.Long,
   * org.mobicents.protocols.ss7.map.api.dialog.MAPProviderError)
   */
  /**
   * On provider error component.
   *
   * @param mapDialog the map dialog
   * @param invokeId the invoke id
   */
  public void onProviderErrorComponent(MAPDialog mapDialog, Long invokeId) {
    log.entry(mapDialog, invokeId);
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPServiceListener#onRejectComponent
   * (org.mobicents.protocols.ss7.map.api.MAPDialog, java.lang.Long,
   * org.mobicents.protocols.ss7.tcap.asn.comp.Problem)
   */
  /**
   * On reject component.
   *
   * @param mapDialog the map dialog
   * @param invokeId the invoke id
   * @param problem the problem
   */
  public void onRejectComponent(MAPDialog mapDialog, Long invokeId,
      Problem problem) {
    log.entry(mapDialog, invokeId, problem);
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.MAPServiceListener#onRejectComponent
   * (org.mobicents.protocols.ss7.map.api.MAPDialog, java.lang.Long,
   * org.mobicents.protocols.ss7.tcap.asn.comp.Problem, boolean)
   */
  @Override
  public void onRejectComponent(MAPDialog mapDialog, Long invokeId,
      Problem problem, boolean isLocalOriginated) {
    log.entry(mapDialog, invokeId, problem, isLocalOriginated);
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.service.supplementary. MAPServiceSupplementaryListener
   * #onUnstructuredSSNotifyRequest(org.mobicents
   * .protocols.ss7.map.api.service.supplementary.UnstructuredSSNotifyRequest)
   */
  @Override
  public void onUnstructuredSSNotifyRequest(
      UnstructuredSSNotifyRequest unstrNotifyInd) {
    log.entry(unstrNotifyInd);
    log.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.service.supplementary. MAPServiceSupplementaryListener
   * #onUnstructuredSSNotifyResponse(org.mobicents .protocols.ss7.map.api.service
   * .supplementary.UnstructuredSSNotifyResponse)
   */
  @Override
  public void onUnstructuredSSNotifyResponse(
      UnstructuredSSNotifyResponse unstrNotifyInd) {

    log.entry(unstrNotifyInd);

    MAPDialogSupplementary dialog = unstrNotifyInd.getMAPDialog();
    org.mobicents.ussdgateway.Dialog xmlDialog =
        new org.mobicents.ussdgateway.Dialog(this.getXmlDialogType(dialog
            .getTCAPMessageType()), dialog.getLocalDialogId(),
            dialog.getReceivedDestReference(),
            dialog.getReceivedOrigReference(), unstrNotifyInd);

    byte[] serializedEvent = null;

    try {
      try {
        serializedEvent = this.getXmlFactory().serialize(xmlDialog);
        log.info(new String(serializedEvent));

      } catch (XMLStreamException e) {
        log.catching(e);
      }

      byte[] xmlPayload =
          this.sendHttpRequest(serializedEvent,
              String.valueOf(dialog.getLocalDialogId()), null, null);


      log.info(xmlPayload);
      this.__cache.invalidate(dialog.getLocalDialogId());

      dialog.close(true);

    } catch (Exception e) {
      log.catching(e);
    }
    log.exit();

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.service.supplementary. MAPServiceSupplementaryListener
   * #onUnstructuredSSRequest(org.mobicents.protocols
   * .ss7.map.api.service.supplementary.UnstructuredSSRequest)
   */
  @Override
  public void onUnstructuredSSRequest(UnstructuredSSRequest unstrReqInd) {
    log.entry(unstrReqInd);

    Dialog xmlDialog =
        new Dialog(this.getXmlDialogType(unstrReqInd.getMAPDialog()
            .getTCAPMessageType()), unstrReqInd.getMAPDialog()
            .getLocalDialogId(), unstrReqInd.getMAPDialog()
            .getReceivedDestReference(), unstrReqInd.getMAPDialog()
            .getReceivedOrigReference(), unstrReqInd);
    try {
      byte[] serializedEvent = this.getXmlFactory().serialize(xmlDialog);
      log.info(new String(serializedEvent));
    } catch (XMLStreamException e) {
      log.catching(e);
    }
    log.exit();

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mobicents.protocols.ss7.map.api.service.supplementary. MAPServiceSupplementaryListener
   * #onUnstructuredSSResponse(org.mobicents.protocols
   * .ss7.map.api.service.supplementary.UnstructuredSSResponse)
   */
  @Override
  public void onUnstructuredSSResponse(UnstructuredSSResponse unstrResInd) {
    log.entry(unstrResInd);
    MAPDialogSupplementary dialog = unstrResInd.getMAPDialog();
    org.mobicents.ussdgateway.Dialog xmlDialog =
        new org.mobicents.ussdgateway.Dialog(this.getXmlDialogType(dialog
            .getTCAPMessageType()), dialog.getLocalDialogId(),
            dialog.getReceivedDestReference(),
            dialog.getReceivedOrigReference(), unstrResInd);

    byte[] serializedEvent = null;

    try {
      try {
        serializedEvent = this.getXmlFactory().serialize(xmlDialog);
        log.info(new String(serializedEvent));

      } catch (XMLStreamException e) {
        log.catching(e);
      }

      byte[] xmlPayload =
          this.sendHttpRequest(serializedEvent,
              String.valueOf(dialog.getLocalDialogId()), null, null);

      Dialog dialogResponseUssd = this.getXmlFactory().deserialize(xmlPayload);

      if (dialogResponseUssd == null) {
        log.error("Received Success Response but couldn't deserialize to Dialog. Dialog is null");
      }
      MAPMessage mapMessage = null;
      if (dialogResponseUssd != null) {
        mapMessage = dialogResponseUssd.getMAPMessage();
      }

      switch (mapMessage.getMessageType()) {
        case unstructuredSSRequest_Request:
          this.addUnstructuredSSRequest((UnstructuredSSRequest) mapMessage,
              dialog);
          break;
        case processUnstructuredSSRequest_Response:
          ProcessUnstructuredSSResponse ussdResponse =
              (ProcessUnstructuredSSResponse) mapMessage;

          dialog.addProcessUnstructuredSSResponse(ussdResponse.getInvokeId(),

          ussdResponse.getDataCodingScheme(), ussdResponse.getUSSDString());

          break;
        default:
          log.error("Received Success Response but unidentified response body");
          break;
      }

      dialog.close(false);

    } catch (Exception e) {
      log.catching(e);
    }
    log.exit();

  }

  /**
   * Put session.
   *
   * @param dialogId the dialog id
   * @param session the session
   */
  private void putSession(String dialogId, Session session) {
    log.entry(dialogId, session);
    DialogListener.getCache().put(dialogId, session);
    log.exit();

  }

  /**
   * Send http request.
   *
   * @param serializedEvent the serialized event
   * @param dialogId the dialog id
   * @param msisdn the msisdn
   * @param url the url
   * @return the byte[]
   * @throws ClientProtocolException the client protocol exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ExecutionException the execution exception
   */
  private byte[] sendHttpRequest(byte[] serializedEvent, final String dialogId,
      final String msisdn, final String url) throws ClientProtocolException,
      IOException, ExecutionException {
    log.entry(serializedEvent, dialogId, msisdn, url);
    CloseableHttpClient httpclient = HttpClients.createDefault();
    byte[] responseBytes;
    Session session = getSession(dialogId, msisdn, url);

    try {
      HttpPost httpPost = new HttpPost(session.getUrl());
      httpPost.setEntity(new ByteArrayEntity(serializedEvent));
      log.info(String.format("Executing request: %s", httpPost.getRequestLine()));
      HttpClientContext context = session.getLocalContext();
      CloseableHttpResponse response = httpclient.execute(httpPost, context);
      session.setLocalContext(context);
      putSession(dialogId, session);
      try {
        log.debug("context: %s" + Utils.getInstance().convert2Json(session));
      } catch (Exception e) {
        log.catching(e);
      }

      responseBytes =
          ByteStreams.toByteArray(response.getEntity().getContent());
    } finally {
      httpclient.close();
    }
    return log.exit(responseBytes);
  }

  /**
   * Sets the m3ua management impl instance.
   *
   * @param m3uaManagementImplInstance the new m3ua management impl instance
   */
  public void setM3uaManagementImplInstance(
      M3UAManagementImpl m3uaManagementImplInstance) {
    this.m3uaManagementImplInstance = m3uaManagementImplInstance;
  }

  /**
   * Sets the management impl instance.
   *
   * @param managementImplInstance the new management impl instance
   */
  public void setManagementImplInstance(ManagementImpl managementImplInstance) {
    this.managementImplInstance = managementImplInstance;
  }

  /**
   * Sets the map provider instance.
   *
   * @param mapProviderInstance the new map provider instance
   */
  public void setMapProviderInstance(MAPProvider mapProviderInstance) {
    this.mapProviderInstance = mapProviderInstance;
  }

  /**
   * Sets the map stack impl instance.
   *
   * @param mapStackImplInstance the new map stack impl instance
   */
  public void setMapStackImplInstance(MAPStackImpl mapStackImplInstance) {
    this.mapStackImplInstance = mapStackImplInstance;
  }

  /**
   * Sets the sccp stack impl instance.
   *
   * @param sccpStackImplInstance the new sccp stack impl instance
   */
  public void setSccpStackImplInstance(SccpStackImpl sccpStackImplInstance) {
    this.sccpStackImplInstance = sccpStackImplInstance;
  }

  /**
   * Sets the server config.
   *
   * @param serverConfig the new server config
   */
  public void setServerConfig(ServerConfig serverConfig) {
    log.entry(serverConfig);
    this.serverConfig = serverConfig;
    log.exit();
  }

  public void addUnstructuredSSRequestNetworkInitiated(
      UnstructuredSSRequest unstructuredSSRequestInd,
      MAPDialogSupplementary mapDialogSupplementary, String url, Dialog dialog)
      throws MAPException {

    log.entry(unstructuredSSRequestInd, mapDialogSupplementary, url, dialog);

    if (mapDialogSupplementary == null) {
      MAPParameterFactory mapParameterFactory =
          this.mapProviderInstance.getMAPParameterFactory();
      ISDNAddressString origReference =
          mapParameterFactory.createISDNAddressString(dialog.getOrigReference()
              .getAddressNature(),
              dialog.getOrigReference().getNumberingPlan(), dialog
                  .getOrigReference().getAddress());
      ISDNAddressString destReference =
          mapParameterFactory.createISDNAddressString(dialog.getDestReference()
              .getAddressNature(),
              dialog.getDestReference().getNumberingPlan(), dialog
                  .getDestReference().getAddress());

      mapDialogSupplementary =
          this.mapProviderInstance.getMAPServiceSupplementary()
              .createNewDialog(
                  MAPApplicationContext.getInstance(
                      MAPApplicationContextName.networkUnstructuredSsContext,
                      MAPApplicationContextVersion.version2),
                  this.getServerConfig().getSccpServerAddress(), origReference,
                  this.getServerConfig().getSccpClientAddress(), destReference);

    }
    Session session = null;
    try {
      session =
          getSession(String.valueOf(mapDialogSupplementary.getLocalDialogId()),
              dialog.getDestReference().getAddress(), url);
    } catch (ExecutionException e) {
      log.catching(e);
    }

    this.putSession(String.valueOf(mapDialogSupplementary.getLocalDialogId()),
        session);

    mapDialogSupplementary.addUnstructuredSSRequest(
        unstructuredSSRequestInd.getDataCodingScheme(),
        unstructuredSSRequestInd.getUSSDString(), null, null);
    try {
      mapDialogSupplementary.send();
    } catch (Exception e) {
      log.catching(e);
    }
    log.exit();

  }

}
