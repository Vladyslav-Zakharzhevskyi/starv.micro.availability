package com.starv.micro.availiability.repository;

import com.starv.micro.availiability.model.ProductAvailability;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "products_availability", path = "availability")
public interface ProductAvailabilityRepository extends CrudRepository<ProductAvailability, Long> {
}
