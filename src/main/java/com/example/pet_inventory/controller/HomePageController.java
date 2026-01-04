package com.example.pet_inventory.controller;

import com.example.pet_inventory.dao.OrderDao;
import com.example.pet_inventory.dao.OrderItemDao;
import com.example.pet_inventory.models.Order;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for HomePage dashboard.
 * Handles displaying orders and exporting sales reports to Excel.
 */
public class HomePageController {

    // ==================== FXML UI Components ====================
    @FXML
    public DatePicker datePickerFrom;
    @FXML
    public DatePicker datePickerTo;
    @FXML
    public TableColumn<Order, String> orderID;
    @FXML
    public TableColumn<Order, BigDecimal> totalAmount;
    @FXML
    public TableColumn<Order, String> dateCreated;
    @FXML
    public TableColumn<Order, String> paymentMethod;
    @FXML
    public TableView<Order> reportTable;

    // ==================== JavaFX Lifecycle ====================
    public void initialize() {
        // Bind TableView columns to Order properties
        orderID.setCellValueFactory(c -> c.getValue().orderIDProperty());
        dateCreated.setCellValueFactory(c -> c.getValue().dateCreatedProperty());
        totalAmount.setCellValueFactory(c -> c.getValue().totalAmountProperty());
        paymentMethod.setCellValueFactory(c -> c.getValue().paymentMethodProperty());

        // Ensure TableView has observable list
        if (reportTable.getItems() == null) {
            reportTable.setItems(FXCollections.observableArrayList());
        }
    }

    // ==================== Export Report ====================
    /**
     * Export orders and order details to Excel file.
     * @param actionEvent trigger event
     */
    public void exportReport(ActionEvent actionEvent) throws SQLException, IOException {
        // Validate DatePicker inputs
        if (datePickerFrom.getValue() == null || datePickerTo.getValue() == null) {
            return; // Exit if dates are not selected
        }

        reportTable.getItems().clear();

        String dateFrom = datePickerFrom.getValue().toString();
        String dateTo = datePickerTo.getValue().plusDays(1).toString(); // include end day

        OrderDao orderDao = new OrderDao();
        OrderItemDao orderItemDao = new OrderItemDao();

        ResultSet rsOrders = orderDao.searchOrder(dateFrom, dateTo);
        ResultSet rsItems = orderItemDao.exportOrder();

        List<Order> orders = new ArrayList<>();

        // Populate TableView and list for Excel
        while (rsOrders.next()) {
            Order order = new Order(
                    rsOrders.getString("orderID"),
                    rsOrders.getString("dateCreated"),
                    rsOrders.getBigDecimal("totalAmount"),
                    rsOrders.getString("paymentMethod")
            );
            reportTable.getItems().add(order);
            orders.add(order);
        }

        // ==================== Excel Export ====================
        Workbook workbook = new XSSFWorkbook();
        Sheet sheetOrders = workbook.createSheet("Sales Report");
        Sheet sheetItems = workbook.createSheet("Order Details");

        // Create common styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle textStyle = createTextStyle(workbook);
        CellStyle priceStyle = createPriceStyle(workbook);

        // Header rows
        createHeaderRow(sheetOrders, new String[]{"Order ID", "Total", "Payment", "Date"}, headerStyle);
        createHeaderRow(sheetItems, new String[]{"Order ID", "Product Name", "Quantity", "Subtotal", "Date", "Payment"}, headerStyle);

        // Fill order report
        int rowNumOrder = 1;
        for (Order o : orders) {
            Row row = sheetOrders.createRow(rowNumOrder++);
            createCell(row, 0, o.getOrderID(), textStyle);
            createCell(row, 1, o.getTotalAmount().doubleValue(), priceStyle);
            createCell(row, 2, o.getPaymentMethod(), textStyle);
            createCell(row, 3, o.getDateCreated(), textStyle);
        }

        // Fill order details
        int rowNumDetails = 1;
        while (rsItems.next()) {
            Row row = sheetItems.createRow(rowNumDetails++);
            createCell(row, 0, rsItems.getString("orderID"), textStyle);
            createCell(row, 1, rsItems.getString("productName"), textStyle);
            createCell(row, 2, rsItems.getInt("quantity"), textStyle);
            createCell(row, 3, rsItems.getBigDecimal("subtotal").doubleValue(), priceStyle);
            createCell(row, 4, rsItems.getString("dateCreated"), textStyle);
            createCell(row, 5, rsItems.getString("paymentMethod"), textStyle);
        }

        // Auto-size columns
        autoSizeColumns(sheetOrders, 4);
        autoSizeColumns(sheetItems, 6);

        // Freeze headers
        sheetOrders.createFreezePane(0,1);
        sheetItems.createFreezePane(0,1);

        // Write Excel file
        try (OutputStream fileOut = new FileOutputStream("report.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();

        // Close DB connections
        rsOrders.getStatement().getConnection().close();
        rsItems.getStatement().getConnection().close();
    }

    // ==================== Helper Methods ====================
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short)14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTextStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createPriceStyle(Workbook workbook) {
        CellStyle style = createTextStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("$#,##0.00"));
        return style;
    }

    private void createHeaderRow(Sheet sheet, String[] headers, CellStyle style) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void createCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int colIndex, double value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int colIndex, int value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void autoSizeColumns(Sheet sheet, int numColumns) {
        for (int i = 0; i < numColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}