package examples;

import com.studiomediatech.queryresponse.QueryBuilder;

import org.springframework.stereotype.Component;

import java.util.Collection;


// #region class
@Component
public class Authors {

    private final QueryBuilder queryBuilder;

    public Authors(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public Collection<String> findAuthors() {
        return queryBuilder.queryFor("authors", String.class)
            .waitingFor(800)
            .orEmpty();
    }
}
// #endregion class
