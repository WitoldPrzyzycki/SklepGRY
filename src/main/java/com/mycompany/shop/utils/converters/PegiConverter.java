
package com.mycompany.shop.utils.converters;

import com.mycompany.shop.entitis.Pegi;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;

@Named
@RequestScoped
public class PegiConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Pegi.valueOf(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String pegi = value.toString();
        return pegi.replace(" ", "_");
    }
    
}
