package vn.hoidanit.jobhunter.domain.res;

import lombok.*;

@Getter
@Setter
public class ResultPaginationDTO {
    private Meta meta;
    private Object result;

    @Setter
    @Getter
    public class Meta {
        private int page,
                pageSize,
                pages;
        private long total;
    }
}
