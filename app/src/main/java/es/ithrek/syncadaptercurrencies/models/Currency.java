package es.ithrek.syncadaptercurrencies.models;

/**
 * Created by Mikel on 11/02/17.
 */

public class Currency {

    private int id;

    private String name;

    private String abbreviation;

    private Integer value;

    private Integer id_backend;

    private Integer is_read;

    /**
     * default constructor
     */
    public Currency() {
    }

    public Currency(int id, String name, String abbreviation, Integer value) {
        super();
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.value = value;
    }

    public Currency(int id, String name, String abbreviation, Integer value, Integer id_backend) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.value = value;
        this.id_backend = id_backend;
    }

    public Currency(int id, String name, String abbreviation, Integer value, Integer id_backend, Integer is_read) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.value = value;
        this.id_backend = id_backend;
        this.is_read = is_read;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
