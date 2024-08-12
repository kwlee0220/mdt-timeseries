package mdt.timeseries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import utils.jdbc.SQLDataType;
import utils.jdbc.SQLDataTypes;
import utils.stream.FStream;

import mdt.model.DataType;

/**
 *
 * @author Kang-Woo Lee (ETRI)
 */
public class RecordJdbcHandler {
	private final RecordSchema m_schema;
	
	public RecordJdbcHandler(RecordSchema schema) {
		m_schema = schema;
	}
	
	public List<Object> loadRecordValues(ResultSet rset) throws SQLException {
		return FStream.from(m_schema.getFieldAll())
						.zipWithIndex(1)
						.mapOrThrow(t -> readFromResultSet(rset, t.index()))
						.toList();
	}
	
	private Object readFromResultSet(ResultSet rset, int fieldIdx) throws SQLException {
		DataType dtype = m_schema.getField(fieldIdx).getType();
		SQLDataType<?,?> converter = SQLDataTypes.fromTypeName(dtype.getName());
		return converter.readFromResultSet(rset, fieldIdx);
	}
	
//	FStream<List<Object>> loadRecords(ResultSet rset) {
//		return JdbcRowSource.select(this::loadRecordValues)
//							.from(rset)
//							.fstream();
//	}
//	
//	List<List<Object>> loadRecordList(ResultSet rset) {
//		return JdbcRowSource.select(this::loadRecordValues)
//							.from(rset)
//							.toList();
//	}
//	
//	FStream<List<Object>> loadRecords(Connection conn, String sqlSuffix) {
//		String sql = m_sql_prefix_select_records + " " + sqlSuffix;
//		return JdbcRowSource.select(this::loadRecordValues)
//							.from(conn)
//							.executeQuery(sql)
//							.fstream();
//	}
//	
//	void load(Record record, ResultSet rset) throws SQLException {
//		int idx = 1;
//		for ( Field field: m_schema.getFieldAll() ) {
//			Object value = field.getType().readFromResultSet(rset, idx);
//			record.setFieldValue(idx, value);
//			++idx;
//		}
//	}
}
