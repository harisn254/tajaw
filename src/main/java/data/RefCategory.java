/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

/**
 *
 * @author Hari
 */
public class RefCategory {
    private Integer idCategory;
    private String name;

    public RefCategory() {
    }

    public RefCategory(Integer idCategory, String name) {
        this.idCategory = idCategory;
        this.name = name;
    }

    public Integer getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Integer idCategory) {
        this.idCategory = idCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
