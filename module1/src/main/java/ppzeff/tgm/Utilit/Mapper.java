package ppzeff.tgm.Utilit;

import ppzeff.tgm.dto.UserActionDto;
import ppzeff.tgm.entity.UserAction;

public class Mapper {
    public static UserActionDto toUserDetailDto(UserAction userAction) {

        return UserActionDto.builder()
                .userId(userAction.getUserId())
                .chatId(userAction.getChatId())
                .userName(userAction.getUserName())
                .lang(userAction.getLang())
                .command(userAction.getCommand())
                .build();
    }

    public static UserAction toUserDetail(UserActionDto userActionDto) {

        return UserAction.builder()
                .userId(userActionDto.getUserId())
                .chatId(userActionDto.getChatId())
                .userName(userActionDto.getUserName())
                .lang(userActionDto.getLang())
                .command(userActionDto.getCommand())
                .build();
    }
}
