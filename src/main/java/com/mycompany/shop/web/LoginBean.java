
package com.mycompany.shop.web;

import com.mycompany.shop.utils.ContextUtils;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named("loginBean")
@RequestScoped
public class LoginBean implements Serializable {

    public LoginBean() {
    }
   
    public String invalidateSession() {
        ContextUtils.invalidateSession();
        return "main";
    }

}
