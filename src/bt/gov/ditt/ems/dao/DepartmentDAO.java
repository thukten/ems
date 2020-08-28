package bt.gov.ditt.ems.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import bt.gov.ditt.ems.dto.DepartmentDTO;
import bt.gov.ditt.ems.dto.EmployeeDTO;
import bt.gov.ditt.ems.util.ConnectionManager;

public class DepartmentDAO {

	private static DepartmentDAO dao = null;
	
	public static DepartmentDAO getInstance() {
		if(dao == null)
			dao = new DepartmentDAO();
		return dao;
	}
	
	public List<DepartmentDTO> getDepartmentList() throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<DepartmentDTO> departmentList = new ArrayList<DepartmentDTO>();
		DepartmentDTO dto = null;
		
		try {
			conn = ConnectionManager.getConnection();
			pst = conn.prepareStatement(GET_DEPARTMENT_LIST);
			rs = pst.executeQuery();
			while(rs.next()) {
				dto = new DepartmentDTO();
				dto.setDepartmentId(rs.getInt("department_id"));
				dto.setDepartmentName(rs.getString("department_nm"));
				departmentList.add(dto);
			}
		} catch (Exception e) {
			throw new Exception();
		} finally {
			conn.close();
			pst.close();
			rs.close();
		}
		
		return departmentList;
	}
	
	public boolean addDepartment(DepartmentDTO dept) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean inserted = false;
		
		try {
			conn = ConnectionManager.getConnection();
			pst = conn.prepareStatement(INSERT_INTO_DEPARTMENT);
			pst.setString(1, dept.getDepartmentName());
			int count = pst.executeUpdate();
			if(count > 0)
				inserted = true;
		} catch (Exception e) {
			throw new Exception();
		} finally {
			conn.close();
			pst.close();
		}
		
		return inserted;
	}
	
	public DepartmentDTO getDepartmentDetail(int deptId) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		DepartmentDTO dto = new DepartmentDTO();
		
		try {
			conn = ConnectionManager.getConnection();
			pst = conn.prepareStatement(GET_DEPARTMENT_LIST + " WHERE a.`department_id`=?");
			pst.setInt(1, deptId);
			rs = pst.executeQuery();
			rs.first();
			
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
	
	public boolean updateDepartment(DepartmentDTO dept) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean updated = false;
		
		try {
			conn = ConnectionManager.getConnection();
			pst = conn.prepareStatement(UPDATE_DEPARTMENT);
			pst.setString(1, dept.getDepartmentName());
			pst.setInt(2, dept.getDepartmentId());
			int count = pst.executeUpdate();
			if(count > 0)
				updated = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			conn.close();
			pst.close();
		}
		
		return updated;
	}
	
	public boolean deleteDepartment(int deptId) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean deleted = false;
		
		try {
			conn = ConnectionManager.getConnection();
			pst = conn.prepareStatement(DELETE_DEPARTMENT);
			pst.setInt(1, deptId);
			int count = pst.executeUpdate();
			if(count > 0)
				deleted = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			conn.close();
			pst.close();
		}
		
		return deleted;
	}
	
	private static final String DELETE_DEPARTMENT = "DELETE FROM `department` WHERE `department_id` = ?";
	
	private static final String UPDATE_DEPARTMENT = "UPDATE `department` SET `department_nm` = ? WHERE `department_id` = ?";
	
	private static final String INSERT_INTO_DEPARTMENT = "INSERT INTO `department` (`department_nm`) VALUES (?);";
	
	private static final String GET_DEPARTMENT_LIST = "SELECT a.`department_id`, a.`department_nm` FROM department a";
}
