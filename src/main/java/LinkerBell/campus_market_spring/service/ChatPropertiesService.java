package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.ChatRoom;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.repository.ChatPropertiesRepository;
import LinkerBell.campus_market_spring.repository.ChatRoomRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatPropertiesService {
    private final ChatPropertiesRepository chatPropertiesRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public void patchAlarm(Long chatRoomId, boolean isAlarm, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
        chatPropertiesRepository.updateIsAlarmByUserAndChatRoom(user, chatRoom, isAlarm);
    }
}
