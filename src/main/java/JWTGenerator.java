import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.text.MessageFormat;

public class JWTGenerator {


    private static final String CONSUMER_KEY = "<CONSUMER KEY FROM SF APP>";

    public static void main(String[] args) {

        String header = "{\"alg\":\"RS256\"}";
        String claimTemplate = "'{'\"iss\": \"{0}\", \"sub\": \"{1}\", \"aud\": \"{2}\", \"exp\": {3}}'";


        try {
            StringBuffer token = new StringBuffer();

            System.out.println(header);
            System.out.println(claimTemplate);


            //Encode the JWT Header and add it to our string to sign
            token.append(Base64.encodeBase64URLSafeString(header.getBytes("UTF-8")));

            //Separate with a period
            token.append(".");

            //Create the JWT Claims Object
            String[] claimArray = new String[4];
            claimArray[0] = CONSUMER_KEY;
            claimArray[1] = "<YOUR EMAIL>@bazaarvoice.com.staging";
            claimArray[2] = "https://test.salesforce.com";
            claimArray[3] = Long.toString((System.currentTimeMillis() / 1000) + 200);
//            claimArray[4]=<JTI>;

            MessageFormat claims;
            claims = new MessageFormat(claimTemplate);
            String payload = claims.format(claimArray);

            System.out.println(payload);

            //Add the encoded claims object
            token.append(Base64.encodeBase64URLSafeString(payload.getBytes("UTF-8")));

            //Load the private key from a keystore
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(new FileInputStream("/Users/<PATH TO KEY STORE>"), "<KEY STORE PASSWORD>".toCharArray());

            PrivateKey privateKey = (PrivateKey) keystore.getKey("<KEY NAME>", "<KEY PASSWORD>".toCharArray());

            //Sign the JWT Header + "." + JWT Claims Object
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(token.toString().getBytes("UTF-8"));
            String signedPayload = Base64.encodeBase64URLSafeString(signature.sign());

            //Separate with a period
            token.append(".");

            //Add the encoded signature
            token.append(signedPayload);

            System.out.println("JWT Token\n" + token);

            AccessTokenResponse accessToken = getAccessToken(token.toString());
            System.out.println(accessToken.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static AccessTokenResponse getAccessToken(String jwtToken) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        map.add("assertion", jwtToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        return restTemplate.postForObject("https://test.salesforce.com/services/oauth2/token", request, AccessTokenResponse.class);


    }


}
