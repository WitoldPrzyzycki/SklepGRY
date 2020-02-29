package com.mycompany.shop.utils.converters;

import com.mycompany.shop.dto.GenreDTO;
import com.mycompany.shop.ejb.endpoints.GenreEndpoint;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class GenreConverter implements Converter {

    @Inject
    private GenreEndpoint genreEndpoint;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
        if (submittedValue == null || submittedValue.isEmpty()) {
            return null;
        }

        return genreEndpoint.findByName(submittedValue);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
        if (modelValue == null) {
            return "";
        }

        if (modelValue instanceof GenreDTO) {
            return String.valueOf(((GenreDTO) modelValue).getName());
        } else {
            throw new ConverterException(new FacesMessage(modelValue + " is not a valid GenreDTO"));
        }
    }
}
