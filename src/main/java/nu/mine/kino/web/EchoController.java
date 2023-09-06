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

import java.net.InetAddress;
import lombok.extern.slf4j.Slf4j;
import nu.mine.kino.service.Hello;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
public class EchoController {

    @RequestMapping(value = "/echoLogger", method = RequestMethod.GET)
    public String defaultEcho() {
        return echo("Default Message.", "INFO");
    }

    @RequestMapping(value = "/echoLogger1", method = RequestMethod.GET)
    public String echo(@RequestParam("message") String message, @RequestParam("level") String level) {

        log.info("hostname: {}", getHostName());
        switch (level) {
            case "DEBUG":
                log.debug(message);
                break;
            case "INFO":
                log.info(message);
                break;
            case "WARN":
                log.warn(message);
                break;
            case "ERROR":
                log.error(message);
                break;
            default:
                log.debug(message);

        }
        return message;
    }

    @RequestMapping(value = "/echoSysout", method = RequestMethod.GET)
    public String echo(@RequestParam("message") String message) {
        System.out.println(message);
        return message;
    }

    @ResponseBody
    @RequestMapping(value = "/echoBody", produces = "application/json; charset=utf-8", method = RequestMethod.POST)
    public Hello helloWorld(@RequestBody Hello hello) {
        log.info("{}", hello);
        System.out.println(hello.toString());
        return hello;
    }

    @ResponseBody
    @RequestMapping(value = "/exception", produces = "application/json; charset=utf-8", method = RequestMethod.POST)
    public Hello helloWorld1(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Hello hello) throws ResponseStatusException {
        log.info("body: {}", hello);

        if (StringUtils.isEmpty(authorization)) {
            // 400番台エラーのサンプル
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "トークンがついていないエラーです");
        }
        log.info("auth: {}", authorization);

        // 500番台エラーのサンプル
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "サービス停止中");

    }

    // @ResponseBody
    // @RequestMapping(value = "/error", produces = "application/json;
    // charset=utf-8", method = RequestMethod.POST)
    // public Hello helloWorld(
    // @RequestHeader(value = "Authorization", required = true) String
    // authorization,
    // @RequestBody Hello hello) throws UNAUTHORIZED_Exception {

    // Matcher matcher = CHALLENGE_PATTERN.matcher(authorization);
    // if (matcher.matches()) {
    // String id_token = matcher.group(1);
    // if (JWTUtils.checkIdToken(id_token)) {
    // return hello;
    // } else {
    // throw new UNAUTHORIZED_Exception("ID Token is invalid.");
    // }
    // }
    // throw new UNAUTHORIZED_Exception(
    // "Authorization ヘッダから、Bearerトークンを取得できませんでした。");
    // }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
        }
        return null;
    }
}