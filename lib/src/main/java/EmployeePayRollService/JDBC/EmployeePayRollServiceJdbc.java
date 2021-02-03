package EmployeePayRollService.JDBC;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayRollServiceJdbc {

	// Enum method to put constants
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	// Array list to put employee details
	private List<EmployeePayrollData> employeePayrollList;

	// Employee pay roll service Constructor
	public EmployeePayRollServiceJdbc(List<EmployeePayrollData> employeePayrollList) {
		this.employeePayrollList = employeePayrollList;
	}

	//Constructor for testcase givenEmployeePayrollInDB_WhenRetrived_ShouldMatchEmployeeCount()
	public EmployeePayRollServiceJdbc() {
	}

	// Read method to take employee data from console
	private void readEmployeePayrollData(Scanner consoleInputReader) {
		System.out.print("Enter Employee ID: ");
		int id = consoleInputReader.nextInt();
		System.out.print("Enter Employee Name: ");
		String name = consoleInputReader.next();
		System.out.print("Enter Employee Salary: ");
		double salary = consoleInputReader.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}

	// Write method to print employee data on console
	public void writeEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO))
			System.out.println("\nWriting Payroll to Console\n" + employeePayrollList);
		else if (ioService.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOService().writeData(employeePayrollList);

	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			return new EmployeePayrollFileIOService().countEntries();
		return 0;
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.DB_IO))
			this.employeePayrollList = new EmployeePayrollDBService().readData();
		return this.employeePayrollList;
	}
}
