package com.example.wso;

import com.example.wso.SSL.LoginAdminServiceClient;
import com.example.wso.SSL.ServiceAdminClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wso2.carbon.service.mgt.stub.types.carbon.ServiceMetaData;
import org.wso2.carbon.service.mgt.stub.types.carbon.ServiceMetaDataWrapper;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;

import java.rmi.RemoteException;

/**
 * @author:Rebecca Jin
 * @date: 2019/9/1,11:51
 * @version: 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SSLTest {

    @Test
    public void testListServices() throws RemoteException, LoginAuthenticationExceptionException,
            LogoutAuthenticationExceptionException{
        System.setProperty("javax.net.ssl.trustStore", "D:/software/WSO2/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        String backEndUrl = "https://localhost:9443";
        LoginAdminServiceClient login = new LoginAdminServiceClient(backEndUrl);
        String session = login.authenticate("admin", "admin");
        ServiceAdminClient serviceAdminClient = new ServiceAdminClient(backEndUrl, session);
        ServiceMetaDataWrapper serviceList = serviceAdminClient.listServices();
        System.out.println("Service Names:");
        for (ServiceMetaData serviceData : serviceList.getServices()) {
            System.out.println(serviceData.getName());
        }

        //login.logOut();

    }
}
