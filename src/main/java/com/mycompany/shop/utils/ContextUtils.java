package com.mycompany.shop.utils;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

public class ContextUtils {

    public ContextUtils() {
    }

    public static ExternalContext getContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    public static void invalidateSession() {
        ((HttpSession) getContext().getSession(true)).invalidate();
    }

    public static String getContextParameter(String paramName) {
        return getContext().getInitParameter(paramName);
    }

    public static ResourceBundle getDefaultBundle() {
        String bundlePath = getContextParameter("resourceBundle.path");
        if (null == bundlePath) {
            return null;
        } else {
            return ResourceBundle.getBundle(bundlePath, FacesContext.getCurrentInstance().getViewRoot().getLocale());
        }
    }

    public static void emitInternationalizedMessage(String id, String key) {
        FacesMessage msg = new FacesMessage(ContextUtils.getDefaultBundle().getString(key));
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        FacesContext.getCurrentInstance().addMessage(id, msg);
    }

    public static String getFileNameFromPart(Part part) {
        final String partHeader = part.getHeader("content-disposition");
        for (String content : partHeader.split(";")) {
            if (content.trim().startsWith("filename")) {
                String fileName = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
                return fileName;
            }
        }
        return null;
    }
}
