package lp.edu.fstats.dto.averages;

import lp.edu.fstats.model.avarages.Averages;

import java.util.List;

public record AveragesResponse(
        List<AverageResponse> averages
) {
    public static AveragesResponse toResponse(List<Averages> source){
        List<AverageResponse> averages = source.stream().map(
                AverageResponse::new
        ).toList();

        return new AveragesResponse(averages);
    }
}
