package aurora.plugin.sap.sync.idoc;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;

import com.sap.conn.idoc.jco.JCoIDocServer;

public class DatabaseTool {
	public static final String DONE_STATUS = "DONE";
	public static final String EXCEPTION_STATUS = "EXCEPTION";
	
	private Connection dbConnection;
	private ILogger logger;

	public DatabaseTool(Connection dbConnection, ILogger logger) {
		this.dbConnection = dbConnection;
		this.logger = logger;
	}

	public int addIDocServer(JCoIDocServer server, String server_name) throws AuroraIDocException {
		int server_id = -1;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String program_id = server.getProgramID();
			String repository_name = server.getRepository().getName();
			String gateway_host = server.getGatewayHost();
			String gateway_service = server.getGatewayService();
			String respository_destination = server.getRepositoryDestination();
			String select_sql = "select t.server_id from fnd_idoc_servers t where t.program_id=? and t.repository_name=? and t.gateway_host=?"
					+ " and t.gateway_service=?";
			statement = dbConnection.prepareStatement(select_sql);
			int index = 1;
			statement.setString(index++, program_id);
			statement.setString(index++, repository_name);
			statement.setString(index++, gateway_host);
			statement.setString(index++, gateway_service);
			rs = statement.executeQuery();
			if (rs.next()) {
				server_id = rs.getInt(1);
				rs.close();
				statement.close();
				String update_sql = "update fnd_idoc_servers t set t.status = 'OK',last_update_date=sysdate where t.server_id ="
						+ server_id;
				Statement st = dbConnection.createStatement();
				st.executeUpdate(update_sql);
				st.close();
				statement.close();
			} else {
				statement = dbConnection.prepareStatement("select fnd_idoc_servers_s.nextval from dual");
				rs = statement.executeQuery();
				if (rs.next()) {
					server_id = rs.getInt(1);
				}
				
				rs.close();
				statement.close();
				String insert_sql = "insert into fnd_idoc_servers(" + "server_id,server_name,program_id,repository_name,gateway_host,"
						+ "gateway_service,respository_destination,status,created_by," + "creation_date,last_updated_by,last_update_date"
						+ ") values" + "(?,?,?,?,?,?,?,?,0,sysdate,0,sysdate)";
				statement = dbConnection.prepareStatement(insert_sql);
				index = 1;
				statement.setInt(index++, server_id);
				statement.setString(index++, server_name);
				statement.setString(index++, program_id);
				statement.setString(index++, repository_name);
				statement.setString(index++, gateway_host);
				statement.setString(index++, gateway_service);
				statement.setString(index++, respository_destination);
				statement.setString(index++, "OK");
				statement.executeUpdate();
			}
		} catch (SQLException e) {
			throw AuroraIDocException.createStatementException(statement, e);
		} finally {
			closeResultSet(rs);
			closeStatement(statement);
		}
		return server_id;
	}

	public int addIDocFile(int serverId, String file_path) throws SQLException, AuroraIDocException {
		String fetch_idoc_file_id = "select fnd_idoc_files_s.nextval from dual";
		Statement statement = null;
		PreparedStatement pStatement = null;
		ResultSet rs = null;
		int idoc_file_id = -1;
		try {
			statement = dbConnection.createStatement();
			rs = statement.executeQuery(fetch_idoc_file_id);
			if (rs.next()) {
				idoc_file_id = rs.getInt(1);
			} else {
				throw new AuroraIDocException("execute sql:" + fetch_idoc_file_id + " failed.");
			}
			rs.close();
			statement.close();
			String insert_sql = "insert into fnd_idoc_files(idoc_file_id,server_id,file_path,created_by,creation_date,last_updated_by,last_update_date) values(?,?,?,0,sysdate,0,sysdate) ";
			pStatement = dbConnection.prepareStatement(insert_sql);
			int index = 1;
			pStatement.setInt(index++, idoc_file_id);
			pStatement.setInt(index++, serverId);
			pStatement.setString(index++, file_path);
			pStatement.executeUpdate();
			pStatement.close();
		} catch (SQLException e) {
			if (pStatement != null)
				throw new AuroraIDocException("execute sql:" + pStatement.toString() + " failed.", e);
			else if (statement != null) {
				throw new AuroraIDocException("execute sql:" + statement.toString() + " failed.", e);
			}
			throw new AuroraIDocException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(statement);
			closeStatement(pStatement);
		}
		return idoc_file_id;
	}

	public void updateIdocFileInfo(int idoc_file_id, CompositeMap control_node) throws AuroraIDocException {
		if (idoc_file_id < 1 || control_node == null)
			return;
		String tabnam = getChildNodeText(control_node, IDocFile.TABNAM_NODE);
		String mandt = getChildNodeText(control_node, IDocFile.MANDT_NODE);
		String docnum = getChildNodeText(control_node, IDocFile.DOCNUM_NODE);
		String docrel = getChildNodeText(control_node, IDocFile.DOCREL_NODE);
		String status = getChildNodeText(control_node, IDocFile.STATUS_NODE);
		String direct = getChildNodeText(control_node, IDocFile.DIRECT_NODE);
		String outmod = getChildNodeText(control_node, IDocFile.OUTMOD_NODE);
		String idoctyp = getChildNodeText(control_node, IDocFile.IDOCTYP_NODE);
		String cimtyp = getChildNodeText(control_node, IDocFile.CIMTYP_NODE);
		String mestyp = getChildNodeText(control_node, IDocFile.MESTYP_NODE);
		String sndpor = getChildNodeText(control_node, IDocFile.SNDPOR_NODE);
		String sndprt = getChildNodeText(control_node, IDocFile.SNDPRT_NODE);
		String sndprn = getChildNodeText(control_node, IDocFile.SNDPRN_NODE);
		String rcvpor = getChildNodeText(control_node, IDocFile.RCVPOR_NODE);
		String rcvprt = getChildNodeText(control_node, IDocFile.RCVPRT_NODE);
		String rcvprn = getChildNodeText(control_node, IDocFile.RCVPRN_NODE);
		String credat = getChildNodeText(control_node, IDocFile.CREDAT_NODE);
		String cretim = getChildNodeText(control_node, IDocFile.CRETIM_NODE);
		String serial = getChildNodeText(control_node, IDocFile.SERIAL_NODE);
		PreparedStatement statement = null;
		try {
			String update_sql = "update fnd_idoc_files set tabnam=?, mandt=?, docnum=?, docrel=?, cimtyp=?,"
					+ " status=?, direct=?, outmod=?, idoctyp=? ,mestyp=?, sndpor=? ,sndprt=? ,sndprn=?, rcvpor=?, rcvprt=? ,"
					+ " rcvprn=?,credat=?, cretim=?, serial=?, last_updated_by=0, last_update_date=sysdate where idoc_file_id = ?";
			statement = dbConnection.prepareStatement(update_sql);
			int index = 1;
			statement.setString(index++, tabnam);
			statement.setString(index++, mandt);
			statement.setString(index++, docnum);
			statement.setString(index++, docrel);
			statement.setString(index++, cimtyp);
			statement.setString(index++, status);
			statement.setString(index++, direct);
			statement.setString(index++, outmod);
			statement.setString(index++, idoctyp);
			statement.setString(index++, mestyp);
			statement.setString(index++, sndpor);
			statement.setString(index++, sndprt);
			statement.setString(index++, sndprn);
			statement.setString(index++, rcvpor);
			statement.setString(index++, rcvprt);
			statement.setString(index++, rcvprn);
			statement.setString(index++, credat);
			statement.setString(index++, cretim);
			statement.setString(index++, serial);
			statement.setInt(index++, idoc_file_id);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			throw AuroraIDocException.createStatementException(statement, e);
		} finally {
			closeStatement(statement);
		}
	}

	private String getSegmentFieldValue(CompositeMap content_node, String segment, String fieldName) {
		CompositeMap parentSegment = getParentSegment(content_node, segment);
		if (parentSegment != null) {
			return getChildNodeText(parentSegment, fieldName);
		}
		return null;
	}

	private String getChildNodeText(CompositeMap segmentNode, String childName) {
		if (segmentNode == null || childName == null)
			return null;
		CompositeMap childNode = segmentNode.getChild(childName);
		if (childNode == null)
			return null;
		return childNode.getText();
	}

	private CompositeMap getParentSegment(CompositeMap node, String segment) {
		if (node == null || segment == null)
			return null;
		if (segment.equals(node.getName())) {
			return node;
		}
		CompositeMap parentNode = node.getParent();
		if (parentNode == null)
			return null;
		return getParentSegment(parentNode, segment);
	}

	public int addInterfaceHeader(int idoc_file_id, CompositeMap controlNode) throws AuroraIDocException {
		if (idoc_file_id < 1 || controlNode == null)
			return -1;
		String idoctyp = getChildNodeText(controlNode, IDocFile.IDOCTYP_NODE);
		String cimtyp = getChildNodeText(controlNode, IDocFile.CIMTYP_NODE);
		String template_code = idoctyp + (cimtyp != null ? cimtyp : "");
		String fetch_interface_header_sql = "select fnd_interface_headers_s.nextval from dual";
		Statement statement = null;
		PreparedStatement pstatement = null;
		ResultSet rs = null;
		int interface_header_id = -1;
		try {
			statement = dbConnection.createStatement();
			rs = statement.executeQuery(fetch_interface_header_sql);
			if (rs.next()) {
				interface_header_id = rs.getInt(1);
			} else {
				throw new AuroraIDocException("execute sql:" + fetch_interface_header_sql + " failed.");
			}
			rs.close();
			statement.close();
			String insert_sql = "insert into fnd_interface_headers(header_id,template_code,attribute_1,created_by,creation_date,last_updated_by,last_update_date)"
					+ " values(?,?,?,0,sysdate,0,sysdate)";
			pstatement = dbConnection.prepareStatement(insert_sql);
			pstatement.setInt(1, interface_header_id);
			pstatement.setString(2, template_code);
			pstatement.setString(3, String.valueOf(idoc_file_id));
			pstatement.executeUpdate();
			pstatement.close();
		} catch (SQLException e) {
			throw AuroraIDocException.createStatementsException(new Statement[] { pstatement, statement }, e);
		} finally {
			closeResultSet(rs);
			closeStatement(statement);
			closeStatement(pstatement);
		}
		return interface_header_id;

	}

	public IDocType getIdocType(CompositeMap controlNode) {
		String idoctyp = getChildNodeText(controlNode, IDocFile.IDOCTYP_NODE);
		String cimtyp = getChildNodeText(controlNode, IDocFile.CIMTYP_NODE);
		return new IDocType(idoctyp, cimtyp);
	}

	public boolean isOrdinal(String idoctyp, String cimtyp) throws AuroraIDocException {
		StringBuffer query_sql = new StringBuffer("select ordinal_flag from fnd_idoc_types where idoctyp=? ");
		if (cimtyp != null)
			query_sql.append(" and cimtyp=?");
		PreparedStatement statement = null;
		ResultSet rs = null;
		String ordinal_flag = null;
		try {
			statement = dbConnection.prepareStatement(query_sql.toString());
			statement.setString(1, idoctyp);
			if (cimtyp != null)
				statement.setString(2, cimtyp);
			rs = statement.executeQuery();
			if (rs.next()) {
				ordinal_flag = rs.getString(1);
			} else {
				throw new AuroraIDocException("idoctyp:" + idoctyp + " cimtyp:" + cimtyp + " execute sql:" + query_sql.toString()
						+ " failed.");
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw AuroraIDocException.createStatementException(statement, e);
		} finally {
			closeResultSet(rs);
			closeStatement(statement);
		}
		return "Y".equalsIgnoreCase(ordinal_flag);
	}

	public void syncMapTables(int idoc_file_id, CompositeMap contentNode) throws AuroraIDocException {
		if (idoc_file_id < 1 || contentNode == null || contentNode.getChilds() == null)
			return;
		PreparedStatement segmentMapsSt = null;
		PreparedStatement fieldMapsSt = null;
		PreparedStatement insertSt = null;
		ResultSet segmentMapsRs = null;
		ResultSet filedMapsRs = null;
		String segment_name = contentNode.getName();
		String segmentMapsSQL = "select t.segment_map_id,t.table_name from fnd_idoc_segment_maps t where t.segment_name = ?";
		String fieldMapsSQL = "select t.parent_segment_name,t.segment_field,t.table_field from fnd_idoc_field_maps t where t.segment_map_id = ?";
		try {
			segmentMapsSt = dbConnection.prepareStatement(segmentMapsSQL);
			segmentMapsSt.setString(1, segment_name);
			segmentMapsRs = segmentMapsSt.executeQuery();
			while (segmentMapsRs.next()) {
				int segment_map_id = segmentMapsRs.getInt(1);
				String table_name = segmentMapsRs.getString(2);
				fieldMapsSt = dbConnection.prepareStatement(fieldMapsSQL);
				fieldMapsSt.setInt(1, segment_map_id);
				filedMapsRs = fieldMapsSt.executeQuery();
				StringBuffer insert_sql = new StringBuffer("insert into " + table_name
						+ " (idoc_file_id,created_by,creation_date,last_updated_by,last_update_date");
				StringBuffer values_sql = new StringBuffer("values(" + idoc_file_id + ",0,sysdate,0,sysdate");
				List<String> values = new LinkedList<String>();
				while (filedMapsRs.next()) {
					String parent_segment_name = filedMapsRs.getString(1);
					String segmentField = filedMapsRs.getString(2);
					String tableField = filedMapsRs.getString(3);
//					insert_sql.append("," + tableField);
					insert_sql.append(",\"" + tableField+"\"");//��垮��..last_update_date,/BEV1/NEDEPFREE,..杩�绉�瀛�娈靛�藉��浼���虹�伴��棰�
					String segmentName = parent_segment_name != null ? parent_segment_name : segment_name;
					String value = getSegmentFieldValue(contentNode, segmentName, segmentField);
					values_sql.append(",?");
					values.add(value != null ? value : "");
				}
				insert_sql.append(")").append(values_sql).append(")");
				try {
					insertSt = dbConnection.prepareStatement(insert_sql.toString());
					int i = 1;
					for (String value : values) {
						insertSt.setString(i++, value);
					}
					insertSt.executeUpdate();
				} catch (Throwable e) {
					throw new AuroraIDocException("execute sql:" + insert_sql.toString() + " in syncMapTables", e);
				}
				insertSt.close();
			}
		} catch (SQLException e) {
			throw AuroraIDocException.createStatementsException(new Statement[] { insertSt, fieldMapsSt, segmentMapsSt }, e);
		} finally {
			closeResultSet(segmentMapsRs);
			closeStatement(segmentMapsSt);
			closeResultSet(filedMapsRs);
			closeStatement(fieldMapsSt);
			closeStatement(insertSt);
		}
		for (int i = 0; i < contentNode.getChilds().size(); i++) {
			CompositeMap child = (CompositeMap) contentNode.getChilds().get(i);
			if (child.getChilds() != null && child.getChilds().size() > 0) {
				syncMapTables(idoc_file_id, child);
			}
		}
	}

	public void addInterfaceLine(int interface_header_id, CompositeMap contentNode) throws AuroraIDocException {
		if (interface_header_id < 1 || contentNode == null)
			return;
		handleContentNode(interface_header_id, 0, contentNode);
	}

	private void handleContentNode(int headerId, int parent_id, CompositeMap content_node) throws AuroraIDocException {
		List<CompositeMap> content_childs = content_node.getChilds();
		if( content_childs == null)
			return;
		if (!isSegmentDefined(content_node.getName()))
			return;
		StringBuffer insert_sql = new StringBuffer(
				"insert into fnd_interface_lines(line_id,line_number,header_id,created_by,creation_date,last_updated_by,last_update_date,"
						+ " source_table,parent_line_id");
		int line_id = getInterfaceLineId();
		StringBuffer values_sql = new StringBuffer("values(?,?,?,0,sysdate,0,sysdate,?,?");
		int index = 1;
		for (int i = 0; i < content_node.getChilds().size(); i++) {
			CompositeMap child = (CompositeMap) content_node.getChilds().get(i);
			if (child.getChilds() != null && child.getChilds().size() > 0) {
				handleContentNode(headerId, line_id, child);
				continue;
			}
			if (!isSegment(child)) {
				insert_sql.append(",attribute_" + (getFieldIndex(content_node.getName(), child.getName())));
				values_sql.append(",?");
			}
		}
		insert_sql.append(")").append(values_sql).append(")");
		PreparedStatement statement = null;
		try {
			statement = dbConnection.prepareStatement(insert_sql.toString());
			index = 1;
			statement.setInt(index++, line_id);
			statement.setInt(index++, line_id);
			statement.setInt(index++, headerId);
			statement.setString(index++, content_node.getName());
			statement.setInt(index++, parent_id);
			List<CompositeMap> childList = content_node.getChilds();
			if (childList != null) {
				for (CompositeMap child : childList) {
					if (child.getChilds() != null && child.getChilds().size() > 0) {
						continue;
					}
					if (!isSegment(child)) {
						statement.setString(index++, child.getText());
					}
				}
			}
			statement.executeUpdate();
			statement.close();
		} catch (Throwable e) {
			throw new AuroraIDocException("execute sql:" + insert_sql.toString() + " in handleContentNode", e);
		} finally {
			closeStatement(statement);
		}
	}

	private boolean isSegmentDefined(String segment) throws AuroraIDocException {
		if (segment == null)
			return false;
		StringBuffer query_sql = new StringBuffer("select 1 from fnd_idoc_segments t where t.segmenttyp=? ");
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = dbConnection.prepareStatement(query_sql.toString());
			statement.setString(1, segment);
			rs = statement.executeQuery();
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			throw AuroraIDocException.createStatementException(statement, e);
		} finally {
			closeResultSet(rs);
			closeStatement(statement);
		}
	}

	private boolean isSegment(CompositeMap content_node) {
		if (content_node == null)
			return false;
		String attribute = "SEGMENT";
		if (content_node.getString(attribute) != null) {
			return true;
		}
		return false;
	}

	private int getInterfaceLineId() throws AuroraIDocException {
		String query_sql = "select fnd_interface_lines_s.nextval from dual";
		Statement statement = null;
		ResultSet rs = null;
		int interface_line_id = -1;
		try {
			statement = dbConnection.createStatement();
			rs = statement.executeQuery(query_sql);
			if (rs.next()) {
				interface_line_id = rs.getInt(1);
			} else {
				throw new AuroraIDocException(" execute sql:" + query_sql + " failed.");
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new AuroraIDocException("execute sql:" + query_sql.toString() + " in handleContentNode", e);
		} finally {
			closeResultSet(rs);
			closeStatement(statement);
		}
		return interface_line_id;
	}

	public void updateInterfaceHeaderStatus(int header_id, String status) throws AuroraIDocException {
		String header_update_sql = "update fnd_interface_headers t set t.status=? where t.header_id =?";
		PreparedStatement statement = null;
		try {
			statement = dbConnection.prepareStatement(header_update_sql);
			statement.setString(1, status);
			statement.setInt(2, header_id);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			throw AuroraIDocException.createStatementException(statement, e);
		} finally {
			closeStatement(statement);
		}
	}

	public void updateIdocFileStatus(int idoc_file_id, String status,String exception_message) throws AuroraIDocException {
		String idoc_update_sql = "update fnd_idoc_files t set t.sync_status=?,t.exception_message=? where t.idoc_file_id =?";
		PreparedStatement statement = null;
		try {
			statement = dbConnection.prepareStatement(idoc_update_sql);
			statement.setString(1, status);
			statement.setString(2, exception_message);
			statement.setInt(3, idoc_file_id);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			throw AuroraIDocException.createStatementException(statement, e);
		} finally {
			closeStatement(statement);
		}
	}

	public void recordFeedback(int idoc_file_id, String status, String message) throws AuroraIDocException {
		String fnd_idoc_feedbacks_sql = "insert into fnd_idoc_feedbacks(idoc_file_id,status,message,created_by,creation_date,last_updated_by,last_update_date)"
				+ "values(?,?,?,0,sysdate,0,sysdate)";
		PreparedStatement statement = null;
		try {
			statement = dbConnection.prepareStatement(fnd_idoc_feedbacks_sql);
			statement.setInt(1, idoc_file_id);
			statement.setString(2, status);
			statement.setString(3, message);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			throw AuroraIDocException.createStatementException(statement, e);
		} finally {
			closeStatement(statement);
		}
	}

	public String queryExecutePkg(int idoc_file_id) throws AuroraIDocException {
		if (idoc_file_id < 1)
			return null;
		String query_sql = "select s.execute_pkg  from fnd_idoc_files t, fnd_idoc_types s  where t.idoc_file_id = " + idoc_file_id
				+ "  and s.idoctyp = t.idoctyp " + " and ((s.cimtyp is null and t.cimtyp is null) or s.cimtyp = t.cimtyp)";
		Statement statement = null;
		ResultSet rs = null;
		String execute_pkg = null;
		try {
			statement = dbConnection.createStatement();
			rs = statement.executeQuery(query_sql);
			if (rs.next()) {
				execute_pkg = rs.getString(1);
			} else {
				throw new AuroraIDocException("execute sql:" + query_sql + " failed!");
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw AuroraIDocException.createSQLException(query_sql, e);
		} finally {
			closeResultSet(rs);
			closeStatement(statement);
		}
		return execute_pkg;
	}

	public Connection getConnection() {
		return dbConnection;
	}

	public String queryFeedbackProc(int idoc_file_id) throws AuroraIDocException {
		if (idoc_file_id < 1)
			return null;
		String query_sql = "select s.feedback_proc  from fnd_idoc_files t, fnd_idoc_types s  where t.idoc_file_id = " + idoc_file_id
				+ "  and s.idoctyp = t.idoctyp " + " and ((s.cimtyp is null and t.cimtyp is null) or s.cimtyp = t.cimtyp)";
		Statement statement = null;
		ResultSet rs = null;
		String execute_pkg = null;
		try {
			statement = dbConnection.createStatement();
			rs = statement.executeQuery(query_sql);
			if (rs.next()) {
				execute_pkg = rs.getString(1);
			} else {
				throw new AuroraIDocException("execute sql:" + query_sql + " failed!");
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw AuroraIDocException.createSQLException(query_sql, e);
		} finally {
			closeResultSet(rs);
			closeStatement(statement);
		}
		return execute_pkg;
	}

	public String executePkg(String executePkg, int idoc_file_id) throws AuroraIDocException {
		String errorMessage = null;
		CallableStatement proc = null;
		try {
			proc = dbConnection.prepareCall("{call ? := " + executePkg + "(?)}");
			proc.registerOutParameter(1, Types.VARCHAR);
			proc.setInt(2, idoc_file_id);
			proc.execute();
			errorMessage = proc.getString(1);
			if (errorMessage == null || "".equals(errorMessage)) {
				dbConnection.commit();
			} else {
				dbConnection.rollback();
			}
			proc.close();
		} catch (SQLException e) {
			rollback();
			throw AuroraIDocException.createStatementException(proc, e);
		} finally {
			closeStatement(proc);
		}
		return errorMessage;

	}

	public void updateIDocServerStatus(int idoc_server_id, String status) throws AuroraIDocException {
		String delete_sql = "update fnd_idoc_servers s set s.status='" + status + "',last_update_date=sysdate where s.server_id="
				+ idoc_server_id;
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();
			statement.executeUpdate(delete_sql);
			statement.close();
		} catch (SQLException e) {
			throw AuroraIDocException.createSQLException(delete_sql, e);
		} finally {
			closeStatement(statement);
		}
	}

	private int getFieldIndex(String segmenttyp, String fieldname) throws AuroraIDocException {
		String get_field_Index_sql = "select t.interface_field_index from fnd_idoc_fields t where t.segmenttyp ='" + segmenttyp
				+ "' and t.fieldname='" + fieldname + "'";
		Statement statement = null;
		ResultSet rs = null;
		int fieldIndex = -1;
		try {
			statement = dbConnection.createStatement();
			rs = statement.executeQuery(get_field_Index_sql);
			if (rs.next()) {
				fieldIndex = rs.getInt(1);
			} else {
				throw new AuroraIDocException(" execute sql:" + get_field_Index_sql + " failed.");
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw AuroraIDocException.createSQLException(get_field_Index_sql, e);
		} finally {
			closeResultSet(rs);
			closeStatement(statement);
		}
		return fieldIndex;
	}

	public List<IDocFile> fetchUnsettledIdocFiles(String program_id) throws AuroraIDocException {
		List<IDocFile> idocList = new LinkedList<IDocFile>();
		String get_HistoryIdocs_sql = "select i.idoc_file_id, i.server_id, i.file_path  from "
				+ " fnd_idoc_files i, fnd_idoc_servers s  where (i.sync_status is null or i.sync_status<>'DONE') "
				+ " and i.server_id = s.server_id" + " and s.program_id='" + program_id + "' order by i.idoc_file_id";
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = dbConnection.createStatement();
			rs = statement.executeQuery(get_HistoryIdocs_sql);
			while (rs.next()) {
				int idoc_id = rs.getInt(1);
				int server_id = rs.getInt(2);
				String file_path = rs.getString(3);
				File file = new File(file_path);
				if (!file.exists()) {
//					continue;
					throw new AuroraIDocException("file :" + file.getAbsolutePath() + " is not exits");
				}
				idocList.add(new IDocFile(file_path, idoc_id, server_id));
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw AuroraIDocException.createSQLException(get_HistoryIdocs_sql, e);
		} finally {
			closeResultSet(rs);
			closeStatement(statement);
		}
		return idocList;
	}

	public void close() {
		closeConnection();
	}

	public void rollback() {
		if (dbConnection == null)
			return;
		try {
			dbConnection.rollback();
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}

	public void commit() {
		if (dbConnection == null)
			return;
		try {
			dbConnection.commit();
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}
	public void disableAutoCommit() {
		if (dbConnection == null)
			return;
		try {
			dbConnection.setAutoCommit(false);
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}
	public void enableAutoCommit() {
		if (dbConnection == null)
			return;
		try {
			dbConnection.setAutoCommit(true);
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}

	public void closeConnection() {
		if (dbConnection == null)
			return;
		try {
			dbConnection.close();
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}

	public void closeResultSet(ResultSet rs) {
		if (rs == null)
			return;
		try {
			rs.close();
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}

	public void closeStatement(Statement stmt) {
		if (stmt == null)
			return;
		try {
			stmt.close();
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}
}
