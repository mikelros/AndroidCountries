package es.ithrek.syncadaptercurrencies.models;

/**
 * Created by Mikel on 11/02/17.
 */

public class Currency {

    private int id;

    private String name;

    private String abbreviation;

    private Integer value;

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
}
