package ir.bigz.spring.restapi;

import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    public Product getProductById(long id) throws Exception {
        return productRepository.findById(id).orElseThrow(() -> new Exception("data not found"));
    }

    public Product createProduct(Product product) throws Exception {
        if(validateFields(product)){
            return productRepository.save(product);
        }

        throw new Exception("data not created");
    }

    public Product updateProduct(Long id, Product product) throws Exception {
        Product find = getProductById(id);
        mapSourceToTarget(product, find);
        productRepository.save(find);
        return find;
    }

    public Product updateFields(Long id, Map<String, Object> fields) throws Exception {
        Product find = getProductById(id);

        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Product.class, key);
            if(Objects.nonNull(field)){
                field.setAccessible(true);
                ReflectionUtils.setField(field, find, value);
            }
        });

        productRepository.save(find);
        return find;
    }

    public void deleteProductById(Long id) throws Exception{
        Product find = getProductById(id);
        productRepository.deleteById(find.getId());
    }

    private boolean validateFields(Product product){
        return Objects.nonNull(product.getName());
    }

    private void mapSourceToTarget(Product source, Product target){
        target.setName(source.getName());
        target.setPrice(source.getPrice());
        target.setDescription(source.getDescription());
    }
}
