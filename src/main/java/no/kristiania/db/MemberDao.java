package no.kristiania.db;

import no.kristiania.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class MemberDao extends AbstractDao <Member> {
    public static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public MemberDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Member member) throws SQLException {
        boolean insertOk = insert(member, "insert into member (first_name, last_name, email, department_id) values (?, ?, ?, ?)");
        if(insertOk)
            logger.info("New member({}) successfully inserted into database", member.getFirstName() + " " + member.getLastName() + ", " + member.getEmail());
    }

    public List <Member> list() throws SQLException {
        return list("select * from member");
    }

    public Member retrieve(long id) throws SQLException {
        return retrieve(id, "select * from member where id = ?");
    }

    public void delete(long id) throws SQLException {
        delete(id, "DELETE FROM task_member WHERE member_id = ?");
        delete(id, "DELETE FROM member WHERE id = ?");
    }

    @Override
    protected void setDataOnStatement(PreparedStatement statement, Member member) throws SQLException {

        statement.setString(1, member.getFirstName());
        statement.setString(2, member.getLastName());
        statement.setString(3, member.getEmail());
        if(member.getDepartmentId() == null){
            statement.setNull(4, Types.INTEGER);
        }else{
            statement.setLong(4, member.getDepartmentId());
        }
    }

    @Override
    protected Member mapRow(ResultSet rs) throws SQLException {
        return new Member(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getLong("department_id")
        );
    }

}
