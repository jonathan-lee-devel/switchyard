package io.jonathanlee.switchyard_api.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jonathanlee.switchyard_api.TestcontainersConfiguration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTests {

  @Autowired private MockMvc mockMvc;
  @Autowired private JwtGrantedAuthoritiesConverter authoritiesConverter;

  /** A token whose authorities are derived by the application's own roles-claim converter. */
  private JwtRequestPostProcessor jwtWithRoles(String... roles) {
    return jwt()
        .jwt(jwt -> jwt.claim(SecurityConfig.ROLES_CLAIM, List.of(roles)))
        .authorities(authoritiesConverter);
  }

  private JwtRequestPostProcessor adminJwt() {
    return jwtWithRoles(SecurityConfig.ADMIN_ROLE);
  }

  private JwtRequestPostProcessor nonAdminJwt() {
    return jwtWithRoles("VIEWER");
  }

  @Test
  void publicFlagReadsNeedNoCredentials() throws Exception {
    mockMvc.perform(get("/v1/flags")).andExpect(status().isOk());
  }

  @Test
  void openApiDocsNeedNoCredentials() throws Exception {
    mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
  }

  @Test
  void anonymousAdminRequestsAreRejectedWith401() throws Exception {
    mockMvc.perform(get("/v1/admin/flags/key/anything")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/v1/admin/audiences")).andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            post("/v1/admin/flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\":\"anon\",\"enabled\":true}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void authenticatedNonAdminRequestsAreRejectedWith403() throws Exception {
    mockMvc
        .perform(get("/v1/admin/flags/key/anything").with(nonAdminJwt()))
        .andExpect(status().isForbidden());
    mockMvc
        .perform(get("/v1/admin/audiences").with(nonAdminJwt()))
        .andExpect(status().isForbidden());
    mockMvc
        .perform(get("/v1/admin/audiences").with(jwt()))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminRoleFromJwtRolesClaimGrantsAccess() throws Exception {
    mockMvc.perform(get("/v1/admin/audiences").with(adminJwt())).andExpect(status().isOk());
    mockMvc
        .perform(get("/v1/admin/flags/key/does-not-exist").with(adminJwt()))
        .andExpect(status().isNotFound());
  }

  @Test
  void adminCanWriteWithoutCsrfToken() throws Exception {
    String key = "sec-" + UUID.randomUUID().toString().substring(0, 8);

    mockMvc
        .perform(
            post("/v1/admin/flags")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\":\"" + key + "\",\"enabled\":true}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.key").value(key))
        .andExpect(jsonPath("$.isEnabled").value(true));

    mockMvc
        .perform(delete("/v1/admin/flags/key/" + key).with(adminJwt()))
        .andExpect(status().isNoContent());
  }
}
