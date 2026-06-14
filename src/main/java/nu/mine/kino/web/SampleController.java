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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nu.mine.kino.service.Hello;

/**
 * Bearer トークン（JWT）認証が必要なエンドポイントのサンプル（/echo）。
 *
 * <p>JWT の検証は Spring Security の resource server（{@link nu.mine.kino.SecurityConfiguration}）が
 * フィルタで行う。Authorization ヘッダが無い・トークンが不正な場合は本メソッドに到達する前に
 * 401 が返るため、コントローラ側は検証ロジックを持たず受け取った内容をそのまま返す。
 * （旧実装は JWTUtils で手動検証していたが Spring Security へ移譲した。task#1056 Step 2）
 */
@RestController
@RequestMapping("/echo")
public class SampleController {

    @CrossOrigin
    @PostMapping(produces = "application/json; charset=utf-8")
    public Hello helloWorld(@RequestBody Hello hello) {
        return hello;
    }
}
