package ru.practicum.main.service;

import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(NewCategoryDto newCategoryDto);

    List<CategoryDto> get(int from, int size);

    CategoryDto getById(Long catId);

    CategoryDto update(Long id, CategoryDto categoryDto);

    Boolean delete(Long id);
}
