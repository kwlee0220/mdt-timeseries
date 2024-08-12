package mdt.timeseries;

import static utils.jdbc.SQLDataTypes.DATE_TIME;
import static utils.jdbc.SQLDataTypes.DURATION;
import static utils.jdbc.SQLDataTypes.LONG;
import static utils.jdbc.SQLDataTypes.STRING;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import utils.LocalDateTimes;
import utils.func.FOption;
import utils.jdbc.JdbcRowSource;
import utils.jdbc.SQLDataTypes;
import utils.stream.FStream;

import de.fraunhofer.iosb.ilt.faaast.service.model.exception.ResourceNotFoundException;
import mdt.model.ModelGenerationException;
import mdt.model.resource.value.MultiLanguagePropertyValue;
import mdt.timeseries.Record;
import mdt.timeseries.RecordSchema;
import mdt.timeseries.Segment;
import mdt.timeseries.SegmentState;


/**
 *
 * @author Kang-Woo Lee (ETRI)
 */
public class SegmentJdbcHandler {
	private final RecordSchema m_schema;
	private final String m_tableName;
	private final List<String> m_columnNames;
	
	private final String m_columnNamesCsv;
	private final RecordJdbcHandler m_recordHandler;
	
	public SegmentJdbcHandler(RecordSchema schema, String tableName, List<String> columnNames) {
		m_schema = schema;
		m_tableName = tableName;
		m_columnNames = columnNames;
		
		m_recordHandler = new RecordJdbcHandler(schema);
		m_columnNamesCsv = FStream.from(m_columnNames).join(", ");
	}

	private static final String SQL_GET_SEGMENT
		= "select * from segments where time_series_id = ? and start_time = ?";
	public Segment load(Connection conn, String timeSeriesId, Instant startTime)
		throws ResourceNotFoundException {
		try {
			Segment segment = new Segment();
			String sql = String.format(SQL_GET_SEGMENT);
			try ( PreparedStatement pstmt = conn.prepareStatement(sql) ) {
				STRING.fillPreparedStatement(pstmt, 1, timeSeriesId);
				DATE_TIME.fillPreparedStatement(pstmt, 2, DATE_TIME.toSQLValue(startTime));
				
				ResultSet rset = pstmt.executeQuery();
				return JdbcRowSource.select(rs -> { load(segment, rs); return segment; })
									.from(rset)
									.first()
									.getOrThrow(() -> newResourceNotFoundException(timeSeriesId, startTime));
			}
		}
		catch ( SQLException e ) {
			String msg = String.format("Failed to generate Segment: %s.%s, cause=%s",
										timeSeriesId, startTime, "" + e);
			throw new ModelGenerationException(msg);
		}
	}
	
	private Segment identifySegment(Connection conn, String timeSeriesId, Instant startTime, Duration duration) {
		Instant endTime = startTime.plus(duration);
		try {
			Segment segment = new Segment();
			segment.setStartTime(startTime);
			segment.setEndTime(endTime);
			
			long count = countSegmentRecords(conn, startTime, endTime);
			if ( count == 0 ) {
				return null;
			}
			segment.setRecordCount(count);
			
			Record last = getLastRecordInSegment(conn, startTime, endTime);
			segment.setLastUpdate(last.getTimestamp());
			
			SegmentState state = (isCompletedSegment(conn, endTime))
									? SegmentState.COMPLETED : SegmentState.IN_PREGRESS;
			segment.setState(state);
			
			return segment;
		}
		catch ( SQLException e ) {
			String msg = String.format("Failed to identify a Segment: id=%s start=%s, end=%s, cause=%s",
										timeSeriesId, LocalDateTimes.fromInstant(startTime),
										LocalDateTimes.fromInstant(endTime), "" + e);
			throw new ModelGenerationException(msg);
		}
	}
	
	private static final String SQL_COUNT_SEGMENT_RECORDS
		= "select count(*) from %s where ts >= ? and ts < ?";
	private long countSegmentRecords(Connection conn, Instant startTime,
										Instant endTime) throws SQLException {
		String sql = String.format(SQL_COUNT_SEGMENT_RECORDS, m_tableName);
		try ( PreparedStatement pstmt = conn.prepareStatement(sql) ) {
			SQLDataTypes.DATE_TIME.fillPreparedStatement(pstmt, 1, startTime);
			SQLDataTypes.DATE_TIME.fillPreparedStatement(pstmt, 2, endTime);
			return JdbcRowSource.selectAsLong()
								.from(pstmt.executeQuery())
								.first()
								.getOrElse(0L);
		}
	}
	
//	private static final String SQL_FIRST_RECORD 
//		= "select %s from %s where ts >= ? and ts < ? order by ts asc limit 1";
//	private Record getFirstRecordInSegment(Connection conn, Instant startTime, Instant endTime) throws SQLException {
//		String sql = String.format(SQL_FIRST_RECORD, m_columnNamesCsv, m_tableName);
//		try ( PreparedStatement pstmt = conn.prepareStatement(sql) ) {
//			DataType.DATE_TIME.fillPreparedStatement(pstmt, 1, startTime);
//			DataType.DATE_TIME.fillPreparedStatement(pstmt, 2, endTime);
//			
//			try ( ResultSet rset = pstmt.executeQuery() ) {
//				List<List<Object>> firstRecord = m_recordHandler.loadRecordList(rset);
//				if ( firstRecord.size() > 0 ) {
//					return new Record(m_schema, firstRecord.get(0));
//				}
//				else {
//					return null;
//				}
//			}
//		}
//	}
	
	private static final String SQL_LAST_RECORD
		= "select %s from %s where ts >= ? and ts < ? order by ts desc limit 1";
	private Record getLastRecordInSegment(Connection conn, Instant startTime, Instant endTime) throws SQLException {
		String sql = String.format(SQL_LAST_RECORD, m_columnNamesCsv, m_tableName);
		try ( PreparedStatement pstmt = conn.prepareStatement(sql) ) {
			SQLDataTypes.DATE_TIME.fillPreparedStatement(pstmt, 1, startTime);
			SQLDataTypes.DATE_TIME.fillPreparedStatement(pstmt, 2, endTime);
			
			return JdbcRowSource.select(m_recordHandler::loadRecordValues)
								.from(pstmt.executeQuery())
								.first()
								.map(colValues -> new Record(m_schema, colValues))
								.getOrNull();
		}
	}
	
	private static final String SQL_ANY_IN_NEXT_SEGMENT = "select ts from %s where ts >= ? limit 1";
	private boolean isCompletedSegment(Connection conn, Instant endTime) throws SQLException {
		String sql = String.format(SQL_ANY_IN_NEXT_SEGMENT, m_columnNamesCsv, m_tableName);
		try ( PreparedStatement pstmt = conn.prepareStatement(sql) ) {
			SQLDataTypes.DATE_TIME.fillPreparedStatement(pstmt, 1, endTime);
			return JdbcRowSource.selectAsObject()
								.from(pstmt.executeQuery())
								.first().isPresent();
		}
	}
	
	private void load(Segment segment, ResultSet rset) throws SQLException {
		String timeSeriesId = rset.getString("time_series_id");
		int seqNo = rset.getInt("seq_no");
		segment.setIdShort(String.format("%s_%07d", timeSeriesId, seqNo));
		MultiLanguagePropertyValue name = FOption.map(rset.getString("name"),
															v -> new MultiLanguagePropertyValue("ko", v));
		segment.setName(name);

		MultiLanguagePropertyValue desc = FOption.map(rset.getString("description"),
															v -> new MultiLanguagePropertyValue("ko", v));
		segment.setDescription(desc);
		
		segment.setRecordCount(rset.getLong("record_count"));
		segment.setStartTime(DATE_TIME.readJavaValueFromResultSet(rset, "start_time"));
		segment.setEndTime(DATE_TIME.readJavaValueFromResultSet(rset, "end_time"));
		segment.setDuration(DURATION.readJavaValueFromResultSet(rset, "duration"));
		segment.setSamplingInterval(LONG.readFromResultSet(rset, "sampling_interval"));
		segment.setSamplingRate(LONG.readFromResultSet(rset, "sampling_rate"));
		segment.setState(FOption.map(rset.getString("state"), SegmentState::valueOf));
		segment.setLastUpdate(DATE_TIME.readJavaValueFromResultSet(rset, "last_update"));
	}
	
	private static ResourceNotFoundException newResourceNotFoundException(String timeSeriesId,
																			Instant startTime) {
		String details = String.format("Resource not found: Segment=%s:%s", timeSeriesId, startTime);
		return new ResourceNotFoundException(details);
	}
}
