package utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class RequestParserTest {

    @Test
    void 파라미터를_디코딩해서_파싱한다() {
        Map<String, String> result = RequestParser.parseParameters(
            "userId=cu&password=password&name=%EC%9D%B4%EB%8F%99%EA%B7%9C&email=brainbackdoor%40gmail.com");

        assertThat(result).containsEntry("userId", "cu");
        assertThat(result).containsEntry("password", "password");
        assertThat(result).containsEntry("name", URLDecoder.decode("%EC%9D%B4%EB%8F%99%EA%B7%9C", StandardCharsets.UTF_8));
        assertThat(result).containsEntry("email", "brainbackdoor@gmail.com");
    }
}
