package com.seatunnel.orchestrator.validator;

import com.seatunnel.orchestrator.enums.PluginType;
import com.seatunnel.orchestrator.exception.ApiException;
import com.seatunnel.orchestrator.model.Edge;
import com.seatunnel.orchestrator.model.EtlPipeline;
import com.seatunnel.orchestrator.model.Node;
import com.seatunnel.orchestrator.projection.EtlBrickProjection;
import com.seatunnel.orchestrator.repository.EtlBrickRepository;
import com.seatunnel.orchestrator.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineValidator {

  private final EtlBrickRepository etlBrickRepository;
  private final CommonUtil commonUtil;

  public void validateEtlPipelineRequest(EtlPipeline request) {
    commonUtil.validateMethodArguments(request);

    List<String> nodeIds = request.getNodes().stream()
      .filter(Objects::nonNull)
      .map(Node::getConnectorId)
      .toList();

    List<EtlBrickProjection> bricks = etlBrickRepository.findAllProjectedByIdIn(nodeIds);

    Set<String> foundBrickIds = bricks.stream()
      .map(EtlBrickProjection::getId)
      .collect(Collectors.toSet());

    List<String> missingBrickIds = nodeIds.stream()
      .filter(id -> !foundBrickIds.contains(id))
      .toList();

    if (!missingBrickIds.isEmpty()) {
      throw new ApiException(HttpStatus.NOT_FOUND,
        "Bricks not found", missingBrickIds);
    }

    boolean hasSourceBrick = bricks.stream()
      .anyMatch(brick -> brick.getPluginType().equals(PluginType.SOURCE));

    boolean hasSinkBrick = bricks.stream()
      .anyMatch(brick -> brick.getPluginType().equals(PluginType.SINK));

    if (!hasSourceBrick || !hasSinkBrick)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "At least one source and sink brick required");

    List<String> errors = validateConnection(request);
    if (!errors.isEmpty()) {
      throw new ApiException(HttpStatus.BAD_REQUEST,
        "Plugin connection validation failed.", errors);
    }

  }


  public static List<String> validateConnection(EtlPipeline pipeline) {
    List<String> errors = new ArrayList<>();

    // Create a map of node IDs to their plugin types for quick lookup
    Map<String, PluginType> nodeTypeMap = new HashMap<>();
    for (Node node : pipeline.getNodes()) {
      if (node.getId() != null && node.getPluginType() != null) {
        nodeTypeMap.put(node.getId(), node.getPluginType());
      }
    }

    // Validate each edge connection
    List<Edge> edges = List.copyOf(pipeline.getEdges());
    for (int i = 0; i < edges.size(); i++) {
      Edge edge = edges.get(i);

      if (edge.getSource() == null) {
        errors.add(String.format("Edge[%d]: Source cannot be null", i));
        continue;
      }

      if (edge.getTarget() == null) {
        errors.add(String.format("Edge[%d]: Target cannot be null", i));
        continue;
      }

      String sourceId = edge.getSource();
      String targetId = edge.getTarget();

      // Check if both nodes exist
      if (!nodeTypeMap.containsKey(sourceId)) {
        errors.add(String.format("Edge[%d]: Source node with id '%s' not found in nodes list", i, sourceId));
        continue;
      }

      if (!nodeTypeMap.containsKey(targetId)) {
        errors.add(String.format("Edge[%d]: Target node with id '%s' not found in nodes list", i, targetId));
        continue;
      }

      PluginType sourceType = nodeTypeMap.get(sourceId);
      PluginType targetType = nodeTypeMap.get(targetId);

      // Validate connection rules
      String error = validateConnection(sourceType, targetType, sourceId, targetId);
      if (error != null) {
        errors.add(String.format("Edge[%d]: %s", i, error));
      }
    }

    return errors;
  }

  private static String validateConnection(PluginType sourceType, PluginType targetType,
                                           String sourceId, String targetId) {
    switch (sourceType) {
      case SOURCE:
        // SOURCE can connect to TRANSFORM or SINK
        if (targetType != PluginType.TRANSFORM && targetType != PluginType.SINK) {
          return String.format("Invalid connection: %s (%s) -> %s (%s). SOURCE can only connect to TRANSFORM or SINK",
            sourceId, sourceType, targetId, targetType);
        }
        break;

      case TRANSFORM:
        // TRANSFORM can only connect to SINK
        if (targetType != PluginType.SINK) {
          return String.format("Invalid connection: %s (%s) -> %s (%s). TRANSFORM can only connect to SINK",
            sourceId, sourceType, targetId, targetType);
        }
        break;

      case SINK:
        // SINK cannot be a source in any edge
        return String.format("Invalid connection: %s (%s) -> %s (%s). SINK cannot be a source node",
          sourceId, sourceType, targetId, targetType);

      default:
        return String.format("Unknown plugin type: %s", sourceType);
    }

    return null; // Valid connection
  }

}
