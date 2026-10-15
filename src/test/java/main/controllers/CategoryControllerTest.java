package main.controllers;

import main.converters.CategoryToCategoryResponseConverter;
import main.dto.Request.CategoryRequest;
import main.dto.Response.CategoryResponse;
import main.entities.Category;
import main.exceptions.BusinessLogicException;
import main.repositories.UserRepository;
import main.security.SecurityConfig;
import main.security.WithMockCustomUser;
import main.service.CategoryService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Import(SecurityConfig.class)
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
    long userId = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private CategoryToCategoryResponseConverter converter;

    @Nested
    @DisplayName("GET /categories")
    class GetAllCategoriesTests {
        @Test
        @WithMockCustomUser
        void shouldReturnEmptyListWhenUserHasNoCategories() throws Exception {
            when(categoryService.getCategoriesByUserId(userId))
                    .thenReturn(List.of());

            mockMvc.perform(get("/categories"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(converter, never()).convert(any());
            verify(categoryService).getCategoriesByUserId(userId);
        }

        @Test
        @WithMockCustomUser
        void shouldReturnListOfUserCategories() throws Exception {
            Category category1 = mock(Category.class);
            Category category2 = mock(Category.class);

            when(categoryService.getCategoriesByUserId(userId))
                    .thenReturn(List.of(category1, category2));
            CategoryResponse response1 = createCategoryResponse(0);
            CategoryResponse response2 = createCategoryResponse(1);
            when(converter.convert(category1))
                    .thenReturn(response1);
            when(converter.convert(category2))
                    .thenReturn(response2);

            mockMvc.perform(get("/categories"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.size()").value(2))
                    .andExpect(jsonPath("$[0].id").value(0))
                    .andExpect(jsonPath("$[1].id").value(1))
                    .andExpect(jsonPath("$[0].categoryName").value("categoryName " + 0))
                    .andExpect(jsonPath("$[1].categoryName").value("categoryName " + 1));
        }

        @Test
        void shouldReturn401WhenUnauthorized() throws Exception {
            mockMvc.perform(get("/categories"))
                    .andExpect(status().isUnauthorized());
        }

    }


    @Nested
    @DisplayName("POST /categories")
    class CreateCategoryTests {
        private static Stream<String> provideBlankString() {
            return Stream.of(
                    "   ",
                    null
            );
        }

        @Test
        @WithMockCustomUser
        void shouldReturn400WhenCategoryLimitExceeded() throws Exception {
            CategoryRequest request = new CategoryRequest("categoryName");
            when(categoryService.createCategory("categoryName", userId))
                    .thenThrow(new BusinessLogicException(
                            "Category limit exceeded",
                            HttpStatus.BAD_REQUEST,
                            "CATEGORY_LIMIT_EXCEEDED"));
            mockMvc.perform(post("/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error")
                            .value("CATEGORY_LIMIT_EXCEEDED"));

            verify(converter, never()).convert(any());
        }

        @ParameterizedTest
        @MethodSource("provideBlankString")
        @WithMockCustomUser
        void shouldReturn400WhenCategoryNameBlank(String invalid) throws Exception {
            CategoryRequest request = new CategoryRequest(invalid);

            mockMvc.perform(post("/categories")
                            .content(om.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.categoryName")
                            .value("Category name cannot be empty"));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn400WhenCategoryNameMoreThan255Symbols() throws Exception {
            CategoryRequest request = new CategoryRequest("A".repeat(256));

            mockMvc.perform(post("/categories")
                            .content(om.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.categoryName")
                            .value("Length must be from 1 to 255 symbols"));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn201AndNewCategory() throws Exception {
            long number = 10L;
            Category category = mock(Category.class);
            when(category.getId()).thenReturn(number);

            when(categoryService.createCategory(
                    "categoryName " + number, userId
            )).thenReturn(category);

            CategoryResponse response = createCategoryResponse(number);

            CategoryRequest request = new CategoryRequest(
                    "categoryName " + number
            );

            when(converter.convert(category)).thenReturn(response);

            mockMvc.perform(post("/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(header().string("Location", Matchers.endsWith("/categories/" + number)))
                    .andExpect(jsonPath("$.id").value(number))
                    .andExpect(jsonPath("$.categoryName").value("categoryName " + number));

            verify(categoryService).createCategory(
                    "categoryName " + number, userId
            );
        }
    }

    private CategoryResponse createCategoryResponse(long number) {
        return new CategoryResponse(
                "categoryName " + number,
                number);
    }

}