package com.starv.micro.availiability.config.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starv.micro.availiability.model.ProductAvailability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.util.List;

@Configuration
public class StartupDataPopulate implements ApplicationListener<ApplicationReadyEvent> {

    @Value("classpath:db/data.json")
    private Resource resource;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MongoTemplate template;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            //save products
            List<ProductAvailability> src = mapper.readValue(resource.getFile(), new TypeReference<>() {});
            List<ProductAvailability> pas = template.findAll(ProductAvailability.class);
            src.stream()
                    .filter(pa -> !isProductExist(pa, pas))
                    .forEach(pa -> template.insert(pa));
            System.out.println("Product were checked and added successfully;");
        } catch (IOException e) {
            System.out.println("Can't parse initial data to insert to Mongo db.");
            throw new RuntimeException("Error handling initial availability data.");
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                //ignore
            } else {
                throw new RuntimeException("Error happened during data processing and save operation.", e);
            }
        }
    }

    private boolean isProductExist(ProductAvailability pa, List<ProductAvailability> pas) {
        return pas.stream()
                .map(ProductAvailability::getSku)
                .anyMatch(sku -> sku.equals(pa.getSku()));
    }

}
