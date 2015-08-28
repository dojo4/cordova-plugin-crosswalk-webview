package org.crosswalk.engine;

import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cordova.ICordovaClientCertRequest;
import org.apache.cordova.LOG;

import org.xwalk.core.ClientCertRequest;

public class XWalkCordovaClientCertRequest implements ICordovaClientCertRequest {
    private static final String TAG = "XWalkCordovaClientCertRequest";

    private final ClientCertRequest request;

    public XWalkCordovaClientCertRequest(ClientCertRequest request) {
        this.request = request;
        LOG.d(TAG, "INITIALIZE");
    }
    
    /**
     * Cancel this request
     */
    public void cancel()
    {
        request.cancel();
    }
    
    /*
     * Returns the host name of the server requesting the certificate.
     */
    public String getHost()
    {
        return request.getHost();
    }
    
    /*
     * Returns the acceptable types of asymmetric keys (can be null).
     */
    public String[] getKeyTypes()
    {
        // return request.getKeyTypes();
        return null;
    }
    
    /*
     * Returns the port number of the server requesting the certificate.
     */
    public int getPort()
    {
        return request.getPort();
    }
    
    /*
     * Returns the acceptable certificate issuers for the certificate matching the private key (can be null).
     */
    public Principal[] getPrincipals()
    {
        // return request.getPrincipals();
        return null;
    }
    
    /*
     * Ignore the request for now. Do not remember user's choice.
     */
    public void ignore()
    {
        request.ignore();
    }
    
    /*
     * Proceed with the specified private key and client certificate chain. Remember the user's positive choice and use it for future requests.
     * 
     * @param privateKey The privateKey
     * @param chain The certificate chain 
     */
    public void proceed(PrivateKey privateKey, X509Certificate[] chain)
    {
        LOG.d(TAG, "PROCEED");
        List<X509Certificate> chainList = new ArrayList<X509Certificate>(chain.length);
        Collections.addAll(chainList, chain);
        request.proceed(privateKey, chainList);
    }
}
