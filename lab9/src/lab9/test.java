import org.junit.jupiter.api.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class test {
    private lab9 app;

    @BeforeEach
    void setUp() {
        app = new lab9(); // Инициализация приложения
    }

    @Test
    void testAddClientData() {
        app.addClientData("Тестовый Клиент", 2, "Газета1, Газета2");
        DefaultTableModel model = (DefaultTableModel) app.table.getModel();
        assertEquals(1, model.getRowCount());
        assertEquals("Тестовый Клиент", model.getValueAt(0, 0));
        assertEquals(2, model.getValueAt(0, 1));
        assertEquals("Газета1, Газета2", model.getValueAt(0, 2));
    }

    @Test
    void testAddPostmanData() {
        app.switchTable();
        app.addPostmanData("Почтальон Иван", 3, "Район1, Район2");
        DefaultTableModel model = (DefaultTableModel) app.table.getModel();
        assertEquals(1, model.getRowCount());
        assertEquals("Почтальон Иван", model.getValueAt(0, 0));
        assertEquals(3, model.getValueAt(0, 1));
        assertEquals("Район1, Район2", model.getValueAt(0, 2));
    }

    @Test
    void testFilterTable() {
        app.addClientData("Клиент1", 1, "Газета1");
        app.addClientData("Клиент2", 2, "Газета2");
        app.filterTable("Клиент1");
        assertEquals(1, app.table.getRowCount());
        assertEquals("Клиент1", app.table.getValueAt(0, 0));
    }

    @Test
    void testSwitchTable() {
        app.tableSelector.setSelectedItem("Почтальоны");
        app.switchTable();
        DefaultTableModel model = (DefaultTableModel) app.table.getModel();
        assertEquals(3, model.getColumnCount());
        assertEquals("ФИО почтальона", model.getColumnName(0));
        assertEquals("Количество маршрутов", model.getColumnName(1));
        assertEquals("Обслуживаемые районы", model.getColumnName(2));
    }

    @Test
    void testSaveToFile() throws IOException {
        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();

        app.addClientData("Клиент1", 1, "Газета1");
        app.addClientData("Клиент2", 2, "Газета2");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            app.saveToFile();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            assertEquals("Клиент1\t1\tГазета1\t", reader.readLine());
            assertEquals("Клиент2\t2\tГазета2\t", reader.readLine());
        }
    }
}
