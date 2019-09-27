package com.capgemini.demo.config;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import javax.net.ssl.SSLContext;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@EnableWebSecurity
class HttpSecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${server.ssl.trust-store-password}")
  private String trustStorePassword;


  @Value("${server.ssl.key-store-password}")
  private String keyStorePassword;

  @Value("${server.ssl.key-password}")
  private String keyPassword;


  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .authorizeRequests()
        .anyRequest()
        .permitAll()
        .and()
        .formLogin()
        .disable()
        .httpBasic()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.NEVER)
        .and()
        .csrf()
        .disable();
  }

  @Bean
  public RestTemplate restTemplate() throws Exception {
    RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
    restTemplate.setErrorHandler(
        new DefaultResponseErrorHandler() {
          @Override
          protected boolean hasError(HttpStatus statusCode) {
            return false;
          }
        });

    return restTemplate;
  }

  private ClientHttpRequestFactory clientHttpRequestFactory() throws Exception {
    return new HttpComponentsClientHttpRequestFactory(httpClient());
  }

  private HttpClient httpClient() throws Exception {
    // Load our keystore and truststore containing certificates that we trust.

    SSLContext sslcontext =
      SSLContexts.custom()
        .loadTrustMaterial( new File("src/main/resources/cert/truststore-client.jks"), trustStorePassword.toCharArray())
        .loadKeyMaterial(
          new File("src/main/resources/cert/client.jks"), keyStorePassword.toCharArray(), keyPassword.toCharArray())
        .build();
    SSLConnectionSocketFactory sslConnectionSocketFactory =
      new SSLConnectionSocketFactory(sslcontext, new NoopHostnameVerifier());
    return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
  }

  @Bean
  public CommonsRequestLoggingFilter requestLoggingFilter() {
    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
    loggingFilter.setIncludeClientInfo(true);
    loggingFilter.setIncludeQueryString(true);
    loggingFilter.setIncludePayload(true);
    return loggingFilter;
  }

}
