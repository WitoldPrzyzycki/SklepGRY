package com.mycompany.shop.ejb.endpoints;

import com.mycompany.shop.dto.GenreDTO;
import com.mycompany.shop.ejb.facades.GenreFacade;
import com.mycompany.shop.ejb.interceptor.LoggingInterceptor;
import com.mycompany.shop.entitis.Genre;
import com.mycompany.shop.exceptions.GenreException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@Stateful
@LocalBean
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(LoggingInterceptor.class)
public class GenreEndpoint extends AbstractEndpoint implements SessionSynchronization, Serializable {

    @Inject
    private GenreFacade genreFacade;

    /**
     * Metoda zwraca listę GenreDTO używanych w aplikacji
     * @return listę GenreDTO
     */
    public List<GenreDTO> findAll() {
        List<GenreDTO> listGenreDTOs = new ArrayList<>();
        List<Genre> listGenre = genreFacade.findAll();
        for (Genre genre : listGenre) {
            listGenreDTOs.add(new GenreDTO(genre.getId(), genre.getName()));
        }
        return listGenreDTOs;
    }

    /**
     * Metoda zwraca obiekt GenreDTO o odpowiadającej nazwie dostarczonej jako 
     * parametr
     * @param name
     * @return GenreDTO
     */
    public GenreDTO findByName(String name) {
        Genre genre = genreFacade.findByName(name);
        GenreDTO genreDTO = new GenreDTO(genre.getId(), genre.getName());
        return genreDTO;
    }

    /**
     * Metoda zwraca obiekt GenreDTO o odpowiadającym id dostarczonym jako 
     * parametr
     * @param id
     * @return GenreDTO
     */
    public Genre findById(long id) {
        return genreFacade.find(id);
    }

    /**
     * Metoda tworzy nowy obiekt Genre o nazwie podanej jako parametr i utrwala 
     * go w warstwie składowania
     * @param name
     * @throws GenreException jeżeli gatunek o podanej nazwie istnieje
     */
    @RolesAllowed("Employee")
    public void crateGenre(String name) throws GenreException {
        Genre genre = new Genre(name);
        genreFacade.createGenre(genre);
    }
}
