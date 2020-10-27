package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T extends SetId> {
    protected final DataSource dataSource;

    public AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected void insert(T t, String sql) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS)){
                setDataOnStatement(statement, t);
                statement.executeUpdate();
                try(ResultSet generatedKeys = statement.getGeneratedKeys()){
                    generatedKeys.next();
                    t.setId(generatedKeys.getLong("id"));
                }
            }
        }
    }

    public List <T> list(String sql) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                try(ResultSet rs = statement.executeQuery()){
                    List <T> t = new ArrayList <>();
                    while(rs.next()){
                        t.add(mapRow(rs));
                    }
                    return t;
                }
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
            }
        }
    }

    protected abstract void setDataOnStatement(PreparedStatement statement, T t) throws SQLException;

    protected abstract T mapRow(ResultSet rs) throws SQLException;
}
