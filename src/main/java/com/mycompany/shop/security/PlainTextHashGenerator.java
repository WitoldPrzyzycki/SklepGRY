
package com.mycompany.shop.security;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;

@ApplicationScoped
@Default
public class PlainTextHashGenerator implements HashGenerator{

    @Override
    public String generateHash(String input) {
        return input;
    }
    
}
