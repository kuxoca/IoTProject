package ppzeff.tgm.repo;

import ppzeff.tgm.entity.UserAction;

public interface UserRepo {
    void save(UserAction userAction);

    UserAction get(Long id);
}
