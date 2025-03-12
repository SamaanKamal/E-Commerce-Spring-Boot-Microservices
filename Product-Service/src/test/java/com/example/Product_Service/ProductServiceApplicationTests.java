package com.example.Product_Service;

import com.example.Product_Service.dto.ProductRequest;
import com.example.Product_Service.dto.ProductResponse;
import com.example.Product_Service.model.Product;
import com.example.Product_Service.repository.ProductRepository;
import com.example.Product_Service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {
	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductService productService;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongo.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString)).andExpect(status().isCreated());
		Assertions.assertEquals(3, productRepository.findAll().size());
	}

	@Test
	void shouldGetAllProducts() throws Exception {
		ProductRequest productRequest1 = new ProductRequest("Product 1", "Description 1", BigDecimal.valueOf(10.0));
		ProductRequest productRequest2 = new ProductRequest("Product 2", "Description 2", BigDecimal.valueOf(20.0));

		productService.createProduct(productRequest1);
		productService.createProduct(productRequest2);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()) // Expecting an HTTP 200 OK status
				.andExpect(jsonPath("$.size()").value(2)) // Expecting the size to be 2
				.andExpect(jsonPath("$[0].name").value("Product 1")) // First product's name should be "Product 1"
				.andExpect(jsonPath("$[1].name").value("Product 2")); // Second product's name should be "Product 2"
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("I Phone 13")
				.description("i phone 13")
				.price(BigDecimal.valueOf(1200))
				.build();
	}

}
