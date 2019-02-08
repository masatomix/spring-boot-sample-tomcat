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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import nu.mine.kino.service.Hello;

@Controller
@RequestMapping("/echo")
// @Slf4j
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
            if (JWTUtils.checkIdToken(id_token)) {
                return hello;
            } else {
                throw new UNAUTHORIZED_Exception("ID Token is invalid.");
            }
        }
        throw new UNAUTHORIZED_Exception(
                "Authorization ヘッダから、Bearerトークンを取得できませんでした。");
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