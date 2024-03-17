package com.api.farmingsoon.domain;


import com.api.farmingsoon.common.clean.DatabaseCleanup;
import com.api.farmingsoon.common.util.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class IntegrationTest {

    @Autowired
    protected WebApplicationContext ctx;

    @Autowired
    protected Transaction transaction;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected DatabaseCleanup databaseCleanup;
}
