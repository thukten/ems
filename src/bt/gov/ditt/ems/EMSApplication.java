package bt.gov.ditt.ems;

import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import org.apache.log4j.PropertyConfigurator;
import bt.gov.ditt.ems.dao.EMSDAO;
import bt.gov.ditt.ems.dto.UserDTO;

public class EMSApplication {
	
	private static Scanner d = new Scanner(System.in);
	private static String userId = null, password = null;

	public static void main(String[] args) throws Exception {
		Properties log4jProperties=new Properties();
		InputStream logConfigurationInputStream = EMSApplication.class.getClassLoader().getResourceAsStream("log4j.properties");
		log4jProperties.load(logConfigurationInputStream);
		PropertyConfigurator.configure(log4jProperties);
		EMSApplication.login();
	}
	
	public static void login() throws Exception {
		System.out.println("***************** Employee Management System :: Login ********************");
		System.out.print("Please enter your user id: ");
		userId = d.next();
		System.out.print("Please enter your password: ");
		password = d.next();
		
		try {
			if(null == userId || null == password) {
				login();
			} else {
				UserDTO user = EMSDAO.getInstance().validateLogin(userId, password);
				
				if(user.isValid()) {
					EMSApplication.loginSuccess(user);
				} else {
					System.out.println("INVALID LOGIN CREDENTIALS, PLEASE TRY AGAIN");
					login();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception occurred please try again");
			login();
		}
	} 

	public static void loginSuccess(UserDTO user) throws Exception {
		System.out.println("Welcome to Employee Management System");
		System.out.println("---------------------------------------------");
		
		if(user.getRole().equals("ADMIN")) {
			System.out.println("Please select an option:");
			System.out.println("1. Manage Employees");
			System.out.println("2. Manage Departments");
			System.out.println("3. Manage Regulations");
			System.out.println("0. Logout");
			System.out.print("Please enter an option between 1-3: ");
			
			int option = d.nextInt();
			switch (option) {
			case 0:
				login();
				break;
			case 1:
				EmployeeManagement.getInstance().manageEmployees(user);
				break;
			case 2:
				DepartmentManagement.getInstance().manageDepartments(user);
				break;
			case 3:
				manageRegulations(user);
				break;
			default:
				System.out.println("Invalid option, please try again");
				loginSuccess(user);
			}
		} else {
			System.out.println("Please select an option:");
			System.out.println("1. Manage Employees");
			System.out.println("2. Manage Departments");
			System.out.println("3. Manage Regulations");
		}
	}
	
	private static void manageRegulations(UserDTO user) {
		System.out.println("Inside management regulations");
	}
}
