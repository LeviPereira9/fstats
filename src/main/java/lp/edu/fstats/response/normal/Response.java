package lp.edu.fstats.response.normal;

import lombok.Builder;
import lombok.Data;
import lp.edu.fstats.util.BrazilTimeUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class Response<T> {

    private String operation;
    private int code;
    private String message;
    private T data;

    @Builder.Default
    private LocalDateTime timestamp = BrazilTimeUtil.nowDateTime();

    private Map<String, String> fieldErrors;
    private List<ActionLink> actions;

}
