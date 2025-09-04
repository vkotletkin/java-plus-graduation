package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) throws ConflictException {

        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("Такая категория событий уже существует");
        }

        Category category = categoryRepository.save(CategoryMapper.mapCategoryDto(categoryDto));
        return CategoryMapper.mapCategory(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) throws NotFoundException, ConflictException {

        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с ID " + catId + " не найдена."));

        boolean categoryExists = categoryRepository.findByName(categoryDto.getName()).stream()
                .anyMatch(c -> !c.getId().equals(catId));

        if (categoryExists) {
            throw new ConflictException(String.format(
                    "Нельзя задать имя категории %s, поскольку такое имя уже используется.",
                    categoryDto.getName()
            ));
        }
        category.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);
        return CategoryMapper.mapCategory(updatedCategory);
    }


    @Override
    public CategoryDto getCategoryById(Long catId) throws NotFoundException {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Указанная категория не найдена " + catId));

        return CategoryMapper.mapCategory(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size))
                .getContent()
                .stream()
                .map(CategoryMapper::mapCategory)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) throws ConflictException, NotFoundException {

        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Категория с id=%d не существует", catId));
        }

        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("Невозможно удаление используемой категории события ");
        }

        categoryRepository.deleteById(catId);
    }
}