package br.com.server.resource.service;

import java.util.Map;

import br.com.server.resource.domain.LoginDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import br.com.server.resource.config.CoreFeignConfiguration;
import feign.Headers;

@FeignClient(value = "ms-authentication", configuration = CoreFeignConfiguration.class)
public interface AuthorizationService {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @GetMapping("/oauth/check_token")
    Map<String, Object> getAcessToken(@RequestParam(value = "token") String token);

    @RequestMapping(method = RequestMethod.POST, value = "/login/client", consumes = "application/json")
    void createClientLogin(@RequestBody LoginDTO loginDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/login/professional", consumes = "application/json")
    void createProfessionalLogin(@RequestBody LoginDTO loginDTO);

}
