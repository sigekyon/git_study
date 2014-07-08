package user.parts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import user.bean.RegistrantInfo;

public class RegInfDAO {
	final static String DRIVER   = "org.gjt.mm.mysql.Driver";
	final static String SERVER   = "172.16.110.206";
	final static String DBNAME   = "task";
	final static String URL = "jdbc:mysql://" + SERVER + "/" + DBNAME + "?useUnicode=true&characterEncoding=UTF-8";
	final static String USER     = "root";
	final static String PASSWORD = "root";

	Connection con;
	Statement stmt;
	ResultSet rs;
	
	public RegInfDAO(){
		try {
			Class.forName (DRIVER);
			this.con = DriverManager.getConnection(URL, USER, PASSWORD);
			this.stmt = con.createStatement ();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insert(String regId, String regName,String regAge){
		try {
			this.stmt.executeUpdate("INSERT INTO task.registrants VALUES('" + regId + "','" + regName +"','" + regAge + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void update(String targetId, String regName, String regAge) {
		try {
			this.stmt.executeUpdate("UPDATE task.registrants SET "
					+ "registrant_name='" + regName + "',"
					+ "registrant_age='" + regAge +"'"
					+ " WHERE registrant_id='" + targetId + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void delete(String targetId) {
		try {
			this.stmt.executeUpdate("DELETE FROM task.registrants WHERE registrant_id='" + targetId + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			if(this.rs!=null){
				this.rs.close();
			}
			this.stmt.close();
			this.con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<RegistrantInfo> getReglist() {
		RegistrantInfo regInfo;
		ArrayList<RegistrantInfo> arrayRegInfo = new ArrayList<RegistrantInfo>();
		rs = exec("SELECT * FROM registrants");
		try {
			while(rs.next()){
				regInfo = new RegistrantInfo(rs.getString("registrant_id"),
											 rs.getString("registrant_name"),
											 rs.getString("registrant_age"));
				arrayRegInfo.add(regInfo);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrayRegInfo;
	}
	public String getNextId() {
		String nextId = null;
		ResultSet rs = exec("SELECT MAX(registrant_id) as nextId FROM task.registrants");
		try {
			while(rs.next()){
				nextId = rs.getString("nextId");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(nextId == null){
			nextId = "001";
		} else{
			nextId = String.format("%1$03d", Integer.parseInt(nextId) + 1);
		}
		return nextId;
	}
	
	private ResultSet exec(String sql){
		ResultSet rs = null;
		try {
			rs = this.stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

}