package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDao {

    private DataSource dataSource;
    private List<Member> members = new ArrayList<>();

    public MemberDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(Member member) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into member (first_name, last_name, email) " + "values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, member.getFirstName());
                statement.setString(2, member.getLastName());
                statement.setString(3, member.getEmail());

                statement.execute();

                try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if(generatedKeys.next()) {
                        member.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        members.add(member);
    }


    /*public List<String> list() {
        return members;
    }*/

    public List<Member> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from member")) {
                try (ResultSet rs = statement.executeQuery()) {
                    members = new ArrayList<>();

                    while (rs.next()) {
                        Member newMember = new Member();
                        newMember.setFirstName(rs.getString("first_name"));
                        newMember.setLastName(rs.getString("last_name"));
                        newMember.setEmail(rs.getString("email"));

                        members.add(newMember);
                    }

                    return members;
                }
            }
        }
    }

    public Member retrieve(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from member where id = ?")) {
                statement.setInt(1, id);

                try (ResultSet rs = statement.executeQuery()) {

                    Member member = new Member();

                    while(rs.next()) {

                        member.setId(rs.getInt("id"));
                        member.setFirstName(rs.getString("first_name"));
                        member.setLastName(rs.getString("last_name"));
                        member.setEmail(rs.getString("email"));
                    }

                    return member;
                }
            }
        }
    }
}
