package com.aggregator;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.1 in JDK 6
 * Generated source version: 2.1
 */
@WebService( name = "HandlingReportService", targetNamespace = "http://ws.handling.interfaces.dddsample.citerus.se/" )
@XmlSeeAlso( {
                 ObjectFactory.class
             } )
public interface HandlingReportService
{

    /**
     * @param arg0
     * @throws HandlingReportErrors_Exception
     */
    @WebMethod
    @RequestWrapper( localName = "submitReport",
                     targetNamespace = "http://ws.handling.interfaces.dddsample.citerus.se/",
                     className = "com.aggregator.SubmitReport" )
    @ResponseWrapper( localName = "submitReportResponse",
                      targetNamespace = "http://ws.handling.interfaces.dddsample.citerus.se/",
                      className = "com.aggregator.SubmitReportResponse" )
    public void submitReport(
        @WebParam( name = "arg0", targetNamespace = "" )
        HandlingReport arg0 )
        throws HandlingReportErrors_Exception
    ;

}
