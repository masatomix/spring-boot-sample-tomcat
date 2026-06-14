package nu.mine.kino;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Actuator の公開範囲が health / info に限定されていることのリグレッションテスト（task#1056 Step 1）。
 *
 * <p>以前は全エンドポイント公開だったため、/actuator/env や /actuator/heapdump 等から
 * 環境変数・メモリダンプが無認証で取得できた。公開を絞った状態が将来も維持されることを担保する。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ActuatorExposureTest {

    @Autowired
    private MockMvc mockMvc;

    /** health は公開されている（200）。 */
    @Test
    void healthIsExposed() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    /** env は公開されていない（404）。秘密情報の漏洩経路を塞いだことを確認する。 */
    @Test
    void envIsNotExposed() throws Exception {
        mockMvc.perform(get("/actuator/env")).andExpect(status().isNotFound());
    }

    /** beans も公開されていない（404）。 */
    @Test
    void beansIsNotExposed() throws Exception {
        mockMvc.perform(get("/actuator/beans")).andExpect(status().isNotFound());
    }

    /** heapdump も公開されていない（404）。 */
    @Test
    void heapdumpIsNotExposed() throws Exception {
        mockMvc.perform(get("/actuator/heapdump")).andExpect(status().isNotFound());
    }
}
