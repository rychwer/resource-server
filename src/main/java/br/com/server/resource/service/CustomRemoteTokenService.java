package br.com.server.resource.service;

import feign.FeignException;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import java.util.Map;

public class CustomRemoteTokenService implements ResourceServerTokenServices {

    private AccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        try {
            Map<String, Object> map = authorizationService.getAcessToken(accessToken);
            if (map == null || map.isEmpty() || map.get("error") != null) {
                throw new InvalidTokenException("Token not allowed");
            }
            return tokenConverter.extractAuthentication(map);
        } catch (RetryableException ex) {
            throw new InvalidTokenException(ex.getCause().getLocalizedMessage());
        } catch (FeignException ex) {
            throw new InvalidTokenException(ex.contentUTF8());
        } catch (Exception ex) {
            throw new InvalidTokenException(ex.getCause().getLocalizedMessage());
        }
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }
}