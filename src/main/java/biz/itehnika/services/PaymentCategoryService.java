package biz.itehnika.services;

import biz.itehnika.config.AppConfig;
import biz.itehnika.model.Customer;
import biz.itehnika.model.PaymentCategory;
import biz.itehnika.repos.CustomerRepository;
import biz.itehnika.repos.PaymentCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentCategoryService {

    private final PaymentCategoryRepository paymentCategoryRepository;
    private final CustomerRepository customerRepository;

    public PaymentCategoryService(PaymentCategoryRepository paymentCategoryRepository, CustomerRepository customerRepository) {
        this.paymentCategoryRepository = paymentCategoryRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public PaymentCategory getByNameAndCustomer(String name, Customer customer) {
        return paymentCategoryRepository.findPaymentCategoryByNameAndCustomer(name, customer);
    }

    @Transactional(readOnly = true)
    public List<PaymentCategory> getPaymentCategoriesByCustomer(Customer customer){     //Get all category
        return paymentCategoryRepository.findPaymentCategoriesByCustomer(customer);
    }

    @Transactional
    public PaymentCategory getById(Long id){
        return paymentCategoryRepository.findById(id).get(); // because <Optional>
    }

    @Transactional
    public boolean addPaymentCategory(String name, String description, Customer customer){
        if (paymentCategoryRepository.existsPaymentCategoryByNameAndCustomer(name, customer)){
            return false;
        }
        PaymentCategory paymentCategory = new PaymentCategory(name, description, customer);
        paymentCategoryRepository.save(paymentCategory);
        return true;
    }

    @Transactional
    public void initPaymentCategoriesForCustomer(Customer customer){
        Customer customerAdmin = customerRepository.findCustomerByLogin(AppConfig.ADMIN_LOGIN);

        List<PaymentCategory> paymentCategories = getPaymentCategoriesByCustomer(customerAdmin);
        for (PaymentCategory category : paymentCategories){
            addPaymentCategory(category.getName(), category.getDescription(), customer);
        }
    }

    @Transactional
    public void deletePaymentCategories(List<Long> ids) {
        ids.forEach(id -> {
            Optional<PaymentCategory> paymentCategories = paymentCategoryRepository.findById(id);
            paymentCategories.ifPresent(u -> paymentCategoryRepository.deleteById(u.getId()));
        });
    }

    @Transactional
    public boolean updatePaymentCategory(Long id, String newName, String newDescription, Customer customer) {
        PaymentCategory paymentCategoryToUpdate = getById(id);
        PaymentCategory paymentCategoryToCheck = getByNameAndCustomer(newName, customer);
        if (paymentCategoryToCheck != null ){
            return false;
        }
        paymentCategoryToUpdate.setName(newName);
        paymentCategoryToUpdate.setDescription(newDescription);
        paymentCategoryRepository.save(paymentCategoryToUpdate);
        return true;
    }

}
