package com.mycompany.shop.web;

import com.mycompany.shop.dto.GenreDTO;
import com.mycompany.shop.dto.ItemDescriptionDTO;
import com.mycompany.shop.ejb.endpoints.GenreEndpoint;
import com.mycompany.shop.ejb.endpoints.ItemDescriptionEndpoint;
import com.mycompany.shop.entitis.ItemDescriptionStatus;
import com.mycompany.shop.entitis.OrderStatus;
import com.mycompany.shop.entitis.Pegi;
import com.mycompany.shop.utils.ContextUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@RequestScoped
@Named("OrdersListSearchBean")
public class OrdersListSearchBean implements Serializable {

    @Inject
    private OrdersListSession ordersListSession;
    @Inject
    private ItemDescriptionEndpoint itemDescriptionEndpoint;
    @Inject
    private GenreEndpoint genreEndpoint;

    private String loginSearch;
    private List<OrderStatus> orderStatusSearchList;
    private OrderStatus orderStatusSearch;
    private Date fromDateSearch;
    private Date toDateSearch;
    private List<ItemDescriptionDTO> itemDescriptionDtoListSearch;
    private List<OrderStatus> orderStatusList;
    private String titleSearch;
    private String genreSearch;
    private Pegi pegiSearch;
    private ItemDescriptionStatus statusSearch;
    private Integer minPriceSearch;
    private Integer maxPriceSearch;
    private List<ItemDescriptionStatus> statusSearchList;
    private List<String> genreList;
    private List<Pegi> pegiList;
    private List<ItemDescriptionStatus> statusList;
    private GenreDTO genreDtoSearch;
    private List<GenreDTO> genreDtoSearchList;
    private List<GenreDTO> genreDtoList;

    @PostConstruct
    public void init() {
        orderStatusList = new ArrayList<>();
        orderStatusList.add(OrderStatus.PLACED);
        orderStatusList.add(OrderStatus.CLOSED);
        genreDtoList = genreEndpoint.findAll();
        pegiList = new ArrayList<>();
        Pegi[] ps = Pegi.values();
        for (Pegi p : ps) {
            pegiList.add(p);
        }
        Collections.sort(pegiList);
        statusList = new ArrayList<>();
        statusList.add(ItemDescriptionStatus.ACTIVE);
        statusList.add(ItemDescriptionStatus.INACTIVE);
    }

    public GenreDTO getGenreDtoSearch() {
        return genreDtoSearch;
    }

    public void setGenreDtoSearch(GenreDTO genreDtoSearch) {
        this.genreDtoSearch = genreDtoSearch;
    }

    public List<GenreDTO> getGenreDtoSearchList() {
        return genreDtoSearchList;
    }

    public void setGenreDtoSearchList(List<GenreDTO> genreDtoSearchList) {
        this.genreDtoSearchList = genreDtoSearchList;
    }

    public List<GenreDTO> getGenreDtoList() {
        return genreDtoList;
    }

    public void setGenreDtoList(List<GenreDTO> genreDtoList) {
        this.genreDtoList = genreDtoList;
    }

    public List<OrderStatus> getOrderStatusSearchList() {
        return orderStatusSearchList;
    }

    public void setOrderStatusSearchList(List<OrderStatus> orderStatusSearchList) {
        this.orderStatusSearchList = orderStatusSearchList;
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

    public String getGenreSearch() {
        return genreSearch;
    }

    public void setGenreSearch(String genreSearch) {
        this.genreSearch = genreSearch;
    }

    public Pegi getPegiSearch() {
        return pegiSearch;
    }

    public void setPegiSearch(Pegi pegiSearch) {
        this.pegiSearch = pegiSearch;
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

    public List<ItemDescriptionStatus> getStatusSearchList() {
        return statusSearchList;
    }

    public void setStatusSearchList(List<ItemDescriptionStatus> statusSearchList) {
        this.statusSearchList = statusSearchList;
    }

    public List<String> getGenreList() {
        return genreList;
    }

    public void setGenreList(List<String> genreList) {
        this.genreList = genreList;
    }

    public List<Pegi> getPegiList() {
        return pegiList;
    }

    public void setPegiList(List<Pegi> pegiList) {
        this.pegiList = pegiList;
    }

    public String getLoginSearch() {
        return loginSearch;
    }

    public void setLoginSearch(String loginSearch) {
        this.loginSearch = "%" + loginSearch + "%";
    }

    public OrderStatus getOrderStatusSearch() {
        return orderStatusSearch;
    }

    public void setOrderStatusSearch(OrderStatus statusSearch) {
        this.orderStatusSearch = statusSearch;
    }

    public List<ItemDescriptionDTO> getItemDescriptionDtoListSearch() {
        return itemDescriptionDtoListSearch;
    }

    public void setItemDescriptionDtoListSearch(List<ItemDescriptionDTO> itemDescriptionDtoListSearch) {
        this.itemDescriptionDtoListSearch = itemDescriptionDtoListSearch;
    }

    public List<OrderStatus> getOrderStatusList() {
        return orderStatusList;
    }

    public void setOrderStatusList(List<OrderStatus> statusList) {
        this.orderStatusList = statusList;
    }

    public Date getFromDateSearch() {
        return fromDateSearch;
    }

    public void setFromDateSearch(Date fromDateSearch) {
        this.fromDateSearch = fromDateSearch;
    }

    public Date getToDateSearch() {
        return toDateSearch;
    }

    public void setToDateSearch(Date toDateSearch) {
        this.toDateSearch = toDateSearch;
    }

    public void conditionsCheck() {
        if (null == orderStatusSearch) {
            orderStatusSearchList = orderStatusList;
        } else {
            orderStatusSearchList = new ArrayList<>();
            orderStatusSearchList.add(orderStatusSearch);
        }
        if (null == fromDateSearch) {
            fromDateSearch = new Date(0);
        }
        if (null == toDateSearch) {
            toDateSearch = new Date();
        }
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

    public String findOrdersByConditions() {
        conditionsCheck();
        ordersListSession.findByConditions(loginSearch, fromDateSearch, toDateSearch, orderStatusSearchList,
                titleSearch, genreDtoSearchList, pegiSearch, minPriceSearch, maxPriceSearch);
        return "orders";
    }
    
    public Boolean isThisOrdersListPath() {
        String path = ContextUtils.getContext().getRequestPathInfo();
        if (path.equals("/orders/orders.xhtml")) {
            return true;
        }
        return false;
    }

}
