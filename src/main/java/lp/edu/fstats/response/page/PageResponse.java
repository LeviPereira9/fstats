package lp.edu.fstats.response.page;


import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class PageResponse<T> {
    private int page;
    private int size;
    private Long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<T> content;

    private PageResponse(){};

    public PageResponse(Page<T> page){
        this.page = page.getNumber() + 1;
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
        this.content = page.getContent();
    }

    public <R> PageResponse<R> map(Function<? super T, ? extends R> mapper){
        List<R> mappedContent = this.content.stream().map(mapper).collect(Collectors.toList());

        PageResponse<R> mappedPage = new PageResponse<>();
        mappedPage.setPage(this.page);
        mappedPage.setSize(this.size);
        mappedPage.setTotalElements(this.totalElements);
        mappedPage.setTotalPages(this.totalPages);
        mappedPage.setHasNext(this.hasNext);
        mappedPage.setHasPrevious(this.hasPrevious);
        mappedPage.setContent(mappedContent);

        return mappedPage;
    }
}
