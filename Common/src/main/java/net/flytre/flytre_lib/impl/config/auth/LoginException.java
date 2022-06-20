package net.flytre.flytre_lib.impl.config.auth;

public final class LoginException extends Exception
{
    public LoginException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LoginException(String message)
    {
        super(message);
    }
}
