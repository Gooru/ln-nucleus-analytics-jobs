package org.gooru.migration.jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.gooru.migration.connections.ConnectionProvider;
import org.gooru.migration.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.retry.ConstantBackoff;

public class EventMigration {
	private static final Logger LOG = LoggerFactory.getLogger(EventMigration.class);
	private static SimpleDateFormat minuteDateFormatter = new SimpleDateFormat("yyyyMMddkkmm");
	private static ConnectionProvider connectionProvider = ConnectionProvider.instance();
	private static PreparedStatement insertEvents = (connectionProvider.getAnalyticsCassandraSession())
			.prepare("INSERT INTO events(event_id,fields)VALUES(?,?)");
	private static PreparedStatement insertEventTimeLine = (connectionProvider.getAnalyticsCassandraSession())
			.prepare("INSERT INTO events_timeline(event_time,event_id)VALUES(?,?);");

	public static void main(String args[]) {
		LOG.info("deploying EventMigration....");
		try {
			String start = args[0];
			String end = args[1];

			Long startTime = minuteDateFormatter.parse(start).getTime();
			LOG.info("startTime : " + start);
			Long endTime = minuteDateFormatter.parse(end).getTime();
			LOG.info("endTime : " + end);
			// String start = "201508251405";
			// Long endTime = new Date().getTime();

			for (Long startDate = startTime; startDate < endTime;) {
				String currentDate = minuteDateFormatter.format(new Date(startDate));
				LOG.info("Running for :" + currentDate);
				// Incrementing time - one minute
				ColumnList<String> et = readWithKey(Constants.EVENT_TIMIELINE, currentDate);
				for (String eventId : et.getColumnNames()) {
					ColumnList<String> ef = readWithKey(Constants.EVENT_DETAIL,
							et.getStringValue(eventId, Constants.NA));
					// Insert event_time_line
					insertData(currentDate, et.getStringValue(eventId, Constants.NA), insertEventTimeLine);
					// Insert events
					insertData(et.getStringValue(eventId, Constants.NA),
							ef.getStringValue(Constants.FIELDS, Constants.NA), insertEvents);
				}
				startDate = new Date(startDate).getTime() + 60000;
				Thread.sleep(200);
			}
		} catch (Exception e) {
			if (e instanceof ArrayIndexOutOfBoundsException) {
				LOG.info("startTime or endTime can not be null. Please make sure the class execution format as below.");
				LOG.info(
						"java -classpath build/libs/migration-scripts-fat.jar: org.gooru.migration.jobs.EventMigration 201508251405 201508251410");
			} else {
				LOG.error("Something went wrong...");
			}
			System.exit(500);
		}
	}

	public static ColumnList<String> readWithKey(String cfName, String key) {

		ColumnList<String> result = null;
		try {
			result = (connectionProvider.getCassandraKeyspace())
					.prepareQuery(connectionProvider.accessColumnFamily(cfName))
					.setConsistencyLevel(ConsistencyLevel.CL_QUORUM).withRetryPolicy(new ConstantBackoff(2000, 5))
					.getKey(key).execute().getResult();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void insertData(String key, String column, PreparedStatement preparedStatement) {
		try {
			BoundStatement boundStatement = new BoundStatement(preparedStatement);
			boundStatement.bind(key, column);
			(connectionProvider.getAnalyticsCassandraSession()).executeAsync(boundStatement);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
