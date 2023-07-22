package ppzeff.tgm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "user_action")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class UserAction extends AbstractEntity {
    @Column(name = "user_id")
    long userId;
    @Column(name = "chat_id")
    long chatId;
    @Column(name = "lang")
    String lang;
    @Column(name = "user_name")
    String userName;
    @Column(name = "command")
    String command;
}
