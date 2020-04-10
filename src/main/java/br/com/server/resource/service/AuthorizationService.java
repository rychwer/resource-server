package br.com.server.resource.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.server.resource.config.CoreFeignConfiguration;
import feign.Headers;

@FeignClient(name = "authorization-server", url = "http://localhost:8090/oauth/check_token", configuration = CoreFeignConfiguration.class)
public interface AuthorizationService {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @GetMapping
    Map<String, Object> getAcessToken(@RequestParam(value = "token") String token);

}
