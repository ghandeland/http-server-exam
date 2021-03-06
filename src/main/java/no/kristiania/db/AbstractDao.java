package no.kristiania.db;

import no.kristiania.http.HttpServer;
import org.postgresql.util.PSQLException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T extends SetId> {

    protected final DataSource dataSource;

    protected AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected boolean insert(T t, String sql) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS)){
                setDataOnStatement(statement, t);
                statement.executeUpdate();

                try(ResultSet generatedKeys = statement.getGeneratedKeys()){
                    generatedKeys.next();
                    t.setId(generatedKeys.getLong("id"));
                }
                return true;
            }catch(PSQLException e){
                HttpServer.logger.info(e.getServerErrorMessage().toString());
                return false;
            }
        }
    }

    protected List <T> list(String sql) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                try(ResultSet rs = statement.executeQuery()){
                    List <T> tList = new ArrayList <>();
                    while(rs.next()){
                        tList.add(mapRow(rs));
                    }
                    return tList;
                }
            }catch(PSQLException e){
                HttpServer.logger.info(e.getServerErrorMessage().toString());
                return null;
            }
        }
    }

    protected T retrieve(Long id, String sql) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setLong(1, id);
                try(ResultSet rs = statement.executeQuery()){
                    if(rs.next()){
                        return mapRow(rs);
                    }else{
                        return null;
                    }
                }
            }catch(PSQLException e){
                HttpServer.logger.info(e.getServerErrorMessage().toString());
                return null;
            }
        }
    }

    protected List <T> retrieveMultiple(Long id, String sql) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setLong(1, id);
                try(ResultSet rs = statement.executeQuery()){
                    List <T> tList = new ArrayList <>();
                    while(rs.next()){
                        tList.add(mapRow(rs));
                    }
                    return tList;
                }
            }catch(PSQLException e){
                HttpServer.logger.info(e.getServerErrorMessage().toString());
                return null;
            }
        }
    }

    protected List <T> filter(String value, String sql) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setObject(1, value);
                try(ResultSet rs = statement.executeQuery()){
                    List <T> tList = new ArrayList <>();
                    while(rs.next()){
                        tList.add(mapRow(rs));
                    }
                    return tList;
                }
            }catch(PSQLException e){
                HttpServer.logger.info(e.getServerErrorMessage().toString());
                return null;
            }
        }
    }

    protected void alter(long id, String value, String sql) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setObject(1, value);
                statement.setLong(2, id);
                statement.executeUpdate();
                HttpServer.logger.info("ALTERED TABLE \"{}\", with value to \"{}\" on id {}", sql.split(" ")[1], value, id);
            }
        }catch(PSQLException e){
            HttpServer.logger.info(e.getServerErrorMessage().toString());
        }
    }

    protected void delete(long id, String sql) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setLong(1, id);
                statement.executeUpdate();
                HttpServer.logger.info("DELETE FROM TABLE \"{}\", WHERE id = {}", sql.split(" ")[2], id);
            }
        }catch(PSQLException e){
            HttpServer.logger.info(e.getServerErrorMessage().toString());
        }
    }

    protected abstract void setDataOnStatement(PreparedStatement statement, T t) throws SQLException;

    protected abstract T mapRow(ResultSet rs) throws SQLException;
}
