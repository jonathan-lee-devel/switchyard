package io.jonathanlee.switchyard_api.ratelimit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jonathanlee.switchyard_api.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(
    properties = {
      "switchyard.rate-limit.public-flags.limit=3",
      "switchyard.rate-limit.public-flags.window=1m"
    })
@AutoConfigureMockMvc
class PublicFlagsRateLimitFilterTests {

  private static final int LIMIT = 3;

  @Autowired private MockMvc mockMvc;

  private static RequestPostProcessor from(String ip) {
    return request -> {
      request.setRemoteAddr(ip);
      return request;
    };
  }

  private MockHttpServletRequestBuilder publicFlags(String ip) {
    return get("/v1/flags").with(from(ip));
  }

  @Test
  void requestsWithinTheLimitSucceedAndReportRemainingBudget() throws Exception {
    String ip = "10.0.0.1";
    for (int i = 1; i <= LIMIT; i++) {
      mockMvc
          .perform(publicFlags(ip))
          .andExpect(status().isOk())
          .andExpect(header().string("X-RateLimit-Limit", Integer.toString(LIMIT)))
          .andExpect(header().string("X-RateLimit-Remaining", Integer.toString(LIMIT - i)))
          .andExpect(header().doesNotExist("Retry-After"));
    }
  }

  @Test
  void requestsBeyondTheLimitAreRejectedWith429() throws Exception {
    String ip = "10.0.0.2";
    for (int i = 0; i < LIMIT; i++) {
      mockMvc.perform(publicFlags(ip)).andExpect(status().isOk());
    }

    mockMvc
        .perform(publicFlags(ip))
        .andExpect(status().isTooManyRequests())
        .andExpect(header().string("X-RateLimit-Remaining", "0"))
        .andExpect(header().exists("Retry-After"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(429))
        .andExpect(jsonPath("$.title").value("Too Many Requests"));
  }

  @Test
  void limitIsTrackedPerClientAddress() throws Exception {
    String exhausted = "10.0.0.3";
    for (int i = 0; i <= LIMIT; i++) {
      mockMvc.perform(publicFlags(exhausted));
    }
    mockMvc.perform(publicFlags(exhausted)).andExpect(status().isTooManyRequests());

    mockMvc.perform(publicFlags("10.0.0.4")).andExpect(status().isOk());
  }

  @Test
  void streamEndpointSharesTheSameBudget() throws Exception {
    String ip = "10.0.0.5";
    for (int i = 0; i < LIMIT; i++) {
      mockMvc.perform(publicFlags(ip)).andExpect(status().isOk());
    }

    mockMvc
        .perform(get("/v1/flags/stream").with(from(ip)))
        .andExpect(status().isTooManyRequests());
  }

  @Test
  void adminEndpointsAreNotRateLimited() throws Exception {
    String ip = "10.0.0.6";
    for (int i = 0; i < LIMIT * 3; i++) {
      mockMvc
          .perform(get("/v1/admin/audiences").with(from(ip)))
          .andExpect(status().isUnauthorized())
          .andExpect(header().doesNotExist("X-RateLimit-Limit"));
    }
    mockMvc
        .perform(
            post("/v1/admin/flags")
                .with(from(ip))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\":\"x\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(header().doesNotExist("X-RateLimit-Limit"));
  }
}
