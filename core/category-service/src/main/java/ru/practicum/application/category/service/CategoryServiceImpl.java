package ru.practicum.application.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.application.category.mapper.CategoryMapper;
import ru.practicum.application.category.model.Category;
import ru.practicum.application.category.repository.CategoryRepository;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.stats.client.EventFeignClient;

import java.util.List;
import java.util.Set;

import static ru.practicum.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventFeignClient eventFeignClient;

    @Override
    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) throws ConflictException {

        if (Boolean.TRUE.equals(categoryRepository.existsByName(categoryDto.getName()))) {
            throw new ConflictException("Категория событий с именем: {0} уже существует", categoryDto.getName());
        }

        Category category = categoryRepository.save(CategoryMapper.toModel(categoryDto));
        return CategoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) throws NotFoundException, ConflictException {

        Category category = categoryRepository.findById(catId)
                .orElseThrow(notFoundException("Категория с идентификатором: {0} - не найдена.", catId));

        boolean categoryExists = categoryRepository.findByName(categoryDto.getName()).stream()
                .anyMatch(c -> !c.getId().equals(catId));

        if (categoryExists) {
            throw new ConflictException(
                    "Нельзя задать имя категории {0}, поскольку такое имя уже используется.", categoryDto.getName());
        }

        category.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);

        return CategoryMapper.toDto(updatedCategory);
    }


    @Override
    public CategoryDto getCategoryById(Long catId) throws NotFoundException {

        Category category = categoryRepository.findById(catId)
                .orElseThrow(notFoundException("Указанная категория не найдена. Идентификатор категории: {0}.", catId));

        return CategoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size))
                .getContent()
                .stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) throws ConflictException, NotFoundException {

        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Категория с идентификатором: {0} не существует", catId);
        }

        if (eventFeignClient.existsByCategoryId(catId)) {
            throw new ConflictException("Невозможно удаление используемой категории события.");

        }

        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getCategoriesByIds(Set<Long> ids) {
        return categoryRepository.findAllById(ids).stream().map(CategoryMapper::toDto).toList();
    }

    @Override
    public boolean existById(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
}
