package EmployeePayRollService.JDBC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

public class EmployeePayRollServiceJdbcTest {

	private static String HOME = System.getProperty("user.home");
	private static String PLAY_WITH_NIO = "TempPlayGround";

	@Test
	public void givenPathWhenCheckedThenConfirm() throws IOException {

		// Checks file exists
		Path homePath = Paths.get(HOME);
		Assert.assertTrue(Files.exists(homePath));

		// Deletes files checks file not exists
		Path playPath = Paths.get(HOME + "/" + PLAY_WITH_NIO);
		if (Files.exists(playPath))
			FileUtils.deleteFiles(playPath.toFile());
		Assert.assertTrue(Files.notExists(playPath));

		// create Directory
		Files.createDirectories(playPath);
		Assert.assertTrue(Files.exists(playPath));

		// Creates file
		IntStream.range(1, 10).forEach(cntr -> {
			Path tempFile = Paths.get(playPath + "/temp" + cntr);
			Assert.assertTrue(Files.notExists(tempFile));
			try {
				Files.createFile(tempFile);
			} catch (IOException e) {
			}
			Assert.assertTrue(Files.exists(tempFile));
		});

		// lists directories and files with extension
		Files.list(playPath).filter(Files::isRegularFile).forEach(System.out::println);
		Files.newDirectoryStream(playPath).forEach(System.out::println);
		Files.newDirectoryStream(playPath, path -> path.toFile().isFile() && path.toString().startsWith("temp"))
				.forEach(System.out::println);
	}

	@Test
	public void givenADirectoryWhenWatchedListsAllTheActivities() throws IOException {
		Path dir = Paths.get(HOME + "/" + PLAY_WITH_NIO);
		Files.list(dir).filter(Files::isRegularFile).forEach(System.out::println);
		new Java8WatchServiceExample(dir).processEvents();
	}

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchNumberOfEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData(1, "Jeff Becos", 100000.0),
				new EmployeePayrollData(2, "Bill Gates", 200000.0),
				new EmployeePayrollData(3, "Mark ZukerBerg", 300000.0) };
		EmployeePayRollServiceJdbc empPayrollService;
		empPayrollService = new EmployeePayRollServiceJdbc(Arrays.asList(arrayOfEmps));
		empPayrollService.writeEmployeePayrollData(EmployeePayRollServiceJdbc.IOService.FILE_IO);
		Assert.assertEquals(3, empPayrollService.countEntries(EmployeePayRollServiceJdbc.IOService.FILE_IO));
	}

	@Test
	public void givenEmployeePayrollInDB_WhenRetrived_ShouldMatchEmployeeCount() throws SQLException {
		EmployeePayRollServiceJdbc employeePayrollService = new EmployeePayRollServiceJdbc();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService
				.readEmployeePayrollServiceData(EmployeePayRollServiceJdbc.IOService.DB_IO);
		System.out.println(employeePayrollData.size());
		Assert.assertEquals(11, employeePayrollData.size());
	}
	
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() throws SQLException {
		EmployeePayRollServiceJdbc employeePayrollService = new EmployeePayRollServiceJdbc();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService
				.readEmployeePayrollServiceData(EmployeePayRollServiceJdbc.IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("Terisa", 300000.00);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
		Assert.assertTrue(result);
	}
	
	@Test
	public void givenDateRange_WhenRetirieved_ShouldMatchEmployeeCount() throws SQLException {
		EmployeePayRollServiceJdbc employeePayrollService = new EmployeePayRollServiceJdbc();
		employeePayrollService.readEmployeePayrollServiceData(EmployeePayRollServiceJdbc.IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2018,01, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService
				.readEmployeePayrollForDateRange(EmployeePayRollServiceJdbc.IOService.DB_IO,startDate,endDate);
		System.out.println(employeePayrollData.size());
		Assert.assertEquals(13, employeePayrollData.size());		
	}
	
	@Test
	public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue() throws SQLException {
		EmployeePayRollServiceJdbc employeePayrollService = new EmployeePayRollServiceJdbc();
		employeePayrollService.readEmployeePayrollServiceData(EmployeePayRollServiceJdbc.IOService.DB_IO);
		Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(EmployeePayRollServiceJdbc.IOService.DB_IO);
		System.out.println(averageSalaryByGender.get("M"));
		Assert.assertTrue(averageSalaryByGender.get("M").equals(2600000.0) &&
				averageSalaryByGender.get("F").equals(300000.00));
	}
	
	@Test
	public void givenNewEmployee_WhenAdded_SouldSyncWithDB() throws SQLException {
		EmployeePayRollServiceJdbc employeePayrollService = new EmployeePayRollServiceJdbc();
		employeePayrollService.readEmployeePayrollServiceData(EmployeePayRollServiceJdbc.IOService.DB_IO);
		employeePayrollService.addEmployeeToPayroll("Mark", 5000000.00,LocalDate.now(), "M");
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
		Assert.assertTrue(result);
	}	
}


