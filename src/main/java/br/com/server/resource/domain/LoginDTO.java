package br.com.server.resource.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {

    @JsonProperty("email")
    private String username;

    private String password;

}
