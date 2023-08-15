package application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class StringHelper {
	public static String outPutFormatation(ResultSet resultSet, StringBuilder builder) throws SQLException {
		double price = resultSet.getDouble("price");
		String category, description, formattedDateTime;
		SimpleDateFormat dateFormat;

		description = resultSet.getString("description");
	    category = truncateString(resultSet.getString("category"));
        dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");
        formattedDateTime = dateFormat.format(resultSet.getTimestamp("datetime"));
        builder.append(category).append(' ').append(String.format("%.2f", price)).append(' ').append('\n').append(formattedDateTime)
        .append('\n').append(description);
        return builder.toString();
	}
	public static StringBuilder HelpWithComment(StringBuilder builder, String comment, String time) {
		int maxCommentLength = 38, timePlasement = 42;
		if (comment.length() <=  maxCommentLength) {
			builder.append(comment);
			addSpaces(builder, timePlasement - comment.length());
			builder.append("At ").append(time);
		}
		else {
			System.out.println("You knew that there will be a probelem");
		}
		return builder;
	}
	public static StringBuilder addSpaces(StringBuilder builder, int spaceNumber) {
		for (int i = 0; i < spaceNumber; i++) builder.append(' ');
		return builder;
	}
	public static String truncateString(String input) {
	    int maxLength = 23;
	    if (input.length() > maxLength) {
	        String truncatedString = input.substring(0, maxLength);
	        return truncatedString + "...";
	    } else {
	        return input;
	    }
	}
}
