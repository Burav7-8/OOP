package cw;

public class ClientNewspaperRelation {
    private int clientId;
    private int newspaperId;

    public ClientNewspaperRelation(int clientId, int newspaperId) {
        this.clientId = clientId;
        this.newspaperId = newspaperId;
    }

    // Геттеры
    public int getClientId() {
        return clientId;
    }

    public int getNewspaperId() {
        return newspaperId;
    }

    // Сеттеры
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public void setNewspaperId(int newspaperId) {
        this.newspaperId = newspaperId;
    }
}
