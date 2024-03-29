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

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import nu.mine.kino.service.Hello;

@RestController
@RequestMapping("/echo2")
@Slf4j
public class Echo2Controller {

    @ResponseBody
    @CrossOrigin
    @RequestMapping(produces = "application/json; charset=utf-8", method = RequestMethod.POST)
    public Hello helloWorld(@RequestBody
    Hello hello) {
        log.debug("start.");
        log.debug("{}", hello);
        log.debug("end.");
        return hello;
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    public String helloWorld() {
        log.debug("start.");
        log.debug("end.");
        return "hello!!!";
    }

}