import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class AmazonScraperApp extends JFrame {

    private JTextField urlField;
    private JTable table;
    private DefaultTableModel model;

    public AmazonScraperApp() {
        setTitle("Amazon Product Scraper");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        urlField = new JTextField(50);
        JButton scrapeButton = new JButton("Scrape Product");
        JButton exportButton = new JButton("Export to CSV");

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Amazon Product URL:"));
        topPanel.add(urlField);
        topPanel.add(scrapeButton);
        topPanel.add(exportButton);

        String[] columns = {"Product Name", "Price", "Rating"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Scrape Button Click
        scrapeButton.addActionListener(e -> {
            String url = urlField.getText();
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .timeout(10 * 1000)
                        .get();

                String name = doc.select("#productTitle").text();
                String price = doc.select(".a-price-whole").first() != null ? doc.select(".a-price-whole").first().text() : "Not Found";
                String rating = doc.select(".a-icon-alt").first() != null ? doc.select(".a-icon-alt").first().text() : "Not Found";

                model.addRow(new Object[]{name, price, rating});
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error fetching data:\n" + ex.getMessage());
            }
        });

        // Export Button Click
        exportButton.addActionListener(e -> {
            try (FileWriter writer = new FileWriter("amazon_products.csv")) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    writer.append(model.getValueAt(i, 0) + "," +
                            model.getValueAt(i, 1) + "," +
                            model.getValueAt(i, 2) + "\n");
                }
                JOptionPane.showMessageDialog(this, "Data exported to amazon_products.csv");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error writing CSV:\n" + ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AmazonScraperApp().setVisible(true));
    }
}
