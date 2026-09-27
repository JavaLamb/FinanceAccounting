package main.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CategoryResponse {
    private String categoryName;
    private long id;
}
