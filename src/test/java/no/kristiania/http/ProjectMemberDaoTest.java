package no.kristiania.http;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectMemberDaoTest {

    @Test
    void shouldListSavedProducts() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();

        ProjectMemberDao projectMemberDao = new ProjectMemberDao(dataSource);

        Member member = sampleMember();
        projectMemberDao.insert(member);

        List<String> nameList = new ArrayList();
        for(Member m : projectMemberDao.list()) {
            nameList.add(m.getFirstName());
        }

        assertThat(nameList)
                .contains(member.getFirstName());
    }

    @Test
    void shouldRetrieveSingleMember() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();

        ProjectMemberDao projectMemberDao = new ProjectMemberDao(dataSource);
        Member projectMember = sampleMember();

        projectMemberDao.insert(projectMember);

        assertThat(projectMemberDao.retrieve(projectMember.getId()))
                //.hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(projectMember);
    }


    private Member sampleMember() {
        return new Member(sampleFirstName(), sampleLastName(), sampleEmail());
    }

    private String sampleFirstName() {
        String[] options = {"John", "Peter", "Will", "Johnny", "Karoline"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

    private String sampleLastName() {
        String[] options = {"Peterson", "Johnson", "Hansen", "Mohammed"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

    private String sampleEmail() {
        String[] options = {"g123@example.com", "9999@covid.com", "1337@pamail.com", "testmail@post.no"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

}
