package lab7;

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

import java.io.File;
import java.util.HashMap;

public class ReportGenerator {

    public static void generatePDFReport(String xmlFilePath, String jrxmlPath, String outputPath) {
        try {
            JRXmlDataSource dataSource = new JRXmlDataSource(xmlFilePath, "/Data/Клиенты/Клиент");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlPath);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            System.out.println("PDF report generated at: " + outputPath);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public static void generateHTMLReport(String xmlFilePath, String jrxmlPath, String outputPath) {
        try {
            JRXmlDataSource dataSource = new JRXmlDataSource(xmlFilePath, "/Data/Почтальоны/Почтальон");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlPath);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);
            JasperExportManager.exportReportToHtmlFile(jasperPrint, outputPath);
            System.out.println("HTML report generated at: " + outputPath);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}


public class lab7 extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> tableSelector;
    private JScrollBar scrollBar;
    private DefaultTableModel clientTableModel;
    private DefaultTableModel postmanTableModel;
    private DefaultTableModel newspaperTableModel;

    public lab7() {
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

        // Обработчики меню "Сохранить" и "Загрузить"
        saveItem.addActionListener(e -> saveAllTablesToFile());
        loadItem.addActionListener(e -> loadAllTablesFromFile());

        // Верхняя панель для выбора таблицы
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableSelector = new JComboBox<>(new String[]{"Клиенты", "Почтальоны", "Газеты"});
        topPanel.add(new JLabel("Выбор таблицы"));
        topPanel.add(tableSelector);
        add(topPanel, BorderLayout.NORTH);

        // Обработчик для JComboBox (выбор таблицы)
        tableSelector.addActionListener(e -> {
            switchTable();
            JOptionPane.showMessageDialog(this, "Вы выбрали таблицу: " + tableSelector.getSelectedItem(), "Информация", JOptionPane.INFORMATION_MESSAGE);
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
        bottomPanel.add(new JLabel("Поиск:"));
        searchField = new JTextField(20);
        bottomPanel.add(searchField);
        JButton searchButton = new JButton("Найти");
        bottomPanel.add(searchButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Обработчик для кнопки поиска
        searchButton.addActionListener(e -> {
            try {
                String searchText = searchField.getText().trim();
                performSearch(searchText);
            } catch (EmptySearchException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.WARNING_MESSAGE);
            } catch (NoResultsFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Информация", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Полоса прокрутки
        scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 100);
        bottomPanel.add(scrollBar);
        scrollBar.addAdjustmentListener(e -> JOptionPane.showMessageDialog(this, "Значение полосы прокрутки: " + scrollBar.getValue(), "Информация", JOptionPane.INFORMATION_MESSAGE));

        // Заполнение данных в таблицах
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

    // Сохранение данных в XML
    private void saveAllTablesToFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.newDocument();

                Element rootElement = doc.createElement("Data");
                doc.appendChild(rootElement);

                // Клиенты
                Element clients = doc.createElement("Клиенты");
                rootElement.appendChild(clients);
                for (int i = 0; i < clientTableModel.getRowCount(); i++) {
                    Element client = doc.createElement("Клиент");
                    clients.appendChild(client);
                    client.appendChild(createElement(doc, "ФИО", clientTableModel.getValueAt(i, 0).toString()));
                    client.appendChild(createElement(doc, "КоличествоГазет", clientTableModel.getValueAt(i, 1).toString()));
                    client.appendChild(createElement(doc, "НоменклатураГазет", clientTableModel.getValueAt(i, 2).toString()));
                }

                // Почтальоны
                Element postmen = doc.createElement("Почтальоны");
                rootElement.appendChild(postmen);
                for (int i = 0; i < postmanTableModel.getRowCount(); i++) {
                    Element postman = doc.createElement("Почтальон");
                    postmen.appendChild(postman);
                    postman.appendChild(createElement(doc, "ФИО", postmanTableModel.getValueAt(i, 0).toString()));
                    postman.appendChild(createElement(doc, "КоличествоМаршрутов", postmanTableModel.getValueAt(i, 1).toString()));
                    postman.appendChild(createElement(doc, "ОбслуживаемыеРайоны", postmanTableModel.getValueAt(i, 2).toString()));
                }

                // Газеты
                Element newspapers = doc.createElement("Газеты");
                rootElement.appendChild(newspapers);
                for (int i = 0; i < newspaperTableModel.getRowCount(); i++) {
                    Element newspaper = doc.createElement("Газета");
                    newspapers.appendChild(newspaper);
                    newspaper.appendChild(createElement(doc, "Название", newspaperTableModel.getValueAt(i, 0).toString()));
                    newspaper.appendChild(createElement(doc, "Тираж", newspaperTableModel.getValueAt(i, 1).toString()));
                    newspaper.appendChild(createElement(doc, "Категория", newspaperTableModel.getValueAt(i, 2).toString()));
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(fileChooser.getSelectedFile());
                transformer.transform(source, result);

                JOptionPane.showMessageDialog(this, "Данные успешно сохранены в XML-файл.");
            } catch (ParserConfigurationException | TransformerException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при сохранении в XML: " + ex.getMessage());
            }
        }
    }

    // Загрузка данных из XML
    private void loadAllTablesFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                File xmlFile = fileChooser.getSelectedFile();
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                clientTableModel.setRowCount(0);
                postmanTableModel.setRowCount(0);
                newspaperTableModel.setRowCount(0);

                NodeList clients = doc.getElementsByTagName("Клиент");
                for (int i = 0; i < clients.getLength(); i++) {
                    Element client = (Element) clients.item(i);
                    clientTableModel.addRow(new Object[]{
                            client.getElementsByTagName("ФИО").item(0).getTextContent(),
                            Integer.parseInt(client.getElementsByTagName("КоличествоГазет").item(0).getTextContent()),
                            client.getElementsByTagName("НоменклатураГазет").item(0).getTextContent()
                    });
                }

                NodeList postmen = doc.getElementsByTagName("Почтальон");
                for (int i = 0; i < postmen.getLength(); i++) {
                    Element postman = (Element) postmen.item(i);
                    postmanTableModel.addRow(new Object[]{
                            postman.getElementsByTagName("ФИО").item(0).getTextContent(),
                            Integer.parseInt(postman.getElementsByTagName("КоличествоМаршрутов").item(0).getTextContent()),
                            postman.getElementsByTagName("ОбслуживаемыеРайоны").item(0).getTextContent()
                    });
                }

                NodeList newspapers = doc.getElementsByTagName("Газета");
                for (int i = 0; i < newspapers.getLength(); i++) {
                    Element newspaper = (Element) newspapers.item(i);
                    newspaperTableModel.addRow(new Object[]{
                            newspaper.getElementsByTagName("Название").item(0).getTextContent(),
                            Integer.parseInt(newspaper.getElementsByTagName("Тираж").item(0).getTextContent()),
                            newspaper.getElementsByTagName("Категория").item(0).getTextContent()
                    });
                }

                JOptionPane.showMessageDialog(this, "Данные успешно загружены из XML-файла.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при загрузке из XML: " + ex.getMessage());
            }
        }
    }

    private Element createElement(Document doc, String name, String value) {
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(value));
        return element;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(lab7::new);
    }
}

class EmptySearchException extends Exception {
    public EmptySearchException(String message) {
        super(message);
    }
}

class NoResultsFoundException extends Exception {
    public NoResultsFoundException(String message) {
        super(message);
    }
}
