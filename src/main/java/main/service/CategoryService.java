package main.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.Category;
import main.entities.User;
import main.exceptions.CategoryException;
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
    public List<Category> getCategoriesByUserId(long userId){
        return categoryRepository.findAllByUserId(userId);
    }

    @Transactional
    public Category createCategory(String categoryName, long userId){
        if(!canCreateMoreCategory(userId)){
            throw new CategoryException("Category limit exceeded");
        }
        User user = userRepository.getReferenceById(userId);
        return categoryRepository.save(new Category(categoryName, user));
    }

    private boolean canCreateMoreCategory(long userId) {
        return categoryRepository.countAllByUserId(userId) < limit;
    }
}
