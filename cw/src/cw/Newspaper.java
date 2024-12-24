package cw;

public class Newspaper {
    private int id;
    private String name;
    private int circulation;
    private String category;

    public Newspaper(int id, String name, int circulation, String category) {
        this.id = id;
        this.name = name;
        this.circulation = circulation;
        this.category = category;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCirculation() {
        return circulation;
    }

    public String getCategory() {
        return category;
    }

    // Сеттеры
    public void setName(String name) {
        this.name = name;
    }

    public void setCirculation(int circulation) {
        this.circulation = circulation;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
