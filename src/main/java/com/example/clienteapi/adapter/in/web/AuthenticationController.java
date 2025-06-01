package com.example.clienteapi.adapter.in.web;

 import com.example.clienteapi.domain.service.TokenService;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.security.authentication.AuthenticationManager;
 import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
 import org.springframework.security.core.Authentication;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RestController;

 @RestController
 @RequestMapping("/login")
 public class AuthenticationController {

     @Autowired
     private AuthenticationManager authenticationManager;

     @Autowired
     private TokenService tokenService;

     @PostMapping
     public TokenResponse login(@RequestBody LoginRequest loginRequest) {
         Authentication authentication = authenticationManager.authenticate(
                 new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
         );

         String token = tokenService.gerarToken(authentication);

         return new TokenResponse(token);
     }
 }