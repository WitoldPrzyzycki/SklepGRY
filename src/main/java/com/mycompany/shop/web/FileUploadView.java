package com.mycompany.shop.web;

import com.mycompany.shop.utils.ContextUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@Named
@RequestScoped
public class FileUploadView implements Serializable {

    private UploadedFile file;

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void upload() {
        if (file != null) {
            ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            String path = servletContext.getRealPath("");
            StringBuilder sb = new StringBuilder(path);
            path = sb.append("/images").toString();
            String filename = file.getFileName();
            int dot = filename.indexOf(".");
            String ext = filename.substring(dot);
            try (InputStream input = file.getInputstream()) {
                Files.copy(input, new File(path, "terefere").toPath());
            } catch (IOException e) {
                ContextUtils.emitInternationalizedMessage(null, "error.file.upload.fail");
            }
            System.out.println(path + filename);
        } else {
//            System.out.println("UNSuccessful");
            FacesMessage message = new FacesMessage("UNSuccessful");
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        if ((file != null)) {
            System.out.println("Successful");
        }
//        FacesMessage msg = new FacesMessage("Successful", event.getFile().getFileName() + " is uploaded.");
//        FacesContext.getCurrentInstance().addMessage(null, msg);
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = servletContext.getRealPath("");
        System.out.println(path);
        StringBuilder sb = new StringBuilder(path);
        sb.append("/images");
        try (InputStream input = file.getInputstream()) {
            Files.copy(input, new File(path, "terefere").toPath());
        } catch (IOException e) {
            System.out.println("LIPA");
        }
    }
}
