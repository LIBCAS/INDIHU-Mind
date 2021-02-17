package core.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GeneratedReportDto {
    protected byte[] content;
    protected String contentType;
    protected String name;
}
