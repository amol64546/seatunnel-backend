package com.seatunnel.orchestrator.service;

import com.seatunnel.orchestrator.annotations.BrickValidation;
import com.seatunnel.orchestrator.model.EtlBrick;
import com.seatunnel.orchestrator.repository.EtlBrickRepository;
import com.seatunnel.orchestrator.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EtlBrickService {

  private final EtlBrickRepository repository;
  private final CommonUtil commonUtil;

  @BrickValidation
  public EtlBrick create(EtlBrick request) {
    commonUtil.validateMethodArguments(request);
    return repository.save(request);
  }

  public EtlBrick getById(String id) {
    Optional<EtlBrick> etlBrickOptional = repository.findById(id);
    if (etlBrickOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
        "Etl brick not found.");
    }
    return etlBrickOptional.get();
  }

  public Page<EtlBrick> getAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  public EtlBrick update(String id, EtlBrick brick) {
    EtlBrick existingBrick = getById(id);
    brick.setId(existingBrick.getId());
    brick.setCreatedOn(existingBrick.getCreatedOn());
    return repository.save(brick);
  }

  public String delete(String id) {
    repository.deleteById(id);
    return "Successfully delete ETL brick with id:%s".formatted(id);
  }

}