package cw;

import java.io.File;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class cw extends JFrame {
    private JTable table;
    private DefaultTableModel clientTableModel;
    private DefaultTableModel postmanTableModel; 
    private DefaultTableModel newspaperTableModel;
    private DefaultTableModel clientNewspaperTableModel;
    private DefaultTableModel postmanAddressTableModel;
    private JComboBox<String> tableSelector;
    

    
 // Панель с кнопками
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton addButton = new JButton("Добавить");
    JButton editButton = new JButton("Редактировать");
    JButton deleteButton = new JButton("Удалить");

    private List<Client> clients = new ArrayList<>();
    private List<Postman> postmen = new ArrayList<>();
    private List<Newspaper> newspapers = new ArrayList<>();
    private List<ClientNewspaperRelation> clientNewspapers = new ArrayList<>();
    private List<PostmanAddressRelation> postmanAddresses = new ArrayList<>();
    
    

    public cw() { 
    	
    	
    	
        setTitle("Почтовая информационная система");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Меню");
        JMenuItem loadItem = new JMenuItem("Загрузить");
        JMenuItem saveItem = new JMenuItem("Сохранить");
        menu.add(loadItem);
        menu.add(saveItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        loadItem.addActionListener(e -> loadDataFromXML());
        saveItem.addActionListener(e -> saveDataToXML());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH); 
        
        

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> tableSelector = new JComboBox<>(new String[]{"Клиенты", "Почтальоны", "Газеты", "Клиенты и Газеты", "Почтальоны и Адреса"});
        topPanel.add(new JLabel("Выбор таблицы"));
        topPanel.add(tableSelector);
        add(topPanel, BorderLayout.NORTH);

        tableSelector.addActionListener(e -> switchTable(tableSelector.getSelectedItem().toString()));

        clientTableModel = new DefaultTableModel(new Object[]{"ФИО клиента", "Адрес"}, 0);
        postmanTableModel = new DefaultTableModel(new Object[]{"ФИО почтальона", "График работы"}, 0);
        newspaperTableModel = new DefaultTableModel(new Object[]{"Название газеты", "Тираж", "Категория"}, 0);
        clientNewspaperTableModel = new DefaultTableModel(new Object[]{"ФИО клиента", "Газеты"}, 0);
        postmanAddressTableModel = new DefaultTableModel(new Object[]{"ФИО почтальона", "Адреса работы"}, 0);

        table = new JTable(clientTableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER);
        
        
        
        addButton.addActionListener(e -> addAction());
        editButton.addActionListener(e -> editAction());
        deleteButton.addActionListener(e -> deleteAction());
        
        

        setVisible(true);
    }

    private void switchTable(String selectedTable) {
        switch (selectedTable) {
            case "Клиенты":
                table.setModel(clientTableModel);
                break;
            case "Почтальоны":
                table.setModel(postmanTableModel);
                break;
            case "Газеты":
                table.setModel(newspaperTableModel);
                break;
            case "Клиенты и Газеты":
                table.setModel(clientNewspaperTableModel);
                break;
            case "Почтальоны и Адреса":
                table.setModel(postmanAddressTableModel);
                break;
        }
    } 
    private void addAction() {
        // Получаем текущую таблицу (model) из JTable
        DefaultTableModel currentTableModel = (DefaultTableModel) table.getModel();

        // Проверяем, какая таблица активна
        if (currentTableModel == clientTableModel) {
            // Если выбрана таблица с клиентами, вызываем метод добавления клиента
            addClient();
        } else if (currentTableModel == postmanTableModel) {
            // Если выбрана таблица с почтальонами, вызываем метод добавления почтальона
            addPostman();
        } else if (currentTableModel == newspaperTableModel) {
            // Если выбрана таблица с газетами, вызываем метод добавления газеты
            addNewspaper();
        } else if (currentTableModel == clientNewspaperTableModel) {
            // Если выбрана таблица с клиентами и газетами, вызываем метод добавления записи о клиенте и газете
            addClientNewspaperRelation();
        } else if (currentTableModel == postmanAddressTableModel) {
            // Если выбрана таблица с почтальонами и адресами, вызываем метод добавления записи о почтальоне и адресах
            addPostmanAddressRelation();
        }
    }

    private void addClient() {
        // Ввод данных через диалоговые окна
        String newName = JOptionPane.showInputDialog(this, "Введите ФИО клиента:");
        if (newName == null || newName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ФИО клиента не может быть пустым!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newAddress = JOptionPane.showInputDialog(this, "Введите адрес клиента:");
        if (newAddress == null || newAddress.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Адрес клиента не может быть пустым!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Генерация нового ID клиента
        int newId = clients.size() + 1;

        // Создание нового клиента
        Client newClient = new Client(newId, newName, newAddress);
        clients.add(newClient);

        // Обновление таблицы клиентов
        updateClientTable();

        
        
        // Запрашиваем, с какими газетами должен быть связан этот клиент
        String[] newspaperNames = newspapers.stream().map(Newspaper::getName).toArray(String[]::new);
        String newspaperName = (String) JOptionPane.showInputDialog(this, "Выберите газету, с которой клиент будет связан (или оставьте пустым):", "Выбор газеты",
                JOptionPane.QUESTION_MESSAGE, null, newspaperNames, newspaperNames[0]);

        // Если газета не выбрана, сохраняем связь с пустым значением
        if (newspaperName != null && !newspaperName.trim().isEmpty()) {
            // Найдем газету по имени
            int newspaperId = newspapers.stream()
                                         .filter(n -> n.getName().equals(newspaperName))
                                         .findFirst()
                                         .orElse(null)
                                         .getId();
            
            // Создаем связь между клиентом и газетой
            ClientNewspaperRelation relation = new ClientNewspaperRelation(newClient.getId(), newspaperId);
            clientNewspapers.add(relation);

            // Обновим таблицу "Клиенты и Газеты"
            updateClientNewspaperTable();
        } else {
            // Добавляем клиента в таблицу "Клиенты и Газеты" даже если он не связан с газетой
            clientNewspaperTableModel.addRow(new Object[]{newClient.getName(), ""});
        }
    }
 
 
    private void addPostman() {
        // Ввод данных через диалоговые окна
        String newName = JOptionPane.showInputDialog(this, "Введите ФИО почтальона:");
        if (newName == null || newName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ФИО почтальона не может быть пустым!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String workSchedule = JOptionPane.showInputDialog(this, "Введите график работы почтальона:");
        if (workSchedule == null || workSchedule.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "График работы почтальона не может быть пустым!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Генерация нового ID почтальона
        int newId = postmen.size() + 1;

        // Создание нового почтальона
        Postman newPostman = new Postman(newId, newName, workSchedule);
        postmen.add(newPostman);

        // Обновление таблицы почтальонов
        updatePostmanTable();

        // Также обновляем связанные таблицы (например, таблицу "Почтальоны и Адреса")
        updatePostmanAddressTable();

        // Запрашиваем, с какими клиентами и адресами должен быть связан этот почтальон
        String[] clientNames = clients.stream().map(Client::getName).toArray(String[]::new);
        String clientName = (String) JOptionPane.showInputDialog(this, "Выберите клиента для почтальона (или оставьте пустым):", "Выбор клиента",
                JOptionPane.QUESTION_MESSAGE, null, clientNames, clientNames[0]);

        // Если клиент не выбран, сохраняем связь с пустым значением
        if (clientName != null && !clientName.trim().isEmpty()) {
            // Найдем клиента по имени
            int clientId = clients.stream()
                                  .filter(c -> c.getName().equals(clientName))
                                  .findFirst()
                                  .orElse(null)
                                  .getId();

            // Создаем связь между почтальоном и клиентом
            PostmanAddressRelation relation = new PostmanAddressRelation(newPostman.getId(), clientId);
            postmanAddresses.add(relation);

            // Обновим таблицу "Почтальоны и Адреса"
            updatePostmanAddressTable();
        } else {
            // Добавляем почтальона в таблицу "Почтальоны и Адреса" даже если он не связан с клиентом
            postmanAddressTableModel.addRow(new Object[]{newPostman.getName(), ""});
        }
    }
 
  
    private void addNewspaper() {
        // Ввод данных через диалоговые окна
        String newName = JOptionPane.showInputDialog(this, "Введите название газеты:");
        if (newName == null || newName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Название газеты не может быть пустым!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String circulationStr = JOptionPane.showInputDialog(this, "Введите тираж газеты:");
        if (circulationStr == null || circulationStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Тираж газеты не может быть пустым!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int circulation;
        try {
            circulation = Integer.parseInt(circulationStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Тираж должен быть числом!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String category = JOptionPane.showInputDialog(this, "Введите категорию газеты:");
        if (category == null || category.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Категория газеты не может быть пустой!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Генерация нового ID для газеты
        int newId = newspapers.size() + 1;

        // Создание новой газеты
        Newspaper newNewspaper = new Newspaper(newId, newName, circulation, category);
        newspapers.add(newNewspaper);

        // Обновление таблицы газеты
        updateNewspaperTable();

        // Также обновляем связанные таблицы "Клиенты и Газеты"
        updateClientNewspaperTable();

        // Запрашиваем, с какими клиентами должна быть связана эта газета
        String[] clientNames = clients.stream().map(Client::getName).toArray(String[]::new);
        String clientName = (String) JOptionPane.showInputDialog(this, "Выберите клиента для газеты (или оставьте пустым):", "Выбор клиента",
                JOptionPane.QUESTION_MESSAGE, null, clientNames, clientNames[0]);

        // Если клиент выбран, сохраняем связь
        if (clientName != null && !clientName.trim().isEmpty()) {
            // Находим клиента по имени
            int clientId = clients.stream()
                                  .filter(c -> c.getName().equals(clientName))
                                  .findFirst()
                                  .orElse(null)
                                  .getId();

            // Создаем связь между клиентом и газетой
            ClientNewspaperRelation relation = new ClientNewspaperRelation(clientId, newNewspaper.getId());
            clientNewspapers.add(relation);

            // Обновляем таблицу "Клиенты и Газеты"
            updateClientNewspaperTable();
        } else {
            // Добавляем газету в таблицу "Клиенты и Газеты" даже если она не связана с клиентом
            clientNewspaperTableModel.addRow(new Object[]{"", newNewspaper.getName()});
        }
    }


    private void addClientNewspaperRelation() {
        // Пример данных для добавления связи "Клиент и Газета"
        int clientId = 1; // Пример ID клиента
        int newspaperId = 1; // Пример ID газеты

        ClientNewspaperRelation newRelation = new ClientNewspaperRelation(clientId, newspaperId);
        clientNewspapers.add(newRelation);

        // Обновляем таблицу
        updateClientNewspaperTable();
    }

    private void addPostmanAddressRelation() {
        // Пример данных для добавления связи "Почтальон и Адрес"
        int postmanId = 1; // Пример ID почтальона
        int clientId = 1; // Пример ID клиента (адрес клиента)

        PostmanAddressRelation newRelation = new PostmanAddressRelation(postmanId, clientId);
        postmanAddresses.add(newRelation);

        // Обновляем таблицу
        updatePostmanAddressTable();
    }


   

    

    private void deleteAction() {
        // Получаем выбранную строку из таблицы
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите объект для удаления.");
            return;
        }

        // Определяем текущую модель таблицы
        DefaultTableModel currentTableModel = (DefaultTableModel) table.getModel();

        // Проверяем какая таблица активна и вызываем соответствующий метод удаления
        if (currentTableModel == clientTableModel) {
            deleteClient(selectedRow);
        } else if (currentTableModel == postmanTableModel) {
            deletePostman(selectedRow);
        } else if (currentTableModel == newspaperTableModel) {
            deleteNewspaper(selectedRow);
        } else if (currentTableModel == clientNewspaperTableModel) {
            deleteClientNewspaperRelation(selectedRow);
        } else if (currentTableModel == postmanAddressTableModel) {
            deletePostmanAddressRelation(selectedRow);
        }

        // Обновляем таблицу после удаления
        updateTable();
    }

    private void deleteClient(int selectedRow) {
        // Логика удаления клиента
        Client clientToRemove = clients.get(selectedRow);
        int clientIdToRemove = clientToRemove.getId();  // Получаем уникальный идентификатор клиента
        
        // Удаление всех связей с газетами для данного клиента
        clientNewspapers.removeIf(relation -> relation.getClientId() == clientIdToRemove);
        updateClientNewspaperTable();
        
        // удаление связей с почтальонами для данных адресов
        postmanAddresses.removeIf(relation -> relation.getClientId() == clientIdToRemove);
        updatePostmanAddressTable();

        // Удаляем клиента из списка
        clients.remove(clientToRemove);
        clientTableModel.removeRow(selectedRow); 
    }

    private void deletePostman(int selectedRow) {
        // Логика удаления почтальона
        Postman postmanToRemove = postmen.get(selectedRow);
        int postmanIdToRemove = postmanToRemove.getId();  // Получаем уникальный идентификатор почтальона

        // Удаление всех связей с адресами для данного почтальона
        postmanAddresses.removeIf(relation -> relation.getPostmanId() == postmanIdToRemove);
        updatePostmanAddressTable();

        // Удаляем почтальона из списка
        postmen.remove(postmanToRemove);
        postmanTableModel.removeRow(selectedRow);
    }

    private void deleteNewspaper(int selectedRow) {
        // Логика удаления газеты
        Newspaper newspaperToRemove = newspapers.get(selectedRow);
        int newspaperIdToRemove = newspaperToRemove.getId();  // Получаем уникальный идентификатор газеты

        // Удаление всех связей с клиентами для данной газеты
        clientNewspapers.removeIf(relation -> relation.getNewspaperId() == newspaperIdToRemove);
        updateClientNewspaperTable();

        // Удаляем газету из списка
        newspapers.remove(newspaperToRemove);
        newspaperTableModel.removeRow(selectedRow);
    }

    private void deleteClientNewspaperRelation(int selectedRow) {
        // Логика удаления связи "Клиенты и Газеты"
        ClientNewspaperRelation relationToRemove = clientNewspapers.get(selectedRow);
        int clientIdToRemove = relationToRemove.getClientId();
        int newspaperIdToRemove = relationToRemove.getNewspaperId();

        // Удаляем эту связь из списка
        clientNewspapers.remove(relationToRemove);
        clientNewspaperTableModel.removeRow(selectedRow);
    }

    private void deletePostmanAddressRelation(int selectedRow) {
        // Логика удаления связи "Почтальоны и Адреса"
        PostmanAddressRelation relationToRemove = postmanAddresses.get(selectedRow);
        int postmanIdToRemove = relationToRemove.getPostmanId();

        // Удаляем эту связь из списка
        postmanAddresses.remove(relationToRemove);
        postmanAddressTableModel.removeRow(selectedRow);
    }


    private void updateTable() {
        // Обновление таблицы после удаления
        table.revalidate();
        table.repaint(); 
    }



    
    
    private void editAction() {
        int selectedRow = table.getSelectedRow();
        int selectedColumn = table.getSelectedColumn();

        if (selectedRow == -1 || selectedColumn == -1) {
            JOptionPane.showMessageDialog(this, "Выберите ячейку для редактирования.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String columnName = table.getColumnName(selectedColumn);
        Object oldValue = table.getValueAt(selectedRow, selectedColumn);

        // Диалог для ввода нового значения
        String newValue = JOptionPane.showInputDialog(this, 
            "Введите новое значение для \"" + columnName + "\":", oldValue);

        if (newValue == null || newValue.trim().isEmpty()) {
            return; // Пользователь отменил ввод
        } 

        // Определяем, какая таблица используется
        if (table.getModel() == clientTableModel) {
            // Редактирование клиентов
            editClient(selectedRow, selectedColumn, newValue);
        } else if (table.getModel() == postmanTableModel) {
            // Редактирование почтальонов
            editPostman(selectedRow, selectedColumn, newValue);
        } else if (table.getModel() == newspaperTableModel) {
            // Редактирование газет
            editNewspaper(selectedRow, selectedColumn, newValue);
        } else if (table.getModel() == clientNewspaperTableModel) {
            // Редактирование связей клиентов и газет
            editClientNewspaperRelation(selectedRow, selectedColumn, newValue);
        } else if (table.getModel() == postmanAddressTableModel) {
            // Редактирование связей почтальонов и адресов
            editPostmanAddressRelation(selectedRow, selectedColumn, newValue);
        }
    }
    
    private void editClient(int row, int column, String newValue) {
        Client client = clients.get(row);
        switch (column) {
            case 0: // Имя клиента
                client.setName(newValue);
                updateClientTable();
                
                for (ClientNewspaperRelation relation : clientNewspapers) {
                    if (relation.getClientId() == client.getId()) {
                        // Если клиент связан с газетой, обновляем таблицу клиент-газета
                        updateClientNewspaperTable();
                    }
                }
                
                break;
            case 1: // Адрес клиента
                client.setAddress(newValue);
                updateClientTable();

                // Обновляем связанные данные в таблице почтальонов и адресов
                for (PostmanAddressRelation relation : postmanAddresses) {
                    if (relation.getClientId() == client.getId()) {
                        updatePostmanAddressTable();
                    }
                }
                break;
        }
    }


    private void editPostman(int row, int column, String newValue) {
        Postman postman = postmen.get(row);
        switch (column) {
            case 0: // Имя почтальона
                postman.setName(newValue);
                updatePostmanTable();
                
             // Обновляем связанные данные в таблице почтальонов и адресов
                for (PostmanAddressRelation relation : postmanAddresses) {
                    if (relation.getPostmanId() == postman.getId()) {
                        // Если почтальон связан с клиентом, обновляем таблицу почтальонов и адресов
                        updatePostmanAddressTable();
                    }
                }
                break;
                
       
            case 1: // График работы
                postman.setWorkSchedule(newValue);
                updatePostmanTable();
                break;
        }
    }


    private void editNewspaper(int row, int column, String newValue) {
        Newspaper newspaper = newspapers.get(row);
        switch (column) {
            case 0: // Название газеты
                newspaper.setName(newValue);
                updateNewspaperTable();

                // Обновляем связанные данные в таблице клиентов и газет
                for (ClientNewspaperRelation relation : clientNewspapers) {
                    if (relation.getNewspaperId() == newspaper.getId()) {
                        updateClientNewspaperTable();
                    }
                }
                break;
            case 1: // Тираж
                newspaper.setCirculation(Integer.parseInt(newValue));
                updateNewspaperTable();
                break;
            case 2: // Категория
                newspaper.setCategory(newValue);
                updateNewspaperTable();
                break;
        }
    }

    
    private void editClientNewspaperRelation(int row, int column, String newValue) {
        ClientNewspaperRelation relation = clientNewspapers.get(row);
        switch (column) {
            case 0: // Имя клиента
                Client client = findClientByName(newValue);
                if (client != null) {
                    relation.setClientId(client.getId());
                    updateClientNewspaperTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Клиент не найден.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 1: // Название газеты
                Newspaper newspaper = findNewspaperByName(newValue);
                if (newspaper != null) {
                    relation.setNewspaperId(newspaper.getId());
                    updateClientNewspaperTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Газета не найдена.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                break;
        }
    }


    private void editPostmanAddressRelation(int row, int column, String newValue) {
        PostmanAddressRelation relation = postmanAddresses.get(row);
        switch (column) {
            case 0: // Имя почтальона
                Postman postman = findPostmanByName(newValue);
                if (postman != null) {
                    relation.setPostmanId(postman.getId());
                    updatePostmanAddressTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Почтальон не найден.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 1: // Адрес
                Client client = findClientByAddress(newValue);
                if (client != null) {
                    relation.setClientId(client.getId());
                    updatePostmanAddressTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Адрес не найден.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                break;
        }
    }


    private Client findClientByName(String name) {
        for (Client client : clients) {
            if (client.getName().equals(name)) {
                return client;
            }
        }
        return null;
    }

    private Newspaper findNewspaperByName(String name) {
        for (Newspaper newspaper : newspapers) {
            if (newspaper.getName().equals(name)) {
                return newspaper;
            }
        }
        return null;
    }

    private Postman findPostmanByName(String name) {
        for (Postman postman : postmen) {
            if (postman.getName().equals(name)) {
                return postman;
            }
        }
        return null;
    }

    private Client findClientByAddress(String address) {
        for (Client client : clients) {
            if (client.getAddress().equals(address)) {
                return client;
            }
        }
        return null;
    }


    
    
 // Обновление таблицы клиентов
    private void updateClientTable() {
        clientTableModel.setRowCount(0);  // Очистка таблицы
        for (Client client : clients) {
            clientTableModel.addRow(new Object[]{client.getName(), client.getAddress()});
        }
    }

    // Обновление таблицы почтальонов
    private void updatePostmanTable() {
        postmanTableModel.setRowCount(0);  // Очистка таблицы
        for (Postman postman : postmen) {
            postmanTableModel.addRow(new Object[]{postman.getName(), postman.getWorkSchedule()});
        }
    }

    // Обновление таблицы газет
    private void updateNewspaperTable() {
        newspaperTableModel.setRowCount(0);  // Очистка таблицы
        for (Newspaper newspaper : newspapers) {
            newspaperTableModel.addRow(new Object[]{newspaper.getName(), newspaper.getCirculation(), newspaper.getCategory()});
        }
    }

    // Обновление таблицы клиентов и газет
    private void updateClientNewspaperTable() {
        clientNewspaperTableModel.setRowCount(0);  // Очистка таблицы
        for (ClientNewspaperRelation relation : clientNewspapers) {
            // Найдем клиента и газету по их id
            Client client = findClientById(relation.getClientId());
            Newspaper newspaper = findNewspaperById(relation.getNewspaperId());
            clientNewspaperTableModel.addRow(new Object[]{client.getName(), newspaper.getName()});
        }
    }

    // Обновление таблицы почтальонов и адресов
    private void updatePostmanAddressTable() {
        postmanAddressTableModel.setRowCount(0);  // Очистка таблицы
        for (PostmanAddressRelation relation : postmanAddresses) {
            // Найдем почтальона по его id
            Postman postman = findPostmanById(relation.getPostmanId());
            Client client = findClientById(relation.getClientId());
            postmanAddressTableModel.addRow(new Object[]{postman.getName(), client.getAddress()});
        }
    }

    // Помощники для поиска клиентов, газет и почтальонов по id
    private Client findClientById(int id) {
        for (Client client : clients) {
            if (client.getId() == id) {
                return client;
            }
        }
        return null;  // Если клиент не найден
    }

    private Newspaper findNewspaperById(int id) {
        for (Newspaper newspaper : newspapers) {
            if (newspaper.getId() == id) {
                return newspaper;
            }
        }
        return null;  // Если газета не найдена
    }

    private Postman findPostmanById(int id) {
        for (Postman postman : postmen) {
            if (postman.getId() == id) {
                return postman;
            }
        }
        return null;  // Если почтальон не найден
    }


    // Загрузка данных из XML
    private void loadDataFromXML() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML файлы", "xml"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(selectedFile);

                loadClients(doc);
                loadPostmen(doc);
                loadNewspapers(doc);
                loadClientNewspapers(doc);
                loadPostmanAddresses(doc);

                switchTable("Клиенты");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка при загрузке данных из файла!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadClients(Document doc) {
        NodeList clientNodes = doc.getElementsByTagName("Client");
        clients.clear();
        for (int i = 0; i < clientNodes.getLength(); i++) {
            Element clientElement = (Element) clientNodes.item(i);
            int id = Integer.parseInt(clientElement.getAttribute("id"));
            String name = clientElement.getAttribute("name");
            String address = clientElement.getAttribute("address");
            clients.add(new Client(id, name, address));
        }
        updateClientTable();
    }

    private void loadPostmen(Document doc) {
        NodeList postmanNodes = doc.getElementsByTagName("Postman");
        postmen.clear();
        for (int i = 0; i < postmanNodes.getLength(); i++) {
            Element postmanElement = (Element) postmanNodes.item(i);
            int id = Integer.parseInt(postmanElement.getAttribute("id"));
            String name = postmanElement.getAttribute("name");
            String workSchedule = postmanElement.getAttribute("workSchedule");
            postmen.add(new Postman(id, name, workSchedule));
        }
        updatePostmanTable();
    }  

    private void loadNewspapers(Document doc) {
        NodeList newspaperNodes = doc.getElementsByTagName("Newspaper");
        newspapers.clear();
        for (int i = 0; i < newspaperNodes.getLength(); i++) {
            Element newspaperElement = (Element) newspaperNodes.item(i);
            int id = Integer.parseInt(newspaperElement.getAttribute("id"));
            String name = newspaperElement.getAttribute("name");
            int circulation = Integer.parseInt(newspaperElement.getAttribute("circulation"));
            String category = newspaperElement.getAttribute("category");
            newspapers.add(new Newspaper(id, name, circulation, category));
        }
        updateNewspaperTable();
    }

    private void loadClientNewspapers(Document doc) {
        NodeList relationNodes = doc.getElementsByTagName("ClientNewspaper");
        clientNewspapers.clear();
        for (int i = 0; i < relationNodes.getLength(); i++) {
            Element relationElement = (Element) relationNodes.item(i);
            int clientId = Integer.parseInt(relationElement.getAttribute("clientId"));
            int newspaperId = Integer.parseInt(relationElement.getAttribute("newspaperId"));
            clientNewspapers.add(new ClientNewspaperRelation(clientId, newspaperId));
        }
        updateClientNewspaperTable();
    }

    private void loadPostmanAddresses(Document doc) {
        NodeList relationNodes = doc.getElementsByTagName("PostmanAddress");
        postmanAddresses.clear();
        for (int i = 0; i < relationNodes.getLength(); i++) {
            Element relationElement = (Element) relationNodes.item(i);
            int postmanId = Integer.parseInt(relationElement.getAttribute("postmanId"));
            int clientId = Integer.parseInt(relationElement.getAttribute("clientId"));
            postmanAddresses.add(new PostmanAddressRelation(postmanId, clientId));
        }
        updatePostmanAddressTable();
    }


    // Сохранение данных в XML
    private void saveDataToXML() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML файлы", "xml"));
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.newDocument();
                
                Element rootElement = doc.createElement("PostalSystem");
                doc.appendChild(rootElement);
                
                // Clients
                Element clientsElement = doc.createElement("Clients");
                rootElement.appendChild(clientsElement);
                for (Client client : clients) {
                    Element clientElement = doc.createElement("Client");
                    clientElement.setAttribute("id", String.valueOf(client.getId()));
                    clientElement.setAttribute("name", client.getName());
                    clientElement.setAttribute("address", client.getAddress());
                    clientsElement.appendChild(clientElement);
                }

                // Postmen
                Element postmenElement = doc.createElement("Postmen");
                rootElement.appendChild(postmenElement);
                for (Postman postman : postmen) {
                    Element postmanElement = doc.createElement("Postman");
                    postmanElement.setAttribute("id", String.valueOf(postman.getId()));
                    postmanElement.setAttribute("name", postman.getName());
                    postmanElement.setAttribute("workSchedule", postman.getWorkSchedule());
                    postmenElement.appendChild(postmanElement);
                }

                // Newspapers
                Element newspapersElement = doc.createElement("Newspapers");
                rootElement.appendChild(newspapersElement);
                for (Newspaper newspaper : newspapers) {
                    Element newspaperElement = doc.createElement("Newspaper");
                    newspaperElement.setAttribute("id", String.valueOf(newspaper.getId()));
                    newspaperElement.setAttribute("name", newspaper.getName());
                    newspaperElement.setAttribute("circulation", String.valueOf(newspaper.getCirculation()));
                    newspaperElement.setAttribute("category", newspaper.getCategory());
                    newspapersElement.appendChild(newspaperElement);
                }

                // ClientNewspapers
                Element clientNewspapersElement = doc.createElement("ClientNewspapers");
                rootElement.appendChild(clientNewspapersElement);
                for (ClientNewspaperRelation relation : clientNewspapers) {
                    Element relationElement = doc.createElement("ClientNewspaper");
                    relationElement.setAttribute("clientId", String.valueOf(relation.getClientId()));
                    relationElement.setAttribute("newspaperId", String.valueOf(relation.getNewspaperId()));
                    clientNewspapersElement.appendChild(relationElement);
                }

                // PostmanAddresses
                Element postmanAddressesElement = doc.createElement("PostmanAddresses");
                rootElement.appendChild(postmanAddressesElement);
                for (PostmanAddressRelation relation : postmanAddresses) {
                    Element relationElement = doc.createElement("PostmanAddress");
                    relationElement.setAttribute("postmanId", String.valueOf(relation.getPostmanId()));
                    relationElement.setAttribute("clientId", String.valueOf(relation.getClientId()));
                    postmanAddressesElement.appendChild(relationElement);
                }

                // Writing to the file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(new DOMSource(doc), new StreamResult(selectedFile));
                JOptionPane.showMessageDialog(this, "Данные успешно сохранены!", "Информация", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка при сохранении данных в файл!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 

    // Классы Client, Postman, Newspaper, ClientNewspaperRelation, PostmanAddressRelation
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new cw();  // Создание и запуск приложения
            }
        });
    }
}
