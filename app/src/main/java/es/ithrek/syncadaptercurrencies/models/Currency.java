package es.ithrek.syncadaptercurrencies.models;

import com.google.gson.annotations.Expose;

/**
 * Created by Mikel on 11/02/17.
 */

public class Currency {

    @Expose
    private Integer id;

    @Expose
    private String name;

    @Expose
    private String abbreviation;

    @Expose
    private Integer value;

    private Integer id_backend;

    private Integer is_read;

    /**
     * default constructor
     */
    public Currency() {
    }

    public Currency(Integer id, String name, String abbreviation, Integer value) {
        super();
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.value = value;
    }

    public Currency(Integer id, String name, String abbreviation, Integer value, Integer id_backend) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.value = value;
        this.id_backend = id_backend;
    }

    public Currency(Integer id, String name, String abbreviation, Integer value, Integer id_backend, Integer is_read) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.value = value;
        this.id_backend = id_backend;
        this.is_read = is_read;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getId_backend() {
        return id_backend;
    }

    public void setId_backend(Integer id_backend) {
        this.id_backend = id_backend;
    }

    public Integer getIs_read() {
        return is_read;
    }

    public void setIs_read(Integer is_read) {
        this.is_read = is_read;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", value=" + value +
                ", id_backend=" + id_backend +
                ", is_read=" + is_read +
                '}';
    }
}
