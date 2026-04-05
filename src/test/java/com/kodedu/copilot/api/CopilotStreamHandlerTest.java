package com.kodedu.copilot.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CopilotStreamHandler, focusing on tool call argument accumulation
 * across multiple streaming SSE chunks.
 */
class CopilotStreamHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<String> contentChunks;
    private List<String> toolCallJsons;
    private AtomicBoolean completed;
    private List<String> errors;
    private CopilotStreamHandler handler;

    @BeforeEach
    void setUp() {
        contentChunks = new ArrayList<>();
        toolCallJsons = new ArrayList<>();
        completed = new AtomicBoolean(false);
        errors = new ArrayList<>();
        handler = new CopilotStreamHandler(
                contentChunks::add,
                toolCallJsons::add,
                () -> completed.set(true),
                errors::add
        );
    }

    /**
     * Reproduces the exact error from the problem statement:
     * streaming tool_calls with partial arguments like {"\"
     */
    @Test
    void shouldAccumulateStreamingToolCallArguments() throws Exception {
        // Chunk 1: function name and empty arguments
        handler.processLine("data: {\"choices\":[{\"delta\":{\"tool_calls\":[{\"function\":{\"name\":\"read_file\",\"arguments\":\"\"},\"type\":\"function\",\"id\":\"call_abc123\",\"index\":0}]}}]}");

        // No tool call should be dispatched yet
        assertEquals(0, toolCallJsons.size(), "Tool call should not be dispatched during accumulation");

        // Chunk 2: partial arguments (the exact scenario from the bug report)
        handler.processLine("data: {\"choices\":[{\"index\":0,\"content_filter_results\":{},\"delta\":{\"content\":null,\"tool_calls\":[{\"function\":{\"arguments\":\"{\\\"\"}, \"index\":0}]}}]}");
        assertEquals(0, toolCallJsons.size());

        // Chunk 3: more argument fragments
        handler.processLine("data: {\"choices\":[{\"delta\":{\"tool_calls\":[{\"function\":{\"arguments\":\"path\"}, \"index\":0}]}}]}");
        assertEquals(0, toolCallJsons.size());

        // Chunk 4: more argument fragments
        handler.processLine("data: {\"choices\":[{\"delta\":{\"tool_calls\":[{\"function\":{\"arguments\":\"\\\":\\\"test.adoc\\\"\"}, \"index\":0}]}}]}");
        assertEquals(0, toolCallJsons.size());

        // Chunk 5: closing brace
        handler.processLine("data: {\"choices\":[{\"delta\":{\"tool_calls\":[{\"function\":{\"arguments\":\"}\"}, \"index\":0}]}}]}");
        assertEquals(0, toolCallJsons.size());

        // Finish reason chunk (no tool_calls in delta) triggers flush
        handler.processLine("data: {\"choices\":[{\"index\":0,\"delta\":{},\"finish_reason\":\"tool_calls\"}]}");

        // Now the tool call should have been dispatched exactly once
        assertEquals(1, toolCallJsons.size(), "Tool call should be dispatched once after accumulation");

        // Parse the dispatched JSON and verify it's valid and complete
        String toolCallJson = toolCallJsons.get(0);
        JsonNode root = objectMapper.readTree(toolCallJson);
        JsonNode toolCalls = root.path("choices").get(0).path("delta").path("tool_calls");
        assertTrue(toolCalls.isArray());
        assertEquals("read_file", toolCalls.get(0).path("function").path("name").asText());

        // The arguments should be a complete, parseable JSON string
        String argsStr = toolCalls.get(0).path("function").path("arguments").asText();
        JsonNode args = objectMapper.readTree(argsStr);
        assertEquals("test.adoc", args.path("path").asText());

        // Tool call ID should be preserved
        assertEquals("call_abc123", toolCalls.get(0).path("id").asText());
    }

    /**
     * Tool call should also flush on [DONE] if no explicit finish_reason chunk arrives.
     */
    @Test
    void shouldFlushToolCallOnDone() throws Exception {
        handler.processLine("data: {\"choices\":[{\"delta\":{\"tool_calls\":[{\"function\":{\"name\":\"list_files\",\"arguments\":\"{\\\"path\\\":\\\".\\\"}\"}}]}}]}");
        assertEquals(0, toolCallJsons.size());

        handler.processLine("data: [DONE]");

        assertEquals(1, toolCallJsons.size());
        assertTrue(completed.get());

        JsonNode root = objectMapper.readTree(toolCallJsons.get(0));
        String argsStr = root.path("choices").get(0).path("delta").path("tool_calls")
                .get(0).path("function").path("arguments").asText();
        JsonNode args = objectMapper.readTree(argsStr);
        assertEquals(".", args.path("path").asText());
    }

    /**
     * Content streaming (non-tool-call) should continue to work as before.
     */
    @Test
    void shouldStillHandleContentStreaming() {
        handler.processLine("data: {\"choices\":[{\"delta\":{\"content\":\"Hello\"}}]}");
        handler.processLine("data: {\"choices\":[{\"delta\":{\"content\":\" world\"}}]}");
        handler.processLine("data: [DONE]");

        assertEquals(2, contentChunks.size());
        assertEquals("Hello", contentChunks.get(0));
        assertEquals(" world", contentChunks.get(1));
        assertTrue(completed.get());
        assertEquals(0, toolCallJsons.size());
    }

    /**
     * Arguments containing escape sequences should be properly preserved
     * through the accumulate → flush → parse round-trip.
     */
    @Test
    void shouldPreserveEscapeSequencesInArguments() throws Exception {
        // Arguments containing newlines and quotes
        handler.processLine("data: {\"choices\":[{\"delta\":{\"tool_calls\":[{\"function\":{\"name\":\"write_file\",\"arguments\":\"{\\\"path\\\":\\\"test.adoc\\\",\\\"content\\\":\\\"line1\\\\nline2\\\"}\"}}]}}]}");
        handler.processLine("data: {\"choices\":[{\"delta\":{},\"finish_reason\":\"tool_calls\"}]}");

        assertEquals(1, toolCallJsons.size());
        JsonNode root = objectMapper.readTree(toolCallJsons.get(0));
        String argsStr = root.path("choices").get(0).path("delta").path("tool_calls")
                .get(0).path("function").path("arguments").asText();
        JsonNode args = objectMapper.readTree(argsStr);
        assertEquals("test.adoc", args.path("path").asText());
        assertEquals("line1\nline2", args.path("content").asText());
    }

    /**
     * Empty lines and null should be ignored.
     */
    @Test
    void shouldIgnoreEmptyAndNullLines() {
        handler.processLine(null);
        handler.processLine("");
        handler.processLine("data: {\"choices\":[{\"delta\":{\"content\":\"ok\"}}]}");
        handler.processLine("data: [DONE]");

        assertEquals(1, contentChunks.size());
        assertEquals("ok", contentChunks.get(0));
        assertTrue(completed.get());
    }

    /**
     * Content with null value should not produce content chunks.
     */
    @Test
    void shouldIgnoreNullContent() {
        handler.processLine("data: {\"choices\":[{\"delta\":{\"content\":null}}]}");
        handler.processLine("data: [DONE]");

        assertEquals(0, contentChunks.size());
        assertTrue(completed.get());
    }

    /**
     * A single chunk with complete arguments (non-streaming case) should still work.
     */
    @Test
    void shouldHandleSingleChunkToolCall() throws Exception {
        handler.processLine("data: {\"choices\":[{\"delta\":{\"tool_calls\":[{\"function\":{\"name\":\"read_file\",\"arguments\":\"{\\\"path\\\":\\\"README.adoc\\\"}\"}, \"id\":\"call_xyz\"}]}}]}");
        handler.processLine("data: {\"choices\":[{\"delta\":{},\"finish_reason\":\"tool_calls\"}]}");

        assertEquals(1, toolCallJsons.size());
        JsonNode root = objectMapper.readTree(toolCallJsons.get(0));
        JsonNode tc = root.path("choices").get(0).path("delta").path("tool_calls").get(0);
        assertEquals("read_file", tc.path("function").path("name").asText());

        JsonNode args = objectMapper.readTree(tc.path("function").path("arguments").asText());
        assertEquals("README.adoc", args.path("path").asText());
        assertEquals("call_xyz", tc.path("id").asText());
    }
}
