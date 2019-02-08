/******************************************************************************
 * Copyright (c) 2014 Masatomi KINO and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *      Masatomi KINO - initial API and implementation
 * $Id$
 ******************************************************************************/
//作成日: 2019/02/08

package nu.mine.kino.web;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

/**
 * @author Masatomi KINO
 * @version $Revision$
 */
@Slf4j
public class JWTUtils {
    public static boolean checkIdToken(String id_token) {
        try {
            SignedJWT decodeObject = SignedJWT.parse(id_token);
            log.debug("Header : " + decodeObject.getHeader());
            log.debug("Payload: " + decodeObject.getPayload());
            log.debug("Sign   : " + decodeObject.getSignature());

            JWSAlgorithm algorithm = decodeObject.getHeader().getAlgorithm();
            JWTClaimsSet set = decodeObject.getJWTClaimsSet();
            log.debug("Algorithm: {}", algorithm.getName());
            log.debug("ExpirationTime(有効期限): {}", set.getExpirationTime());
            log.debug("IssueTime(発行日時): {}", set.getIssueTime());
            log.debug("Subject(key): {}", set.getSubject());
            log.debug("Issuer(発行元): {}", set.getIssuer());
            log.debug("Audience: {}", set.getAudience());
            log.debug("Nonce: {}", set.getClaim("nonce"));
            log.debug("auth_time(認証日時): {}", set.getDateClaim("auth_time"));
            log.debug("鍵アルゴリズム({})", algorithm.getName());
            boolean isExpired = new Date().after(set.getExpirationTime());
            log.debug("now after ExpirationTime?: {}", isExpired);
            if (isExpired) {
                log.warn("有効期限が切れています。");
                return false;
            }
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

    private static boolean checkRSSignature(SignedJWT decodeObject,
            String jwks_uri) throws JOSEException, IOException, ParseException {
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

    private static Map<String, Object> getResource(String target)
            throws IOException {
        Client client = ClientBuilder.newClient();
        Response restResponse = client.target(target)
                .request(MediaType.APPLICATION_JSON_TYPE).get();
        String result = restResponse.readEntity(String.class);
        log.debug(result);
        return json2Map(result);
    }

    private static Map<String, Object> json2Map(String result)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result,
                new TypeReference<Map<String, Object>>() {
                });
    }
}
