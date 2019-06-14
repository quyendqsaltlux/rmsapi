package saltlux.ctv.tranSS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import saltlux.ctv.tranSS.payload.employee.Employee;
import saltlux.ctv.tranSS.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/findUserByUsernameLike")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public List<Employee> search(@RequestParam(value = "keyword") String keyWord) {
        return employeeService.findPM(keyWord);
    }
}
