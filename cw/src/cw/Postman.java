package cw;

public class Postman {
    private int id;
    private String name;
    private String workSchedule;

    public Postman(int id, String name, String workSchedule) {
        this.id = id;
        this.name = name;
        this.workSchedule = workSchedule;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWorkSchedule() {
        return workSchedule;
    }

    // Сеттеры
    public void setName(String name) {
        this.name = name;
    }

    public void setWorkSchedule(String workSchedule) {
        this.workSchedule = workSchedule;
    }
}
