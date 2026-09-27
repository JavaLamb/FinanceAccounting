package main.repositories;

import main.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUserId(long userId);
    Optional<Category> findByIdAndUserId(long categoryId, long userId);
    int countAllByUserId(long userId);

}