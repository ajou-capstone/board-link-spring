package LinkerBell.campus_market_spring.dto;

import lombok.Data;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import java.util.List;
@Data
public class SliceResponse<T> {
    protected final List<T> content;
    protected final Sort sort;
    protected final int currentPage;
    protected final int size;
    protected final boolean hasPrevious;
    protected final boolean hasNext;

    public SliceResponse(SliceImpl<T> slice) {
        this.content = slice.getContent();
        this.sort = slice.getSort();
        this.currentPage = slice.getNumber();
        this.size = slice.getSize();
        this.hasPrevious = slice.hasPrevious();
        this.hasNext = slice.hasNext();
    }
}
