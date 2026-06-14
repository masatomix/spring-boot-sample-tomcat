package nu.mine.kino;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 現代化リファクタリング（task#1056）の安全網となるスモークテスト。
 *
 * <p>JWT 検証を伴わない公開エンドポイントのみを対象に、アプリケーションコンテキストの
 * 起動と基本的なリクエスト/レスポンスの疎通を確認する。後続フェーズ（jakarta 統一・
 * Spring Boot 4.0 への更新・JWT 検証の resource-server 化）でこれらが壊れていないことの
 * リグレッション基準とする。
 */
@SpringBootTest
@AutoConfigureMockMvc
class SmokeTest {

    @Autowired
    private MockMvc mockMvc;

    /** コンテキストが正常に起動し、MockMvc が注入されること。 */
    @Test
    void contextLoads() throws Exception {
        // @Autowired の MockMvc が解決できれば、SpringBootApplication の起動は成功している。
    }

    /** GET /status が "hello" を返すこと。 */
    @Test
    void statusReturnsHello() throws Exception {
        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello"));
    }

    /** GET /echo2 が "hello!!!" を返すこと。 */
    @Test
    void echo2GetReturnsHello() throws Exception {
        mockMvc.perform(get("/echo2"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello!!!"));
    }

    /** POST /echo2 が送信した JSON をそのままエコーすること。 */
    @Test
    void echo2PostEchoesBody() throws Exception {
        String body = "{\"id\":\"1\",\"name\":\"taro\"}";
        mockMvc.perform(post("/echo2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("taro"));
    }
}
