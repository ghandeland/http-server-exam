package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DepartmentDao extends AbstractDao <Department> {

    public DepartmentDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Department department) throws SQLException {
        insert(department, "insert into department (name) values (?)");
    }

    public List<Department> list() throws SQLException {
        return list("select * from department");
    }

    public Department retrieve(long id) throws SQLException {

        Department department = retrieve(id, "select * from department where id = ?");
        if(department == null) {
            System.out.println("Department not found");
            return null;
        }
        return department;
    }

    @Override
    protected void setDataOnStatement(PreparedStatement statement, Department department) throws SQLException {
        statement.setString(1, department.getName());
    }

    @Override
    protected Department mapRow(ResultSet rs) throws SQLException {
        Department department = new Department();

        department.setId(rs.getLong("id"));
        department.setName(rs.getString("name"));

        return department;
    }
}
