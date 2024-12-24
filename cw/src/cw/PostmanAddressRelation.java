package cw;

public class PostmanAddressRelation {
    private int postmanId;
    private int clientId;

    public PostmanAddressRelation(int postmanId, int clientId) {
        this.postmanId = postmanId;
        this.clientId = clientId;
    }

    // Геттеры
    public int getPostmanId() {
        return postmanId;
    }

    public int getClientId() {
        return clientId;
    }

    // Сеттеры
    public void setPostmanId(int postmanId) {
        this.postmanId = postmanId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
}
