package com.cedacri.microservice_project.service;

import com.cedacri.microservice_project.dto.ProductDto;

import java.util.concurrent.ExecutionException;

public interface ProductService {

    String createProduct(ProductDto productDto) throws ExecutionException, InterruptedException;

}
