
package com.mycompany.shop.utils.converters;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;


@Named
@RequestScoped
public class PriceConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String tempString = value.replace(",", ".");
        BigDecimal value1 = new BigDecimal(tempString);
        BigDecimal value2 = value1.multiply(new BigDecimal(100));
        return value2.intValue();
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        BigDecimal value1 = new BigDecimal((Integer) value);
        BigDecimal value2 = value1.divide(new BigDecimal(100));
        NumberFormat f = NumberFormat.getCurrencyInstance(new Locale("pl"));
        if (f instanceof DecimalFormat) {
            ((DecimalFormat) f).format(value2);
        }
        
        return value2.toPlainString().replace(".", ",");
    }

}
