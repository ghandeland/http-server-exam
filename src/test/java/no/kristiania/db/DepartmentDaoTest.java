package no.kristiania.db;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class DepartmentDaoTest {
    private DepartmentDao departmentDao;

    public static Department sampleDepartment() {
        String[] options = {"HR", "IT", "Marketing", "Management", "Engineering", "Sales"};
        Random random = new Random();
        return new Department(options[random.nextInt(options.length)]);
    }

    @BeforeEach
    void setup() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        departmentDao = new DepartmentDao(dataSource);
    }

    @Test
    void shouldRetrieveSavedDepartment() throws SQLException {

        Department departmentSample = sampleDepartment();
        departmentDao.insert(departmentSample);

        Department departmentRetrieved = departmentDao.retrieve(departmentSample.getId());

        assertThat(departmentSample).isNotEqualTo(departmentRetrieved);
        assertThat(departmentSample.getName()).isEqualTo(departmentRetrieved.getName());
    }

    @Test
    void shouldRetrieveDepartmentList() throws SQLException {
        Department sampleDepartment1 = sampleDepartment();
        Department sampleDepartment2 = sampleDepartment();
        Department sampleDepartment3 = sampleDepartment();

        List <Department> departmentList = new ArrayList <>();
        departmentList.add(sampleDepartment1);
        departmentList.add(sampleDepartment2);
        departmentList.add(sampleDepartment3);

        for(Department d : departmentList){
            departmentDao.insert(d);
        }

        for(int i = 0 ; i < 3 ; i++){
            assertThat(departmentList.get(i)).hasNoNullFieldsOrProperties();
            assertThat(departmentList.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(departmentDao.retrieve(departmentList.get(i).getId()));
        }
    }

}
