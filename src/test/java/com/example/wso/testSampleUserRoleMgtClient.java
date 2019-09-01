package com.example.wso;

import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wso2.carbon.um.ws.api.stub.ClaimValue;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceStub;

import java.io.File;

/**
 * @author:Rebecca Jin
 * @date: 2019/9/1,13:20
 * @version: 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class testSampleUserRoleMgtClient {
    private static String SEVER_URL = "https://localhost:9443/services/";
    private static String USER_NAME = "admin";
    private static String PASSWORD = "admin";

    @Test
    public void testSampleUserRoleMgtClient(){
        //证书位置
        String trustStore = System.getProperty("user.dir") + File.separator +
                "src" + File.separator + "main" + File.separator +
                "resources" + File.separator + "wso2carbon.jks";
        System.setProperty("javax.net.ssl.trustStore",trustStore);
        ConfigurationContext configContext;
        try {
            configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem( null, null);
            String serviceEndPoint = SEVER_URL + "RemoteUserStoreManagerService";
            //创建stub
            RemoteUserStoreManagerServiceStub adminStub = new RemoteUserStoreManagerServiceStub(configContext, serviceEndPoint);
            ServiceClient client = adminStub._getServiceClient();
            Options option = client.getOptions();
            option.setProperty(HTTPConstants.COOKIE_STRING, null);
            //设置基本headers
            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setUsername(USER_NAME);
            auth.setPassword(PASSWORD);


            auth.setPreemptiveAuthentication(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, auth);
            option.setManageSession(true);
            //调用API
            String[] users = adminStub.listUsers("*", 100);
            if(users != null){
                System.out.println("Listing user names of Carbon server...... ");
                for(String user : users){
                    System.out.println("User Name : " + user);
                }
            }
            try{
                adminStub.addUser("asela", "password", null, null, null, false);
                System.out.println("User is created successfully");
            } catch (Exception e){
                System.err.println("User creation is failed");
                e.printStackTrace();
            }
            boolean  authenticate = false;
            try{
                authenticate = adminStub.authenticate("asela", "password");
            } catch (Exception e ){
                e.printStackTrace();
            }

            if(authenticate){
                System.out.println("User is authenticated successfully");
            } else {
                System.err.println("User is authentication failed");
            }

            /**
             * creates role by assigning created user
             */
            try{
                adminStub.addRole("testRole", new String[]{"asela"}, null);
                System.out.println("Role is created successfully");
            } catch (Exception e){
                System.err.println("Role creation is failed");
                e.printStackTrace();
            }


            /**
             * set user attribute to user asela
             */
            try{
                adminStub.setUserClaimValue("asela", "http://wso2.org/claims/emailaddress", "asela@wso2.com", null);
                System.out.println("User Attribute is updated successfully ");
            } catch (Exception e){
                System.err.println("User Attribute updating is failed");
                e.printStackTrace();
            }

            /**
             * set multiple attributes to user asela
             */
            try{

                ClaimValue email = new ClaimValue();
                email.setClaimURI("http://wso2.org/claims/emailaddress");
                email.setValue("newasela@wso2.com");

                ClaimValue givenName = new ClaimValue();
                givenName.setClaimURI("http://wso2.org/claims/givenname");
                givenName.setValue("Asela Pathberiya");

                ClaimValue[] values = new ClaimValue[]{email, givenName};

                adminStub.setUserClaimValues("asela", values , null);

                System.out.println("User Attributes are updated successfully ");
            } catch (Exception e){
                System.err.println("User Attributes updating is failed");
                e.printStackTrace();
            }

            /**
             * get users of the role
             */
            try{
                String[] usersList = adminStub.getUserListOfRole("testRole");
                System.out.println("Listing user names assigned to testRole...... ");
                for(String user : usersList){
                    System.out.println("Assigned User : " + user);
                }
            } catch (Exception e){
                System.err.println("Users can not be retrieved");
                e.printStackTrace();
            }

            /**
             * you can retrieve the cookie to use for sub sequent communications
             */
            String authCookie = (String) adminStub._getServiceClient().getServiceContext()
                    .getProperty(HTTPConstants.COOKIE_STRING);

            System.out.println(authCookie);

            /**
             * If WSO2 Carbon has been connected with multiple user stores. Say you need to create a user in
             * domain called  it.com,  user name must be passed as   it.com/asela
             */
            //adminStub.addUser("it.com/asela", "password", null, null, null);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
