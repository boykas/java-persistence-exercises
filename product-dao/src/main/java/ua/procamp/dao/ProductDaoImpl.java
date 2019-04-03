package ua.procamp.dao;

import ua.procamp.exception.DaoOperationException;
import ua.procamp.model.Product;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ProductDaoImpl implements ProductDao {
    private static final Logger LOG = Logger.getLogger(ProductDaoImpl.class.getSimpleName());

    private DataSource dataSource;
    private final static String SAVE_PRODUCT_QUERY = "INSERT INTO products (name, producer, price, expiration_date, creation_time) VALUES (?,?,?,?,now())";
    private final static String SELECT_ALL_QUERY = "SELECT * FROM products";
    private final static String SELECT_PRODUCT_QUERY = "SELECT * FROM products WHERE id = ?";
    private final static String UPDATE_PRODUCT_QUERY = "UPDATE products SET name = ?, producer = ?, price = ?, expiration_date = ? WHERE id = ?";
    private final static String DELETE_PRODUCT_QUERY = "DELETE FROM products WHERE id = ?";


    public ProductDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Product product) {
        if (product == null || product.getName() == null || product.getProducer() == null || product.getExpirationDate() == null) {
            throw new DaoOperationException("Error saving product: " + product.toString());
        }
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SAVE_PRODUCT_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, product.getName());
            statement.setString(2, product.getProducer());
            statement.setBigDecimal(3, product.getPrice());
            statement.setDate(4, java.sql.Date.valueOf(product.getExpirationDate()));
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                product.setId((long) rs.getInt("id"));
            }
        } catch (SQLException e) {
            throw new DaoOperationException("something went wrong");
        }
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL_QUERY);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product(resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("producer"),
                        resultSet.getBigDecimal("price"),
                        resultSet.getDate("expiration_date").toLocalDate(),
                        resultSet.getTimestamp("creation_time").toLocalDateTime());
                products.add(product);
            }
        } catch (SQLException e) {
            throw new DaoOperationException("something went wrong");
        }
        return products;
    }

    @Override
    public Product findOne(Long id) {
        Product product = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PRODUCT_QUERY);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                throw new DaoOperationException("Product with id = " + id + " does not exist");
            }
            product = new Product(resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("producer"),
                    resultSet.getBigDecimal("price"),
                    resultSet.getDate("expiration_date").toLocalDate(),
                    resultSet.getTimestamp("creation_time").toLocalDateTime());
        } catch (SQLException e) {
            throw new DaoOperationException("something went wrong");
        }
        return product;
    }

    @Override
    public void update(Product product) {
        if (product.getId() == null) {
            throw new DaoOperationException("Product id cannot be null");
        }
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PRODUCT_QUERY);
            statement.setString(1, product.getName());
            statement.setString(2, product.getProducer());
            statement.setBigDecimal(3, product.getPrice());
            statement.setDate(4, Date.valueOf(product.getExpirationDate()));
            statement.setLong(5, product.getId());
            if (statement.executeUpdate() != 1) {
                throw new DaoOperationException("Product with id = " + product.getId() + " does not exist");
            }
        } catch (SQLException e) {
            throw new DaoOperationException("something went wrong");
        }

    }

    @Override
    public void remove(Product product) {
        if (product.getId() == null) {
            throw new DaoOperationException("Product id cannot be null");
        }
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_PRODUCT_QUERY);
            statement.setLong(1, product.getId());

            if (statement.executeUpdate() != 1) {
                throw new DaoOperationException("Product with id = " + product.getId() + " does not exist");
            }
        } catch (SQLException e) {
            throw new DaoOperationException("something went wrong");
        }
    }
}
