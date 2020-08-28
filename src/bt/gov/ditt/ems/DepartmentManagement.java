package bt.gov.ditt.ems;

import java.util.List;
import java.util.Scanner;
import bt.gov.ditt.ems.dao.DepartmentDAO;
import bt.gov.ditt.ems.dto.DepartmentDTO;
import bt.gov.ditt.ems.dto.UserDTO;
import bt.gov.ditt.ems.util.ConsoleTableFormatter;

public class DepartmentManagement {
	
	private static Scanner d = new Scanner(System.in);
	private static DepartmentManagement dept = null;
	
	public static DepartmentManagement getInstance() {
		if(dept == null)
			dept = new DepartmentManagement();
		return dept;
	}
	
	public void manageDepartments(UserDTO user) throws Exception {
		List<DepartmentDTO> departmentList = DepartmentDAO.getInstance().getDepartmentList();
		
		ConsoleTableFormatter tl = new ConsoleTableFormatter(2, "Department ID", "Department Name").sortBy(0).withUnicode(true);
		departmentList.forEach(element -> tl.addRow(Integer.toString(element.getDepartmentId()), element.getDepartmentName()));
		tl.print();
		
		System.out.println("Please select an option:");
		System.out.println("1. Add New Department");
		System.out.println("2. Edit Department");
		System.out.println("3. Delete a Department");
		System.out.println("0. Back");
		System.out.print("Please enter an option: ");
		int option = d.nextInt();
		
		switch (option) {
		case 0:
			EMSApplication.loginSuccess(user);
			break;
		case 1:
			addNewDepartment(user);
			break;
		case 2:
			editDepartment(user);
			break;
		case 3:
			deleteEmployee(user);
			break;
		default:
			System.out.println("Invalid option, please try again");
			manageDepartments(user);
			break;
		}
	}
	
	private void addNewDepartment(UserDTO user) throws Exception {
		DepartmentDTO dept = new DepartmentDTO(); 
		System.out.print("Enter department name: ");
		dept.setDepartmentName(d.next());
		
		boolean isInserted = DepartmentDAO.getInstance().addDepartment(dept);
		if(isInserted) {
			System.out.print("Department successfully added. Add another department [Y/N] ");
			String confirm = d.next();
			
			if(confirm.equals("Y"))
				addNewDepartment(user);
			else
				manageDepartments(user);
		}
	}
	
	private void editDepartment(UserDTO user) throws Exception {
		DepartmentDTO dept = new DepartmentDTO();
		String confirmFlag = "";
		
		try {
			System.out.print("Please enter a department id to edit: ");
			int deptId = d.nextInt();
			DepartmentDTO dto = DepartmentDAO.getInstance().getDepartmentDetail(deptId);
			
			dept.setDepartmentId(deptId);
			
			System.out.print("Change Department Name? [Y/N] ");
			confirmFlag = d.next();
			if(confirmFlag.equals("Y")) {
				System.out.print("Enter New Department Name: ");
				dept.setDepartmentName(d.next());
			} else {
				dept.setDepartmentName(dto.getDepartmentName());
			}
			
			System.out.println("You updated following details, do you really want to update it?");
			System.out.println("-------------------------------------------------");
			System.out.println("Department ID: " + dept.getDepartmentId());
			System.out.println("Department Name: " + dept.getDepartmentName());
			System.out.println("-------------------------------------------------");
			System.out.print("Please enter Y to add and N to cancel: ");
			String confirm = d.next();
			
			if(confirm.equals("Y")) {
				boolean isUpdated = DepartmentDAO.getInstance().updateDepartment(dept);
				if(isUpdated) {
					System.out.print("Department successfully updated. Update another department [Y/N] ");
					confirm = d.next();
					
					if(confirm.equals("Y"))
						editDepartment(user);
					else
						manageDepartments(user);
				}
			} else {
				editDepartment(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		}
	}
	
	private void deleteEmployee(UserDTO user) throws Exception {
		try {
			System.out.print("Please enter a department id to delete: ");
			int deptId = d.nextInt();
			
			DepartmentDTO dept = DepartmentDAO.getInstance().getDepartmentDetail(deptId);
			System.out.println("Do you really want to delete the follow department?");
			System.out.println("-------------------------------------------------");
			System.out.println("Department ID: " + dept.getDepartmentId());
			System.out.println("Department: " + dept.getDepartmentName());
			System.out.println("-------------------------------------------------");
			System.out.print("Please enter Y to delete and N to cancel: ");
			String confirm = d.next();
			
			if(confirm.equals("Y")) {
				boolean isDeleted = DepartmentDAO.getInstance().deleteDepartment(deptId);
				if(isDeleted) {
					System.out.println("Selected department successfully deleted");
					manageDepartments(user);
				}
			} else {
				manageDepartments(user);
			}
		} catch (Exception e) {
			throw new Exception();
		}
	}
}
