package main.service.categoryService;

import main.entities.Category;
import main.entities.User;
import main.exceptions.BusinessLogicException;
import main.repositories.CategoryRepository;
import main.repositories.UserRepository;
import main.service.CategoryService;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.assertj.core.api.InstanceOfAssertFactories;
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
        BusinessLogicException ex = assertThatThrownBy(() -> subj.createCategory("name", userId))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessage("Category limit exceeded")
                .asInstanceOf(InstanceOfAssertFactories.type(BusinessLogicException.class))
                .actual();
        assertThat(ex.getErrorCode()).isEqualTo("CATEGORY_LIMIT_EXCEEDED");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

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
        Mockito.when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(mockSavedCategory);

        Category res = subj.createCategory(categoryName, userId);

        assertThat(res).isEqualTo(mockSavedCategory);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        Category categoryToSave = captor.getValue();

        assertThat(categoryToSave.getUser()).isEqualTo(mockUser);
        assertThat(categoryToSave.getTransactionCategoryName()).isEqualTo(categoryName);
    }
}