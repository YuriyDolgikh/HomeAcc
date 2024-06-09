package biz.itehnika.controllers;

import biz.itehnika.model.Customer;
import biz.itehnika.model.PaymentCategory;
import biz.itehnika.services.CustomerService;
import biz.itehnika.services.PaymentCategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PaymentCategoryController {

    private final CustomerService customerService;
    private final PaymentCategoryService paymentCategoryService;

    public PaymentCategoryController(CustomerService customerService, PaymentCategoryService paymentCategoryService) {
        this.customerService = customerService;
        this.paymentCategoryService = paymentCategoryService;
    }

    @GetMapping("/addNewCategory")
    public String addNewCategory() {
        return "addNewCategory";
    }

    @PostMapping(value = "/addNewCategory")
    public String addNewCategory(@RequestParam String name,
                                 @RequestParam String description,
                                 Model model) {

        User user = CustomerController.getCurrentUser();
        Customer customer = customerService.findByLogin(user.getUsername());

        if ( ! paymentCategoryService.addPaymentCategory(name, description, customer)) {
            model.addAttribute("exists", true);
            model.addAttribute("name", name);
            model.addAttribute("description", description);
            return "addNewCategory";
        }
        model.addAttribute("added", true);
        model.addAttribute("name", name);

        if (CustomerController.isAdmin(user)){
            return "redirect:/admin";
        }
        return "redirect:/settings";
    }

    @PostMapping(value = "/deleteCategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')") // SpEL !!!
    public String deleteCategory(@RequestParam(name = "toDelete", required = false) List<Long> ids, Model model) {
        if (ids != null && !ids.isEmpty()) {
            paymentCategoryService.deletePaymentCategories(ids);
        }
        User user = CustomerController.getCurrentUser();
        Customer customer = customerService.findByLogin(user.getUsername());
        model.addAttribute("categories", paymentCategoryService.getPaymentCategoriesByCustomer(customer));

        if (CustomerController.isAdmin(user)){
            return "redirect:/admin";
        }
        return "redirect:/settings";
    }

    @GetMapping("/updateCategory/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public String updateCategory(@PathVariable(value = "id") Long id, Model model) {

        PaymentCategory paymentCategory = paymentCategoryService.getById(id);

        model.addAttribute("name", paymentCategory.getName());
        model.addAttribute("description", paymentCategory.getDescription());

        return "updateCategory";
    }

    @PostMapping(value = "/updateCategory")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')") // SpEL !!!
    public String updateCategory(@RequestParam() Long id,
                                 @RequestParam() String name,
                                 @RequestParam(required = false) String description,
                                 Model model) {
        User user = CustomerController.getCurrentUser();
        Customer customer = customerService.findByLogin(user.getUsername());
        PaymentCategory paymentCategory = paymentCategoryService.getById(id);

        if ( ! paymentCategoryService.updatePaymentCategory(paymentCategory.getId(), name, description, customer)) {
            model.addAttribute("exists", true);
            model.addAttribute("name", paymentCategory.getName());
            model.addAttribute("description", paymentCategory.getDescription());
        }else {
            model.addAttribute("updated", true);
            model.addAttribute("name", name);
            model.addAttribute("description", description);
        }
        model.addAttribute("id", paymentCategory.getId());

        if (CustomerController.isAdmin(user)){
            return "redirect:/admin";
        }
        return "redirect:/settings";
    }

}
