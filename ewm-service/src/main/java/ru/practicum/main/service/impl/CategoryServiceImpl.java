package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.entity.Category;
import ru.practicum.main.exception.NotAvailableException;
import ru.practicum.main.mapper.CategoryMapper;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.service.CategoryService;
import ru.practicum.main.util.Pagination;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.main.exception.NotFoundException.notFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category category = categoryMapper.toCategory(newCategoryDto);
        return categoryMapper.toCategoryDto(
                categoryRepository.save(category)
        );
    }

    @Override
    public List<CategoryDto> get(int from, int size) {
        return categoryRepository.findAll(new Pagination(from, size, Sort.unsorted()))
                .stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long catId) {
        return categoryMapper.toCategoryDto(
                categoryRepository.findById(catId)
                        .orElseThrow(notFoundException("Category with id={0} hasn't found", catId))
        );
    }

    @Override
    public CategoryDto update(Long catId, CategoryDto categoryDto) {
        Category category = getCategoryOrThrowException(catId);

        category.setName(categoryDto.getName());

        return categoryMapper.toCategoryDto(
                categoryRepository.save(category)
        );
    }

    @Override
    public Boolean delete(Long catId) {
        getCategoryOrThrowException(catId);
        try {
            categoryRepository.deleteById(catId);
            return true;
        } catch (DataIntegrityViolationException e) {
            throw new NotAvailableException("The category isn't empty");
        }
    }

    private Category getCategoryOrThrowException(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(notFoundException("Category with id={0} hasn't found", catId)
        );
    }
}
