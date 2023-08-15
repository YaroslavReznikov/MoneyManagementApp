package application;  
import java.sql.SQLException;
import java.util.HashMap;
import java.sql.ResultSet;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;  
import javafx.scene.control.Label;  
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;  
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import java.time.LocalDate;
import java.time.format.TextStyle;

import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import java.time.LocalDateTime;


public class Main extends Application implements EventHandler<ActionEvent> {  
  
      
public static void main(String[] args) {  
	launch(args);     
}  
private static TextField priceField = new TextField();
private static TextField categoryField = new TextField();
private static TextField descriptionField = new TextField();
private static GridPane root;
private static SqlConnector sqlConection;
private static int daysBack = 0;
private static PieChart pieChart;
private static StackPane circularControl;

@Override
public void start(Stage primaryStage) {
    root = new GridPane();
    primaryStageInit(primaryStage, Main.class);
    buttonsInit(this);
    labelsAndTextInit();
    CircularInit();
    display();
}
public static void CircularInit() {
    // Initialize the circular control if it's null (happens only once)
    if (circularControl == null) {
        circularControl = new StackPane();
        circularControl.getStyleClass().add("circular-control");
        circularControl.setMinSize(100, 100);
        circularControl.setMaxSize(100, 100);
        circularControl.setPadding(new Insets(10));

        StackPane topHalf = new StackPane();
        topHalf.getStyleClass().add("top-half");
        StackPane bottomHalf = new StackPane();
        bottomHalf.getStyleClass().add("bottom-half");
        circularControl.getStyleClass().add("circularControl");
        circularControl.getChildren().addAll(topHalf, bottomHalf);
        root.addRow(4, circularControl);
    }

    // Update the content of the circular control based on the current date
    LocalDate currentDate = LocalDate.now().plusDays(daysBack);
    String dayOfWeek = String.valueOf(currentDate.getDayOfMonth());

    Locale.setDefault(new Locale("ru", "RU"));
    String shortMonthName = currentDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

    Text dayText = new Text(dayOfWeek);
    Text monthText = new Text(shortMonthName);
    StackPane.setAlignment(dayText, Pos.TOP_CENTER);
    StackPane.setAlignment(monthText, Pos.BOTTOM_CENTER);

    // Update the content of the topHalf and bottomHalf
    StackPane topHalf = (StackPane) circularControl.getChildren().get(0);
    StackPane bottomHalf = (StackPane) circularControl.getChildren().get(1);
    topHalf.getChildren().setAll(dayText);
    bottomHalf.getChildren().setAll(monthText);
}
public static void labelsAndTextInit() {
    Label goodPrice = new Label("Good Price");
    Label category = new Label("Category");
    Label description = new Label("Description");
    goodPrice.getStyleClass().add("upper");
    category.getStyleClass().add("upper");
    description.getStyleClass().add("upper");
    priceField.getStyleClass().add("textField");
    categoryField.getStyleClass().add("textField");
    descriptionField.getStyleClass().add("textField");
    goodPrice.setWrapText(true);

    root.addRow(0, goodPrice, priceField);
    root.addRow(1, category, categoryField);
    root.addRow(2, description, descriptionField);
}

public static void buttonsInit(Main mainInstance) {
    Button submitButton = new Button("Submit");
    Button plusButton = new Button("->");
    Button minusButton = new Button("<-");
    plusButton.getStyleClass().add("plusButton");
    minusButton.getStyleClass().add("minusButton");

    submitButton.setOnAction(e -> mainInstance.handle(e));
    plusButton.setOnAction(e -> {
        try {
            mainInstance.plusButtonHandler(e);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    });
    minusButton.setOnAction(e -> {
        try {
            mainInstance.minusButtonHandler(e);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    });

    root.add(submitButton, 1, 3);
    root.add(plusButton, 2, 3);
    root.add(minusButton, 3, 3);
}
public static void primaryStageInit(Stage primaryStage,  Class<?> mainClass) {
    int width = 1850;
    int height = 1000;
    Scene scene = new Scene(root, width, height);
    String cssPath = mainClass.getResource("application.css").toExternalForm();
    scene.getStylesheets().add(cssPath);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Text Field Example");
    primaryStage.setWidth(width);
    primaryStage.setHeight(height);
    primaryStage.setMaxHeight(height);
    primaryStage.setMaxWidth(width);
    primaryStage.setMinHeight(height);
    primaryStage.setMinWidth(width);
    primaryStage.centerOnScreen();
    primaryStage.show();
}

public static void display() {
    try {
        showEveryThingThatwasAddInPD();
    } catch (SQLException e1) {
        e1.printStackTrace();
    }
    try {
        generatePie();
    } catch (SQLException e1) {
        e1.printStackTrace();
    }
}
public void handle(ActionEvent event) {
	try {
		sqlConection = new SqlConnector();
	} catch (SQLException e) {
		e.printStackTrace();
	}
	try {
		removeAllPersonalInfos(root, 6);
		sqlConection.addToDb(Float.parseFloat(priceField.getText()), categoryField.getText(), descriptionField.getText(), LocalDateTime.now().plusDays(daysBack));
	} catch (NumberFormatException e) {
		e.printStackTrace();
	} catch (SQLException e) {
		e.printStackTrace();
	}
	try {
		showEveryThingThatwasAddInPD();
	} catch (SQLException e) {
		e.printStackTrace();
	}
}
public static void showEveryThingThatwasAddInPD() throws SQLException {
	removeAllPersonalInfos(root, 6);
	int currentLine = 5, rowIndex = 3, rowMax = 10;
	StringBuilder output;
	SqlConnector sqlConection = null;
	Label personalInfo =  new Label();
	generatePie();
	try {
		sqlConection = new SqlConnector();
	} catch (SQLException e) {
		e.printStackTrace();
	}
	ResultSet resultSet = sqlConection.someDayData( LocalDateTime.now().plusDays(daysBack));
	while (resultSet.next()) {
		currentLine ++;
		output = new StringBuilder();
		personalInfo = new Label(StringHelper.outPutFormatation(resultSet, output));
		personalInfo.getStyleClass().add("personalInfo");
		personalInfo.setWrapText(true);
		personalInfo.setPrefWidth(50); // Set the preferred width for wrapping
		if (currentLine == rowMax) {
			rowIndex += 1;
			currentLine = 6;
		}
	    root.add(personalInfo, rowIndex, currentLine);
	}
	while (currentLine < rowMax - 1 && rowIndex == 3) {
		currentLine++;
		personalInfo = new Label("");
		personalInfo.getStyleClass().add("emptyPersonalInfo");
		personalInfo.setWrapText(true);
		personalInfo.setPrefWidth(50); // Set the preferred width for wrapping
	    root.add(personalInfo, rowIndex, currentLine);

	}
   }
public static void removeAllPersonalInfos(GridPane gridPane, int startRow) {
    int numRows = gridPane.getRowCount();

    for (int i = numRows - 1; i >= startRow; i--) {
    	final int end = i;
    	gridPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) == end);

    }
}

public static HashMap<String, Double> CategorySpendingsPM(LocalDateTime datetime) throws SQLException{
	HashMap<String, Double> result = new HashMap<>();
	try {
		sqlConection = new SqlConnector();
	} catch (SQLException e) {
		e.printStackTrace();
	}
	ResultSet resultSet = sqlConection.getAllColumnsFromMonth(datetime);
	String category;
	Double price;
	while (resultSet.next()) {
		price = resultSet.getDouble("price");
		category = resultSet.getString("category");
		if (result.containsKey(category)) result.put(category, result.get(category) + price);
		else result.put(category, price);
	}
	
	return result;
}

public static void generatePie() throws SQLException {
    HashMap<String, Double> categorySpendings = CategorySpendingsPM(LocalDateTime.now().plusDays(daysBack));
    List<Data> dataList = new ArrayList<>();
    Double value;
    String key;
    if (pieChart != null) {
        root.getChildren().remove(pieChart);
        pieChart = null; // Set pieChart to null after removing it
    }
    for (Entry<String, Double> entry : categorySpendings.entrySet()) {
        key = entry.getKey();
        value = entry.getValue();
        dataList.add(new Data(key, value));
    }
    
    int height = 500, width = 400;
    
    pieChart = new PieChart(FXCollections.observableList(dataList));
    pieChart.setClockwise(true); 

    pieChart.setLabelLineLength(10);
    pieChart.setLegendVisible(false);
    pieChart.getData().forEach(data -> data.getNode().setOnMouseEntered(event -> pieChartEventHandler(event, data)));
    pieChart.setMinHeight(height);
    pieChart.setMinWidth(width);
    pieChart.setMaxHeight(height);
    pieChart.setMaxWidth(width);

    root.add(pieChart, 1, 10, 5, 5);

}
public static void pieChartEventHandler(MouseEvent event, Data data) {
    Tooltip tooltip = new Tooltip(String.format("%s\n%.2f", data.getName(), data.getPieValue()));
    Tooltip.install(data.getNode(), tooltip);
    System.out.println( data.getPieValue());
 }

public void plusButtonHandler(ActionEvent event) throws SQLException {
	if (daysBack < 0) {
		daysBack ++;
		showEveryThingThatwasAddInPD();
		CircularInit();
	   }
	if (LocalDateTime.now().plusDays(daysBack - 1).getMonth() != LocalDateTime.now().plusDays(daysBack).getMonth()
			|| LocalDateTime.now().plusDays(daysBack - 1).getYear() != LocalDateTime.now().plusDays(daysBack).getYear()) {
		generatePie();
	   }
	}
	


public void minusButtonHandler(ActionEvent event) throws SQLException {
		daysBack --;
		showEveryThingThatwasAddInPD();
		CircularInit();
		if (LocalDateTime.now().plusDays(daysBack + 1).getMonth() != LocalDateTime.now().plusDays(daysBack).getMonth()
				|| LocalDateTime.now().plusDays(daysBack + 1).getYear() != LocalDateTime.now().plusDays(daysBack).getYear()) {
			generatePie();
			
		}
}
		
}