package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
public class SqlConnector {
    private static SqlConnector instance = null;
    private Connection connection;
    private Statement statement;

    SqlConnector() throws SQLException {
        try {
            SetUp setup = new SetUp();
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + setup.databaseName,
                    setup.name, setup.password);
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            System.out.print(e);
        }
    }

    public static synchronized SqlConnector getInstance() throws SQLException {
        if (instance == null)
            instance = new SqlConnector();
  
        return instance;
    }
    
    public void addToDb(float price, String category, String description, LocalDateTime datetime) throws SQLException {
        String query = "INSERT INTO AllSpendings (category, description, price, datetime) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, category);
            statement.setString(2, description);
            statement.setFloat(3, price);
            if (datetime.getDayOfMonth() != LocalDateTime.now().getDayOfMonth()) 
            	datetime = datetime.withHour(23).withMinute(59);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = datetime.format(formatter);
            statement.setString(4, formattedDateTime);
            statement.executeUpdate();

        }
    }
    public void close() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Failed to close database resources: " + e.getMessage());
        }
    }
    public ResultSet someDayData(LocalDateTime datetime) throws SQLException {
    	int day, year, month;
        String query;
        PreparedStatement preparedStatement;
        HashMap<Month, Integer> hashMap;
        hashMap = createJavaMonthsToSqlValuesMap();
        day = datetime.getDayOfMonth();
        year = datetime.getYear();
        month = hashMap.get(datetime.getMonth());
        query = "SELECT category, description, price, datetime FROM AllSpendings "
                + "WHERE DAY(datetime) = ? AND MONTH(datetime) = ? AND YEAR(datetime) = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, day);
        preparedStatement.setInt(2, month);
        preparedStatement.setInt(3, year);


        return preparedStatement.executeQuery();
    }
    public ResultSet getAllColumnsFromMonth(LocalDateTime datetime) throws SQLException {
        String query = "SELECT category, price, datetime FROM AllSpendings "
                + "WHERE MONTH(datetime) = ? AND YEAR(datetime) = ?";
        HashMap<Month, Integer> map =  createJavaMonthsToSqlValuesMap();
        int year, month;
        year = datetime.getYear();
        month = map.get(datetime.getMonth());
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, month);
        statement.setInt(2, year);
        return statement.executeQuery(); 
    }
    public static HashMap<Month, Integer> createJavaMonthsToSqlValuesMap() {
        HashMap<Month, Integer> map = new HashMap<>();
        map.put(Month.JANUARY, 1);
        map.put(Month.FEBRUARY, 2);
        map.put(Month.MARCH, 3);
        map.put(Month.APRIL, 4);
        map.put(Month.MAY, 5);
        map.put(Month.JUNE, 6);
        map.put(Month.JULY, 7);
        map.put(Month.AUGUST, 8);
        map.put(Month.SEPTEMBER, 9);
        map.put(Month.OCTOBER, 10);
        map.put(Month.NOVEMBER, 11);
        map.put(Month.DECEMBER, 12);
        return map;
    }
}