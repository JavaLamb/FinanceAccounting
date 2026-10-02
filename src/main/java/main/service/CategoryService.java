package main.service;

import main.exceptions.BusinessLogicException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.Category;
import main.entities.User;
import main.repositories.CategoryRepository;
import main.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final int limit = 5;

    @Transactional
    public List<Category> getCategoriesByUserId(long userId) {
        return categoryRepository.findAllByUserId(userId);
    }

    @Transactional
    public Category createCategory(String categoryName, long userId) {
        if (!canCreateMoreCategory(userId)) {
            throw new BusinessLogicException(
                    "Category limit exceeded",
                    HttpStatus.BAD_REQUEST,
                    "CATEGORY_LIMIT_EXCEEDED");
        }
        User user = userRepository.getReferenceById(userId);
        return categoryRepository.save(new Category(categoryName, user));
    }

    private boolean canCreateMoreCategory(long userId) {
        return categoryRepository.countAllByUserId(userId) < limit;
    }
}
