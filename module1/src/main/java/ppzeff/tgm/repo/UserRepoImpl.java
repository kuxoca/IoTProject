package ppzeff.tgm.repo;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import ppzeff.tgm.Utilit.db.HibernateUtils;
import ppzeff.tgm.entity.UserAction;

@Slf4j
public class UserRepoImpl implements UserRepo {
    SessionFactory sessionFactory = HibernateUtils.getSessionFactory(UserAction.class);

    @Override
    public void save(UserAction userAction) {
        try (var session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.persist(userAction);
            session.getTransaction().commit();
            log.info("save userAction: {}", userAction);
        }
    }

    @Override
    public UserAction get(Long id) {
        UserAction userAction;
        try (var session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            userAction = session.get(UserAction.class, id);
            session.getTransaction().commit();
        }
        log.info("get userAction: {}", userAction);
        return userAction;
    }

}
