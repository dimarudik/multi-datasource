```
git clone https://github.com/dimarudik/multi-datasource.git
cd ./multi-datasource
```

```
./dockerfiles/buildContainerImage.sh -x -v 21.3.0
```

```
docker run --name oracle \
    -p 1521:1521 -p 5500:5500 \
    -e ORACLE_PWD=oracle_4U \
    -v ./dockerfiles/scripts:/docker-entrypoint-initdb.d \
    -d oracle/database:21.3.0-xe
```

```
sqlplus test/test@(description=(address=(host=localhost)(protocol=tcp)(port=1521))(connect_data=(service_name=xepdb1)))
```

```
docker run --name postgres \
        -e POSTGRES_USER=postgres \
        -e POSTGRES_PASSWORD=postgres \
        -e POSTGRES_DB=postgres \
        -p 5432:5432 \
        -v ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql \
        -v ./sql/.psqlrc:/var/lib/postgresql/.psqlrc \
        -d postgres \
        -c shared_preload_libraries="pg_stat_statements,auto_explain" \
        -c max_connections=200 \
        -c logging_collector=on \
        -c log_directory=pg_log \
        -c log_filename=%u_%a.log \
        -c log_min_duration_statement=3 \
        -c log_statement=all \
        -c auto_explain.log_min_duration=0 \
        -c auto_explain.log_analyze=true
```

```
psql postgresql://test:test@localhost/postgres
```

```
./gradlew clean build --no-daemon -x test
java -jar ./build/libs/multi-datasource-0.0.1-SNAPSHOT.jar
```

Just Insert to default datasource:
```
curl -X POST -i -H "Content-Type:application/json" -d '{"name": "FrodoBaggins", "gender": true}' http://localhost:8080/api/user
```

Dual Insert into two data sources ([Outbox pattern](https://medium.com/@egorponomarev/outbox-pattern-in-spring-boot-8e8cf116f044) & [Optimistic Locking](https://aurigait.com/blog/locking-in-spring-boot/)):
```
curl -X POST -i -H "Content-Type:application/json" -d '{"name": "FrodoBaggins", "gender": true}' http://localhost:8080/api/multiuser
```
