/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nu.mine.kino.web;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.X509CertUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;
import nu.mine.kino.service.Hello;

@Controller
@RequestMapping("/echo")
@Slf4j
public class SampleController {
    private static final Pattern CHALLENGE_PATTERN = Pattern
            .compile("^Bearer *([^ ]+) *$", Pattern.CASE_INSENSITIVE);

    @ResponseBody
    @CrossOrigin
    @RequestMapping(produces = "application/json; charset=utf-8", method = RequestMethod.POST)
    public Hello helloWorld(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestBody Hello hello) throws UNAUTHORIZED_Exception {

        Matcher matcher = CHALLENGE_PATTERN.matcher(authorization);
        if (matcher.matches()) {
            String id_token = matcher.group(1);
            if (checkIdToken(id_token)) {
                return hello;
            } else {
                throw new UNAUTHORIZED_Exception("ID Token is invalid.");
            }
        }
        throw new UNAUTHORIZED_Exception(
                "Authorization ヘッダから、Bearerトークンを取得できませんでした。");
    }

    private boolean checkIdToken(String id_token) {
        try {
            SignedJWT decodeObject = SignedJWT.parse(id_token);
            log.debug("Header : " + decodeObject.getHeader());
            log.debug("Payload: " + decodeObject.getPayload());
            log.debug("Sign   : " + decodeObject.getSignature());

            JWSAlgorithm algorithm = decodeObject.getHeader().getAlgorithm();
            JWTClaimsSet set = decodeObject.getJWTClaimsSet();
            log.debug("Algorithm: {}", algorithm.getName());
            log.debug("Subject: {}", set.getSubject());
            log.debug("Issuer: {}", set.getIssuer());
            log.debug("Audience: {}", set.getAudience());
            log.debug("Nonce: {}", set.getClaim("nonce"));
            log.debug("now before ExpirationTime?: {}",
                    new Date().before(set.getExpirationTime()));
            log.debug("公開鍵({})", algorithm.getName());

            String jwks_uri = "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";
            return checkRSSignature(decodeObject, jwks_uri);

        } catch (ParseException e) {
            log.warn("サーバの公開鍵の取得に失敗しています.{}", e.getMessage());
        } catch (IOException e) {
            log.warn("サーバの公開鍵の取得に失敗しています.{}", e.getMessage());
        } catch (JOSEException e) {
            log.warn("Verify処理に失敗しています。{}", e.getMessage());
        }
        return false;
    }

    private boolean checkRSSignature(SignedJWT decodeObject, String jwks_uri)
            throws JOSEException, IOException, ParseException {
        // Headerから KeyIDを取得して、
        String keyID = decodeObject.getHeader().getKeyID();
        log.debug("KeyID: {}", keyID);

        Map<String, Object> resource = getResource(jwks_uri);
        String object = resource.get(keyID).toString();
        X509Certificate cert = X509CertUtils.parse(object);
        RSAKey rsaKey = RSAKey.parse(cert);

        JWSVerifier verifier = new RSASSAVerifier(rsaKey);
        boolean verify = decodeObject.verify(verifier);
        log.debug("valid？: {}", verify);
        return verify;
    }

    private Map<String, Object> getResource(String target) throws IOException {
        Client client = ClientBuilder.newClient();
        Response restResponse = client.target(target)
                .request(MediaType.APPLICATION_JSON_TYPE).get();
        String result = restResponse.readEntity(String.class);
        log.debug(result);
        return json2Map(result);
    }

    private Map<String, Object> json2Map(String result) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result,
                new TypeReference<Map<String, Object>>() {
                });
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    class UNAUTHORIZED_Exception extends Exception {
        private static final long serialVersionUID = -6715447914841144335L;

        public UNAUTHORIZED_Exception(String message) {
            super(message);
        }
    }

    // private void snippet() throws IOException, FirebaseAuthException {
    // String id_token = "";
    // ClassLoader loader = Thread.currentThread().getContextClassLoader();
    // // resources に置いちゃう
    // InputStream serviceAccount = loader.getResourceAsStream(
    // "xxxxxxxx-firebase-adminsdk-xxxxxxxxx.json");
    // FirebaseOptions options = new FirebaseOptions.Builder()
    // .setCredentials(GoogleCredentials.fromStream(serviceAccount))
    // .setDatabaseUrl("https://xxxxxxxx.firebaseio.com").build();
    // FirebaseApp.initializeApp(options);
    //
    // FirebaseToken decodedToken = FirebaseAuth.getInstance()
    // .verifyIdToken(id_token);
    // String uid = decodedToken.getUid();
    //
    // }
}