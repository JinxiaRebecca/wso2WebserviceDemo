package com.example.wso.webservice;


import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.axis2.context.ConfigurationContextFactory;




import java.io.File;

/**
 * @author:Rebecca Jin
 * @date: 2019/9/1,14:06
 * @version: 1.0
 * 租户管理webservice客户端
 */
public class TenantManagementServiceClient {
    private static String SEVER_URL = "https://localhost:9443/services/";
    private static String USER_NAME = "admin";
    private static String PASSWORD = "admin";
    private String serviceName="TenantMgtAdminService";
    private TenantMgtAdminServiceStub tenantMgtStub;

    //不需要cookie
    public TenantManagementServiceClient(){
        //证书位置
        String trustStore = System.getProperty("user.dir") + File.separator +
                "src" + File.separator + "main" + File.separator +
                "resources" + File.separator + "wso2carbon.jks";
        System.setProperty("javax.net.ssl.trustStore",trustStore);
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        ConfigurationContext configContext;
        try {
            configContext =ConfigurationContextFactory.createConfigurationContextFromFileSystem( null, null);
            String serviceEndPoint = SEVER_URL + serviceName;
            //创建stub
            tenantMgtStub = new TenantMgtAdminServiceStub(configContext,serviceEndPoint);
            ServiceClient client = tenantMgtStub._getServiceClient();
            Options option = client.getOptions();
            option.setProperty(HTTPConstants.COOKIE_STRING, null);
            //设置基本headers
            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setUsername(USER_NAME);
            auth.setPassword(PASSWORD);
            auth.setPreemptiveAuthentication(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, auth);
            option.setManageSession(true);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
