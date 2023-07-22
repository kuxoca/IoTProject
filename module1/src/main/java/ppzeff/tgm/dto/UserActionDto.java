package ppzeff.tgm.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class UserActionDto {
    long userId;
    long chatId;
    String lang;
    String userName;
    String command;
}
