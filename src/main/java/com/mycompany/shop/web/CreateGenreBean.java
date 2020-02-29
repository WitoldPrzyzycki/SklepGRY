package com.mycompany.shop.web;

import com.mycompany.shop.dto.GenreDTO;
import com.mycompany.shop.ejb.endpoints.GenreEndpoint;
import com.mycompany.shop.exceptions.GenreException;
import com.mycompany.shop.utils.ContextUtils;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

@RequestScoped
@Named("createGenreBean")
public class CreateGenreBean {

    @Inject
    private GenreEndpoint genreEndpoint;

    private List<GenreDTO> genreDTOs;
    private GenreDTO genreDTO;
    private String name;
    private DataModel<GenreDTO> dataModelGenreDTOs;

    public List<GenreDTO> getGenreDTOs() {
        return genreDTOs;
    }

    @PostConstruct
    public void init() {
        genreDTOs = genreEndpoint.findAll();
        dataModelGenreDTOs = new ListDataModel<>(genreDTOs);
    }

    public GenreDTO getGenreDTO() {
        return genreDTO;
    }

    public void setGenreDTO(GenreDTO genreDTO) {
        this.genreDTO = genreDTO;
    }

    public void setGenreDTOs(List<GenreDTO> genreDTOs) {
        this.genreDTOs = genreDTOs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataModel<GenreDTO> getDataModelGenreDTOs() {
        return dataModelGenreDTOs;
    }

    public void setDataModelGenreDTOs(DataModel<GenreDTO> dataModelGenreDTOs) {
        this.dataModelGenreDTOs = dataModelGenreDTOs;
    }

    public String createGenre() {
        try {
            genreEndpoint.crateGenre(name);
            return "addItemDescription";
        } catch (GenreException ge) {
            ContextUtils.emitInternationalizedMessage(null, ge.getMessage());
            return "";
        }
    }

    public String activateGenre() {
        return "";
    }

    public String deActivateGenre() {
        return "";
    }
}
