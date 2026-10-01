package main.controllers;

import lombok.RequiredArgsConstructor;
import main.config.CustomUserDetails;
import main.converters.CategoryToCategoryResponseConverter;
import main.dto.Request.CategoryRequest;
import main.dto.Response.CategoryResponse;
import main.entities.Category;
import main.exceptions.CategoryException;
import main.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.*;

@RequestMapping("/categories")
@RequiredArgsConstructor
@Controller
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryToCategoryResponseConverter converter;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAllByUserIdService(@AuthenticationPrincipal CustomUserDetails userDetails){
        long userId = userDetails.getId();
        List<CategoryResponse> list = categoryService.getCategoriesByUserId(userId).stream().map(converter::convert).toList();
        return ok(list);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @Validated @RequestBody CategoryRequest dto){
        try {
            long userId = userDetails.getId();
            Category newCategory = categoryService.createCategory(dto.categoryName(), userId);
            URI url = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(newCategory.getId())
                    .toUri();
            return created(url).body(converter.convert(newCategory));
        } catch (CategoryException e) {
            return status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
