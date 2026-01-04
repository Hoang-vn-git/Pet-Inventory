package com.example.pet_inventory.dao;

import com.example.pet_inventory.models.Category;
import com.example.pet_inventory.models.Product;
import com.example.pet_inventory.utils.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product table.
 * Provides CRUD operations and JSON export for products.
 */
public class ProductDao {

    /** Display all products */
    public ResultSet displayProducts() throws SQLException {
        String query = "SELECT * FROM product";
        Connection con = DBUtil.getConnection();
        PreparedStatement stmt = con.prepareStatement(query);
        return stmt.executeQuery();
    }

    /** Search products with multiple optional filters */
    public ResultSet searchProducts(String productName, String productCategory, String productUPC, Integer productQuantity) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * FROM product WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (productCategory != null && !productCategory.isBlank() && !productCategory.equalsIgnoreCase("None")) {
            query.append(" AND productCategory = ?");
            params.add(productCategory);
        }
        if (productName != null && !productName.isBlank()) {
            for (String kw : productName.split("\\s+")) {
                query.append(" AND productName LIKE ?");
                params.add("%" + kw + "%");
            }
        }
        if (productUPC != null && !productUPC.isBlank()) {
            query.append(" AND productUPC LIKE ?");
            params.add("%" + productUPC + "%");
        }
        if (productQuantity != null && productQuantity >= 0) {
            query.append(" AND productQuantity = ?");
            params.add(productQuantity);
        }

        Connection con = DBUtil.getConnection();
        PreparedStatement stmt = con.prepareStatement(query.toString());
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }
        return stmt.executeQuery();
    }

    /** Search product by UPC */
    public ResultSet searchProductByUPC(String productUPC) throws SQLException {
        String query = "SELECT * FROM product WHERE productUPC = ?";
        Connection con = DBUtil.getConnection();
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, productUPC);
        return stmt.executeQuery();
    }

    /** Insert a new product */
    public void insertProduct(Product product) throws SQLException {
        String query = "INSERT INTO product (productUPC, productName, productCategory, productQuantity, productPrice) VALUES (?,?,?,?,?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, product.getProductUPC());
            stmt.setString(2, product.getProductName());
            stmt.setString(3, product.getProductCategory().getDbValue());
            stmt.setInt(4, product.getQuantity());
            stmt.setBigDecimal(5, product.getPrice());

            stmt.executeUpdate();
        }
    }

    /** Update product quantity after sale */
    public void soldProductByUPC(String productUPC, int productQuantity) throws SQLException {
        String query = "UPDATE product SET productQuantity = productQuantity - ? WHERE productUPC = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, productQuantity);
            stmt.setString(2, productUPC);
            stmt.executeUpdate();
        }
    }

    /** Update existing product */
    public void updateProduct(String productUPC, String productName, Category productCategory, int productQuantity, BigDecimal productPrice) throws SQLException {
        String query = """
                UPDATE product
                SET productName = ?, productCategory = ?, productQuantity = ?, productPrice = ?
                WHERE productUPC = ?
                """;
        try (Connection con = DBUtil.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, productName);
            stmt.setString(2, productCategory.getDbValue());
            stmt.setInt(3, productQuantity);
            stmt.setBigDecimal(4, productPrice);
            stmt.setString(5, productUPC);

            stmt.executeUpdate();
        }
    }

    /** Delete product */
    public void deleteProduct(String productUPC) throws SQLException {
        String query = "DELETE FROM product WHERE productUPC = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, productUPC);
            stmt.executeUpdate();
        }
    }

    /**
     * Insert or update a product (upsert).
     * Uses MySQL ON DUPLICATE KEY UPDATE.
     */
    public void saveAll(Product product) throws SQLException {
        String query = """
                INSERT INTO product (productUPC, productName, productCategory, productQuantity, productPrice)
                VALUES (?,?,?,?,?)
                ON DUPLICATE KEY UPDATE
                productName = VALUES(productName),
                productCategory = VALUES(productCategory),
                productQuantity = VALUES(productQuantity),
                productPrice = VALUES(productPrice);
                """;
        try (Connection con = DBUtil.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, product.getProductUPC());
            stmt.setString(2, product.getProductName());
            stmt.setString(3, product.getProductCategory().getDbValue());
            stmt.setInt(4, product.getQuantity());
            stmt.setBigDecimal(5, product.getPrice());

            stmt.executeUpdate();
        }
    }

    /** Export products as JSON (for AssistantController) */
    public ResultSet JSONproduct() throws SQLException {
        String query = """
                SELECT JSON_ARRAYAGG(
                           JSON_OBJECT(
                               'productUPC', productUPC,
                               'productName', productName,
                               'productPrice', productPrice,
                               'productCategory', productCategory,
                               'productQuantity', productQuantity
                           )
                       ) AS product_json
                FROM (
                         SELECT productUPC, productName, productCategory, productQuantity, productPrice
                         FROM product
                         ORDER BY productQuantity
                     ) p
                """;

        Connection con = DBUtil.getConnection();
        PreparedStatement stmt = con.prepareStatement(query);
        return stmt.executeQuery();
    }
}