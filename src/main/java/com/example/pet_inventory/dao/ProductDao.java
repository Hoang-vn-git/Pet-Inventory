package com.example.pet_inventory.dao;

import com.example.pet_inventory.models.Product;
import com.example.pet_inventory.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    public ResultSet displayProducts() throws SQLException {
        String query = "SELECT * FROM PRODUCT";

        Connection myCon = DBUtil.getConnection();
        PreparedStatement myStmt = myCon.prepareStatement(query);

        return myStmt.executeQuery();
    }

    public ResultSet searchProducts(String productName, String productCategory, String productUPC, Integer productQuantity) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * FROM PRODUCT WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (productCategory != null && !productCategory.isBlank() && !productCategory.equalsIgnoreCase("None")) {
            query.append(" AND productCategory = ?");
            params.add(productCategory);
        }
        if (productName != null && !productName.isBlank()) {
            String[] keywords = productName.split("\\s+");
            for (String kw : keywords) {
                query.append(" AND productName LIKE ?");
                params.add("%" + kw + "%");
            }
        }
        if (productUPC != null && !productUPC.isBlank()) {
            query.append(" AND productUPC LIKE ?");
            params.add("%" + productUPC + "%");
        }
        if (productQuantity != null &&  productQuantity >=0 ) {
            query.append(" AND productQuantity = ?");
            params.add(productQuantity);
        }
        Connection myCon = DBUtil.getConnection();
        PreparedStatement myStmt = myCon.prepareStatement(query.toString());


        for (int i = 0; i < params.size(); i++) {
            myStmt.setObject(i + 1, params.get(i));
        }

        return myStmt.executeQuery();

    }

    public void insertProduct(Product product) throws SQLException {
        String query = "INSERT INTO PRODUCT (productUPC, productName, productCategory, productQuantity, productPrice) VALUES (?,?,?,?,?)";

        Connection myCon = DBUtil.getConnection();
        PreparedStatement myStmt = myCon.prepareStatement(query);

        myStmt.setString(1, product.getProductUPC());
        myStmt.setString(2, product.getProductName());
        myStmt.setString(3, product.getProductCategory().getDbValue());
        myStmt.setInt(4, product.getQuantity());
        myStmt.setBigDecimal(5, product.getPrice());

        myStmt.executeUpdate();
    }

    public ResultSet searchProductByUPC(String productUPC) throws SQLException {
        String query = "SELECT * FROM PRODUCT WHERE productUPC = ?";

        Connection myCon = DBUtil.getConnection();
        PreparedStatement myStmt = myCon.prepareStatement(query);

        myStmt.setString(1, productUPC);

        return myStmt.executeQuery();
    }
}
