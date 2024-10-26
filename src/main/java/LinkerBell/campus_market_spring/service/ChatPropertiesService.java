package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.repository.ChatPropertiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatPropertiesService {
    private final ChatPropertiesRepository chatPropertiesRepository;

}
