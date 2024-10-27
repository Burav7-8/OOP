package lab4;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Побережный Егор 3312
 * @version 1.1
 */
public class lab4 extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> tableSelector;
    private JScrollBar scrollBar;

    public lab4() {
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
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Обработчик для меню "Сохранить"
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();
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
                JOptionPane.showMessageDialog(lab4.this, "Вы выбрали таблицу: " + tableSelector.getSelectedItem(), "Информация", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Модель таблицы
        tableModel = new DefaultTableModel(new Object[]{"ФИО клиента", "Количество газет", "Номенклатура газет"}, 0);
        table = new JTable(tableModel);
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
                    JOptionPane.showMessageDialog(lab4.this, ex.getMessage(), "Ошибка", JOptionPane.WARNING_MESSAGE);
                } catch (NoResultsFoundException ex) {
                    JOptionPane.showMessageDialog(lab4.this, ex.getMessage(), "Информация", JOptionPane.INFORMATION_MESSAGE);
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
                JOptionPane.showMessageDialog(lab4.this, "Значение полосы прокрутки: " + value, "Информация", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Пример добавления данных
        loadClientData();
        setVisible(true);
    }

    private void performSearch(String searchText) throws EmptySearchException, NoResultsFoundException {
        if (searchText.isEmpty()) {
            throw new EmptySearchException("Вы ничего не ввели.");
        }

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
        // Фильтруем по всем столбцам (0, 1, 2)
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 0, 1, 2));

        // Проверяем, есть ли результаты
        if (table.getRowCount() == 0) {
            throw new NoResultsFoundException("В списке нет такого: " + searchText);
        }
    }

    private void addClientData(String clientName, int newspaperCount, String newspaperList) {
        tableModel.addRow(new Object[]{clientName, newspaperCount, newspaperList});
    }

    private void addPostmanData(String postmanName, int routes, String areas) {
        tableModel.addRow(new Object[]{postmanName, routes, areas});
    }

    private void addNewspaperData(String newspaperName, int circulation, String category) {
        tableModel.addRow(new Object[]{newspaperName, circulation, category});
    }

    private void filterTable(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Если строка поиска пустая, показываем все строки
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                // Фильтруем по всем столбцам (0, 1, 2)
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 0, 1, 2));
            } catch (Exception e) {
                System.err.println("Ошибка фильтрации: " + e.getMessage());
                sorter.setRowFilter(null); // если ошибка, покажем все
            }
        }
    }

    private void switchTable() {
        String selectedTable = (String) tableSelector.getSelectedItem();
        tableModel.setRowCount(0);  // Очистка таблицы
        if (selectedTable.equals("Клиенты")) {
            tableModel.setColumnIdentifiers(new Object[]{"ФИО клиента", "Количество газет", "Номенклатура газет"});
            loadClientData();
        } else if (selectedTable.equals("Почтальоны")) {
            tableModel.setColumnIdentifiers(new Object[]{"ФИО почтальона", "Количество маршрутов", "Обслуживаемые районы"});
            loadPostmanData();
        } else if (selectedTable.equals("Газеты")) {
            tableModel.setColumnIdentifiers(new Object[]{"Название газеты", "Тираж", "Категория"});
            loadNewspaperData();
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

    // Метод для сохранения данных в файл
    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        writer.write(tableModel.getValueAt(i, j).toString() + "\t");
                    }
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(this, "Файл успешно сохранен!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при сохранении файла: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new lab4());
    }

    // Пользовательские исключения
    public static class EmptySearchException extends Exception {
        public EmptySearchException(String message) {
            super(message);
        }
    }

    public static class NoResultsFoundException extends Exception {
        public NoResultsFoundException(String message) {
            super(message);
        }
    }
}
