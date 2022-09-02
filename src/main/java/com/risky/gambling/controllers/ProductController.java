package com.risky.gambling.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.risky.gambling.daos.ProductDAO;
import com.risky.gambling.mappers.ProductMapper;
import com.risky.gambling.models.Product;
import com.risky.gambling.utils.ComparisonOperator;
import com.risky.gambling.utils.DataMapping;
import com.risky.gambling.utils.Helpers;
import com.risky.gambling.utils.ViewName;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {
    
    @Autowired
    private ProductDAO productDAO;

    @ModelAttribute("statusList")
    public Map<Integer, String> getStatusList() {
        Map<Integer, String> statusList = new HashMap<Integer, String>();
        statusList.put(Helpers.INCOMPLETE, "Incomplete");
        statusList.put(Helpers.COMPLETED, "Completed");
        return statusList;
    }

    static String currentPage = "1";
    static String recordPerPage = "3";
    
    @GetMapping("/products")
    public String list(Model model) 
    {
        try {
            String orderBy = "created_at";
            String[] limit = {String.valueOf(Integer.parseInt(recordPerPage)*Integer.parseInt(currentPage) - Integer.parseInt(recordPerPage)), 
                              String.valueOf(Integer.parseInt(recordPerPage))};
            List<Product> listPerPage = productDAO.get(null, null, null, null, orderBy, limit);
            List<Product> allItems = productDAO.get(null, null, null, null, orderBy, null);
            Double totalPages = Math.ceil(allItems.size()/Integer.parseInt(recordPerPage));
            model.addAttribute("products", listPerPage);
            model.addAttribute("allItems", allItems.size());
            model.addAttribute("totalPages", totalPages);
            return ViewName.PRODUCT_LIST;
        } catch (Exception e) {
            return ViewName.ERROR_404;
        }
    }

    @GetMapping("/product-list")
    @ResponseBody
    public List<Product> listPagination(@RequestParam(name = "currentPage", required = false, defaultValue = "1") String currentPage, 
                                    @RequestParam(name = "recordPerPage", required = false, defaultValue = "3") String recordPerPage) 
    {
        try {
            String orderBy = "created_at";
            String[] limit = {String.valueOf(Integer.parseInt(recordPerPage)*Integer.parseInt(currentPage) - Integer.parseInt(recordPerPage)), 
                              String.valueOf(Integer.parseInt(recordPerPage))};
            List<Product> listPerPage = productDAO.get(null, null, null, null, orderBy, limit);
            return listPerPage;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @GetMapping("/products/create")
    public String createView(Model model)
    {
        try {
            model.addAttribute("product", new ProductMapper());
            return ViewName.PRODUCT_DETAIL;
        } catch (Exception e) {
            return ViewName.ERROR_404;
        }
    }

    @PostMapping("/products/create")
    public String createAction(@Valid @ModelAttribute("product") ProductMapper ProductMapper, BindingResult bindingResult, 
                                RedirectAttributes atts, Model model) {
        try {
            if(bindingResult.hasErrors()){
                model.addAttribute("oldData", ProductMapper);
                return ViewName.PRODUCT_DETAIL;
            }
            ArrayList<DataMapping> data = new ArrayList<DataMapping>();
            data.add(DataMapping.getInstance("title", ProductMapper.getName()));
            data.add(DataMapping.getInstance("description", ProductMapper.getDescription()));
            data.add(DataMapping.getInstance("thumbnail", ProductMapper.getThumbnail()));
            data.add(DataMapping.getInstance("price", String.valueOf(ProductMapper.getPrice())));
            data.add(DataMapping.getInstance("qty", String.valueOf(ProductMapper.getQty())));
            data.add(DataMapping.getInstance("created_at", LocalDateTime.now().toString()));
            data.add(DataMapping.getInstance("status", String.valueOf(ProductMapper.getStatus())));
            Number created = productDAO.create(data);
            if(created.intValue() > 0){
                atts.addFlashAttribute("success", "Successfully created new product");
            } else {
                atts.addFlashAttribute("fail", "Whoops! Something went wrong, cannot create new product");
            }
        } catch (Exception e) {
            atts.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/products/update")
    public String updateView(@RequestParam int id, Model model)
    {
        try {
            ArrayList<ComparisonOperator> conditions = new ArrayList<ComparisonOperator>();
            conditions.add(ComparisonOperator.getInstance("", "id", "=", String.valueOf(id), ""));
            ProductMapper productMapper = productDAO.getById(conditions);
            model.addAttribute("product", productMapper);
            return ViewName.PRODUCT_DETAIL;
        } catch (Exception e) {
            return ViewName.ERROR_404;
        }
    }

    @PostMapping("/products/update")
    public String updateAction(@RequestParam int id, @Valid @ModelAttribute("product") ProductMapper ProductMapper, BindingResult bindingResult, 
                                  RedirectAttributes atts, Model model) {
        try {
            if(bindingResult.hasErrors()){
                model.addAttribute("oldData", ProductMapper);
                return ViewName.PRODUCT_DETAIL;
            }
            ArrayList<ComparisonOperator> conditions = new ArrayList<ComparisonOperator>();
            conditions.add(ComparisonOperator.getInstance("", "id", "=", String.valueOf(id), ""));
            ProductMapper dbItem = productDAO.getById(conditions);
            ArrayList<DataMapping> data = new ArrayList<DataMapping>();
            if(!dbItem.getName().equals(ProductMapper.getName())){
                data.add(DataMapping.getInstance("title", ProductMapper.getName()));
            }
            if(!dbItem.getDescription().equals(ProductMapper.getDescription())){
                data.add(DataMapping.getInstance("description", ProductMapper.getDescription()));
            }
            if(!dbItem.getThumbnail().equals(ProductMapper.getThumbnail())){
                data.add(DataMapping.getInstance("thumbnail", ProductMapper.getThumbnail()));
            }
            if(!String.valueOf(dbItem.getPrice()).equals(String.valueOf(ProductMapper.getPrice()))){
                data.add(DataMapping.getInstance("price", String.valueOf(ProductMapper.getPrice())));
            }
            if(!String.valueOf(dbItem.getQty()).equals(String.valueOf(ProductMapper.getQty()))){
                data.add(DataMapping.getInstance("qty", String.valueOf(ProductMapper.getQty())));
            }
            if(!String.valueOf(dbItem.getStatus()).equals(String.valueOf(ProductMapper.getStatus()))){
                data.add(DataMapping.getInstance("status", String.valueOf(ProductMapper.getStatus())));
            }
            boolean updated = productDAO.update(data, conditions);
            if(updated){
                atts.addFlashAttribute("success", "Successfully updated new Product");
            } else {
                atts.addFlashAttribute("fail", "Whoops! Something went wrong, cannot update new Product");
            }
        } catch (Exception e) {
            atts.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }
    
    @PostMapping("/products/delete")
    public String deleteAction(@RequestParam int id, RedirectAttributes atts)
    {
        try {
            ArrayList<ComparisonOperator> conditions = new ArrayList<ComparisonOperator>();
            conditions.add(ComparisonOperator.getInstance("", "id", "=", String.valueOf(id), ""));
            boolean deleted = productDAO.delete(conditions);
            if(deleted){
                atts.addFlashAttribute("success", "Successfully deleted new Product");
            } else {
                atts.addFlashAttribute("fail", "Whoops! Something went wrong, cannot delete new Product");
            }
        } catch (Exception e) {
            atts.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }
    
    @GetMapping("/product-search")
    @ResponseBody
    public List<Product>  onSearchAll(@RequestParam(name = "search", required = false) String search, 
                               @RequestParam(name = "startDate", required = false) String startDate,
                               @RequestParam(name = "endDate", required = false) String endDate, 
                               @RequestParam(name = "status", required = false) String status, Model model)
    {
        try {
            ArrayList<ComparisonOperator> conditions = conditionList(search, startDate, endDate, status);
            String orderBy = "created_at";
            System.out.println("search:"+search+"-startDate:"+startDate);
            System.out.println("endDate:"+endDate+"-status:"+status);
            List<Product> searchedItems = productDAO.get(null, conditions, null, null, orderBy, null);
            model.addAttribute("searchedItems", searchedItems);
            return searchedItems;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @GetMapping("/product-searchPagination")
    @ResponseBody
    public List<Product> onSearchPagination(@RequestParam(name = "search", required = false) String search, 
                               @RequestParam(name = "startDate", required = false) String startDate,
                               @RequestParam(name = "endDate", required = false) String endDate, 
                               @RequestParam(name = "status", required = false) String status,
                               @RequestParam(name = "currentPage", required = false, defaultValue = "1") String currentPage, 
                               @RequestParam(name = "recordPerPage", required = false, defaultValue = "3") String recordPerPage) 
    {
        try {
            ArrayList<ComparisonOperator> conditions = conditionList(search, startDate, endDate, status);
            String orderBy = "created_at";
            System.out.println("search:"+search+"-startDate:"+startDate);
            System.out.println("endDate:"+endDate+"-status:"+status);
            String[] limit = {String.valueOf(Integer.parseInt(recordPerPage)*Integer.parseInt(currentPage) - Integer.parseInt(recordPerPage)), 
                              String.valueOf(Integer.parseInt(recordPerPage))};
            List<Product> listPerPage = productDAO.get(null, conditions, null, null, orderBy, limit);
            return listPerPage;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<ComparisonOperator> conditionList(String search, String startDate, String endDate, String status)
    {
        ArrayList<ComparisonOperator> conditions = new ArrayList<ComparisonOperator>();
        //four
        if(search != null && startDate != null && endDate != null && status != null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "start_date", ">=", startDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "end_date", "<=", endDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        //one
        if(search != null && startDate == null && endDate == null && status == null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
        }
        if(search == null && startDate != null && endDate == null && status == null){
            conditions.add(ComparisonOperator.getInstance("", "start_date", "=", startDate, ""));
        }
        if(search == null && startDate == null && endDate != null && status == null){
            conditions.add(ComparisonOperator.getInstance("", "end_date", "=", endDate, ""));
        }
        if(search == null && startDate == null && endDate == null && status != null){
            conditions.add(ComparisonOperator.getInstance("", "status", "=", status, ""));
        }
        //two
        if(search != null && startDate != null && endDate == null && status == null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "start_date", "=", startDate, ""));
        }
        if(search == null && startDate != null && endDate != null && status == null){
            conditions.add(ComparisonOperator.getInstance("", "start_date", ">=", startDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "end_date", "<=", endDate, ""));
        }
        if(search == null && startDate == null && endDate != null && status != null){
            conditions.add(ComparisonOperator.getInstance("", "end_date", "=", endDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        if(search != null && startDate == null && endDate != null && status == null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "end_date", "=", endDate, ""));
        }
        if(search != null && startDate == null && endDate == null && status != null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        if(search == null && startDate != null && endDate == null && status != null){
            conditions.add(ComparisonOperator.getInstance("", "start_date", "=", startDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        //three
        if(search != null && startDate != null && endDate != null && status == null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "start_date", ">=", startDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "end_date", "<=", endDate, ""));
        }
        if(search != null && startDate != null && endDate == null && status != null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "start_date", "=", startDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        if(search != null && startDate == null && endDate != null && status != null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "end_date", "=", endDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        if(search == null && startDate != null && endDate != null && status != null){
            conditions.add(ComparisonOperator.getInstance(" and ", "start_date", ">=", startDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "end_date", "<=", endDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        return conditions;
    }

}

class searchParam {
    public String search;
    public String startDate;
    public String endDate;
    public String status;
    public static searchParam searchParam;

    static searchParam getInstance(String search, String startDate, String endDate, String status){
        if(searchParam == null){
            searchParam item = new searchParam();
            item.search = search;
            item.startDate = startDate;
            item.endDate = endDate;
            item.status = status;
            return item;
        }
        return searchParam;
    }
}
