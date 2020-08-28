package bt.gov.ditt.ems.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import bt.gov.ditt.ems.dto.EmployeeDTO;
import bt.gov.ditt.ems.dto.UserDTO;
import bt.gov.ditt.ems.util.ConnectionManager;

public class EMSDAO {
	
	private static EMSDAO dao = null;
	
	public static EMSDAO getInstance() {
		if(dao == null)
			dao = new EMSDAO();
		return dao;
	}

	public UserDTO validateLogin(String userId, String password) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UserDTO dto = new UserDTO();
		
		try {
			conn = ConnectionManager.getConnection();
			pst = conn.prepareStatement(VALIDATE_USER_DETAILS);
			pst.setString(1, userId);
			pst.setString(2, password);
			rs = pst.executeQuery();
			rs.first();
			
			if(rs.getInt("rowCount") > 0) {
				dto.setLoginId(userId);
				dto.setRole(rs.getString("role"));
				dto.setValid(true);
			} else {
				dto.setValid(false);
			}
		} catch (Exception e) {
			dto.setValid(false);
		} finally {
			conn.close();
			pst.close();
			rs.close();
		}
		
		return dto;
	}
	
	public List<EmployeeDTO> getEmployeeList() throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<EmployeeDTO> employeeList = new ArrayList<EmployeeDTO>();
		EmployeeDTO employee = null;
		
		try {
			conn = ConnectionManager.getConnection();
			pst = conn.prepareStatement(GET_USER_LIST);
			rs = pst.executeQuery();
			while(rs.next()) {
				employee = new EmployeeDTO();
				employee.setEmpId(rs.getInt("empid"));
				employee.setFirstName(rs.getString("firstname"));
				employee.setLastName(rs.getString("lastname"));
				employee.setDob(rs.getString("dob"));
				employee.setEmail(rs.getString("email"));
				employee.setDepartmentId(rs.getInt("department_id"));
				employee.setDepartmentName(rs.getString("department_nm"));
				
				employeeList.add(employee);
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			conn.close();
			pst.close();
			rs.close();
		}
		
		return employeeList;
	}
	
	public boolean addEmployee(EmployeeDTO employee) throws Exception {
		Connection conn = null;
		CallableStatement cal = null;
		boolean inserted = false;
		
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			java.util.Date dobDate = df.parse(employee.getDob());
			String dobStr = sdf.format(dobDate);
			
			conn = ConnectionManager.getConnection();
			cal = conn.prepareCall("{call addEmp_sp(?, ?, ?, ?, ?)}");
			cal.setString(1, employee.getFirstName());
			cal.setString(2, employee.getLastName());
			cal.setString(3, dobStr);
			cal.setString(4, employee.getEmail());
			cal.setInt(5, employee.getDepartmentId());
			int count = cal.executeUpdate();
			
			if(count > 0)
				inserted = true;
		} catch (Exception e) {
			e.printStackTrace();
			inserted = false;
			throw new Exception();
		} finally {
			conn.close();
			cal.close();
		}
		
		return inserted;
	}
	
	public EmployeeDTO getEmployeeDetail(int empId) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		EmployeeDTO dto = new EmployeeDTO();
		
		try {
			conn = ConnectionManager.getConnection();
			pst = conn.prepareStatement(GET_USER_LIST + " WHERE a.`empid`=?");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
			rs.first();
			
			dto.setEmpId(rs.getInt("empid"));
			dto.setFirstName(rs.getString("firstname"));
			dto.setLastName(rs.getString("lastname"));
			dto.setDob(rs.getString("dob"));
			dto.setEmail(rs.getString("email"));
			dto.setDepartmentId(rs.getInt("department_id"));
			dto.setDepartmentName(rs.getString("department_nm"));
		} catch (Exception e) {
			throw new Exception();
		} finally {
			conn.close();
			pst.close();
			rs.close();
		}
		
		return dto;
	}
	
	public boolean updateEmployeeDetails(EmployeeDTO employee) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean updated = false;
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			java.util.Date dobDate = df.parse(employee.getDob());
			String dobStr = sdf.format(dobDate);
			
			conn = ConnectionManager.getConnection();
			pst = conn.prepareStatement(UPDATE_EMPLOYEE_DETAILS);
			pst.setString(1, employee.getFirstName());
			pst.setString(2, employee.getLastName());
			pst.setString(3, dobStr);
			pst.setString(4, employee.getEmail());
			pst.setInt(5, employee.getDepartmentId());
			pst.setInt(6, employee.getEmpId());
			int count = pst.executeUpdate();
			if(count > 0)
				updated = true;
		} catch (Exception e) {
			throw new Exception();
		} finally {
			conn.close();
			pst.close();
		}
		
		return updated;
	}
	
	public boolean deleteEmployee(int empId) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean deleted = false;
		
		try {
			conn = ConnectionManager.getConnection();
			pst = conn.prepareStatement(DELETE_FROM_LOGIN_MASTER);
			pst.setInt(1, empId);
			int count = pst.executeUpdate();
			
			if(count > 0) {
				pst.close();
				
				pst = conn.prepareStatement(DELETE_EMPLOYEE);
				pst.setInt(1, empId);
				count = pst.executeUpdate();
				if(count > 0)
					deleted = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			conn.close();
			pst.close();
		}
		
		return deleted;
	}
	
	private static final String DELETE_FROM_LOGIN_MASTER = "DELETE FROM login_master WHERE userid=?";
	
	private static final String DELETE_EMPLOYEE = "DELETE FROM `employees` WHERE `empid` = ?";
	
	private static final String UPDATE_EMPLOYEE_DETAILS = "UPDATE "
			+ "  `employees` "
			+ "SET "
			+ "  `firstname` = ?, "
			+ "  `lastname` = ?, "
			+ "  `dob` = ?, "
			+ "  `email` = ?, "
			+ "  `department_id` = ? "
			+ "WHERE `empid` = ?";
	
	private static final String VALIDATE_USER_DETAILS = "SELECT COUNT(*) rowCount, a.role FROM login_master a WHERE a.userid=? AND a.password=?";
	
	private static final String GET_USER_LIST = "SELECT "
			+ "  a.`empid`, "
			+ "  a.`firstname`, "
			+ "  a.`lastname`, "
			+ "  a.`dob`, "
			+ "  a.`email`, "
			+ "  a.`department_id`, "
			+ "  b.`department_nm` "
			+ "FROM "
			+ "  `employees` a "
			+ "  LEFT JOIN department b "
			+ "    ON a.`department_id` = b.`department_id`";
}
