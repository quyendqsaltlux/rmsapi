package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saltlux.ctv.tranSS.model.Role;
import saltlux.ctv.tranSS.model.RoleName;
import saltlux.ctv.tranSS.model.User;
import saltlux.ctv.tranSS.payload.employee.Employee;
import saltlux.ctv.tranSS.repository.user.RoleRepository;
import saltlux.ctv.tranSS.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EmployeeService {

    private final UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    public EmployeeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Employee> findPM(String keyWord) {
        Optional<Role> roleOptional = roleRepository.findByName(RoleName.ROLE_PM);
        if (!roleOptional.isPresent()) {
            return new ArrayList<>();
        }
        List<User> optionalUser = userRepository
                .findTop20ByUsernameLikeAndRolesContainsOrderByUsername("%" + keyWord + "%", roleOptional.get());
        List<Employee> employees = new ArrayList<>();
        for (User user : optionalUser) {
            Employee employee = new Employee();
            BeanUtils.copyProperties(user, employee);
            employees.add(employee);
        }
        return employees;
    }

}