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

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class SessionController {

    @Autowired
    HttpSession session;

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    public String save(Model model) {
        // 保存
        log.debug("/save.start");
        String data = "保存したいデータ1";
        session.setAttribute("data", data);

        String sessionId = session.getId();
        String hostname = getHostName();
        model.addAttribute("data", data);
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("ID", "Save画面");
        model.addAttribute("hostname", hostname);
        return "index";
    }

    @RequestMapping(value = "/load", method = RequestMethod.GET)
    public String load(Model model) {
        log.debug("session is New ?:{}", session.isNew());
        String data = (String) session.getAttribute("data"); // 取得

        log.debug(data);
        String sessionId = session.getId();
        String hostname = getHostName();
        model.addAttribute("data", data);
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("ID", "Load画面");
        model.addAttribute("hostname", hostname);
        session.invalidate(); // クリア
        return "index";
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
        }
        return null;
    }
}
