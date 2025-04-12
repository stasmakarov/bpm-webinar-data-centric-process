package io.jmix.bpm.webinar.processdata.security;

import io.jmix.core.security.SystemAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class SystemAuthHelper {

    @Autowired
    private  SystemAuthenticator systemAuthenticator;

    public  <T> T runWithSystemAuth(Supplier<T> action) {
        systemAuthenticator.begin("admin");
        try {
            return action.get();
        } finally {
            systemAuthenticator.end();
        }
    }

}
