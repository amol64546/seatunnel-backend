package com.seatunnel.orchestrator.service;

import com.seatunnel.orchestrator.annotations.ConnectorValidation;
import com.seatunnel.orchestrator.model.Connector;
import com.seatunnel.orchestrator.projection.ConnectorProjection;
import com.seatunnel.orchestrator.repository.ConnectorRepository;
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
public class ConnectorService {

  private final ConnectorRepository repository;
  private final CommonUtil commonUtil;

  @ConnectorValidation
  public Connector create(Connector request) {
    commonUtil.validateMethodArguments(request);
    return repository.save(request);
  }

  public Connector getById(String id) {
    Optional<Connector> etlBrickOptional = repository.findById(id);
    if (etlBrickOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
        "Connector not found.");
    }
    return etlBrickOptional.get();
  }

  public Page<ConnectorProjection> getAll(Pageable pageable) {
    return repository.findAllProjectedBy(pageable);
  }

  public Connector update(String id, Connector brick) {
    Connector existingBrick = getById(id);
    brick.setId(id);
    brick.setCreatedOn(existingBrick.getCreatedOn());
    return create(brick);
  }

  public String delete(String id) {
    repository.deleteById(id);
    return "Successfully delete connector with id:%s".formatted(id);
  }

}