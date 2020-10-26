package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class MemberDao extends AbstractDao <Member> {

    public MemberDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Member member) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(
                    "insert into member (first_name, last_name, email) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)){

                statement.setString(1, member.getFirstName());
                statement.setString(2, member.getLastName());
                statement.setString(3, member.getEmail());

                statement.execute();

                try(ResultSet generatedKeys = statement.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        member.setId(generatedKeys.getLong(1));
                    }
                }
            }
        }
    }

    public List <Member> list() throws SQLException {
        return list("select * from member");
    }

    public Member retrieve(long id) throws SQLException {
        return retrieve(id, "select * from member where id = ?");
    }

    @Override
    protected Member mapRow(ResultSet rs) throws SQLException {
        Member member = new Member();

        member.setId(rs.getInt("id"));
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        member.setEmail(rs.getString("email"));

        return member;
    }
}
