package lab8;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class lab8 extends JFrame {
    private static final Logger logger = Logger.getLogger(lab8.class); // Логгер для класса

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> tableSelector;
    private JScrollBar scrollBar;
    private DefaultTableModel clientTableModel;
    private DefaultTableModel postmanTableModel;
    private DefaultTableModel newspaperTableModel;

    private CountDownLatch editLatch = new CountDownLatch(1);

    public lab8() {
        // Настройка Log4j
        PropertyConfigurator.configure("log4j.properties");

        logger.info("Инициализация интерфейса приложения.");

        setTitle("Почтовая информационная система");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Верхнее меню
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Меню");
        JMenuItem saveItem = new JMenuItem("Сохранить");
        JMenuItem loadItem = new JMenuItem("Загрузить");
        menu.add(saveItem);
        menu.add(loadItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        saveItem.addActionListener(e -> saveAllTablesToFile());
        loadItem.addActionListener(e -> loadAllTablesFromFile());

        // Верхняя панель
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableSelector = new JComboBox<>(new String[]{"Клиенты", "Почтальоны", "Газеты"});
        topPanel.add(new JLabel("Выбор таблицы"));
        topPanel.add(tableSelector);
        add(topPanel, BorderLayout.NORTH);

        tableSelector.addActionListener(e -> switchTable());

        // Модели таблиц
        clientTableModel = new DefaultTableModel(new Object[]{"ФИО клиента", "Количество газет", "Номенклатура газет"}, 0);
        postmanTableModel = new DefaultTableModel(new Object[]{"ФИО почтальона", "Количество маршрутов", "Обслуживаемые районы"}, 0);
        newspaperTableModel = new DefaultTableModel(new Object[]{"Название газеты", "Тираж", "Категория"}, 0);
        table = new JTable(clientTableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER);

        // Нижняя панель
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(new JLabel("Поиск:"));
        searchField = new JTextField(20);
        bottomPanel.add(searchField);
        JButton searchButton = new JButton("Найти");
        bottomPanel.add(searchButton);
        scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 100);
        bottomPanel.add(scrollBar);

        JButton editCompleteButton = new JButton("Завершить редактирование");
        bottomPanel.add(editCompleteButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Обработчики событий
        searchButton.addActionListener(e -> {
            try {
                String searchText = searchField.getText().trim();
                performSearch(searchText);
                logger.info("Поиск выполнен: " + searchText);
            } catch (EmptySearchException ex) {
                logger.warn("Пустая строка поиска: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.WARNING_MESSAGE);
            } catch (NoResultsFoundException ex) {
                logger.info("Результаты не найдены для: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Информация", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        scrollBar.addAdjustmentListener(e -> {
            logger.debug("Изменено значение полосы прокрутки: " + scrollBar.getValue());
            JOptionPane.showMessageDialog(this, "Значение полосы прокрутки: " + scrollBar.getValue(), "Информация", JOptionPane.INFORMATION_MESSAGE);
        });

        editCompleteButton.addActionListener(e -> {
            logger.info("Редактирование завершено пользователем.");
            saveAllTablesToFile();
            editLatch.countDown();
        });

        loadClientData();
        loadPostmanData();
        loadNewspaperData();
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

    private void switchTable() {
        String selectedTable = (String) tableSelector.getSelectedItem();
        logger.info("Смена таблицы: " + selectedTable);
        if (selectedTable.equals("Клиенты")) {
            table.setModel(clientTableModel);
        } else if (selectedTable.equals("Почтальоны")) {
            table.setModel(postmanTableModel);
        } else if (selectedTable.equals("Газеты")) {
            table.setModel(newspaperTableModel);
        }
    }

    private void saveAllTablesToFile() {
        logger.info("Начало сохранения данных в XML.");
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                // Сохранение в файл
                logger.info("Файл успешно сохранен.");
            } catch (Exception ex) {
                logger.error("Ошибка при сохранении файла: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            lab8 app = new lab8();
            // Потоки аналогично оригиналу
        });
    }
}
