package com.eikona.tech.dto;

public class AuthenticationResponse {
	
private final String authorize_token;

public String getAuthorize_token() {
	return authorize_token;
}

public AuthenticationResponse(String authorize_token) {
	this.authorize_token = authorize_token;
}


}
