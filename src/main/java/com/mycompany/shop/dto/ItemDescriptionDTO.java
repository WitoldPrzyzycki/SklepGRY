package com.mycompany.shop.dto;

import com.mycompany.shop.entitis.ItemDescriptionStatus;
import com.mycompany.shop.entitis.Pegi;
import java.util.List;

public class ItemDescriptionDTO {

    private final Long id;
    private String title;
    private String genre;
    private Pegi pegi;
    private int price;
    private ItemDescriptionStatus status;
    private GenreDTO genreDTO;
    private List<ItemDTO> itemsDtoList;

    public ItemDescriptionDTO(Long id, String title, GenreDTO genre, Pegi pegi, ItemDescriptionStatus status, int price) {
        this.id = id;
        this.title = title;
        this.genreDTO = genre;
        this.pegi = pegi;
        this.status = status;
        this.price = price;
    }

    public GenreDTO getGenreDTO() {
        return genreDTO;
    }

    public void setGenreDTO(GenreDTO genreDTO) {
        this.genreDTO = genreDTO;
    }

    public Long getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPegi() {
        return pegi.toString();
    }

    public void setPegi(Pegi pegi) {
        this.pegi = pegi;
    }

    public List<ItemDTO> getItemsDtoCollection() {
        return itemsDtoList;
    }

    public void setItemsDtoList(List<ItemDTO> itemsDtoList) {
        this.itemsDtoList = itemsDtoList;
    }

    public ItemDTO getItem() {
        return itemsDtoList.get(0);
    }

    public ItemDescriptionStatus getStatus() {
        return status;
    }

    public void setStatus(ItemDescriptionStatus status) {
        this.status = status;
    }

}
