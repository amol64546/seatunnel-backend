package com.seatunnel.orchestrator.service;

import com.seatunnel.orchestrator.enums.PluginType;
import com.seatunnel.orchestrator.model.Edge;
import com.seatunnel.orchestrator.model.EtlBrick;
import com.seatunnel.orchestrator.model.EtlPipeline;
import com.seatunnel.orchestrator.model.Node;
import com.seatunnel.orchestrator.projection.EtlPipelineProjection;
import com.seatunnel.orchestrator.repository.EtlBrickRepository;
import com.seatunnel.orchestrator.repository.EtlPipelineRepository;
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
public class EtlPipelineService {


  private final EtlPipelineRepository repository;
  private final EtlBrickRepository etlBrickRepository;
  private final EtlPipelineRepository etlPipelineRepository;

  public EtlPipeline create(EtlPipeline pipeline) {
    if (StringUtils.isNotBlank(pipeline.getId())) {
      EtlPipelineProjection etlPipelineProjection = getEtlPipelineProjection(pipeline.getId());
      pipeline.setId(pipeline.getId());
      pipeline.setCreatedOn(etlPipelineProjection.getCreatedOn());
    }

    // updating nodes config
    pipeline.getNodes().forEach(node -> {
      EtlBrick brick = etlBrickRepository.findEtlBrickById(node.getId());
      node.setName(brick.getName());
      node.setPluginType(brick.getPluginType());
      brick.getConfig().putAll(node.getConfig());
      node.setConfig(brick.getConfig());
    });
    connectNodes(pipeline);
    return repository.save(pipeline);
  }


  private void connectNodes(EtlPipeline pipeline) {
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

    String commonId = UUID.randomUUID().toString().replaceAll("-", "");

    Map<String, Object> sourceConfig = sourceMap.get(edge.getSource());
    Map<String, Object> targetMapConfig = targetMap.get(edge.getTarget());

    List<String> pluginInput = new ArrayList<>();
    Object raw = targetMapConfig.get(PLUGIN_INPUT);
    if (raw instanceof List<?> list) {
      for (Object e : list) {
        if (e != null) pluginInput.add(String.valueOf(e));
      }
    } else {
      pluginInput = new ArrayList<>();
    }

    pluginInput.add(commonId);

    sourceConfig.put(PLUGIN_OUTPUT, commonId);
    targetMapConfig.put(PLUGIN_INPUT, pluginInput);
  }


  public EtlPipeline getById(String id) {
    Optional<EtlPipeline> optionalEtlPipeline = repository.findById(id);
    if (optionalEtlPipeline.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
        "Etl pipeline not found.");
    }

    return optionalEtlPipeline.get();
  }

  public Page<EtlPipeline> getAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  public String delete(String id) {
    repository.deleteById(id);
    return "Successfully delete ETL pipeline with id:%s".formatted(id);
  }

  public EtlPipelineProjection getEtlPipelineProjection(String id) {
    EtlPipelineProjection etlPipeline = etlPipelineRepository.findProjectedById(id);
    if (ObjectUtils.isEmpty(etlPipeline)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
        "Etl pipeline not found.");
    }
    return etlPipeline;
  }


}
