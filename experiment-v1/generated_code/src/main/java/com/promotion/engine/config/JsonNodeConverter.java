package com.promotion.engine.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.r2dbc.core.Parameter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Utility class for converting JsonNode objects to and from String for R2DBC persistence.
 */
@Component
public class JsonNodeConverter {
    private static final Logger logger = LoggerFactory.getLogger(JsonNodeConverter.class);
    private final ObjectMapper objectMapper;

    public JsonNodeConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Converts a JsonNode to a String.
     *
     * @param node The JsonNode to convert
     * @return The JSON string representation
     */
    public String convertToString(JsonNode node) {
        if (node == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            logger.error("Error converting JsonNode to String", e);
            return "{}";
        }
    }

    /**
     * Converts a String to a JsonNode.
     *
     * @param json The JSON string
     * @return The JsonNode representation
     */
    public JsonNode convertToJsonNode(String json) {
        if (json == null || json.isEmpty()) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            logger.error("Error converting String to JsonNode", e);
            return objectMapper.createObjectNode();
        }
    }
} 