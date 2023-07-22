package ppzeff.tgm.Utilit.db;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.Arrays;

public final class HibernateUtils {

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
    public static SessionFactory sessionFactory;

    private HibernateUtils() {
    }

    public static SessionFactory getSessionFactory(Class<?>... annotatedClasses) {
        if (sessionFactory == null) {
// TODO сделать автопоиск аннотированных классов @Entity
            sessionFactory = buildSessionFactory(HIBERNATE_CFG_FILE, annotatedClasses);
        }
        return sessionFactory;
    }

    private static SessionFactory buildSessionFactory(Configuration configuration, Class<?>... annotatedClasses) {
        MetadataSources metadataSources = new MetadataSources(createServiceRegistry(configuration));
        Arrays.stream(annotatedClasses).forEach(metadataSources::addAnnotatedClass);

        Metadata metadata = metadataSources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    private static SessionFactory buildSessionFactory(String configResourceName, Class<?>... annotatedClasses) {
        Configuration configuration = new Configuration().configure(configResourceName);
        return buildSessionFactory(configuration, annotatedClasses);
    }

    private static SessionFactory buildSessionFactory(Class<?>... annotatedClasses) {
        Configuration configuration = new Configuration().configure(HIBERNATE_CFG_FILE);
        return buildSessionFactory(configuration, annotatedClasses);
    }

    private static StandardServiceRegistry createServiceRegistry(Configuration configuration) {
        return new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
    }

}
