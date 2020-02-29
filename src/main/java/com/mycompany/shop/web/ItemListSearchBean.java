package com.mycompany.shop.web;

import com.mycompany.shop.dto.GenreDTO;
import com.mycompany.shop.ejb.endpoints.GenreEndpoint;
import com.mycompany.shop.ejb.endpoints.ItemDescriptionEndpoint;
import com.mycompany.shop.entitis.ItemDescriptionStatus;
import com.mycompany.shop.entitis.Pegi;
import com.mycompany.shop.utils.ContextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;


@RequestScoped
@Named("itemListSearchBean")
public class ItemListSearchBean {

    @Inject
    private ItemListSession itemListSession;
    @Inject
    private EmployeeItemListSession employeeItemListSession;
    @Inject
    private ItemDescriptionEndpoint itemDescriptionEndpoint;
    @Inject
    private GenreEndpoint genreEndpoint;

    private String titleSearch;
    private GenreDTO genreDtoSearch;
    private List<GenreDTO> genreDtoSearchList;
    private Pegi pegiSearch;
    private ItemDescriptionStatus statusSearch;
    private Integer minPriceSearch;
    private Integer maxPriceSearch;
    private List<ItemDescriptionStatus> statusSearchList;
    private List<GenreDTO> genreDtoList;
    private List<Pegi> pegiList;
    private List<ItemDescriptionStatus> statusList;

    public List<GenreDTO> getGenreDtoSearchList() {
        return genreDtoSearchList;
    }

    public void setGenreDtoSearchList(List<GenreDTO> genreDtoSearchList) {
        this.genreDtoSearchList = genreDtoSearchList;
    }

    public GenreDTO getGenreDtoSearch() {
        return genreDtoSearch;
    }

    public void setGenreDtoSearch(GenreDTO genreDtoSearch) {
        this.genreDtoSearch = genreDtoSearch;
    }

    public List<GenreDTO> getGenreDtoList() {
        return genreDtoList;
    }

    public void setGenreDtoList(List<GenreDTO> genreDtoList) {
        this.genreDtoList = genreDtoList;
    }

    public Integer getMinPriceSearch() {
        return minPriceSearch;
    }

    public void setMinPriceSearch(Integer minPriceSearch) {
        this.minPriceSearch = minPriceSearch;
    }

    public Integer getMaxPriceSearch() {
        return maxPriceSearch;
    }

    public void setMaxPriceSearch(Integer maxPriceSearch) {
        this.maxPriceSearch = maxPriceSearch;
    }

    public List<Pegi> getPegiList() {
        return pegiList;
    }

    public void setPegiList(List<Pegi> pegiList) {
        this.pegiList = pegiList;
    }

    public Pegi getPegiSearch() {
        return pegiSearch;
    }

    public void setPegiSearch(Pegi pegiSearch) {
        this.pegiSearch = pegiSearch;
    }

    public ItemListSearchBean() {
    }

    public ItemDescriptionStatus getStatusSearch() {
        return statusSearch;
    }

    public void setStatusSearch(ItemDescriptionStatus statusSearch) {
        this.statusSearch = statusSearch;
    }

    public List<ItemDescriptionStatus> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<ItemDescriptionStatus> statusList) {
        this.statusList = statusList;
    }

    public String getTitleSearch() {
        return titleSearch;
    }

    public void setTitleSearch(String titleSearch) {
        this.titleSearch = "%" + titleSearch + "%";
    }

    @PostConstruct
    public void init() {
        pegiList = new ArrayList<>();
        Pegi[] ps = Pegi.values();
        for (Pegi p : ps) {
            pegiList.add(p);
        }
        Collections.sort(pegiList);
        statusList = new ArrayList<>();
        statusList.add(ItemDescriptionStatus.ACTIVE);
        statusList.add(ItemDescriptionStatus.INACTIVE);
        genreDtoList = genreEndpoint.findAll();
    }

    public String findActiveIdsByConditions() {
        conditionsCheck();
        itemListSession.findActiveIdsByConditions(titleSearch, genreDtoSearchList, pegiSearch, minPriceSearch, maxPriceSearch);
        return "main";
    }

    private void conditionsCheck() {
        if (null == titleSearch) {
            titleSearch = "%";
        }
        if (null == genreDtoSearch) {
            genreDtoSearchList = genreDtoList;
        } else {
            genreDtoSearchList = new ArrayList<>();
            genreDtoSearchList.add(genreDtoSearch);
        }
        if (null == pegiSearch) {
            pegiSearch = pegiList.get(pegiList.size() - 1);
        }
        if (null == statusSearch) {
            statusSearchList = statusList;
        } else {
            statusSearchList = new ArrayList<>();
            statusSearchList.add(statusSearch);
        }
        if (null == minPriceSearch) {
            minPriceSearch = 0;
        }
        if (null == maxPriceSearch) {
            maxPriceSearch = 99999;//tu może poszukać w bazie maxa 
        }
    }

    public String findEmployeeIdsByConditions() {
        conditionsCheck();
        employeeItemListSession.findIdsByConditions(titleSearch, genreDtoSearchList, pegiSearch, statusSearchList, minPriceSearch, maxPriceSearch);
        return "employeeItemList";
    }

    public Boolean isThisEmployeeItemListPath() {
        String path = ContextUtils.getContext().getRequestPathInfo();
        if (path.equals("/employeeItemList/employeeItemList.xhtml")) {
            return true;
        }
        return false;
    }

}
