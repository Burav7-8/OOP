package lab5;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class lab5 extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> tableSelector;
    private JScrollBar scrollBar;

    private DefaultTableModel clientTableModel;
    private DefaultTableModel postmanTableModel;
    private DefaultTableModel newspaperTableModel;

    public lab5() {
        setTitle("Почтовая информационная система");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Верхнее меню
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Меню");

        // Добавляем пункт меню "Сохранить"
        JMenuItem saveItem = new JMenuItem("Сохранить");
        menu.add(saveItem);

        // Добавляем пункт меню "Загрузить"
        JMenuItem loadItem = new JMenuItem("Загрузить");
        menu.add(loadItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Обработчик для меню "Сохранить"
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAllTablesToFile();
            }
        });

        // Обработчик для меню "Загрузить"
        loadItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllTablesFromFile();
            }
        });

        // Верхняя панель для выбора таблицы
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tableSelector = new JComboBox<>(new String[]{"Клиенты", "Почтальоны", "Газеты"});
        topPanel.add(new JLabel("Выбор таблицы"));
        topPanel.add(tableSelector);
        add(topPanel, BorderLayout.NORTH);

        // Обработчик для JComboBox (выбор таблицы)
        tableSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchTable();
                JOptionPane.showMessageDialog(lab5.this, "Вы выбрали таблицу: " + tableSelector.getSelectedItem(), "Информация", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Модели таблиц
        clientTableModel = new DefaultTableModel(new Object[]{"ФИО клиента", "Количество газет", "Номенклатура газет"}, 0);
        postmanTableModel = new DefaultTableModel(new Object[]{"ФИО почтальона", "Количество маршрутов", "Обслуживаемые районы"}, 0);
        newspaperTableModel = new DefaultTableModel(new Object[]{"Название газеты", "Тираж", "Категория"}, 0);

        table = new JTable(clientTableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER);

        // Нижняя панель с поиском
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Поиск:");
        bottomPanel.add(searchLabel);
        searchField = new JTextField(20);
        bottomPanel.add(searchField);
        JButton searchButton = new JButton("Найти");
        bottomPanel.add(searchButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Обработчик для кнопки поиска
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String searchText = searchField.getText().trim();
                    performSearch(searchText);
                } catch (EmptySearchException ex) {
                    JOptionPane.showMessageDialog(lab5.this, ex.getMessage(), "Ошибка", JOptionPane.WARNING_MESSAGE);
                } catch (NoResultsFoundException ex) {
                    JOptionPane.showMessageDialog(lab5.this, ex.getMessage(), "Информация", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Полоса прокрутки
        scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 100);
        bottomPanel.add(scrollBar);

        // Обработчик для полосы прокрутки (JScrollBar)
        scrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                int value = scrollBar.getValue();
                JOptionPane.showMessageDialog(lab5.this, "Значение полосы прокрутки: " + value, "Информация", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Заполнение данных в таблицах
        loadClientData();
        loadPostmanData();  // Добавлено для загрузки данных почтальонов
        loadNewspaperData(); // Добавлено для загрузки данных газет

        setVisible(true);
    }

    private void performSearch(String searchText) throws EmptySearchException, NoResultsFoundException {
        if (searchText.isEmpty()) {
            throw new EmptySearchException("Вы ничего не ввели.");
        }
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 0, 1, 2));
        if (table.getRowCount() == 0) {
            throw new NoResultsFoundException("В списке нет такого: " + searchText);
        }
    }

    private void addClientData(String clientName, int newspaperCount, String newspaperList) {
        clientTableModel.addRow(new Object[]{clientName, newspaperCount, newspaperList});
    }

    private void addPostmanData(String postmanName, int routes, String areas) {
        postmanTableModel.addRow(new Object[]{postmanName, routes, areas});
    }

    private void addNewspaperData(String newspaperName, int circulation, String category) {
        newspaperTableModel.addRow(new Object[]{newspaperName, circulation, category});
    }

    private void switchTable() {
        String selectedTable = (String) tableSelector.getSelectedItem();
        if (selectedTable.equals("Клиенты")) {
            table.setModel(clientTableModel);
        } else if (selectedTable.equals("Почтальоны")) {
            table.setModel(postmanTableModel);
        } else if (selectedTable.equals("Газеты")) {
            table.setModel(newspaperTableModel);
        }
    }

    private void loadClientData() {
        addClientData("Шутенко Ян Эдуардович", 2, "Ведомости, Коммерсант");
        addClientData("Романенко Глеб Викторович", 1, "Известия");
        addClientData("Лобанов Семен Семенович", 3, "Аргументы и факты, Ведомости");
        addClientData("Быков Андрей Евгеньевич", 3, "Правда");
        addClientData("Мошнов Вячеслав Валерьевич", 3, "Экспресс-Газета");
    }

    private void loadPostmanData() {
        addPostmanData("Иванов Сергей Петрович", 5, "Центральный, Северный");
        addPostmanData("Кузнецов Андрей Викторович", 3, "Западный");
        addPostmanData("Петров Алексей Николаевич", 4, "Восточный, Южный");
    }

    private void loadNewspaperData() {
        addNewspaperData("Ведомости", 50000, "Экономика");
        addNewspaperData("Коммерсант", 30000, "Бизнес");
        addNewspaperData("Правда", 20000, "Политика");
        addNewspaperData("Известия", 40000, "Общество");
        addNewspaperData("Аргументы и факты", 45000, "Общая информация");
    }

    private void saveAllTablesToFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                // Сохранение клиентов
                writer.write("Клиенты\n");
                for (int i = 0; i < clientTableModel.getRowCount(); i++) {
                    for (int j = 0; j < clientTableModel.getColumnCount(); j++) {
                        writer.write(clientTableModel.getValueAt(i, j).toString() + "\t");
                    }
                    writer.newLine();
                }
                writer.newLine(); // Пустая строка для разделения таблиц

                // Сохранение почтальонов
                writer.write("Почтальоны\n");
                for (int i = 0; i < postmanTableModel.getRowCount(); i++) {
                    for (int j = 0; j < postmanTableModel.getColumnCount(); j++) {
                        writer.write(postmanTableModel.getValueAt(i, j).toString() + "\t");
                    }
                    writer.newLine();
                }
                writer.newLine(); // Пустая строка для разделения таблиц

                // Сохранение газет
                writer.write("Газеты\n");
                for (int i = 0; i < newspaperTableModel.getRowCount(); i++) {
                    for (int j = 0; j < newspaperTableModel.getColumnCount(); j++) {
                        writer.write(newspaperTableModel.getValueAt(i, j).toString() + "\t");
                    }
                    writer.newLine();
                }

                JOptionPane.showMessageDialog(this, "Все таблицы успешно сохранены!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при сохранении файла: " + ex.getMessage());
            }
        }
    }

    // Метод для загрузки всех таблиц из файла
    private void loadAllTablesFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                String line;
                String currentTable = null;

                // Сначала очищаем все таблицы
                clientTableModel.setRowCount(0); // Очистка таблицы клиентов
                postmanTableModel.setRowCount(0); // Очистка таблицы почтальонов
                newspaperTableModel.setRowCount(0); // Очистка таблицы газет

                while ((line = reader.readLine()) != null) {
                    if (line.equals("Клиенты") || line.equals("Почтальоны") || line.equals("Газеты")) {
                        currentTable = line;
                        switchTableTo(currentTable);
                    } else if (!line.isEmpty() && currentTable != null) {
                        String[] rowData = line.split("\t");
                        if (currentTable.equals("Клиенты")) {
                            clientTableModel.addRow(rowData);
                        } else if (currentTable.equals("Почтальоны")) {
                            postmanTableModel.addRow(rowData);
                        } else if (currentTable.equals("Газеты")) {
                            newspaperTableModel.addRow(rowData);
                        }
                    }
                }
                JOptionPane.showMessageDialog(this, "Все таблицы успешно загружены!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при загрузке файла: " + ex.getMessage());
            }
        }
    }

    // Метод для переключения на таблицу по имени
    private void switchTableTo(String tableName) {
        tableSelector.setSelectedItem(tableName);
        switchTable();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new lab5());
    }

    // Исключение для пустого поиска
    class EmptySearchException extends Exception {
        public EmptySearchException(String message) {
            super(message);
        }
    }

    // Исключение для отсутствия результатов поиска
    class NoResultsFoundException extends Exception {
        public NoResultsFoundException(String message) {
            super(message);
        }
    }
}
