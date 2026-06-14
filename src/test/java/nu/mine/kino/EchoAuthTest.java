package nu.mine.kino;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * /echo の Bearer トークン認証が Spring Security の resource server に移譲されたことを
 * 確認するリグレッションテスト（task#1056 Step 2）。
 *
 * <p>有効な Firebase トークンが無くても検証できる「拒否系」を対象とする。トークン未提示・
 * 不正トークンはいずれも JWK Set の取得前に 401 となるため、ネットワークに依存しない。
 * 正常系（有効なトークンで 200）は実トークンとプロジェクト設定が必要なため対象外。
 */
@SpringBootTest
@AutoConfigureMockMvc
class EchoAuthTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BODY = "{\"id\":\"1\",\"name\":\"taro\"}";

    /** Authorization ヘッダが無ければ 401。 */
    @Test
    void echoWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(post("/echo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
                .andExpect(status().isUnauthorized());
    }

    /** 不正な Bearer トークンは 401。 */
    @Test
    void echoWithInvalidTokenIsUnauthorized() throws Exception {
        mockMvc.perform(post("/echo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-valid-jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
                .andExpect(status().isUnauthorized());
    }
}
