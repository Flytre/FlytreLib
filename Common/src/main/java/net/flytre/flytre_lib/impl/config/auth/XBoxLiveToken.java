package net.flytre.flytre_lib.impl.config.auth;

public record XBoxLiveToken(String token, String uhs) {

	public String getToken() {
		return token;
	}

	public String getUHS() {
		return uhs;
	}
}
