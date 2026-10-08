package main.service;

import main.entities.Category;
import main.entities.User;
import main.exceptions.BusinessLogicException;
import main.repositories.CategoryRepository;
import main.repositories.UserRepository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    CategoryService subj;

    @Test
    void shouldThrowExceptionWhenLimitExceeded() {
        long userId = 1;
        int exceededLimit = 5;
        Mockito.when(categoryRepository.countAllByUserId(userId)).thenReturn(exceededLimit);
        assertThatThrownBy(() -> subj.createCategory("name", userId))
                .isInstanceOf(BusinessLogicException.class)
                .hasFieldOrPropertyWithValue("errorCode", "CATEGORY_LIMIT_EXCEEDED")
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);

        verify(categoryRepository).countAllByUserId(userId);
        verify(categoryRepository, never()).save(any());
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldCreateCategorySuccessfullyWhenWithinLimit() {
        long userId = 1;
        int withinLimit = 4;

        String categoryName = "name";
        Mockito.when(categoryRepository.countAllByUserId(userId)).thenReturn(withinLimit);

        User mockUser = Mockito.mock(User.class);
        Mockito.when(userRepository.getReferenceById(userId)).thenReturn(mockUser);

        Category mockSavedCategory = new Category(categoryName, mockUser);
        Mockito.when(categoryRepository.save(Mockito.any())).thenReturn(mockSavedCategory);


        Category res = subj.createCategory(categoryName, userId);
        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        Category categoryToSave = captor.getValue();

        assertThat(res).isEqualTo(mockSavedCategory);

        assertThat(categoryToSave.getUser()).isEqualTo(mockUser);
        assertThat(categoryToSave.getTransactionCategoryName()).isEqualTo(categoryName);
    }
}