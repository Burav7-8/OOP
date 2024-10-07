package lab2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Побережный Егор 3312
 * @version 1.0
 */

public class lab2 extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> tableSelector;

    public lab2() {
        setTitle("Почтовая информационная система");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Верхнее меню
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Меню");
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Верхняя панель для выбора таблицы
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        tableSelector = new JComboBox<>(new String[]{"Клиенты", "Почтальоны", "Газеты"});
        topPanel.add(new JLabel("Выбор таблицы"));
        topPanel.add(tableSelector);

        add(topPanel, BorderLayout.NORTH);

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
                String searchText = searchField.getText().toLowerCase();
                filterTable(searchText);
            }
        });

        // Пример добавления данных
        addClientData("Шутенко Ян Эдуардович", 2, "Ведомости, Коммерсант");
        addClientData("Романенко Глеб Викторович", 1, "Известия");
        addClientData("Лобанов Семен Семенович", 3, "Аргументы и факты, Ведомости");
        addClientData("Быков Андрей Евгеньевич", 3, "Правда");
        addClientData("Мошнов Вячеслав Валерьевич", 3, "Экспресс-Газета");

        setVisible(true);
    }

    private void addClientData(String clientName, int newspaperCount, String newspaperList) {
        tableModel.addRow(new Object[]{clientName, newspaperCount, newspaperList});
    }

    private void filterTable(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new lab2());
    }
}
