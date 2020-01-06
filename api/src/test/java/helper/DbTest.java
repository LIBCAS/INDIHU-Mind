package helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import core.config.ObjectMapperProducer;
import core.store.DomainStore;
import core.util.ApplicationContextUtils;
import mockit.MockUp;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.internal.SessionImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.util.FileSystemUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public abstract class DbTest {
    private static EntityManagerFactory factory;

    private EntityManager em;

    protected static final ObjectMapper objectMapper = new ObjectMapperProducer().objectMapper(false, false);

    protected EntityManager getEm() {
        return em;
    }

    protected void flushCache() {
        if (em != null) {
            em.getTransaction().commit();
        } else {
            em = factory.createEntityManager();
        }

        em.getTransaction().begin();
    }


    @BeforeClass
    public static void dbTestClassSetUp() throws Exception {
        Logger.getRootLogger().setLevel(Level.INFO);

        new MockUp<ApplicationContextUtils>() {
            @mockit.Mock
            public ObjectMapper getBean(Class<ObjectMapper> objectMapperClass) {
                return objectMapper;
            }
        };

        factory = initializeFactory();
    }

    @AfterClass
    public static void dbTestClassTearDown() throws Exception {
        if (factory != null) {
            factory.close();
            factory = null;
        }
    }

    @Before
    public void dbTestSetUp() throws Exception {
        flushCache();

        setSyntax();
    }

    @After
    public void dbTestTearDown() throws Exception {
        if (em != null) {
            clearDatabase();
            //em.getTransaction().commit();
            em.close();
            em = null;
        }
    }

    public void setSyntax() throws SQLException {
        Connection c = ((SessionImpl) em.getDelegate()).connection();
        Statement s = c.createStatement();

        s.execute("SET DATABASE SQL SYNTAX PGS TRUE");
        s.close();
    }

    public void clearDatabase() throws SQLException {
        Connection c = ((SessionImpl) em.getDelegate()).connection();
        Statement s = c.createStatement();

        //s.execute("DROP SCHEMA PUBLIC CASCADE ");
        s.execute("TRUNCATE SCHEMA PUBLIC AND COMMIT");
        s.close();
    }

    public void initializeStores(DomainStore... stores) {
        for (DomainStore store : stores) {
            store.setEntityManager(em);
            store.setQueryFactory(new JPAQueryFactory(em));
        }
    }

    protected static void delete(String path) throws IOException {
        File file = new File(path);
        FileSystemUtils.deleteRecursively(file);
    }

    private static EntityManagerFactory initializeFactory() throws IOException {
        Properties props = new Properties();
        props.load(ClassLoader.getSystemResourceAsStream("application.properties"));

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(props.getProperty("datasource.driverClassName"));
        dataSource.setUrl(props.getProperty("datasource.url"));
        dataSource.setUsername(props.getProperty("datasource.username"));
        dataSource.setPassword(props.getProperty("datasource.password"));

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setPackagesToScan("core", "cz");
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setDataSource(dataSource);
        em.setJpaProperties(props);
        em.afterPropertiesSet();
        return em.getNativeEntityManagerFactory();
    }
}
