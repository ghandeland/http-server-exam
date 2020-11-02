package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class MemberDao extends AbstractDao <Member> {

    public MemberDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Member member) throws SQLException {
        insert(member, "insert into member (first_name, last_name, email, department_id) values (?, ?, ?, ?)");
    }

    public List <Member> list() throws SQLException {
        return list("select * from member");
    }

    public Member retrieve(long id) throws SQLException {
        return retrieve(id, "select * from member where id = ?");
    }

    @Override
    protected void setDataOnStatement(PreparedStatement statement, Member member) throws SQLException {

        statement.setString(1, member.getFirstName());
        statement.setString(2, member.getLastName());
        statement.setString(3, member.getEmail());
        if(member.getDepartmentId() == null) {
            statement.setNull(4, Types.INTEGER);
        } else {
            statement.setLong(4, member.getDepartmentId());
        }
    }

    @Override
    protected Member mapRow(ResultSet rs) throws SQLException {
        Member member = new Member();

        member.setId(rs.getInt("id"));
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        member.setEmail(rs.getString("email"));
        member.setDepartmentId(rs.getLong("department_id"));

        return member;
    }
}
