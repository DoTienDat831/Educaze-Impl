package com.example.student_management;

import com.example.student_management.dao.ClassDAO;
import com.example.student_management.dao.DepartmentDAO;
import com.example.student_management.dao.StudentDAO;
import com.example.student_management.dto.ClassRequest;
import com.example.student_management.dto.DepartmentRequest;
import com.example.student_management.dto.StudentRequest;
import com.example.student_management.entity.Class;
import com.example.student_management.entity.Department;
import com.example.student_management.entity.Student;
import com.example.student_management.mapper.ClassMapper;
import com.example.student_management.mapper.DepartmentMapper;
import com.example.student_management.mapper.StudentMapper;
import com.example.student_management.service.ClassService;
import com.example.student_management.service.DepartmentServiceImpl;
import com.example.student_management.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CrudServiceTest {
    private MemoryDepartmentDAO departmentDAO;
    private MemoryClassDAO classDAO;
    private MemoryStudentDAO studentDAO;
    private DepartmentServiceImpl departments;
    private ClassService classes;
    private StudentService students;

    @BeforeEach
    void setUp() {
        departmentDAO = new MemoryDepartmentDAO();
        studentDAO = new MemoryStudentDAO();
        classDAO = new MemoryClassDAO(departmentDAO, studentDAO);
        DepartmentMapper departmentMapper = new DepartmentMapper();
        ClassMapper classMapper = new ClassMapper(departmentMapper);
        StudentMapper studentMapper = new StudentMapper(departmentMapper, classMapper);
        departments = new DepartmentServiceImpl(departmentDAO, departmentMapper);
        classes = new ClassService(classDAO, departmentDAO, classMapper);
        students = new StudentService(studentDAO, departmentDAO, classDAO, studentMapper);
    }

    @Test
    void departmentAndClassCreateReadUpdateDeleteUseStableStatuses() {
        var department = departments.create(new DepartmentRequest("CS", "Computing", new BigDecimal("1200")));
        assertNotNull(department.id());
        assertEquals(department.id(), departments.findById(department.id()).id());

        var updatedDepartment = departments.update(department.id(),
                new DepartmentRequest("CS", "Computer Science", new BigDecimal("1500")));
        assertEquals("Computer Science", updatedDepartment.departmentName());
        assertStatus(HttpStatus.NOT_FOUND, () -> departments.update(999L,
                new DepartmentRequest("NO", "Missing", BigDecimal.ONE)));

        var studentClass = classes.createClass(new ClassRequest("CS-A", department.id()));
        assertEquals(department.id(), studentClass.department().id());
        assertEquals(studentClass.id(), classes.getClassById(studentClass.id()).id());
        assertEquals("CS-B", classes.updateClass(studentClass.id(),
                new ClassRequest("CS-B", department.id())).name());
        assertStatus(HttpStatus.NOT_FOUND, () -> classes.updateClass(999L,
                new ClassRequest("Missing", department.id())));
        assertStatus(HttpStatus.CONFLICT, () -> departments.deleteById(department.id()));

        classes.deleteClass(studentClass.id());
        departments.deleteById(department.id());
        assertNull(departments.findById(department.id()));
        assertStatus(HttpStatus.NOT_FOUND, () -> classes.deleteClass(studentClass.id()));
    }

    @Test
    void studentCrudPersistsClassAndClearsItWhenPutClassIdIsNull() {
        Department department = departmentDAO.save(new Department("SCI", "Science", new BigDecimal("1000")));
        classes.createClass(new ClassRequest("SCI-A", department.getId()));
        Class studentClass = classDAO.findClassByName("SCI-A");

        StudentRequest create = new StudentRequest("An", LocalDate.of(2005, 1, 1), "Female",
                LocalDate.of(2023, 9, 1), "an@example.edu", department.getId(), studentClass.getId());
        var created = students.createStudent(create);
        assertEquals("An", students.getStudentById(created.id()).name());
        assertEquals(studentClass.getId(), students.getStudentById(created.id()).studentClass().id());
        assertEquals(1, students.getAllStudents().size());
        assertStatus(HttpStatus.CONFLICT, () -> classes.deleteClass(studentClass.getId()));

        StudentRequest update = new StudentRequest("An Updated", LocalDate.of(2005, 1, 1), "Female",
                LocalDate.of(2023, 9, 1), "an@example.edu", department.getId(), null);
        var updated = students.updateStudent(created.id(), update);
        assertEquals("An Updated", updated.name());
        assertNull(updated.studentClass());
        assertEquals(created.studentCode(), updated.studentCode());

        students.deleteStudent(created.id());
        assertNull(students.getStudentById(created.id()));
        assertStatus(HttpStatus.NOT_FOUND, () -> students.updateStudent(created.id(), update));
        assertStatus(HttpStatus.NOT_FOUND, () -> students.deleteStudent(created.id()));
    }

    @Test
    void studentRejectsMissingOrCrossDepartmentClassIdsAsBadRequest() {
        Department first = departmentDAO.save(new Department("A", "A", BigDecimal.ONE));
        Department second = departmentDAO.save(new Department("B", "B", BigDecimal.ONE));
        Class classInSecond = classDAO.saveClass("B-1", second);
        StudentRequest mismatch = new StudentRequest("Student", null, null, null, null,
                first.getId(), classInSecond.getId());
        assertStatus(HttpStatus.BAD_REQUEST, () -> students.createStudent(mismatch));
        StudentRequest missingClass = new StudentRequest("Student", null, null, null, null,
                first.getId(), 999L);
        assertStatus(HttpStatus.BAD_REQUEST, () -> students.createStudent(missingClass));
    }

    private static void assertStatus(HttpStatus expected, org.junit.jupiter.api.function.Executable executable) {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, executable);
        assertEquals(expected, exception.getStatusCode());
    }

    private static final class MemoryDepartmentDAO implements DepartmentDAO {
        private final Map<Long, Department> departmentRows = new HashMap<>();
        private long nextDepartmentId = 1;
        private MemoryClassDAO classDAO;
        private MemoryStudentDAO studentDAO;

        void setReferences(MemoryClassDAO classDAO, MemoryStudentDAO studentDAO) {
            this.classDAO = classDAO;
            this.studentDAO = studentDAO;
        }

        @Override public List<Department> findAll() { return new ArrayList<>(departmentRows.values()); }
        @Override public Department findById(Long id) { return departmentRows.get(id); }
        @Override public Department save(Department department) {
            department.setId(nextDepartmentId++);
            departmentRows.put(department.getId(), department);
            return department;
        }
        @Override public Department update(Department department) {
            departmentRows.put(department.getId(), department);
            return department;
        }
        @Override public void deleteById(Long id) { departmentRows.remove(id); }
        @Override public boolean isReferenced(Long id) {
            return classDAO.rows().stream().anyMatch(row -> row.getDepartment() != null && id.equals(row.getDepartment().getId()))
                    || studentDAO.rows().stream().anyMatch(row -> row.getDepartment() != null && id.equals(row.getDepartment().getId()));
        }
    }

    private static final class MemoryClassDAO implements ClassDAO {
        private final Map<Long, Class> classRows = new HashMap<>();
        private final MemoryDepartmentDAO departmentDAO;
        private final MemoryStudentDAO studentDAO;
        private long nextClassId = 1;

        MemoryClassDAO(MemoryDepartmentDAO departmentDAO, MemoryStudentDAO studentDAO) {
            this.departmentDAO = departmentDAO;
            this.studentDAO = studentDAO;
            departmentDAO.setReferences(this, studentDAO);
        }

        @Override public Class save(Class studentClass) {
            studentClass.setId(nextClassId++);
            classRows.put(studentClass.getId(), studentClass);
            return studentClass;
        }
        Class saveClass(String name, Department department) {
            Class studentClass = new Class();
            studentClass.setName(name);
            studentClass.setDepartment(department);
            return save(studentClass);
        }
        Class findClassByName(String name) {
            return classRows.values().stream().filter(row -> name.equals(row.getName())).findFirst().orElse(null);
        }
        @Override public Class findById(Long id) { return classRows.get(id); }
        @Override public List<Class> findAll() { return new ArrayList<>(classRows.values()); }
        @Override public void update(Class studentClass) { classRows.put(studentClass.getId(), studentClass); }
        @Override public void delete(Long id) { classRows.remove(id); }
        @Override public boolean hasStudents(Long id) {
            return studentDAO.rows().stream().anyMatch(row -> row.getStudentClass() != null && id.equals(row.getStudentClass().getId()));
        }
        List<Class> rows() { return List.copyOf(classRows.values()); }
    }

    private static final class MemoryStudentDAO implements StudentDAO {
        private final Map<Long, Student> studentRows = new HashMap<>();
        private long nextStudentId = 1;
        @Override public Student save(Student student) {
            student.setId(nextStudentId++);
            studentRows.put(student.getId(), student);
            return student;
        }
        @Override public Student findById(Long id) { return studentRows.get(id); }
        @Override public List<Student> findAll() { return new ArrayList<>(studentRows.values()); }
        @Override public void update(Student student) { studentRows.put(student.getId(), student); }
        @Override public void delete(Long id) { studentRows.remove(id); }
        List<Student> rows() { return List.copyOf(studentRows.values()); }
    }
}
