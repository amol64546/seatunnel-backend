package com.seatunnel.orchestrator.service;

import com.seatunnel.orchestrator.enums.PluginType;
import com.seatunnel.orchestrator.model.Connector;
import com.seatunnel.orchestrator.model.Edge;
import com.seatunnel.orchestrator.model.Node;
import com.seatunnel.orchestrator.model.Pipeline;
import com.seatunnel.orchestrator.projection.PipelineProjection;
import com.seatunnel.orchestrator.repository.ConnectorRepository;
import com.seatunnel.orchestrator.repository.PipelineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static com.seatunnel.orchestrator.util.Constants.PLUGIN_INPUT;
import static com.seatunnel.orchestrator.util.Constants.PLUGIN_OUTPUT;

@Service("etl_pipeline_service")
@Slf4j
@RequiredArgsConstructor
public class PipelineService {


  private final PipelineRepository repository;
  private final ConnectorRepository connectorRepository;
  private final PipelineRepository pipelineRepository;

  public Pipeline create(Pipeline pipeline) {

    // if id is present, it means update operation, otherwise create operation
    if (StringUtils.isNotBlank(pipeline.getId())) {
      PipelineProjection pipelineProjection = getEtlPipelineProjection(pipeline.getId());
      pipeline.setId(pipeline.getId());
      pipeline.setCreatedOn(pipelineProjection.getCreatedOn());
    }

    // updating nodes config
    pipeline.getNodes().forEach(node -> {
      Connector brick = connectorRepository.findEtlBrickById(node.getConnectorId());
      node.setName(brick.getName());
      node.setPluginType(brick.getPluginType());
      brick.getConfig().putAll(node.getConfig()); // config from existing nodes
      node.setConfig(brick.getConfig()); // override with config from request body
    });
    connectNodes(pipeline);
    return repository.save(pipeline);
  }

  private void connectNodes(Pipeline pipeline) {
    Map<String, Map<String, Object>> sourceMap = new HashMap<>();
    Map<String, Map<String, Object>> transformMap = new HashMap<>();
    Map<String, Map<String, Object>> sinkMap = new HashMap<>();

    for (Node node : pipeline.getNodes()) {
      if (node.getPluginType().equals(PluginType.SOURCE)) {
        sourceMap.put(node.getId(), node.getConfig());
      }
      if (node.getPluginType().equals(PluginType.TRANSFORM)) {
        transformMap.put(node.getId(), node.getConfig());
      }
      if (node.getPluginType().equals(PluginType.SINK)) {
        sinkMap.put(node.getId(), node.getConfig());
      }
    }

    // source -> plugin_output - string
    // target -> plugin_input  - list of string
    pipeline.getEdges().forEach(
      edge -> {
        Map<String, Map<String, Object>> from = null;
        if (sourceMap.containsKey(edge.getSource())) {
          from = sourceMap;
        } else if (transformMap.containsKey(edge.getSource())) {
          from = transformMap;
        }

        Map<String, Map<String, Object>> to = null;
        if (transformMap.containsKey(edge.getTarget())) {
          to = transformMap;
        } else if (sinkMap.containsKey(edge.getTarget())) {
          to = sinkMap;
        }

        if (from != null && to != null) {
          connectSourceToTarget(edge, from, to);
        }
      }
    );
  }

  private void connectSourceToTarget(Edge edge, Map<String, Map<String, Object>> sourceMap,
                                     Map<String, Map<String, Object>> targetMap) {
    Map<String, Object> sourceConfig = sourceMap.get(edge.getSource());
    Map<String, Object> targetConfig = targetMap.get(edge.getTarget());

    String commonId = (String) sourceConfig.get(PLUGIN_OUTPUT);
    if (StringUtils.isBlank(commonId)) {
      commonId = UUID.randomUUID().toString().replaceAll("-", "");
    }

    // if plugin_input already exists,
    // it means this target node has multiple source nodes,
    // we need to append the new source node to the existing plugin_input list
    List<String> pluginInput = new ArrayList<>();
    Object raw = targetConfig.get(PLUGIN_INPUT);
    if (raw instanceof List<?> list) {
      for (Object e : list) {
        if (e != null) pluginInput.add(String.valueOf(e));
      }
    }

    pluginInput.add(commonId);

    sourceConfig.put(PLUGIN_OUTPUT, commonId);
    targetConfig.put(PLUGIN_INPUT, pluginInput);
  }


  public Pipeline getById(String id) {
    Optional<Pipeline> optionalEtlPipeline = repository.findById(id);
    if (optionalEtlPipeline.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
        "Etl pipeline not found.");
    }

    return optionalEtlPipeline.get();
  }

  public Page<PipelineProjection> getAll(Pageable pageable) {
    return repository.findAllProjectedBy(pageable);
  }

  public String delete(String id) {
    repository.deleteById(id);
    return "Successfully delete ETL pipeline with id:%s".formatted(id);
  }

  public PipelineProjection getEtlPipelineProjection(String id) {
    PipelineProjection etlPipeline = pipelineRepository.findProjectedById(id);
    if (ObjectUtils.isEmpty(etlPipeline)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
        "Etl pipeline not found.");
    }
    return etlPipeline;
  }


}
