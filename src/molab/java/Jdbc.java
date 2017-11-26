package molab.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import molab.java.entity.District;
import molab.java.entity.Proxy;
import molab.java.util.Status;

public class Jdbc {
	
	private static final Logger log = Logger.getLogger(Jdbc.class.getName());
	
	private Connection conn = null;
	private Statement stmt = null;
	
	public Jdbc(String host, String username, String password) {
		String url = "jdbc:mysql://" + host + ":3306/AST?useUnicode=true&amp;characterEncoding=UTF-8";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, username, password);
			stmt = conn.createStatement();
		} catch (ClassNotFoundException e) {
			log.severe(e.getMessage());
		} catch (SQLException e) {
			log.severe(e.getMessage());
		}
		log.info("Database connection established suceessfully.");
	}
	
	public boolean exist(String ip, Integer port) {
		String sql = String.format("SELECT COUNT(*) FROM PROXY WHERE IP='%s' AND PORT='%d'", ip, port);
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if(rs.getInt(1) == 0) {
					return false;
				} else {
					return true;
				}
			}
		} catch (SQLException e) {
			log.severe(e.getMessage());
		}
		return false;
	}
	
	public int saveProxy(String ip, Integer port, 
			Integer districtCode, String districtName) {
		String sql = String.format("INSERT INTO PROXY(IP,PORT,DISTRICT_CODE,DISTRICT_NAME,ISUSED,TIME) VALUES('%s',%d,'%s','%s',%d,%tQ)", 
				ip, port, districtCode, districtName, Status.Common.FALSE.getInt(), System.currentTimeMillis());
		try {
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			log.severe(e.getMessage());
		}
		return 0;
	}
	
	public static String parseSql(String ip, Integer port, 
			Integer districtCode, String districtName) {
		return String.format("INSERT INTO PROXY(IP,PORT,DISTRICT_CODE,DISTRICT_NAME,ISUSED,TIME) VALUES('%s',%d,'%s','%s',%d,%tQ)", 
				ip, port, districtCode, districtName, Status.Common.FALSE.getInt(), System.currentTimeMillis());
	}
	
	public int[] batchUpdate(List<String> sqls) {
		try {
			if(sqls.size() > 0) {
				conn.setAutoCommit(false);
				for(String sql : sqls) {
					stmt.addBatch(sql);
				}
				return stmt.executeBatch();
			}
		} catch (SQLException e) {
			log.severe(e.getMessage());
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				log.severe(e.getMessage());
			}
		}
		return null;
	}
	
	public Integer getCount() {
		String sql = "SELECT COUNT(*) FROM PROXY WHERE ISUSED=0";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			log.severe(e.getMessage());
		}
		return 0;
	}
	
	public List<Proxy> getProxy(Integer index, Integer n) {
		String sql = "SELECT * FROM PROXY WHERE ISUSED=0 ORDER BY TIME ASC LIMIT " + index + "," + n;
		try {
			ResultSet rs = stmt.executeQuery(sql);
			List<Proxy> pl = new ArrayList<Proxy>();
			while (rs.next()) {
				Proxy p = new Proxy(rs.getInt("ID"), rs.getString("IP"),
						rs.getInt("PORT"), rs.getInt("ISUSED"),
						rs.getLong("TIME"));
				pl.add(p);
			}
			return pl;
		} catch (SQLException e) {
			log.severe(e.getMessage());
		}
		return null;
	}
	
	public int updateTime(List<Integer> ids) {
		String sql = "UPDATE PROXY SET TIME=" + System.currentTimeMillis() + " WHERE ID IN (" + parse(ids) + ")";
		try {
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			log.severe(e.getMessage());
		}
		return 0;
	}
	
	public int updateIsused(List<Integer> ids) {
		String sql = "UPDATE PROXY SET ISUSED=2 WHERE ID IN (" + parse(ids) + ")";
		try {
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			log.severe(e.getMessage());
		}
		return 0;
	}
	
	private String parse(List<Integer> ids) {
		StringBuffer sb = new StringBuffer();
		for(Integer id : ids) {
			sb.append(String.valueOf(id)).append(",");
		}
		if(sb != null && sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	public List<District> findAllDistrict() {
		String sql = "SELECT * FROM DISTRICT";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			List<District> dl = new ArrayList<District>();
			while (rs.next()) {
				District d = new District(rs.getInt("ID"), rs.getInt("CODE"),
						rs.getString("NAME"), rs.getString("SHORT"));
				dl.add(d);
			}
			return dl;
		} catch (SQLException e) {
			log.severe(e.getMessage());
		}
		return null;
	}
	
	public void close() throws SQLException {
		if(stmt != null) {
			stmt.close();
		}
		if(conn != null) {
			conn.close();
		}
	}

}
