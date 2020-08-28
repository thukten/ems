package bt.gov.ditt.ems;

import java.util.List;
import java.util.Scanner;

import bt.gov.ditt.ems.dao.DepartmentDAO;
import bt.gov.ditt.ems.dao.EMSDAO;
import bt.gov.ditt.ems.dto.DepartmentDTO;
import bt.gov.ditt.ems.dto.EmployeeDTO;
import bt.gov.ditt.ems.dto.UserDTO;
import bt.gov.ditt.ems.util.ConsoleTableFormatter;

public class EmployeeManagement {
	
	private static Scanner d = new Scanner(System.in);
	private static EmployeeManagement employeeManagement = null;
	
	public static EmployeeManagement getInstance() {
		if(employeeManagement == null)
			employeeManagement = new EmployeeManagement();
		return employeeManagement;
	}
	
	public void manageEmployees(UserDTO user) throws Exception {
		List<EmployeeDTO> employeeList = EMSDAO.getInstance().getEmployeeList();
		
		ConsoleTableFormatter tl = new ConsoleTableFormatter(6, "Employee ID", "First Name", "Last Name", "DOB", "Email", "Department").sortBy(0).withUnicode(true);
		employeeList.forEach(element -> tl.addRow(Integer.toString(element.getEmpId()), element.getFirstName(), element.getLastName(), element.getDob(), element.getEmail(), element.getDepartmentName()));
		tl.print();
		
		System.out.println("Please select an option:");
		System.out.println("1. Add New Employee");
		System.out.println("2. Edit An Employee");
		System.out.println("3. Delete An Employee");
		System.out.println("0. Back");
		System.out.print("Please enter an option: ");
		int option = d.nextInt();
		
		switch (option) {
		case 0:
			EMSApplication.loginSuccess(user);
			break;
		case 1:
			addNewEmployee(user);
			break;
		case 2:
			editEmployee(user);
			break;
		case 3:
			deleteEmployee(user);
			break;
		default:
			System.out.println("Invalid option, please try again");
			manageEmployees(user);
			break;
		}
	}
	
	private void addNewEmployee(UserDTO user) throws Exception {
		EmployeeDTO employee = new EmployeeDTO(); 
		System.out.print("Enter first name: ");
		employee.setFirstName(d.next());
		System.out.print("Enter last name: ");
		employee.setLastName(d.next());
		System.out.print("Enter date of birth (dd-mm-yyyy): ");
		employee.setDob(d.next());
		System.out.print("Enter email id: ");
		employee.setEmail(d.next());
		System.out.println("Select a department");
		List<DepartmentDTO> departmentList = DepartmentDAO.getInstance().getDepartmentList();
		for(DepartmentDTO dept : departmentList) {
			System.out.println(dept.getDepartmentId()+"."+dept.getDepartmentName());
		}
		System.out.print("Please select department: ");
		employee.setDepartmentId(d.nextInt());
		
		System.out.println("You entered following details, do you really want to add it?");
		System.out.println("-------------------------------------------------");
		System.out.println("First Name: " + employee.getFirstName());
		System.out.println("Last Name: " + employee.getLastName());
		System.out.println("DOB: " + employee.getDob());
		System.out.println("Email: " + employee.getEmail());
		System.out.println("Department: " + employee.getDepartmentId());
		System.out.println("-------------------------------------------------");
		System.out.print("Please enter Y to add and N to cancel: ");
		String confirm = d.next();
		
		if(confirm.equals("Y")) {
			boolean isInserted = EMSDAO.getInstance().addEmployee(employee);
			if(isInserted) {
				System.out.print("Employee successfully added. Add another employee [Y/N] ");
				confirm = d.next();
				
				if(confirm.equals("Y"))
					addNewEmployee(user);
				else
					manageEmployees(user);
			}
		} else {
			addNewEmployee(user);
		}
	}
	
	private void editEmployee(UserDTO user) throws Exception {
		EmployeeDTO employee = new EmployeeDTO();
		String confirmFlag = "";
		
		try {
			System.out.print("Please enter an employee id to edit: ");
			int empId = d.nextInt();
			EmployeeDTO dto = EMSDAO.getInstance().getEmployeeDetail(empId);
			
			employee.setEmpId(dto.getEmpId());
			
			System.out.print("Change First Name? [Y/N] ");
			confirmFlag = d.next();
			if(confirmFlag.equals("Y")) {
				System.out.print("Enter New First Name: ");
				employee.setFirstName(d.next());
			} else {
				employee.setFirstName(dto.getFirstName());
			}
			
			System.out.print("Change Last Name? [Y/N] ");
			confirmFlag = d.next();
			if(confirmFlag.equals("Y")) {
				System.out.print("Enter New Last Name: ");
				employee.setLastName(d.next());
			} else {
				employee.setFirstName(dto.getLastName());
			}
			
			System.out.print("Change Date of Birth? [Y/N] ");
			confirmFlag = d.next();
			if(confirmFlag.equals("Y")) {
				System.out.print("Enter New DOB: ");
				employee.setDob(d.next());
			} else {
				employee.setDob(dto.getDob());
			}
			
			System.out.print("Change Email ID? [Y/N] ");
			confirmFlag = d.next();
			if(confirmFlag.equals("Y")) {
				System.out.print("Enter New Email ID: ");
				employee.setEmail(d.next());
			} else {
				employee.setEmail(dto.getEmail());
			}
			
			System.out.print("Change Department? [Y/N] ");
			confirmFlag = d.next();
			if(confirmFlag.equals("Y")) {
				List<DepartmentDTO> departmentList = DepartmentDAO.getInstance().getDepartmentList();
				for(DepartmentDTO dept : departmentList) {
					System.out.println(dept.getDepartmentId()+"."+dept.getDepartmentName());
				}
				System.out.print("Please select a new department: ");
				employee.setDepartmentId(d.nextInt());
			} else {
				employee.setDepartmentId(dto.getDepartmentId());
			}
			
			System.out.println("You updated following details, do you really want to update it?");
			System.out.println("-------------------------------------------------");
			System.out.println("Employee ID: " + employee.getEmpId());
			System.out.println("First Name: " + employee.getFirstName());
			System.out.println("Last Name: " + employee.getLastName());
			System.out.println("DOB: " + employee.getDob());
			System.out.println("Email: " + employee.getEmail());
			System.out.println("Department: " + employee.getDepartmentId());
			System.out.println("-------------------------------------------------");
			System.out.print("Please enter Y to add and N to cancel: ");
			String confirm = d.next();
			
			if(confirm.equals("Y")) {
				boolean isUpdated = EMSDAO.getInstance().updateEmployeeDetails(employee);
				if(isUpdated) {
					System.out.print("Employee details successfully updated. Update another employee [Y/N] ");
					confirm = d.next();
					
					if(confirm.equals("Y"))
						editEmployee(user);
					else
						manageEmployees(user);
				}
			} else {
				editEmployee(user);
			}
		} catch (Exception e) {
			throw new Exception();
		}
	}
	
	private void deleteEmployee(UserDTO user) throws Exception {
		try {
			System.out.print("Please enter an employee id to delete: ");
			int empId = d.nextInt();
			
			EmployeeDTO employee = EMSDAO.getInstance().getEmployeeDetail(empId);
			System.out.println("Do you really want to delete the follow employee?");
			System.out.println("-------------------------------------------------");
			System.out.println("Employee ID: " + employee.getEmpId());
			System.out.println("First Name: " + employee.getFirstName());
			System.out.println("Last Name: " + employee.getLastName());
			System.out.println("DOB: " + employee.getDob());
			System.out.println("Email: " + employee.getEmail());
			System.out.println("Department: " + employee.getDepartmentId());
			System.out.println("-------------------------------------------------");
			System.out.print("Please enter Y to delete and N to cancel: ");
			String confirm = d.next();
			
			if(confirm.equals("Y")) {
				boolean isDeleted = EMSDAO.getInstance().deleteEmployee(empId);
				if(isDeleted) {
					System.out.println("Selected employee successfully deleted");
					manageEmployees(user);
				}
			} else {
				manageEmployees(user);
			}
		} catch (Exception e) {
			throw new Exception();
		}
	}
}
