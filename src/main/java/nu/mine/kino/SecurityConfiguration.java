package nu.mine.kino;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security の resource server 設定（task#1056 Step 2）。
 *
 * <p>旧来は {@code JWTUtils} が Authorization ヘッダの Bearer トークンを手動検証していたが、
 * Spring Security の OAuth2 Resource Server へ移譲する。検証鍵は Firebase の JWK Set
 * （{@code application.properties} の {@code spring.security.oauth2.resourceserver.jwt.jwk-set-uri}）から
 * 取得する。
 *
 * <p>認可ポリシー: {@code POST /echo} のみ有効な JWT を要求し、その他のサンプル
 * エンドポイント（/status, /echo2, /session, /echoBody, /actuator/health 等）は公開する。
 * REST API のためセッションを用いた CSRF 保護は無効化する。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/echo").authenticated()
                .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults());
        return http.build();
    }
}
