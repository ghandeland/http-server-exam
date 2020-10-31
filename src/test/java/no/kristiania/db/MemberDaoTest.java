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

public class MemberDaoTest {
    MemberDao memberDao;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        memberDao = new MemberDao(dataSource);
    }

    @Test
    void shouldListSavedMember() throws SQLException {
        Member member = sampleMember();
        memberDao.insert(member);

        List <String> nameList = new ArrayList <>();
        for(Member m : memberDao.list()){
            nameList.add(m.getFirstName());
        }
        assertThat(nameList).contains(member.getFirstName());
    }

    @Test
    void shouldRetrieveSingleMember() throws SQLException {
        Member projectMember = sampleMember();

        memberDao.insert(projectMember);

        assertThat(memberDao.retrieve(projectMember.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(projectMember);
    }


    public static Member sampleMember() {
        return new Member(sampleFirstName(), sampleLastName(), sampleEmail());
    }

    public static String sampleFirstName() {
        String[] options = {"John", "Peter", "Will", "Johnny", "Karoline"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

    public static String sampleLastName() {
        String[] options = {"Peterson", "Johnson", "Hansen", "Mohammed"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

    public static String sampleEmail() {
        String[] options = {"g123@example.com", "9999@covid.com", "1337@pamail.com", "testmail@post.no"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

}
