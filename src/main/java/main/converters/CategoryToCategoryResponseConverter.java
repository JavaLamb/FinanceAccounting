package main.converters;

import main.dto.Response.CategoryResponse;
import main.entities.Category;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CategoryToCategoryResponseConverter implements Converter<Category, CategoryResponse> {
    @Override
    public CategoryResponse convert(Category source) {
        return CategoryResponse.builder()
                .id(source.getId())
                .categoryName(source.getTransactionCategoryName())
                .build();
    }
}
