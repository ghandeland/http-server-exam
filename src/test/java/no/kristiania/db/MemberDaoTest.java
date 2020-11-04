package no.kristiania.db;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static no.kristiania.db.DepartmentDaoTest.sampleDepartment;
import static org.assertj.core.api.Assertions.assertThat;

public class MemberDaoTest {
    MemberDao memberDao;
    JdbcDataSource dataSource;

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

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        this.dataSource = dataSource;
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
                .usingRecursiveComparison()
                .isEqualTo(projectMember);
    }

    @Test
    void shouldRetrieveCorrectMemberWhileCheckingDepartment() throws SQLException {

        DepartmentDao departmentDao = new DepartmentDao(dataSource);

        Department sampleDepartment1 = sampleDepartment();
        Department sampleDepartment2 = sampleDepartment();

        departmentDao.insert(sampleDepartment1);
        departmentDao.insert(sampleDepartment2);

        Member sampleMember1 = sampleMember();
        Member sampleMember2 = sampleMember();

        sampleMember1.setDepartmentId(sampleDepartment1.getId());
        sampleMember2.setDepartmentId(sampleDepartment2.getId());

        memberDao.insert(sampleMember1);
        memberDao.insert(sampleMember2);

        Member retrievedMember1 = memberDao.retrieve(sampleMember1.getId());
        Member retrievedMember2 = memberDao.retrieve(sampleMember2.getId());

        assertThat(retrievedMember1)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(sampleMember1);

        assertThat(retrievedMember2)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(sampleMember2);

        long retrievedDepartmentId1 = retrievedMember1.getDepartmentId();
        long retrievedDepartmentId2 = retrievedMember2.getDepartmentId();

        assertThat(retrievedDepartmentId1).isEqualTo(sampleDepartment1.getId());
        assertThat(retrievedDepartmentId2).isEqualTo(sampleDepartment2.getId());
    }

    @Test
    @DisplayName("test delete member")
    void testDeleteMember() throws SQLException {
        Member sampleMember1 = sampleMember();
        Member sampleMember2 = sampleMember();
        Member sampleMember3 = sampleMember();

        memberDao.insert(sampleMember1);
        memberDao.insert(sampleMember2);
        memberDao.insert(sampleMember3);

        memberDao.delete(sampleMember2.getId());

        assertThat(memberDao.list())
                .extracting(Member::getId)
                .doesNotContain(sampleMember2.getId())
                .contains(sampleMember1.getId())
                .contains(sampleMember3.getId());
    }

}
