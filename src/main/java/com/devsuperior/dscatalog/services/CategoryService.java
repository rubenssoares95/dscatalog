package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exception.DatabaseException;
import com.devsuperior.dscatalog.services.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

  @Autowired private CategoryRepository repository;

  @Transactional(readOnly = true)
  public List<CategoryDTO> findAll() {
    List<Category> list = repository.findAll();
    return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public CategoryDTO findById(Long id) {
    var obj = repository.findById(id);
    var entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
    return new CategoryDTO(entity);
  }

  @Transactional
  public CategoryDTO create(CategoryDTO categoryDTO) {
    var entity = new Category();
    entity.setName(categoryDTO.getName());
    entity = repository.save(entity);
    return new CategoryDTO(entity);
  }

  @Transactional
  public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
    try {
        Category entity = repository.getOne(id);
        entity.setName(categoryDTO.getName());
        entity = repository.save(entity);
        return new CategoryDTO(entity);
    } catch (EntityNotFoundException e) {
        throw new ResourceNotFoundException("Id not found " + id);
    }
  }

  public void delete(Long id) {
    try {
      repository.deleteById(id);
    }
    catch (EmptyResultDataAccessException e) {
      throw new ResourceNotFoundException("Id not found " + id);
    }
    catch (DataIntegrityViolationException e) {
      throw new DatabaseException("Integrity violation");
    }
  }
}
