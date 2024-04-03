package com.elice.team4.singleShop.product.mapper;

import com.elice.team4.singleShop.product.controller.ProductForm;
import com.elice.team4.singleShop.product.domain.Product;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-03T12:16:28+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.6.jar, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toProduct(ProductForm form) {
        if ( form == null ) {
            return null;
        }

        Product product = new Product();

        product.setCategory( stringToCategory( form.getCategory() ) );
        product.setId( form.getId() );
        product.setName( form.getName() );
        product.setSummary( form.getSummary() );
        product.setDescription( form.getDescription() );
        product.setImage( form.getImage() );
        product.setStock( form.getStock() );
        product.setPrice( form.getPrice() );

        return product;
    }
}
